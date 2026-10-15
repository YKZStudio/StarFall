package studio.ykz.starfall;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.particles.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.effect.*;
import net.minecraft.world.level.*;
import net.minecraft.world.phys.*;
import net.minecraft.sounds.*;

public final class Effects {
 private Effects() {}
 public static boolean allowed(Projectile source, Entity target) {
  if (target.isSpectator()) return false;
  if (source.getOwner() instanceof Player p && target instanceof Player other && p != other)
   return p.canHarmPlayer(other) && ((ServerLevel)source.level()).isPvpAllowed();
  return true;
 }
 public static void detonate(ServerLevel level, Projectile source, Vec3 point, Kind kind, Grade grade) {
  if (kind == Kind.BLAST_ROCKET || kind == Kind.SAFE_ROCKET || kind == Kind.BLAST_ARROW) {
   boolean terrain = Balance.breaksTerrain(kind != Kind.BLAST_ROCKET, Starfall.config.terrainDamage)
      && source.mayInteract(level, net.minecraft.core.BlockPos.containing(point)) && source.mayBreak(level);
   float power = kind == Kind.BLAST_ARROW ? 1.2f * grade.radius : Starfall.config.explosionPower * grade.radius;
   var calculator = new ExplosionDamageCalculator() {
    @Override public boolean shouldDamageEntity(Explosion e, Entity target) { return allowed(source,target) && super.shouldDamageEntity(e,target); }
    @Override public float getKnockbackMultiplier(Entity target) { return allowed(source,target) ? super.getKnockbackMultiplier(target) : 0; }
    @Override public float getEntityDamageAmount(Explosion e, Entity target, float exposure) {
     // Cap point-blank damage before armor: 10 HP rockets, 6 HP arrows.
     return Math.min((kind == Kind.BLAST_ARROW ? 6 : 10) * grade.damage, super.getEntityDamageAmount(e,target,exposure)) * Starfall.config.damageScale;
    }
   };
   level.explode(source, level.damageSources().explosion(source,source.getOwner()), calculator,
       point, power, false, terrain ? Level.ExplosionInteraction.TNT : Level.ExplosionInteraction.NONE);
  } else if (!kind.decorative()) {
   double radius = (kind == Kind.PULSE_ARROW ? 3 : 2.5) * grade.radius;
   for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, new AABB(point,point).inflate(radius))) {
    double distance = target.position().distanceTo(point);
    if (distance > radius || !allowed(source,target)) continue;
    var hit = level.clip(new ClipContext(point, target.getEyePosition(), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, source));
    if (hit.getType() != HitResult.Type.MISS) continue;
    if (kind == Kind.FROST_ARROW) target.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, (int)(60 * grade.duration), 1), source.getOwner());
    if (kind == Kind.MARK_ARROW) target.addEffect(new MobEffectInstance(MobEffects.GLOWING, (int)(160 * grade.duration), 0), source.getOwner());
    if (kind == Kind.PULSE_ARROW) {
     Vec3 delta = target.position().subtract(point).normalize().scale(0.85 * grade.radius * Balance.falloff(distance,radius));
     target.push(delta.x, 0.18, delta.z); target.hurtMarked = true;
    }
   }
  }
  burst(level,point,kind,grade);
 }
 public static void burst(ServerLevel level, Vec3 p, Kind kind, Grade grade) {
  int n = kind.decorative() ? Starfall.config.particleBudget : Starfall.config.particleBudget / 2;
  n = Math.min(192, Math.round(n * grade.radius));
  var dust = new DustParticleOptions(kind.color,1.25f);
  for (int i=0;i<n;i++) {
   double t = i / (double)n, a = Math.PI * 2 * t;
   Vec3 v;
   if (kind == Kind.HALO_ROCKET) v = new Vec3(Math.cos(a)*3, Math.sin(a)*3, Math.sin(a)*.6);
   else if (kind == Kind.SPIRAL_ROCKET) v = new Vec3(Math.cos(a*3)*2, (t-.5)*6, Math.sin(a*3)*2);
   else if (kind == Kind.WILLOW_ROCKET) {
    double strand = i%12 * Math.PI/6, f = (i/12.0)/(n/12.0);
    v = new Vec3(Math.cos(strand)*f*3, 1-f*f*4, Math.sin(strand)*f*3);
   } else {
    double y=1-2*t, r=Math.sqrt(Math.max(0,1-y*y)), phi=i*2.399963;
    v=new Vec3(Math.cos(phi)*r,y,Math.sin(phi)*r).scale(1.6);
   }
   v=v.scale(grade.radius);
   level.sendParticles(dust,p.x+v.x,p.y+v.y,p.z+v.z,1,0,0,0,0);
   if (i%4==0) level.sendParticles(ParticleTypes.FIREWORK,p.x+v.x,p.y+v.y,p.z+v.z,1,.03,.03,.03,.02);
  }
  level.playSound(null,p.x,p.y,p.z,SoundEvents.FIREWORK_ROCKET_TWINKLE,SoundSource.NEUTRAL,1,.9f);
 }
}
