package studio.ykz.starfall.mixin;
import studio.ykz.starfall.Starfall;
import net.minecraft.world.item.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Mixin(ProjectileWeaponItem.class)
public abstract class AmmoMixin {
 @Inject(method="useAmmo",at=@At("HEAD"),cancellable=true)
 private static void starfall$consume(ItemStack weapon, ItemStack ammo, LivingEntity shooter, boolean multishotCopy, CallbackInfoReturnable<ItemStack> ci) {
  if (Starfall.kind(ammo)==null || multishotCopy || shooter.hasInfiniteMaterials()) return;
  // Main shot always costs one. Vanilla marks multishot copies intangible.
  if (ammo.isEmpty()) { ci.setReturnValue(ItemStack.EMPTY); return; }
  var result=ammo.split(1);
  if (ammo.isEmpty() && shooter instanceof Player p) p.getInventory().removeItem(ammo);
  ci.setReturnValue(result);
 }
}
