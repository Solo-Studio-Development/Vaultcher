package net.solostudio.vaultcher.enums.keys;

import lombok.Getter;
import net.solostudio.vaultcher.Vaultcher;
import net.solostudio.vaultcher.processor.MessageProcessor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Getter
public enum MessageKeys {
    RELOAD("messages.reload"),
    ALREADY_EXISTS("messages.already-created"),
    CANT_BE_NEGATIVE("messages.cant-be-negative"),
    CREATED("messages.created"),
    REDEEMED("messages.redeemed"),
    USES_ZERO("messages.uses-zero"),
    NEED_NUMBER("messages.need-number"),
    NOT_EXISTS("messages.not-exists"),
    DELETED("messages.deleted"),
    EDIT_CMD("messages.edit-cmd"),
    EDIT_USES("messages.edit-uses"),
    EDIT_NAME("messages.edit-name"),
    OFFLINE_PLAYER("messages.offline-player"),
    NOT_AN_OWNER("messages.not-an-owner"),
    ALREADY_AN_OWNER("messages.already-owner"),
    SUCCESSFUL_ADD("messages.successful-add"),
    FIRST_PAGE("messages.first-page"),
    TARGET_GIVE("messages.successful-give-target"),
    PLAYER_GIVE("messages.successful-give-player"),
    PLAYER_REQUIRED("message.player-required"),
    INVALID_NUMBER("message.invalid-number"),
    NO_PERMISSION("message.no-permission"),
    INVALID_PLAYER("message.invalid-player"),
    MISSING_ARGUMENT("message.missing-argument"),
    TARGET_DOESNT_EXISTS("messages.target-doesnt-exists"),
    HELP("messages.help"),
    LAST_PAGE("messages.last-page");

    private final String path;

    MessageKeys(@NotNull final String path) {
        this.path = path;
    }

    public String getMessage() {
        return MessageProcessor.process(Vaultcher.getInstance().getLanguage().getString(path));
    }

    public List<String> getMessages() {
        return Vaultcher.getInstance().getLanguage().getList(path)
                .stream()
                .map(MessageProcessor::process)
                .toList();
    }
}
