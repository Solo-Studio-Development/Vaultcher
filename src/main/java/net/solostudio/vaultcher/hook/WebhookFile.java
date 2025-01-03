package net.solostudio.vaultcher.hook;

import net.solostudio.vaultcher.Vaultcher;
import net.solostudio.vaultcher.managers.ConfigurationManager;

import java.io.File;

public class WebhookFile extends ConfigurationManager {
    public WebhookFile() {
        super(Vaultcher.getInstance().getDataFolder().getPath() + File.separator + "settings", "webhook");
        save();
    }
}
