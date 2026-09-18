package com.apmsmp.apm;

import com.google.gson.*;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;

import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public final class ApmZones {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("apm-zones.json");
    private static final Map<UUID, List<Point>> drafts = new HashMap<>();
    private static final List<Zone> zones = new ArrayList<>();

    private ApmZones() {}

    public static void initialize() {
        load();

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
            dispatcher.register(Commands.literal("apmzone")
                .requires(Commands.hasPermission(Commands.LEVEL_ADMINS))
                .then(Commands.literal("addpoint").executes(ctx -> addPoint(ctx.getSource().getPlayerOrException())))
                .then(Commands.literal("clearpoints").executes(ctx -> clearPoints(ctx.getSource().getPlayerOrException())))
                .then(Commands.literal("create")
                    .then(Commands.argument("name", StringArgumentType.word())
                        .executes(ctx -> create(ctx.getSource().getPlayerOrException(), StringArgumentType.getString(ctx, "name")))))
                .then(Commands.literal("delete")
                    .then(Commands.argument("name", StringArgumentType.word())
                        .executes(ctx -> delete(ctx.getSource().getPlayerOrException(), StringArgumentType.getString(ctx, "name")))))
                .then(Commands.literal("list").executes(ctx -> list(ctx.getSource().getPlayerOrException())))
            )
        );

        PlayerBlockBreakEvents.BEFORE.register((level, player, pos, state, blockEntity) ->
            (player instanceof ServerPlayer serverPlayer && bypass(serverPlayer)) || !isProtected((ServerLevel) level, pos)
        );

        UseBlockCallback.EVENT.register((player, level, hand, hit) -> {
            if (!(level instanceof ServerLevel serverLevel) || !(player instanceof ServerPlayer serverPlayer)) return InteractionResult.PASS;
            if (bypass(serverPlayer)) return InteractionResult.PASS;
            BlockPos clicked = hit.getBlockPos();
            BlockPos adjacent = clicked.relative(hit.getDirection());
            if (isProtected(serverLevel, clicked) || isProtected(serverLevel, adjacent)) {
                serverPlayer.sendSystemMessage(Component.literal("§cZone protégée : modification/interactions interdites."));
                return InteractionResult.FAIL;
            }
            return InteractionResult.PASS;
        });
    }

    public static boolean isProtected(ServerLevel level, BlockPos pos) {
        String dimension = level.dimension().identifier().toString();
        double x = pos.getX() + 0.5;
        double z = pos.getZ() + 0.5;
        synchronized (zones) {
            for (Zone zone : zones) {
                if (zone.dimension.equals(dimension) && inside(zone.points, x, z)) return true;
            }
        }
        return false;
    }

    private static boolean bypass(ServerPlayer player) {
        return player.level().getServer().getPlayerList().isOp(player.getGameProfile());
    }

    private static int addPoint(ServerPlayer player) {
        List<Point> points = drafts.computeIfAbsent(player.getUUID(), k -> new ArrayList<>());
        BlockPos p = player.blockPosition();
        points.add(new Point(p.getX(), p.getZ()));
        player.sendSystemMessage(Component.literal("§aPoint " + points.size() + " ajouté : X " + p.getX() + " Z " + p.getZ()));
        return points.size();
    }

    private static int clearPoints(ServerPlayer player) {
        drafts.remove(player.getUUID());
        player.sendSystemMessage(Component.literal("§ePoints temporaires effacés."));
        return 1;
    }

    private static int create(ServerPlayer player, String name) {
        List<Point> points = drafts.getOrDefault(player.getUUID(), List.of());
        if (points.size() < 3) {
            player.sendSystemMessage(Component.literal("§cIl faut au moins 3 points. Fais /apmzone addpoint en faisant le tour de la zone."));
            return 0;
        }
        synchronized (zones) {
            if (zones.stream().anyMatch(z -> z.name.equalsIgnoreCase(name))) {
                player.sendSystemMessage(Component.literal("§cUne zone appelée " + name + " existe déjà."));
                return 0;
            }
            zones.add(new Zone(name, player.level().dimension().identifier().toString(), new ArrayList<>(points)));
            save();
        }
        drafts.remove(player.getUUID());
        player.sendSystemMessage(Component.literal("§aZone polygonale '" + name + "' créée avec " + points.size() + " points. PvP autorisé, blocs protégés."));
        return 1;
    }

    private static int delete(ServerPlayer player, String name) {
        boolean removed;
        synchronized (zones) {
            removed = zones.removeIf(z -> z.name.equalsIgnoreCase(name));
            if (removed) save();
        }
        player.sendSystemMessage(Component.literal(removed ? "§aZone '" + name + "' supprimée." : "§cZone introuvable : " + name));
        return removed ? 1 : 0;
    }

    private static int list(ServerPlayer player) {
        synchronized (zones) {
            if (zones.isEmpty()) {
                player.sendSystemMessage(Component.literal("§eAucune zone APM."));
            } else {
                player.sendSystemMessage(Component.literal("§6Zones APM : " + String.join(", ", zones.stream().map(z -> z.name).toList())));
            }
            return zones.size();
        }
    }

    private static boolean inside(List<Point> polygon, double x, double z) {
        boolean inside = false;
        for (int i = 0, j = polygon.size() - 1; i < polygon.size(); j = i++) {
            Point a = polygon.get(i), b = polygon.get(j);
            if (((a.z > z) != (b.z > z)) && (x < (double)(b.x - a.x) * (z - a.z) / (double)(b.z - a.z) + a.x)) inside = !inside;
        }
        return inside;
    }

    private static void load() {
        zones.clear();
        if (!Files.exists(FILE)) return;
        try {
            JsonArray arr = JsonParser.parseString(Files.readString(FILE, StandardCharsets.UTF_8)).getAsJsonArray();
            for (JsonElement e : arr) zones.add(GSON.fromJson(e, Zone.class));
            ApmMod.LOGGER.info("{} zone(s) APM chargée(s)", zones.size());
        } catch (Exception e) {
            ApmMod.LOGGER.error("Impossible de charger apm-zones.json", e);
        }
    }

    private static void save() {
        try {
            Files.createDirectories(FILE.getParent());
            Files.writeString(FILE, GSON.toJson(zones), StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (Exception e) {
            ApmMod.LOGGER.error("Impossible de sauvegarder apm-zones.json", e);
        }
    }

    private static final class Point {
        int x, z;
        Point(int x, int z) { this.x = x; this.z = z; }
    }

    private static final class Zone {
        String name;
        String dimension;
        List<Point> points;
        Zone(String name, String dimension, List<Point> points) {
            this.name = name; this.dimension = dimension; this.points = points;
        }
    }
}
