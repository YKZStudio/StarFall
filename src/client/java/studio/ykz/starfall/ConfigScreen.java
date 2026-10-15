package studio.ykz.starfall;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
public final class ConfigScreen extends Screen {
 private final Screen parent;
 private final StarfallConfig draft;
 private boolean failed;
 public ConfigScreen(Screen parent) { super(tr("title")); this.parent=parent; this.draft=Starfall.config.copy(); }
 private static Component tr(String key,Object...args) { return Component.translatable("config.starfall."+key,args); }
 private boolean editable() { return minecraft.getConnection()==null || minecraft.hasSingleplayerServer(); }
 @Override protected void init() {
  int x=width/2-120, y=Math.max(38,height/2-65);
  var terrain=addRenderableWidget(Button.builder(tr("terrain",Component.translatable(draft.terrainDamage?"options.on":"options.off")),b->{draft.terrainDamage=!draft.terrainDamage;b.setMessage(tr("terrain",Component.translatable(draft.terrainDamage?"options.on":"options.off")));}).bounds(x,y,240,20).build());
  var damage=addRenderableWidget(Button.builder(tr("damage",draft.damageScale),b->{draft.damageScale=draft.damageScale>=2?0.5f:draft.damageScale+.5f;b.setMessage(tr("damage",draft.damageScale));}).bounds(x,y+24,240,20).build());
  var power=addRenderableWidget(Button.builder(tr("power",draft.explosionPower),b->{draft.explosionPower=draft.explosionPower>=3?1:draft.explosionPower+.5f;b.setMessage(tr("power",draft.explosionPower));}).bounds(x,y+48,240,20).build());
  var particles=addRenderableWidget(Button.builder(tr("particles",draft.particleBudget),b->{draft.particleBudget=draft.particleBudget>=144?24:draft.particleBudget+24;b.setMessage(tr("particles",draft.particleBudget));}).bounds(x,y+72,240,20).build());
  terrain.active=damage.active=power.active=particles.active=editable();
  var save=addRenderableWidget(Button.builder(tr("save"),b->{
   try { draft.save(); var next=draft.copy(); var server=minecraft.getSingleplayerServer();
    if(server!=null) server.execute(()->Starfall.config=next); else Starfall.config=next;
    onClose();
   } catch(RuntimeException e) { failed=true; }
  }).bounds(x,y+102,118,20).build()); save.active=editable();
  addRenderableWidget(Button.builder(tr("cancel"),b->onClose()).bounds(x+122,y+102,118,20).build());
 }
 @Override public void extractRenderState(GuiGraphicsExtractor g,int x,int y,float delta) {
  super.extractRenderState(g,x,y,delta);
  g.centeredText(font,title,width/2,16,0xFFF2D79D);
  g.textWithWordWrap(font,tr(failed?"error":editable()?"hint":"remote"),20,height-32,width-40,failed?0xFFFF7777:0xFFAAAAAA);
 }
 @Override public void onClose() { minecraft.gui.setScreen(parent); }
}
