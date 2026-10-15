package studio.ykz.starfall;

/** Pure rules shared by gameplay and regression tests. */
public final class Balance {
    private Balance() {}
    public static double falloff(double distance, double radius) {
        if (!Double.isFinite(distance) || !Double.isFinite(radius) || radius <= 0) return 0;
        return Math.max(0, Math.min(1, 1 - Math.max(0, distance) / radius));
    }
    public static float clamp(float value, float fallback, float min, float max) {
        return Float.isFinite(value) ? Math.max(min, Math.min(max, value)) : fallback;
    }
    public static boolean breaksTerrain(boolean safe, boolean enabled) { return !safe && enabled; }
    public static float explosionDamage(float vanillaDamage, float baseCap, float gradeDamage, boolean highPayload, float damageScale) {
        // Do not amplify vanilla's one-HP minimum through complete cover.
        if (vanillaDamage<=0) return 0;
        float scaled = highPayload ? Math.max(0,vanillaDamage-1)*gradeDamage : vanillaDamage;
        return Math.min(baseCap*gradeDamage,scaled)*damageScale;
    }
}
