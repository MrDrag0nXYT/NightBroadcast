package zxc.MrDrag0nXYT.nightBroadcast.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import zxc.MrDrag0nXYT.nightBroadcast.NightBroadcast;
import zxc.MrDrag0nXYT.nightBroadcast.util.Utilities;
import zxc.MrDrag0nXYT.nightBroadcast.util.config.Config;
import zxc.MrDrag0nXYT.nightBroadcast.util.config.Messages;

import java.util.List;

import static zxc.MrDrag0nXYT.nightBroadcast.NightBroadcast.cooldown;

public class AdminCommand implements CommandExecutor, TabCompleter {

    private final NightBroadcast plugin;

    private Config mainConfig;
    private Messages messagesConfig;

    public AdminCommand(NightBroadcast plugin, Config mainConfig, Messages messagesConfig) {
        this.plugin = plugin;
        this.mainConfig = mainConfig;
        this.messagesConfig = messagesConfig;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        YamlConfiguration messages = messagesConfig.getConfig();

        if (strings.length == 0) {
            for (String string : messages.getStringList("commands.nightbroadcast.usage")) {
                commandSender.sendMessage(
                        Utilities.setColor(string)
                );
            }
            return true;
        }

        switch (strings[0].toLowerCase()) {

            case "reload" -> {
                if (commandSender.hasPermission("nightbc.admin.reload")) {
                    cooldown.clear();
                    plugin.reload();
                    for (String string : messages.getStringList("commands.nightbroadcast.reloaded")) {
                        commandSender.sendMessage(
                                Utilities.setColor(string)
                        );
                    }

                } else {
                    for (String string : messages.getStringList("global.no-permission")) {
                        commandSender.sendMessage(
                                Utilities.setColor(string)
                        );
                    }
                }
            }

            default -> {
                for (String string : messages.getStringList("commands.nightbroadcast.usage")) {
                    commandSender.sendMessage(
                            Utilities.setColor(string)
                    );
                }
            }
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (strings.length == 1) {
            return List.of("reload");
        }

        return List.of();
    }
}
