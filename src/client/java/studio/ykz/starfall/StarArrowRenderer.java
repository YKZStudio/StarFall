package studio.ykz.starfall;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.item.*;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.util.Mth;
public final class StarArrowRenderer extends EntityRenderer<StarArrowEntity,StarArrowRenderer.State> {
 private final ItemModelResolver models;
 public static final class State extends EntityRenderState { public float yaw,pitch; public final ItemStackRenderState item=new ItemStackRenderState(); }
 public StarArrowRenderer(EntityRendererProvider.Context ctx) {super(ctx);models=ctx.getItemModelResolver();}
 @Override public State createRenderState() {return new State();}
 @Override public void extractRenderState(StarArrowEntity e,State s,float delta) {
  super.extractRenderState(e,s,delta);s.yaw=Mth.lerp(delta,e.yRotO,e.getYRot());s.pitch=Mth.lerp(delta,e.xRotO,e.getXRot());
  models.updateForNonLiving(s.item,e.visualItem(),ItemDisplayContext.NONE,e);
 }
 @Override public void submit(State s,PoseStack pose,SubmitNodeCollector collector,CameraRenderState camera) {
  pose.pushPose();pose.mulPose(Axis.YP.rotationDegrees(s.yaw));pose.mulPose(Axis.XP.rotationDegrees(s.pitch+90));
  pose.scale(.75f,.75f,.75f);s.item.submit(pose,collector,s.lightCoords,OverlayTexture.NO_OVERLAY,s.outlineColor);pose.popPose();
  super.submit(s,pose,collector,camera);
 }
}
