package net.solostudio.vaultcher.language;

import net.solostudio.vaultcher.Vaultcher;
import net.solostudio.vaultcher.utils.ConfigUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class Language extends ConfigUtils {
    public Language(@NotNull String name) {
        super(Vaultcher.getInstance().getDataFolder().getPath() + File.separator + "locales", name);
        save();
    }
}
