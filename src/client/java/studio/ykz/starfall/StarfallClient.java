package studio.ykz.starfall;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
public final class StarfallClient implements ClientModInitializer {
 @Override public void onInitializeClient() { EntityRendererRegistry.register(Starfall.ARROW_ENTITY,StarArrowRenderer::new); }
}
