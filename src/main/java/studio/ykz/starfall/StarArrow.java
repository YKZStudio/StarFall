package studio.ykz.starfall;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import java.util.function.Consumer;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.core.Position;
import net.minecraft.core.Direction;
public final class StarArrow extends ArrowItem {
 public final Kind kind;
 public final Grade grade;
 public StarArrow(Kind kind, Grade grade, Properties p) { super(p); this.kind = kind; this.grade=grade; }
 @Override public AbstractArrow createArrow(Level l,ItemStack ammo,LivingEntity shooter,ItemStack weapon) { return new StarArrowEntity(l,shooter,ammo,weapon); }
 @Override public Projectile asProjectile(Level l,Position p,ItemStack ammo,Direction direction) {
  var arrow=new StarArrowEntity(l,p.x(),p.y(),p.z(),ammo.copyWithCount(1));
  arrow.pickup=AbstractArrow.Pickup.DISALLOWED; return arrow;
 }
 @Override public void appendHoverText(ItemStack s, TooltipContext c, TooltipDisplay d, Consumer<Component> out, TooltipFlag f) {
  out.accept(Component.translatable("grade.starfall."+grade.name().toLowerCase(java.util.Locale.ROOT)).withStyle(ChatFormatting.GOLD));
  out.accept(Component.translatable("tip.starfall." + grade.path(kind)).withStyle(ChatFormatting.GRAY));
  out.accept(Component.translatable("tip.starfall.consumable").withStyle(ChatFormatting.DARK_GRAY));
 }
}
