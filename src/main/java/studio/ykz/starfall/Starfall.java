package studio.ykz.starfall;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.*;
import net.minecraft.resources.*;
import net.minecraft.world.item.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.component.*;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.core.dispenser.ProjectileDispenseBehavior;
import it.unimi.dsi.fastutil.ints.IntList;
import org.slf4j.*;
import java.util.*;

public final class Starfall implements ModInitializer {
    public static final String ID = "starfall";
    public static final Logger LOGGER = LoggerFactory.getLogger(ID);
    public static volatile StarfallConfig config = new StarfallConfig();
    public static final EntityType<StarArrowEntity> ARROW_ENTITY = Registry.register(BuiltInRegistries.ENTITY_TYPE,id("arrow"),
        EntityType.Builder.<StarArrowEntity>of(StarArrowEntity::new,MobCategory.MISC).sized(.5f,.5f).clientTrackingRange(4).updateInterval(1)
        .build(ResourceKey.create(Registries.ENTITY_TYPE,id("arrow"))));
    public static final Map<String, Item> ITEMS = new LinkedHashMap<>();
    public static Identifier id(String name) { return Identifier.fromNamespaceAndPath(ID, name); }
    public static Kind kind(ItemStack stack) {
        if (stack.getItem() instanceof StarArrow a) return a.kind;
        if (stack.getItem() instanceof StarRocket r) return r.kind;
        return null;
    }
    public static Grade grade(ItemStack stack) {
        if (stack.getItem() instanceof StarArrow a) return a.grade;
        if (stack.getItem() instanceof StarRocket r) return r.grade;
        return Grade.BASIC;
    }
    @Override public void onInitialize() {
        config = StarfallConfig.load();
        for (Kind kind : Kind.values()) for (Grade grade : Grade.values()) {
            if (!grade.supports(kind)) continue;
            String path = grade.path(kind);
            var props = new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id(path)));
            if (kind.rocket) props.component(DataComponents.FIREWORKS, new Fireworks(1,
                List.of(new FireworkExplosion(FireworkExplosion.Shape.SMALL_BALL,
                    IntList.of(kind.color), IntList.of(0xFFF0C2), true, true))));
            Item item = kind.rocket ? new StarRocket(kind, grade, props) : new StarArrow(kind, grade, props);
            Registry.register(BuiltInRegistries.ITEM, id(path), item);
            ITEMS.put(path, item);
            DispenserBlock.registerBehavior(item, new ProjectileDispenseBehavior(item));
        }
        CreativeModeTabEvents.modifyOutputEvent(ResourceKey.create(Registries.CREATIVE_MODE_TAB,Identifier.withDefaultNamespace("combat"))).register(e -> ITEMS.values().forEach(e::accept));
        ServerLifecycleEvents.SERVER_STARTING.register(server -> config = StarfallConfig.load());
    }
}
