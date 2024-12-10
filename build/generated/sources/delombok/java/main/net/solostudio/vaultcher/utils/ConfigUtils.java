package net.solostudio.vaultcher.utils;

import lombok.Setter;
import net.solostudio.vaultcher.processor.MessageProcessor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class ConfigUtils {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ConfigUtils.class);
    private YamlConfiguration yml;
    private String name;
    private File config;

    public ConfigUtils(@NotNull String dir, @NotNull String name) {
        File file = new File(dir);
        if (!file.exists()) {
            if (!file.mkdirs()) return;
        }
        config = new File(dir, name + ".yml");
        if (!config.exists()) {
            try {
                if (!config.createNewFile()) return;
            } catch (IOException exception) {
                LoggerUtils.error(exception.getMessage());
            }
        }
        yml = YamlConfiguration.loadConfiguration(config);
        yml.options().copyDefaults(true);
        this.name = name;
    }

    public void reload() {
        yml = YamlConfiguration.loadConfiguration(config);
        save();
    }

    public void set(@NotNull String path, Object value) {
        yml.set(path, value);
        save();
    }

    public void save() {
        try {
            yml.save(config);
        } catch (IOException exception) {
            LoggerUtils.error(exception.getMessage());
        }
    }

    public List<String> getList(@NotNull String path) {
        return yml.getStringList(path).stream().map(MessageProcessor::process).toList();
    }

    public List<String> getLoreList(@NotNull String path) {
        return getList(path).stream().map(MessageProcessor::process).toList();
    }

    public boolean getBoolean(@NotNull String path) {
        return yml.getBoolean(path);
    }

    public int getInt(@NotNull String path) {
        return yml.getInt(path);
    }

    public String getString(@NotNull String path) {
        return yml.getString(path);
    }

    @Nullable
    public ConfigurationSection getSection(@NotNull String path) {
        return yml.getConfigurationSection(path);
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    public ConfigUtils() {
    }

    public YamlConfiguration getYml() {
        return this.yml;
    }

    public String getName() {
        return this.name;
    }
}
