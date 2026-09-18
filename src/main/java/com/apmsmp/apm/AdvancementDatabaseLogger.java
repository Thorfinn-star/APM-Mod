package com.apmsmp.apm;

import com.google.gson.*;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;

public final class AdvancementDatabaseLogger {
    private static final Logger LOGGER = LoggerFactory.getLogger("APM-AdvancementDB");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final DateTimeFormatter MC_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss Z");
    private static ScheduledExecutorService executor;
    private static Config config;
    private static Path gameDir;

    private AdvancementDatabaseLogger() {}

    public static void start() {
        gameDir = FabricLoader.getInstance().getGameDir();
        try {
            config = loadConfig();
        } catch (Exception e) {
            LOGGER.error("Impossible de charger la configuration APM Advancement DB", e);
            return;
        }
        if (!config.enabled) {
            LOGGER.warn("APM Advancement DB est desactive. Configure config/apm-advancement-db.json puis mets enabled=true.");
            return;
        }
        executor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "apm-advancement-db");
            t.setDaemon(true);
            return t;
        });
        executor.scheduleWithFixedDelay(AdvancementDatabaseLogger::syncSafe, 2, Math.max(5, config.scanIntervalSeconds), TimeUnit.SECONDS);
        LOGGER.info("APM Advancement DB active (scan toutes les {} secondes).", Math.max(5, config.scanIntervalSeconds));
    }

    public static void stop() {
        if (executor != null) executor.shutdownNow();
    }

    public static void recordLive(String uuid, String playerName, String advancement) {
        if (config == null || !config.enabled || executor == null || executor.isShutdown()) return;
        if (advancement.contains(":recipes/")) return;
        executor.execute(() -> {
            try {
                Class.forName("org.mariadb.jdbc.Driver");
                String url = "jdbc:mariadb://" + config.host + ":" + config.port + "/" + config.database
                        + "?connectTimeout=5000&socketTimeout=10000&useUnicode=true&characterEncoding=utf8";
                try (Connection cn = DriverManager.getConnection(url, config.username, config.password)) {
                    ensureSchema(cn);
                    insert(cn, uuid, playerName, advancement, advancement, Timestamp.from(Instant.now()));
                }
                LOGGER.info("Succes enregistre en direct: {} -> {}", playerName, advancement);
            } catch (Throwable t) {
                LOGGER.error("Impossible d'enregistrer le succes en direct {} pour {}", advancement, playerName, t);
            }
        });
    }

    private static void syncSafe() {
        try { sync(); }
        catch (Throwable t) { LOGGER.error("Erreur pendant la synchronisation des succes", t); }
    }

    private static void sync() throws Exception {
        Map<String, String> names = loadUserCache();
        Path advancements = resolveWorldDir().resolve("players").resolve("advancements");
        if (!Files.isDirectory(advancements)) {
            LOGGER.debug("Dossier advancements introuvable: {}", advancements);
            return;
        }

        Class.forName("org.mariadb.jdbc.Driver");
        String url = "jdbc:mariadb://" + config.host + ":" + config.port + "/" + config.database
                + "?connectTimeout=5000&socketTimeout=10000&useUnicode=true&characterEncoding=utf8";
        try (Connection cn = DriverManager.getConnection(url, config.username, config.password)) {
            ensureSchema(cn);
            try (DirectoryStream<Path> files = Files.newDirectoryStream(advancements, "*.json")) {
                for (Path file : files) importPlayer(cn, file, names);
            }
        }
    }

    private static void importPlayer(Connection cn, Path file, Map<String, String> names) {
        String uuid = file.getFileName().toString().replaceFirst("\\.json$", "");
        String playerName = names.getOrDefault(uuid.toLowerCase(Locale.ROOT), uuid.substring(0, Math.min(16, uuid.length())));
        try {
            JsonObject root = JsonParser.parseString(Files.readString(file, StandardCharsets.UTF_8)).getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : root.entrySet()) {
                if (!entry.getValue().isJsonObject()) continue;
                JsonObject progress = entry.getValue().getAsJsonObject();
                if (!progress.has("done") || !progress.get("done").getAsBoolean()) continue;
                if (entry.getKey().startsWith("minecraft:recipes/")) continue;
                Timestamp earned = completionTime(progress);
                insert(cn, uuid, playerName, entry.getKey(), entry.getKey(), earned);
            }
        } catch (Exception e) {
            LOGGER.warn("Impossible de lire {}", file.getFileName(), e);
        }
    }

    private static Timestamp completionTime(JsonObject progress) {
        Instant latest = null;
        JsonObject criteria = progress.has("criteria") && progress.get("criteria").isJsonObject()
                ? progress.getAsJsonObject("criteria") : null;
        if (criteria != null) {
            for (JsonElement value : criteria.asMap().values()) {
                if (!value.isJsonPrimitive()) continue;
                try {
                    Instant instant = OffsetDateTime.parse(value.getAsString(), MC_TIME).toInstant();
                    if (latest == null || instant.isAfter(latest)) latest = instant;
                } catch (Exception ignored) {}
            }
        }
        return Timestamp.from(latest != null ? latest : Instant.now());
    }

    private static void insert(Connection cn, String uuid, String playerName, String advancement, String name, Timestamp earned) throws SQLException {
        String sql = "INSERT IGNORE INTO apm_advancements (uuid, player_name, advancement, advancement_name, earned_at) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, uuid);
            ps.setString(2, playerName);
            ps.setString(3, advancement);
            ps.setString(4, name);
            ps.setTimestamp(5, earned);
            ps.executeUpdate();
        }
    }

    private static void ensureSchema(Connection cn) throws SQLException {
        try (Statement st = cn.createStatement()) {
            st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS apm_advancements (
                  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
                  uuid VARCHAR(36) NOT NULL,
                  player_name VARCHAR(32) NOT NULL,
                  advancement VARCHAR(255) NOT NULL,
                  advancement_name VARCHAR(255) NOT NULL,
                  earned_at DATETIME NOT NULL,
                  PRIMARY KEY (id),
                  UNIQUE KEY unique_player_advancement (uuid, advancement)
                )
                """);
            try { st.executeUpdate("ALTER TABLE apm_advancements MODIFY player_name VARCHAR(32) NOT NULL, MODIFY advancement VARCHAR(255) NOT NULL, MODIFY advancement_name VARCHAR(255) NOT NULL"); }
            catch (SQLException e) { LOGGER.warn("Schema existant conserve (ALTER refuse): {}", e.getMessage()); }
        }
    }

    private static Path resolveWorldDir() {
        Properties p = new Properties();
        try (var in = Files.newInputStream(gameDir.resolve("server.properties"))) { p.load(in); }
        catch (IOException ignored) {}
        return gameDir.resolve(p.getProperty("level-name", "world"));
    }

    private static Map<String, String> loadUserCache() {
        Map<String, String> result = new HashMap<>();
        Path path = gameDir.resolve("usercache.json");
        if (!Files.exists(path)) return result;
        try {
            JsonArray arr = JsonParser.parseString(Files.readString(path, StandardCharsets.UTF_8)).getAsJsonArray();
            for (JsonElement e : arr) {
                JsonObject o = e.getAsJsonObject();
                if (o.has("uuid") && o.has("name")) result.put(o.get("uuid").getAsString().toLowerCase(Locale.ROOT), o.get("name").getAsString());
            }
        } catch (Exception e) { LOGGER.warn("Impossible de lire usercache.json", e); }
        return result;
    }

    private static Config loadConfig() throws IOException {
        Path path = FabricLoader.getInstance().getConfigDir().resolve("apm-advancement-db.json");
        if (!Files.exists(path)) {
            Config sample = new Config();
            Files.createDirectories(path.getParent());
            Files.writeString(path, GSON.toJson(sample), StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW);
            return sample;
        }
        Config loaded = GSON.fromJson(Files.readString(path, StandardCharsets.UTF_8), Config.class);
        return loaded == null ? new Config() : loaded;
    }

    private static final class Config {
        boolean enabled = false;
        String host = "mysql2.ouiheberg.com";
        int port = 3306;
        String database = "s36160_apm_stats";
        String username = "CHANGE_ME";
        String password = "CHANGE_ME";
        int scanIntervalSeconds = 15;
    }
}
