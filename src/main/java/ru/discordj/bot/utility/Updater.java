package ru.discordj.bot.utility;

import java.io.*;
import java.net.*;
import java.util.concurrent.TimeUnit;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import org.json.*;

public class Updater {
    private static final long CHECK_INTERVAL_MS = TimeUnit.DAYS.toMillis(1); // 1 сутки

    public static void startAutoUpdate() {
        String currentVersion = getCurrentVersionFromManifest();
        if (currentVersion == null) {
            System.err.println("[Updater] Не удалось определить версию из MANIFEST.MF, автоапдейт отключён.");
            return;
        }
        Thread updaterThread = new Thread(() -> {
            while (true) {
                try {
                    checkForUpdate(currentVersion);
                    Thread.sleep(CHECK_INTERVAL_MS);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }, "AutoUpdater");
        updaterThread.setDaemon(true);
        updaterThread.start();
    }

    public static void checkForUpdate(String currentVersion) {
        try {
            URI uri = URI.create("https://api.github.com/repos/AABakotin/DiscordjBot/releases/latest");
            URL url = uri.toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Accept", "application/vnd.github.v3+json");
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) response.append(inputLine);
            in.close();

            JSONObject release = new JSONObject(response.toString());
            String latestVersion = release.getString("tag_name");
            if (latestVersion.startsWith("v")) {
                latestVersion = latestVersion.substring(1);
            }
            if (latestVersion.equals(currentVersion)) {
                System.out.println("[Updater] Версия актуальна: " + currentVersion + ". Обновление не требуется.");
                return;
            }

            // Новая версия найдена — скачиваем
            JSONArray assets = release.getJSONArray("assets");
            for (int i = 0; i < assets.length(); i++) {
                JSONObject asset = assets.getJSONObject(i);
                String name = asset.getString("name");
                if (name.endsWith(".jar")) {
                    String downloadUrl = asset.getString("browser_download_url");
                    try (InputStream in2 = URI.create(downloadUrl).toURL().openStream();
                         FileOutputStream fos = new FileOutputStream("update.jar")) {
                        byte[] buffer = new byte[4096];
                        int n;
                        while ((n = in2.read(buffer)) != -1) fos.write(buffer, 0, n);
                    }
                    System.out.println("[Updater] Найдена новая версия: " + latestVersion + ". Скачан update.jar. Подготовка к обновлению...");

                    // Сохраняем токен (если доступен)
                    String token = getToken();
                    if (token != null) {
                        try {
                            java.nio.file.Files.writeString(java.nio.file.Paths.get("token.txt"), token);
                        } catch (Exception e) {
                            System.err.println("[Updater] Не удалось сохранить токен в token.txt: " + e.getMessage());
                        }
                    }

                    // Создаём флаг для скрипта обновления
                    try {
                        java.nio.file.Files.createFile(java.nio.file.Paths.get("update.flag"));
                        System.out.println("[Updater] Флаг обновления создан. Бот будет перезапущен.");
                    } catch (Exception e) {
                        System.err.println("[Updater] Не удалось создать update.flag: " + e.getMessage());
                    }

                    System.exit(0); // Выход, чтобы скрипт сделал замену
                }
            }
        } catch (Exception e) {
            System.err.println("[Updater] Ошибка при проверке обновлений: " + e.getMessage());
        }
    }

    private static String getToken() {
        // Приоритет: переменная окружения -> system property -> token.txt -> аргументы процесса
        if (System.getenv().containsKey("DISCORD_TOKEN")) {
            return System.getenv("DISCORD_TOKEN");
        }
        if (System.getProperty("DISCORD_TOKEN") != null) {
            return System.getProperty("DISCORD_TOKEN");
        }
        try {
            java.nio.file.Path path = java.nio.file.Paths.get("token.txt");
            if (java.nio.file.Files.exists(path)) {
                return java.nio.file.Files.readString(path).trim();
            }
        } catch (Exception e) {
            // ignore
        }
        // Последняя попытка: поиск токена в аргументах JVM
        String[] processArgs = java.lang.management.ManagementFactory.getRuntimeMXBean().getInputArguments().toArray(new String[0]);
        for (String arg : processArgs) {
            if (arg != null && arg.matches("[A-Za-z0-9._-]{20,}")) {
                return arg;
            }
        }
        return null;
    }

    public static String getCurrentVersionFromManifest() {
        try {
            InputStream manifestStream = Updater.class.getClassLoader().getResourceAsStream("META-INF/MANIFEST.MF");
            if (manifestStream == null) return null;
            Manifest manifest = new Manifest(manifestStream);
            Attributes attr = manifest.getMainAttributes();
            return attr.getValue("Implementation-Version");
        } catch (Exception e) {
            return null;
        }
    }
}