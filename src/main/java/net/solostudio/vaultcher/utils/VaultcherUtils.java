package net.solostudio.vaultcher.utils;

import lombok.experimental.UtilityClass;
import net.solostudio.vaultcher.Vaultcher;
import net.solostudio.vaultcher.enums.keys.ConfigKeys;
import net.solostudio.vaultcher.enums.keys.MessageKeys;
import net.solostudio.vaultcher.managers.VaultcherData;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.stream.IntStream;

@SuppressWarnings("deprecation")
@UtilityClass
public class VaultcherUtils {
    private void playParticle(@NotNull Player player) {
        if (!ConfigKeys.REDEEM_IS_PARTICLE_ENABLED.getBoolean()) return;

        Vaultcher.getInstance().getScheduler().runTaskAsynchronously(() -> {
            IntStream.range(0, 10).forEach(i -> {
                double xOffset = Math.random() - 0.5;
                double yOffset = Math.random();
                double zOffset = Math.random() - 0.5;

                player.getWorld().spawnParticle(Particle.valueOf(ConfigKeys.REDEEM_PARTICLE.getString()),
                        player.getLocation().add(0, 1, 0),
                        10,
                        xOffset, yOffset, zOffset,
                        0.1);
            });
        });
    }

    private void playSound(@NotNull Player player) {
        if (!ConfigKeys.REDEEM_IS_SOUND_ENABLED.getBoolean()) return;

        Optional<String> optionalSoundName = Optional.of(ConfigKeys.REDEEM_SOUND.getString());

        optionalSoundName.ifPresent(soundName -> {
            Sound sound = Sound.valueOf(soundName.toUpperCase());

            if (ConfigKeys.REDEEM_PLAY_SOUND_AT_LOCATION.getBoolean()) player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
            else player.playSound(player, sound, 1.0f, 1.0f);
        });
    }

    public void redeemVaultcher(@NotNull Player player, @NotNull String name) {
        Vaultcher.getDatabase().takeVaultcher(name, player.getName());
        Vaultcher.getDatabase().decrementUses(name);

        playParticle(player);
        playSound(player);

        player.sendMessage(MessageKeys.REDEEMED.getMessage());
    }
}
