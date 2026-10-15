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
}
