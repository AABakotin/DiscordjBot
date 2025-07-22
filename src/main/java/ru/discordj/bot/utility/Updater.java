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
            if (!latestVersion.equals(currentVersion)) {
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
                        System.out.println("[Updater] Найдена новая версия: " + latestVersion + ". Скачан update.jar. Перезапуск...");
                        String token = null;
                        // Пытаемся получить токен из переменной окружения или системных свойств
                        if (System.getenv().containsKey("DISCORD_TOKEN")) {
                            token = System.getenv("DISCORD_TOKEN");
                        } else if (System.getProperty("DISCORD_TOKEN") != null) {
                            token = System.getProperty("DISCORD_TOKEN");
                        }
                        // Если токен не найден, пробуем получить из аргументов процесса
                        String[] processArgs = java.lang.management.ManagementFactory.getRuntimeMXBean().getInputArguments().toArray(new String[0]);
                        for (String arg : processArgs) {
                            if (arg != null && arg.matches("[A-Za-z0-9._-]{20,}")) {
                                token = arg;
                                break;
                            }
                        }
                        ProcessBuilder pb;
                        if (token != null) {
                            // Сохраняем токен в файл для новой версии
                            try {
                                java.nio.file.Files.write(java.nio.file.Paths.get("token.txt"), token.getBytes(java.nio.charset.StandardCharsets.UTF_8));
                            } catch (Exception e) {
                                System.err.println("[Updater] Не удалось сохранить токен в token.txt: " + e.getMessage());
                            }
                            // Перезаписываем основной jar-файл новой версией
                            java.nio.file.Path mainJar = java.nio.file.Paths.get("DiscordjBot-1.0-jar-with-dependencies.jar");
                            java.nio.file.Path backupJar = java.nio.file.Paths.get("DiscordjBot-1.0-jar-with-dependencies.jar.bak");
                            java.nio.file.Path updateJar = java.nio.file.Paths.get("update.jar");
                            try {
                                // Удаляем старый .bak, если есть
                                if (java.nio.file.Files.exists(backupJar)) {
                                    java.nio.file.Files.delete(backupJar);
                                }
                                // Переименовываем текущий jar в .bak
                                if (java.nio.file.Files.exists(mainJar)) {
                                    java.nio.file.Files.move(mainJar, backupJar);
                                }
                                // Переименовываем update.jar в основной jar
                                java.nio.file.Files.move(updateJar, mainJar);
                            } catch (Exception e) {
                                System.err.println("[Updater] Не удалось заменить основной jar-файл: " + e.getMessage());
                            }
                        }
                        // Запускаем обновлённый основной jar-файл
                        if (token != null) {
                            pb = new ProcessBuilder("java", "-jar", "DiscordjBot-1.0-jar-with-dependencies.jar", token);
                            pb.environment().put("DISCORD_TOKEN", token);
                        } else {
                            pb = new ProcessBuilder("java", "-jar", "DiscordjBot-1.0-jar-with-dependencies.jar");
                        }
                        pb.start();
                        System.exit(0);
                    }
                }
            } else {
                System.out.println("[Updater] Версия актуальна: " + currentVersion);
            }
        } catch (Exception e) {
            System.err.println("[Updater] Ошибка при проверке обновлений: " + e.getMessage());
        }
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