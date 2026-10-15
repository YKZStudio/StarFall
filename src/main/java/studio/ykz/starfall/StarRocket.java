package studio.ykz.starfall;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import java.util.function.Consumer;
public final class StarRocket extends FireworkRocketItem {
 public final Kind kind;
 public final Grade grade;
 public StarRocket(Kind kind, Grade grade, Properties p) { super(p); this.kind = kind; this.grade=grade; }
 @Override public InteractionResult use(Level l, Player p, InteractionHand h) {
  // Combat rockets are ammunition, never an accidental elytra suicide boost.
  if (!kind.decorative()) return InteractionResult.PASS;
  return super.use(l,p,h);
 }
 @Override public void appendHoverText(ItemStack s, TooltipContext c, TooltipDisplay d, Consumer<Component> out, TooltipFlag f) {
  out.accept(Component.translatable("grade.starfall."+grade.name().toLowerCase(java.util.Locale.ROOT)).withStyle(ChatFormatting.GOLD));
  out.accept(Component.translatable("tip.starfall." + grade.path(kind)).withStyle(ChatFormatting.GRAY));
  out.accept(Component.translatable(kind.decorative() ? "tip.starfall.display" : "tip.starfall.crossbow").withStyle(ChatFormatting.DARK_GRAY));
 }
}
