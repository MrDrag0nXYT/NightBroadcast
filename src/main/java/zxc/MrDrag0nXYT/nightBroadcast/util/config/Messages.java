package zxc.MrDrag0nXYT.nightBroadcast.util.config;

import org.bukkit.configuration.file.YamlConfiguration;
import zxc.MrDrag0nXYT.nightBroadcast.NightBroadcast;

import java.io.File;
import java.util.List;

public class Messages {

    private final NightBroadcast plugin;
    private File file;
    private YamlConfiguration config;

    public Messages(NightBroadcast plugin) {
        this.plugin = plugin;

        init();
        updateConfig();
    }

    private void init() {
        file = new File(plugin.getDataFolder(), "messages.yml");
        if (!file.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(file);
    }

    public void save() {
        try {
            config.save(file);
        } catch (Exception e) {
            plugin.getLogger().severe(String.valueOf(e));
        }
    }

    public void reload() {
        try {
            config.load(file);
        } catch (Exception e) {
            plugin.getLogger().severe(String.valueOf(e));
        }
    }

    public YamlConfiguration getConfig() {
        return config;
    }



    private void checkConfigValue(String key, Object defaultValue) {
        if (!config.contains(key)) {
            config.set(key, defaultValue);
        }
    }

    private void updateConfig() {
        checkConfigValue("global.no-permission", List.of(" <#745c97>NightBroadcast <#c0c0c0>› <#fcfcfc>У вас <#d45079>недостаточно прав</#d45079> для выполнения этой команды!"));
        checkConfigValue("global.only-for-players", List.of(" <#745c97>NightBroadcast <#c0c0c0>› <#fcfcfc>Сообщение в этой категории может отправлять <#d45079>только игрок</#d45079>!"));

        checkConfigValue("commands.broadcast.not-played-min-time", List.of(" <#745c97>NightBroadcast <#c0c0c0>› <#fcfcfc>Для использования этой команды <#745c97>вам необходимо наиграть хотя бы 1 час</#745c97>!"));
        checkConfigValue("commands.broadcast.usage", List.of(
                "",
                " <#745c97>NightBroadcast <#c0c0c0>› <#fcfcfc>Использование:",
                "  <#c0c0c0>‣ <click:suggest_command:'/broadcast'><#745c97>/broadcast категория текст</click> <#c0c0c0>- <#fcfcfc>отправить сообщение",
                ""
        ));
        checkConfigValue("commands.broadcast.no-permission", List.of(" <#745c97>NightBroadcast <#c0c0c0>› <#fcfcfc>У вас <#d45079>недостаточно прав</#d45079> для отправки в эту категорию!"));
        checkConfigValue("commands.broadcast.not-found", List.of(" <#745c97>NightBroadcast <#c0c0c0>› <#fcfcfc>Категория <#745c97>%category_name%</#745c97> не найдена"));
        checkConfigValue("commands.broadcast.cooldown", List.of(" <#745c97>NightBroadcast <#c0c0c0>› <#fcfcfc>Вы не можете использовать эту команду <#d45079>так часто</#d45079>!"));

        checkConfigValue("commands.nightbroadcast.usage", List.of(
                "",
                " <#745c97>NightBroadcast <#c0c0c0>› <#fcfcfc>Использование:",
                "  <#c0c0c0>‣ <click:suggest_command:'/nightbroadcast reload'><#745c97>/nightbroadcast reload</click> <#c0c0c0>- <#fcfcfc>перезагрузить плагин",
                ""
        ));
        checkConfigValue("commands.nightbroadcast.reloaded", List.of(" <#745c97>NightBroadcast <#c0c0c0>› <#fcfcfc>Плагин <#ace1af>успешно</#ace1af> перезагружен"));

        save();
    }
}
