package studio.ykz.starfall;

public enum Kind {
    BLAST_ROCKET("blast_rocket", true, 0xED764B),
    SAFE_ROCKET("safe_rocket", true, 0x4ADACB),
    HALO_ROCKET("halo_rocket", true, 0xEEC66D),
    WILLOW_ROCKET("willow_rocket", true, 0xB6E875),
    SPIRAL_ROCKET("spiral_rocket", true, 0xBE8CF0),
    BLAST_ARROW("blast_arrow", false, 0xED764B),
    FROST_ARROW("frost_arrow", false, 0x7EDDF4),
    PULSE_ARROW("pulse_arrow", false, 0xBE8CF0),
    MARK_ARROW("mark_arrow", false, 0xEEC66D);
    public final String id;
    public final boolean rocket;
    public final int color;
    Kind(String id, boolean rocket, int color) { this.id = id; this.rocket = rocket; this.color = color; }
    public boolean decorative() { return this == HALO_ROCKET || this == WILLOW_ROCKET || this == SPIRAL_ROCKET; }
}
