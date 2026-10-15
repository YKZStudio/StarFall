package studio.ykz.starfall.mixin;
import studio.ykz.starfall.StarRocket;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.*;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.function.Predicate;
@Mixin(CrossbowItem.class)
public abstract class CrossbowMixin {
 @Inject(method={"getSupportedHeldProjectiles","getAllSupportedProjectiles"},at=@At("RETURN"),cancellable=true)
 private void starfall$ammo(CallbackInfoReturnable<Predicate<ItemStack>> ci) {
  ci.setReturnValue(ci.getReturnValue().or(s -> s.getItem() instanceof StarRocket));
 }
 @Inject(method="createProjectile",at=@At("HEAD"),cancellable=true)
 private void starfall$rocket(Level level,LivingEntity shooter,ItemStack weapon,ItemStack ammo,boolean critical,CallbackInfoReturnable<Projectile> ci) {
  if(ammo.getItem() instanceof StarRocket) ci.setReturnValue(new FireworkRocketEntity(level,ammo,shooter,shooter.getX(),shooter.getEyeY()-.15,shooter.getZ(),true));
 }
 @Inject(method="getDurabilityUse",at=@At("HEAD"),cancellable=true)
 private void starfall$durability(ItemStack ammo,CallbackInfoReturnable<Integer> ci) { if(ammo.getItem() instanceof StarRocket) ci.setReturnValue(3); }
 @Inject(method="getShootingPower",at=@At("HEAD"),cancellable=true)
 private static void starfall$speed(ChargedProjectiles loaded,CallbackInfoReturnable<Float> ci) {
  if(loaded.itemCopies().stream().anyMatch(s->s.getItem() instanceof StarRocket)) ci.setReturnValue(1.6f);
 }
}
