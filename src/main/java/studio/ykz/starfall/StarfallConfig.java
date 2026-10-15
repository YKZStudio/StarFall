package studio.ykz.starfall;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import java.nio.file.*;
import java.io.IOException;

public final class StarfallConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public boolean terrainDamage = true;
    public float damageScale = 1;
    public float explosionPower = 2;
    public int particleBudget = 96;
    public static Path path() { return FabricLoader.getInstance().getConfigDir().resolve("starfall.json"); }
    public StarfallConfig copy() { return GSON.fromJson(GSON.toJson(this), StarfallConfig.class); }
    public void normalize() {
        damageScale = Balance.clamp(damageScale, 1, 0, 2);
        explosionPower = Balance.clamp(explosionPower, 2, 1, 3);
        particleBudget = Math.max(24, Math.min(144, particleBudget));
    }
    public static StarfallConfig load() {
        if (!Files.exists(path())) { var c = new StarfallConfig(); c.save(); return c; }
        try {
            var c = GSON.fromJson(Files.readString(path()), StarfallConfig.class);
            if (c == null) throw new IllegalArgumentException("Empty config");
            c.normalize(); return c;
        } catch (IOException | RuntimeException e) {
            Starfall.LOGGER.error("Cannot read starfall.json; using safe defaults without overwriting the file", e);
            var c = new StarfallConfig(); c.terrainDamage = false; return c;
        }
    }
    public void save() {
        normalize();
        try {
            Files.createDirectories(path().getParent());
            var tmp = Files.createTempFile(path().getParent(), "starfall-", ".tmp");
            try { Files.writeString(tmp, GSON.toJson(this)); Files.move(tmp, path(), StandardCopyOption.REPLACE_EXISTING); }
            finally { Files.deleteIfExists(tmp); }
        } catch (IOException e) { throw new IllegalStateException("Could not save starfall.json", e); }
    }
}
