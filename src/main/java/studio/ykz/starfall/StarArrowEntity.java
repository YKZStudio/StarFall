package studio.ykz.starfall;

import net.minecraft.network.syncher.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.phys.*;

/** Dedicated entity: item identity is synced for the actual 3-D projectile model. */
public final class StarArrowEntity extends AbstractArrow {
 private static final EntityDataAccessor<ItemStack> VISUAL = SynchedEntityData.defineId(StarArrowEntity.class, EntityDataSerializers.ITEM_STACK);
 public StarArrowEntity(EntityType<? extends StarArrowEntity> type, Level level) { super(type,level); }
 public StarArrowEntity(Level level, LivingEntity shooter, ItemStack ammo, ItemStack weapon) {
  super(Starfall.ARROW_ENTITY,shooter,level,ammo,weapon); entityData.set(VISUAL,ammo.copyWithCount(1));
 }
 public StarArrowEntity(Level level,double x,double y,double z,ItemStack ammo) {
  super(Starfall.ARROW_ENTITY,x,y,z,level,ammo,null);entityData.set(VISUAL,ammo.copyWithCount(1));
 }
 @Override protected void defineSynchedData(SynchedEntityData.Builder b) { super.defineSynchedData(b);b.define(VISUAL,ItemStack.EMPTY); }
 public ItemStack visualItem() { return entityData.get(VISUAL); }
 @Override protected ItemStack getDefaultPickupItem() { return new ItemStack(Items.ARROW); }
 @Override protected void readAdditionalSaveData(ValueInput input) {
  super.readAdditionalSaveData(input);entityData.set(VISUAL,getPickupItemStackOrigin().copyWithCount(1));
 }
 @Override protected void onHitEntity(EntityHitResult hit) {
  if (!(level() instanceof ServerLevel server)) return;
  var kind=Starfall.kind(getPickupItemStackOrigin()); var grade=Starfall.grade(getPickupItemStackOrigin());
  if (kind==null) { super.onHitEntity(hit);return; }
  if (kind==Kind.BLAST_ARROW) Effects.detonate(server,this,hit.getLocation(),kind,grade);
  else if (Effects.allowed(this,hit.getEntity()) && hit.getEntity().hurtServer(server,
      server.damageSources().arrow(this,getOwner()),4*grade.damage*Starfall.config.damageScale))
      Effects.detonate(server,this,hit.getLocation(),kind,grade);
  discard();
 }
 @Override protected void onHitBlock(BlockHitResult hit) {
  if (!(level() instanceof ServerLevel server)) return;
  var kind=Starfall.kind(getPickupItemStackOrigin());
  if (kind==null) {super.onHitBlock(hit);return;}
  Effects.detonate(server,this,hit.getLocation().add(hit.getDirection().getUnitVec3().scale(.08)),kind,Starfall.grade(getPickupItemStackOrigin()));
  discard();
 }
}
