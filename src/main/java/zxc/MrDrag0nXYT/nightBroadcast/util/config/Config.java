package zxc.MrDrag0nXYT.nightBroadcast.util.config;

import org.bukkit.configuration.file.YamlConfiguration;
import zxc.MrDrag0nXYT.nightBroadcast.NightBroadcast;

import java.io.File;
import java.util.List;

public class Config {

    private final NightBroadcast plugin;
    private File file;
    private YamlConfiguration config;

    public Config(NightBroadcast plugin) {
        this.plugin = plugin;

        init();
        updateConfig();
    }

    private void init() {
        file = new File(plugin.getDataFolder(), "config.yml");
        if (!file.exists()) {
            plugin.saveResource("config.yml", false);
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
        checkConfigValue("enable-metrics", true);
        checkConfigValue("send-broadcasts-to-console", false);
        checkConfigValue("min-played-time", 3600);

        checkConfigValue("categories.bc.cooldown", 5);
        checkConfigValue("categories.bc.permission", "nbc.categories.bc");
        checkConfigValue("categories.bc.only-for-players", false);
        checkConfigValue("categories.bc.title.enabled", true);
        checkConfigValue("categories.bc.title.title", "<#745c97>Объявление");
        checkConfigValue("categories.bc.title.subtitle", " ");
        checkConfigValue("categories.bc.title.actionbar", "<#fcfcfc>%player_name% <#c0c0c0>›</#c0c0c0> %broadcasttext%");
        checkConfigValue("categories.bc.title.time.fade-in", 10);
        checkConfigValue("categories.bc.title.time.stay", 70);
        checkConfigValue("categories.bc.title.time.fade-out", 20);
        checkConfigValue("categories.bc.sound.enabled", true);
        checkConfigValue("categories.bc.sound.name", "BLOCK_NOTE_BLOCK_PLING");
        checkConfigValue("categories.bc.sound.volume", 1.0);
        checkConfigValue("categories.bc.sound.pitch", 1.0);
        checkConfigValue("categories.bc.format", List.of(" <#745c97>Объявление</#745c97> <#c0c0c0>•</#c0c0c0> <#fcfcfc>%player_name% <#c0c0c0>›</#c0c0c0> %broadcasttext%"));

        checkConfigValue("categories.ad.only-for-players", true);
        checkConfigValue("categories.ad.format", List.of(" <#ace1af>Реклама</#ace1af> <#c0c0c0>•</#c0c0c0> <#fcfcfc>%player_name% <#c0c0c0>›</#c0c0c0> %broadcasttext%"));

        checkConfigValue("categories.pvp.format", List.of(" <#d45079>PvP</#d45079> <#c0c0c0>•</#c0c0c0> <#fcfcfc>%player_name% <#c0c0c0>›</#c0c0c0> %broadcasttext%"));
        save();
    }
}
