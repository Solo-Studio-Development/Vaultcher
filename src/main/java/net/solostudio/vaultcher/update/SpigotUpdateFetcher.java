package net.solostudio.vaultcher.update;

import lombok.Getter;
import net.solostudio.vaultcher.Vaultcher;
import net.solostudio.vaultcher.utils.LoggerUtils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@SuppressWarnings("deprecation")
public class SpigotUpdateFetcher {
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private final int resourceId;
    @Getter public static String latestVersion;

    public SpigotUpdateFetcher(int resourceId) {
        this.resourceId = resourceId;
    }

    private void getVersion(Consumer<String> consumer) {
        CompletableFuture.runAsync(() -> {
            try {
                URI uri = new URI("https://api.spigotmc.org/legacy/update.php?resource=" + this.resourceId + "/~");
                HttpRequest request = HttpRequest.newBuilder(uri).GET().build();

                httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                        .thenAccept(response -> {
                            if (response.statusCode() == 200 && response.body() != null) {
                                latestVersion = response.body().trim();
                                consumer.accept(latestVersion);
                            } else {
                                LoggerUtils.warn("Failed to get response from Spigot API: " + response.statusCode());
                            }
                        })
                        .exceptionally(exception -> {
                            LoggerUtils.warn("Error while fetching version: " + exception.getMessage());
                            return null;
                        });
            } catch (Exception exception) {
                LoggerUtils.warn("Exception in getVersion: " + exception.getMessage());
            }
        }, Vaultcher.getInstance().getScheduler()::runTaskAsynchronously);
    }

    public static void checkUpdates() {
        new SpigotUpdateFetcher(121258).getVersion(version -> {
            LoggerUtils.info(Vaultcher.getInstance().getDescription().getVersion().equals(version)
                    ? "Everything is up to date!"
                    : "You are using an outdated version! Please update to the newest version: " + version);
        });
    }
}