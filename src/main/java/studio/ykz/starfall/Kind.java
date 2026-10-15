package studio.ykz.starfall;

public enum Kind {
    BLAST_ROCKET("blast_rocket", true, 0xED764B),
    SAFE_ROCKET("safe_rocket", true, 0x4ADACB),
    HALO_ROCKET("halo_rocket", true, 0xEEC66D),
    WILLOW_ROCKET("willow_rocket", true, 0xB6E875),
    SPIRAL_ROCKET("spiral_rocket", true, 0xBE8CF0),
    STAR_ROCKET("star_rocket", true, 0x89CFFF),
    HEART_ROCKET("heart_rocket", true, 0xF28BAE),
    BLAST_ARROW("blast_arrow", false, 0xED764B),
    FROST_ARROW("frost_arrow", false, 0x7EDDF4),
    PULSE_ARROW("pulse_arrow", false, 0xBE8CF0),
    MARK_ARROW("mark_arrow", false, 0xEEC66D),
    VENOM_ARROW("venom_arrow", false, 0x81C75A),
    GRAVITY_ARROW("gravity_arrow", false, 0x7588DC);
    public final String id;
    public final boolean rocket;
    public final int color;
    Kind(String id, boolean rocket, int color) { this.id = id; this.rocket = rocket; this.color = color; }
    public boolean explosive() { return this == BLAST_ROCKET || this == SAFE_ROCKET || this == BLAST_ARROW; }
    public boolean decorative() {
        return this == HALO_ROCKET || this == WILLOW_ROCKET || this == SPIRAL_ROCKET
            || this == STAR_ROCKET || this == HEART_ROCKET;
    }
}
