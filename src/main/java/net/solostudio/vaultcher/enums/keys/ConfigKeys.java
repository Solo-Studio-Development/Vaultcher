package net.solostudio.vaultcher.enums.keys;

import net.solostudio.vaultcher.Vaultcher;
import net.solostudio.vaultcher.processor.MessageProcessor;
import org.jetbrains.annotations.NotNull;

public enum ConfigKeys {
    DATABASE("database.type"),
    LANGUAGE("language"),
    USES_MUST_BE_BIGGER_THAN_ONE("uses-must-be-bigger-than-one"),
    WEBHOOK_ENABLED("webhook.enabled"),
    WEBHOOK_URL("webhook.url"),

    WEBHOOK_VAULTCHER_CREATE_EMBED_TITLE("webhook.vaultcher-create-embed.title"),
    WEBHOOK_VAULTCHER_CREATE_EMBED_DESCRIPTION("webhook.vaultcher-create-embed.description"),
    WEBHOOK_VAULTCHER_CREATE_EMBED_COLOR("webhook.vaultcher-create-embed.color"),
    WEBHOOK_VAULTCHER_CREATE_EMBED_AUTHOR_NAME("webhook.vaultcher-create-embed.author-name"),
    WEBHOOK_VAULTCHER_CREATE_EMBED_AUTHOR_URL("webhook.vaultcher-create-embed.author-url"),
    WEBHOOK_VAULTCHER_CREATE_EMBED_AUTHOR_ICON("webhook.vaultcher-create-embed.author-icon"),
    WEBHOOK_VAULTCHER_CREATE_EMBED_FOOTER_TEXT("webhook.vaultcher-create-embed.footer-text"),
    WEBHOOK_VAULTCHER_CREATE_EMBED_FOOTER_ICON("webhook.vaultcher-create-embed.footer-icon"),
    WEBHOOK_VAULTCHER_CREATE_EMBED_THUMBNAIL("webhook.vaultcher-create-embed.thumbnail"),
    WEBHOOK_VAULTCHER_CREATE_EMBED_IMAGE("webhook.vaultcher-create-embed.image"),

    WEBHOOK_VAULTCHER_DELETE_EMBED_TITLE("webhook.vaultcher-delete-embed.title"),
    WEBHOOK_VAULTCHER_DELETE_EMBED_DESCRIPTION("webhook.vaultcher-delete-embed.description"),
    WEBHOOK_VAULTCHER_DELETE_EMBED_COLOR("webhook.vaultcher-delete-embed.color"),
    WEBHOOK_VAULTCHER_DELETE_EMBED_AUTHOR_NAME("webhook.vaultcher-delete-embed.author-name"),
    WEBHOOK_VAULTCHER_DELETE_EMBED_AUTHOR_URL("webhook.vaultcher-delete-embed.author-url"),
    WEBHOOK_VAULTCHER_DELETE_EMBED_AUTHOR_ICON("webhook.vaultcher-delete-embed.author-icon"),
    WEBHOOK_VAULTCHER_DELETE_EMBED_FOOTER_TEXT("webhook.vaultcher-delete-embed.footer-text"),
    WEBHOOK_VAULTCHER_DELETE_EMBED_FOOTER_ICON("webhook.vaultcher-delete-embed.footer-icon"),
    WEBHOOK_VAULTCHER_DELETE_EMBED_THUMBNAIL("webhook.vaultcher-delete-embed.thumbnail"),
    WEBHOOK_VAULTCHER_DELETE_EMBED_IMAGE("webhook.vaultcher-delete-embed.image"),

    WEBHOOK_VAULTCHER_EDIT_USE_EMBED_TITLE("webhook.vaultcher-edituse-embed.title"),
    WEBHOOK_VAULTCHER_EDIT_USE_EMBED_DESCRIPTION("webhook.vaultcher-edituse-embed.description"),
    WEBHOOK_VAULTCHER_EDIT_USE_EMBED_COLOR("webhook.vaultcher-edituse-embed.color"),
    WEBHOOK_VAULTCHER_EDIT_USE_EMBED_AUTHOR_NAME("webhook.vaultcher-edituse-embed.author-name"),
    WEBHOOK_VAULTCHER_EDIT_USE_EMBED_AUTHOR_URL("webhook.vaultcher-edituse-embed.author-url"),
    WEBHOOK_VAULTCHER_EDIT_USE_EMBED_AUTHOR_ICON("webhook.vaultcher-edituse-embed.author-icon"),
    WEBHOOK_VAULTCHER_EDIT_USE_EMBED_FOOTER_TEXT("webhook.vaultcher-edituse-embed.footer-text"),
    WEBHOOK_VAULTCHER_EDIT_USE_EMBED_FOOTER_ICON("webhook.vaultcher-edituse-embed.footer-icon"),
    WEBHOOK_VAULTCHER_EDIT_USE_EMBED_THUMBNAIL("webhook.vaultcher-edituse-embed.thumbnail"),
    WEBHOOK_VAULTCHER_EDIT_USE_EMBED_IMAGE("webhook.vaultcher-edituse-embed.image"),

    WEBHOOK_VAULTCHER_EDIT_COMMAND_EMBED_TITLE("webhook.vaultcher-editcommand-embed.title"),
    WEBHOOK_VAULTCHER_EDIT_COMMAND_EMBED_DESCRIPTION("webhook.vaultcher-editcommand-embed.description"),
    WEBHOOK_VAULTCHER_EDIT_COMMAND_EMBED_COLOR("webhook.vaultcher-editcommand-embed.color"),
    WEBHOOK_VAULTCHER_EDIT_COMMAND_EMBED_AUTHOR_NAME("webhook.vaultcher-editcommand-embed.author-name"),
    WEBHOOK_VAULTCHER_EDIT_COMMAND_EMBED_AUTHOR_URL("webhook.vaultcher-editcommand-embed.author-url"),
    WEBHOOK_VAULTCHER_EDIT_COMMAND_EMBED_AUTHOR_ICON("webhook.vaultcher-editcommand-embed.author-icon"),
    WEBHOOK_VAULTCHER_EDIT_COMMAND_EMBED_FOOTER_TEXT("webhook.vaultcher-editcommand-embed.footer-text"),
    WEBHOOK_VAULTCHER_EDIT_COMMAND_EMBED_FOOTER_ICON("webhook.vaultcher-editcommand-embed.footer-icon"),
    WEBHOOK_VAULTCHER_EDIT_COMMAND_EMBED_THUMBNAIL("webhook.vaultcher-editcommand-embed.thumbnail"),
    WEBHOOK_VAULTCHER_EDIT_COMMAND_EMBED_IMAGE("webhook.vaultcher-editcommand-embed.image"),

    WEBHOOK_VAULTCHER_EDIT_NAME_EMBED_TITLE("webhook.vaultcher-editname-embed.title"),
    WEBHOOK_VAULTCHER_EDIT_NAME_EMBED_DESCRIPTION("webhook.vaultcher-editname-embed.description"),
    WEBHOOK_VAULTCHER_EDIT_NAME_EMBED_COLOR("webhook.vaultcher-editname-embed.color"),
    WEBHOOK_VAULTCHER_EDIT_NAME_EMBED_AUTHOR_NAME("webhook.vaultcher-editname-embed.author-name"),
    WEBHOOK_VAULTCHER_EDIT_NAME_EMBED_AUTHOR_URL("webhook.vaultcher-editname-embed.author-url"),
    WEBHOOK_VAULTCHER_EDIT_NAME_EMBED_AUTHOR_ICON("webhook.vaultcher-editname-embed.author-icon"),
    WEBHOOK_VAULTCHER_EDIT_NAME_EMBED_FOOTER_TEXT("webhook.vaultcher-editname-embed.footer-text"),
    WEBHOOK_VAULTCHER_EDIT_NAME_EMBED_FOOTER_ICON("webhook.vaultcher-editname-embed.footer-icon"),
    WEBHOOK_VAULTCHER_EDIT_NAME_EMBED_THUMBNAIL("webhook.vaultcher-editname-embed.thumbnail"),
    WEBHOOK_VAULTCHER_EDIT_NAME_EMBED_IMAGE("webhook.vaultcher-editname-embed.image"),

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

    NAVIGATION_MENU_TITLE("navigation-menu.title"),
    NAVIGATION_USER_ACCESSIBLE_MENU_SLOT("navigation-menu.user-accessible-menu-item.slot"),
    NAVIGATION_FULL_OVERVIEW_MENU_SLOT("navigation-menu.full-overview-menu-item.slot"),
    NAVIGATION_MENU_SIZE("navigation-menu.size"),
    NAVIGATION_MENU_FILLER_GLASS("navigation-menu.filler-glass");

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
}
