package net.solostudio.vaultcher.enums.keys;

import net.solostudio.vaultcher.Vaultcher;
import net.solostudio.vaultcher.processor.MessageProcessor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public enum ConfigKeys {
    DATABASE("database.type"),
    LANGUAGE("language"),
    USES_MUST_BE_BIGGER_THAN_ONE("uses-must-be-bigger-than-one"),
    ALIASES("aliases"),

    BACK_TO_NAVIGATION_SLOT("back-to-navigation-item.slot"),

    USER_ACCESSIBLE_MENU_TITLE("user-accessible-menu.title"),
    USER_ACCESSIBLE_MENU_TICK("user-accessible-menu.update-tick"),
    USER_ACCESSIBLE_MENU_SIZE("user-accessible-menu.size"),
    USER_ACCESSIBLE_BACK_SLOT("user-accessible-menu.back-item.slot"),
    USER_ACCESSIBLE_FORWARD_SLOT("user-accessible-menu.forward-item.slot"),
    USER_ACCESSIBLE_FILLER_GLASS("user-accessible-menu.filler-glass"),

    FULL_OVERVIEW_MENU_TITLE("full-overview-menu.title"),
    FULL_OVERVIEW_MENU_TICK("full-overview-menu.update-tick"),
    FULL_OVERVIEW_MENU_SIZE("full-overview-menu.size"),
    FULL_OVERVIEW_BACK_SLOT("full-overview-menu.back-item.slot"),
    FULL_OVERVIEW_FILLER_GLASS("full-overview-menu.filler-glass"),
    FULL_OVERVIEW_FORWARD_SLOT("full-overview-menu.forward-item.slot"),

    PLACEHOLDER_YES("placeholders.yes-holder"),
    PLACEHOLDER_NO("placeholders.no-holder"),
    PLACEHOLDER_NO_CODE("placeholders.no-code-holder"),

    REFERRAL_LENGTH("referral.length"),
    REFERRAL_COMMAND_CREATOR("referral.command-creator"),
    REFERRAL_COMMAND_ACTIVATOR("referral.command-activator"),

    NAVIGATION_MENU_TITLE("navigation-menu.title"),
    NAVIGATION_USER_ACCESSIBLE_MENU_SLOT("navigation-menu.user-accessible-menu-item.slot"),
    NAVIGATION_FULL_OVERVIEW_MENU_SLOT("navigation-menu.full-overview-menu-item.slot"),
    NAVIGATION_MENU_SIZE("navigation-menu.size"),
    NAVIGATION_MENU_FILLER_GLASS("navigation-menu.filler-glass"),

    REDEEM_PARTICLE("redeem.particle.name"),
    REDEEM_IS_PARTICLE_ENABLED("redeem.particle.enabled"),
    REDEEM_PLAY_SOUND_AT_LOCATION("redeem.play-sound-at-location"),
    REDEEM_IS_SOUND_ENABLED("redeem.sound.enabled"),
    REDEEM_SOUND("redeem.sound.name");

    private final String path;

    ConfigKeys(@NotNull final String path) {
        this.path = path;
    }

    public String getString() {
        return MessageProcessor.process(Vaultcher.getInstance().getConfiguration().getString(path));
    }

    public boolean getBoolean() {
        return Vaultcher.getInstance().getConfiguration().getBoolean(path);
    }

    public int getInt() {
        return Vaultcher.getInstance().getConfiguration().getInt(path);
    }

    public List<String> getList() {
        return Vaultcher.getInstance().getConfiguration().getList(path);
    }
}
