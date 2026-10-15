package studio.ykz.starfall;
/** Fixed identities preserve upgrades across EnergyExchange purchases and saves. */
public enum Grade {
 BASIC("",1f,1f,1f), REFINED("refined_",1.35f,1.15f,1.5f), MASTER("master_",1.65f,1.3f,2f),
 HEAVY("heavy_",4f,1.8f,2f), SIEGE("siege_",12f,2.7f,2f), CATACLYSM("cataclysm_",40f,4f,2f);
 public final String prefix;
 public final float damage, radius, duration;
 Grade(String prefix,float damage,float radius,float duration) { this.prefix=prefix;this.damage=damage;this.radius=radius;this.duration=duration; }
 public String path(Kind kind) { return prefix+kind.id; }
 public boolean supports(Kind kind) { return ordinal()<=MASTER.ordinal() || kind.explosive(); }
}
