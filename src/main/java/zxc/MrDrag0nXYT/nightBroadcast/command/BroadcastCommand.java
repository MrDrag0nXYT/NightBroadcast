package zxc.MrDrag0nXYT.nightBroadcast.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.TitlePart;
import net.kyori.adventure.util.Ticks;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import zxc.MrDrag0nXYT.nightBroadcast.NightBroadcast;
import zxc.MrDrag0nXYT.nightBroadcast.util.Utilities;
import zxc.MrDrag0nXYT.nightBroadcast.util.config.Config;
import zxc.MrDrag0nXYT.nightBroadcast.util.config.Messages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static zxc.MrDrag0nXYT.nightBroadcast.NightBroadcast.cooldown;

public class BroadcastCommand implements CommandExecutor, TabCompleter {

    private final NightBroadcast plugin;

    private Config mainConfig;
    private Messages messagesConfig;

    public BroadcastCommand(NightBroadcast plugin, Config mainConfig, Messages messagesConfig) {
        this.plugin = plugin;
        this.mainConfig = mainConfig;
        this.messagesConfig = messagesConfig;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        YamlConfiguration messages = messagesConfig.getConfig();
        YamlConfiguration config = mainConfig.getConfig();

        if (strings.length < 2) {
            for (String string : messages.getStringList("commands.broadcast.usage")) {
                commandSender.sendMessage(
                        Utilities.setColor(string)
                );
            }
            return true;
        }

        if (!commandSender.hasPermission("nightbc.player.broadcast")) {
            for (String string : messages.getStringList("global.no-permission")) {
                commandSender.sendMessage(
                        Utilities.setColor(string)
                );
            }
            return true;
        }

        if (commandSender instanceof Player player) {
            if ((player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20) <= config.getLong("min-played-time", 3600L)) {
                for (String string : messages.getStringList("commands.broadcast.not-played-min-time")) {
                    player.sendMessage(Utilities.setColor(string));
                }
                return true;
            }

            if (cooldown.containsKey(player.getUniqueId())) {
                for (String string : messages.getStringList("commands.broadcast.cooldown")) {
                    player.sendMessage(
                            Utilities.setColor(string)
                    );
                }
                return true;
            }
        }

        ConfigurationSection categoriesSection = config.getConfigurationSection("categories");

        String[] messageArray = Arrays.copyOfRange(strings, 1, strings.length);
        String message = String.join(" ", messageArray);

        if (categoriesSection != null) {

            if (categoriesSection.contains(strings[0])) {

                if (categoriesSection.getString(strings[0] + ".permission") != null) {

                    if (commandSender.hasPermission(
                            categoriesSection.getString(strings[0] + ".permission")
                    )) {
                        sendMessageWithCheck(commandSender, strings, messages, config, categoriesSection, message);

                    } else {
                        for (String string : messages.getStringList("commands.broadcast.no-permission")) {
                            commandSender.sendMessage(
                                    Utilities.setColor(string)
                            );
                        }
                        return true;
                    }

                } else {
                    sendMessageWithCheck(commandSender, strings, messages, config, categoriesSection, message);
                }

            } else {
                for (String string : messages.getStringList("commands.broadcast.not-found")) {
                    commandSender.sendMessage(
                            Utilities.setColor(
                                    string
                                            .replace("%category_name%", strings[0])
                            )
                    );
                }
            }
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        YamlConfiguration config = mainConfig.getConfig();
        ConfigurationSection categoriesSection = config.getConfigurationSection("categories");

        if (strings.length == 1) {
            if (categoriesSection != null) {
                List<String> categories = new ArrayList<>();

                for (String category : categoriesSection.getKeys(false)) {
                    if (categoriesSection.getString(category + ".permission") != null) {
                        if (commandSender.hasPermission(
                                categoriesSection.getString(category + ".permission")
                        )) {
                            categories.add(category);
                        }
                    } else {
                        categories.add(category);
                    }
                }
                return categories;
            }
        }

        return List.of();
    }

    /*
     * А вот вы когда-нибудь задумывались что
     */

    private void sendMessageWithCheck(@NotNull CommandSender commandSender, @NotNull String[] strings, YamlConfiguration messages, YamlConfiguration config, ConfigurationSection categoriesSection, String message) {
        if (categoriesSection.getBoolean(strings[0] + ".only-for-players", true)) {

            if (commandSender instanceof Player player) {
                broadcastMessage(player, config, strings[0], message);

            } else {
                for (String string : messages.getStringList("global.only-for-players")) {
                    commandSender.sendMessage(
                            Utilities.setColor(string)
                    );
                }
            }

        } else {
            if (commandSender instanceof Player player) {
                broadcastMessage(player, config, strings[0], message);
                return;
            }

            broadcastMessage(null, config, strings[0], message);
        }
    }

    private void broadcastMessage(Player sender, YamlConfiguration yamlConfiguration, String category, String message) {
        for (String string : yamlConfiguration.getStringList("categories." + category + ".format")) {
            String formattedString = string.replace("%broadcasttext%", message);
            Component readyString = Utilities.setColorWithPlaceholders(
                    sender,
                    formattedString
            );

            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage(readyString);
            }

            if (yamlConfiguration.getBoolean("send-broadcasts-to-console", false)) {
                Bukkit.getConsoleSender().sendMessage(readyString);
            }
        }

        broadcastTitle(sender, message, yamlConfiguration, category);

        if (yamlConfiguration.getBoolean("categories." + category + ".sound.enabled", false)) {
            broadcastSound(yamlConfiguration, category);
        }

        if (!sender.hasPermission("nightbc.player.broadcast.cooldown.bypass")) {
            long cooldownTime = yamlConfiguration.getLong("categories." + category + ".cooldown", 0) * 1000;

            if (cooldownTime > 0) {
                cooldown.put(sender.getUniqueId(), cooldownTime);
                Bukkit.getScheduler().runTaskLater(plugin, () -> cooldown.remove(sender.getUniqueId()), cooldownTime / 50);
            }
        }
    }

    private void broadcastTitle(Player sender, String message, YamlConfiguration yamlConfiguration, String category) {
        if (yamlConfiguration.contains("categories." + category + ".title")) {
            if (yamlConfiguration.getBoolean("categories." + category + ".title.enabled", false)) {

                Component title = Utilities.setColorWithPlaceholders(sender, yamlConfiguration.getString(
                                "categories." + category + ".title.title", " ")
                        .replace("%broadcasttext%", message)
                );
                Component subtitle = Utilities.setColorWithPlaceholders(sender, yamlConfiguration.getString(
                                "categories." + category + ".title.subtitle", " ")
                        .replace("%broadcasttext%", message)
                );
                Component actionbar = Utilities.setColorWithPlaceholders(sender, yamlConfiguration.getString(
                                "categories." + category + ".title.actionbar", " ")
                        .replace("%broadcasttext%", message)
                );

                long fadeIn = yamlConfiguration.getLong("categories." + category + ".title.time.fade-in", 10);
                long stay = yamlConfiguration.getLong("categories." + category + ".title.time.stay", 70);
                long fadeOut = yamlConfiguration.getLong("categories." + category + ".title.time.fade-out", 20);

                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.sendTitlePart(TitlePart.TITLE, title);
                    player.sendTitlePart(TitlePart.SUBTITLE, subtitle);
                    player.sendTitlePart(TitlePart.TIMES, Title.Times.times(Ticks.duration(fadeIn), Ticks.duration(stay), Ticks.duration(fadeOut)));
                    player.sendActionBar(actionbar);
                }
            }
        }
    }

    private void broadcastSound(YamlConfiguration yamlConfiguration, String category) {
        String soundName = yamlConfiguration.getString("categories." + category + ".sound.name");

        if (soundName != null) {
            Sound sound = Sound.valueOf(soundName);
            float volume = (float) yamlConfiguration.getDouble("categories." + category + ".sound.volume", 1.0);
            float pitch = (float) yamlConfiguration.getDouble("categories." + category + ".sound.pitch", 1.0);

            for (Player player : Bukkit.getOnlinePlayers()) {
                player.playSound(player.getLocation(), sound, volume, pitch);
            }
        }
    }
}
