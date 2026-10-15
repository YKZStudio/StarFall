package studio.ykz.starfall;
import com.terraformersmc.modmenu.api.*;
public final class ModMenuIntegration implements ModMenuApi {
 @Override public ConfigScreenFactory<?> getModConfigScreenFactory() { return ConfigScreen::new; }
}
