package com.tylerflar.utils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.json.JSONObject;

public class UpdateChecker {

    private final JavaPlugin plugin;
    private final String owner = "TylerFlar";
    private final String repo = "minecord-link";

    public UpdateChecker(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void checkForUpdates() {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            try {
                String currentVersion = plugin.getDescription().getVersion();
                String latestVersion = getLatestVersion();

                if (latestVersion != null && !currentVersion.equals(latestVersion)) {
                    plugin.getLogger().info("A new version of MineCord-Link is available!");
                    plugin.getLogger().info("Current version: " + currentVersion);
                    plugin.getLogger().info("Latest version: " + latestVersion);
                    plugin.getLogger().info("Downloading and installing the update...");

                    if (downloadAndInstallUpdate(latestVersion)) {
                        plugin.getLogger().info("Update downloaded successfully. It will be applied on the next server restart.");
                    } else {
                        plugin.getLogger().warning("Failed to download and install the update.");
                    }
                } else {
                    plugin.getLogger().info("MineCord-Link is up to date!");
                }
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to check for updates: " + e.getMessage());
            }
        });
    }

    private String getLatestVersion() throws Exception {
        URI uri = new URI("https", "api.github.com", "/repos/" + owner + "/" + repo + "/releases/latest", null);
        URL url = uri.toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        if (conn.getResponseCode() == 200) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            JSONObject jsonResponse = new JSONObject(response.toString());
            return jsonResponse.getString("tag_name").replace("v", "");
        }

        return null;
    }

    private boolean downloadAndInstallUpdate(String version) {
        try {
            URI uri = new URI("https", "github.com", "/" + owner + "/" + repo + "/releases/download/v" + version + "/minecord-link-" + version + ".jar", null);
            URL url = uri.toURL();
            Path pluginsFolder = plugin.getDataFolder().getParentFile().toPath();
            Path newJarPath = pluginsFolder.resolve("MineCord-Link-" + version + ".jar");

            // Find the current JAR file
            Path currentJarPath = Files.list(pluginsFolder)
                    .filter(path -> path.getFileName().toString().toLowerCase().startsWith("minecord-link"))
                    .filter(path -> path.getFileName().toString().toLowerCase().endsWith(".jar"))
                    .findFirst()
                    .orElseThrow(() -> new IOException("Current MineCord-Link JAR not found"));

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            if (conn.getResponseCode() == 200) {
                // Download the new version
                Files.copy(conn.getInputStream(), newJarPath, StandardCopyOption.REPLACE_EXISTING);

                // Delete the old JAR file
                Files.deleteIfExists(currentJarPath);

                plugin.getLogger().info("Update downloaded successfully. The old version has been deleted.");
                plugin.getLogger().info("The new version will be applied on the next server restart.");
                return true;
            } else {
                plugin.getLogger().warning("Failed to download update. Server responded with code: " + conn.getResponseCode());
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to download update: " + e.getMessage());
            e.printStackTrace(); // Log the full stack trace for debugging
        }
        return false;
    }
}