package studio.ykz.starfall.mixin;
import studio.ykz.starfall.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(FireworkRocketEntity.class)
public abstract class RocketMixin {
 @Inject(method="explode",at=@At("HEAD"),cancellable=true)
 private void starfall$explode(ServerLevel level, CallbackInfo ci) {
  var self=(FireworkRocketEntity)(Object)this;
  Kind kind=Starfall.kind(self.getItem());
  if (kind==null) return;
  // No native firework damage: ornamental rockets really are harmless.
  level.broadcastEntityEvent(self,(byte)17);
  Effects.detonate(level,self,self.position(),kind,Starfall.grade(self.getItem()));
  self.discard(); ci.cancel();
 }
}
