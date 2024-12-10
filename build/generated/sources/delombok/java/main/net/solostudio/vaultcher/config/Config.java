package net.solostudio.vaultcher.config;

import net.solostudio.vaultcher.Vaultcher;
import net.solostudio.vaultcher.utils.ConfigUtils;

public class Config extends ConfigUtils {
    public Config() {
        super(Vaultcher.getInstance().getDataFolder().getPath(), "config");
        save();
    }
}
