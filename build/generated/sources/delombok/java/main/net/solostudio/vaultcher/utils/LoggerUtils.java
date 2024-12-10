package net.solostudio.vaultcher.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public final class LoggerUtils {
    private static final Logger logger = LogManager.getLogger("Vaultcher");

    public static void info(@NotNull String msg, @NotNull Object... objs) {
        logger.info(msg, objs);
    }

    public static void warn(@NotNull String msg, @NotNull Object... objs) {
        logger.warn(msg, objs);
    }

    public static void error(@NotNull String msg, @NotNull Object... objs) {
        logger.error(msg, objs);
    }

    private LoggerUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
