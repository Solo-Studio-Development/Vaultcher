package net.solostudio.vaultcher.managers;

import org.jetbrains.annotations.NotNull;

public record VaultcherData(@NotNull String vaultcherName, @NotNull String command, int uses) {}
