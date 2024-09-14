package zxc.MrDrag0nXYT.nightBroadcast;

import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import zxc.MrDrag0nXYT.nightBroadcast.command.AdminCommand;
import zxc.MrDrag0nXYT.nightBroadcast.command.BroadcastCommand;
import zxc.MrDrag0nXYT.nightBroadcast.util.Utilities;
import zxc.MrDrag0nXYT.nightBroadcast.util.config.Config;
import zxc.MrDrag0nXYT.nightBroadcast.util.config.Messages;

import java.util.HashMap;
import java.util.UUID;

public final class NightBroadcast extends JavaPlugin {

    private Config mainConfig;
    private Messages messagesConfig;

    public static final HashMap<UUID, Long> cooldown = new HashMap<>();

    @Override
    public void onEnable() {
        mainConfig = new Config(this);
        messagesConfig = new Messages(this);

        if (mainConfig.getConfig().getBoolean("enable-metrics", true)) {
            Metrics metrics = new Metrics(this, 23354);
        }

        getCommand("broadcast").setExecutor(new BroadcastCommand(this, mainConfig, messagesConfig));
        getCommand("nightbroadcast").setExecutor(new AdminCommand(this, mainConfig, messagesConfig));

        sendTitle(true);
    }

    @Override
    public void onDisable() {
        sendTitle(false);
    }

    public void reload() {
        mainConfig.reload();
        messagesConfig.reload();
    }



    private void sendTitle(boolean isEnable) {
        String isEnableMessage = isEnable ? "<#ace1af>Plugin successfully loaded!" : "<#d45079>Plugin successfully unloaded!";

        ConsoleCommandSender sender = Bukkit.getConsoleSender();

        sender.sendMessage(Utilities.setColor(" "));
        sender.sendMessage(Utilities.setColor(" <#a880ff>█▄░█ █ █▀▀ █░█ ▀█▀ █▄▄ █▀█ █▀█ ▄▀█ █▀▄ █▀▀ ▄▀█ █▀ ▀█▀</#a880ff>    <#696969>|</#696969>    <#fcfcfc>Version: <#a880ff>" + this.getDescription().getVersion() + "</#a880ff>"));
        sender.sendMessage(Utilities.setColor(" <#a880ff>█░▀█ █ █▄█ █▀█ ░█░ █▄█ █▀▄ █▄█ █▀█ █▄▀ █▄▄ █▀█ ▄█ ░█░</#a880ff>    <#696969>|</#696969>    <#fcfcfc>Author: <#a880ff>MrDrag0nXYT (https://drakoshaslv.ru)</#a880ff>"));
        sender.sendMessage(Utilities.setColor(" "));
        sender.sendMessage(Utilities.setColor(" " + isEnableMessage));
        sender.sendMessage(Utilities.setColor(" "));
    }
}
