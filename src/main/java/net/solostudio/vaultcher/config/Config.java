package net.solostudio.vaultcher.config;

import net.solostudio.vaultcher.Vaultcher;
import net.solostudio.vaultcher.managers.ConfigurationManager;

public class Config extends ConfigurationManager {
    public Config() {
        super(Vaultcher.getInstance().getDataFolder().getPath(), "config");
        save();
    }
}
