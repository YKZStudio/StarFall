package studio.ykz.starfall;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.entity.projectile.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffects;
import java.nio.file.*;
import java.lang.reflect.*;
import java.util.*;

/** Temporary dedicated-server tests. Not included in the release mod. */
public final class ServerSmoke implements ModInitializer {
 private final List<String> results=new ArrayList<>();
 private void check(boolean ok,String message) {if(!ok)throw new AssertionError(message);results.add("PASS "+message);}
 private ItemStack item(String id) { return new ItemStack(Starfall.ITEMS.get(id)); }
 @Override public void onInitialize() {
  ServerLifecycleEvents.SERVER_STARTED.register(server -> {
   for(int x=0;x<=7;x++) for(int z=-1;z<=1;z++) server.overworld().setChunkForced(x,z,true);
  });
  int[] ticks={0};
  ServerTickEvents.END_SERVER_TICK.register(server -> {
   if(++ticks[0]!=40)return;
   try {run(server.overworld());results.add("ALL SERVER TESTS PASSED");}
   catch(Throwable e) {results.add("FAIL "+e);Starfall.LOGGER.error("Smoke test failed",e);}
   finally {
    try {Files.write(Path.of("starfall-test-results.txt"),results);} catch(Exception e){throw new RuntimeException(e);}
    results.forEach(Starfall.LOGGER::info);server.halt(false);
   }
  });
 }
 private void run(ServerLevel l) throws Exception {
  check(Starfall.ITEMS.size()==27,"27 items registered");
  var owner=new Zombie(l);owner.setNoAi(true);owner.setPos(0,104,0);
  var crossbow=(CrossbowItem)Items.CROSSBOW;
  var create=CrossbowItem.class.getDeclaredMethod("createProjectile",Level.class,LivingEntity.class,ItemStack.class,ItemStack.class,boolean.class);create.setAccessible(true);
  var ammo=ProjectileWeaponItem.class.getDeclaredMethod("useAmmo",ItemStack.class,ItemStack.class,LivingEntity.class,boolean.class);ammo.setAccessible(true);
  var explode=FireworkRocketEntity.class.getDeclaredMethod("explode",ServerLevel.class);explode.setAccessible(true);
  for(Kind k:Kind.values()) for(Grade g:Grade.values()) {
   var stack=item(g.path(k));
   check(crossbow.getAllSupportedProjectiles().test(stack),"Crossbow accepts "+g.path(k));
   var projectile=(Projectile)create.invoke(crossbow,l,owner,new ItemStack(Items.CROSSBOW),stack,false);
   check(k.rocket?projectile instanceof FireworkRocketEntity:projectile instanceof StarArrowEntity,"Correct projectile "+g.path(k));
   var input=stack.copyWithCount(8);ammo.invoke(null,new ItemStack(Items.BOW),input,owner,false);
   check(input.getCount()==7,"One ammo consumed "+g.path(k));
   var arrowItem=stack.getItem();
   if(arrowItem instanceof StarArrow a) {
    var arrow=(StarArrowEntity)a.asProjectile(l,new Vec3(0,100,0),stack,net.minecraft.core.Direction.UP);
    check(arrow.visualItem().is(stack.getItem()),"Synced 3D arrow model "+g.path(k));
   }
  }
  // Use actual rocket explode method to exercise the required mixin.
  for(Grade g:Grade.values()) {
   Vec3 p=new Vec3(16+g.ordinal()*16,100,0);
   for(BlockPos b:BlockPos.betweenClosed(BlockPos.containing(p).offset(-3,-1,-3),BlockPos.containing(p).offset(3,-1,3)))l.setBlock(b,Blocks.GLASS.defaultBlockState(),3);
   var mob=new Zombie(l);mob.setNoAi(true);mob.setPos(p.x+1,p.y,p.z);l.addFreshEntity(mob);
   check(l.getEntitiesOfClass(Zombie.class,mob.getBoundingBox().inflate(1)).contains(mob),"Test target is tracked "+g);
   var r=new FireworkRocketEntity(l,owner,p.x,p.y,p.z,item(g.path(Kind.SAFE_ROCKET)));
   explode.invoke(r,l);
   check(mob.getHealth()<20,"Safe rocket hurts entities "+g);
   check(mob.getHealth()>=20-10*g.damage-.01,"Safe rocket damage bounded "+g);
   int count=0;for(BlockPos b:BlockPos.betweenClosed(BlockPos.containing(p).offset(-3,-1,-3),BlockPos.containing(p).offset(3,-1,3)))if(l.getBlockState(b).is(Blocks.GLASS))count++;
   check(count==49,"Safe rocket preserves all 49 glass blocks "+g);
   check(r.isRemoved(),"Rocket consumed exactly once "+g);mob.discard();
  }
  for(Kind kind:new Kind[]{Kind.HALO_ROCKET,Kind.WILLOW_ROCKET,Kind.SPIRAL_ROCKET}) {
   Vec3 p=new Vec3(80,100,0);var mob=new Zombie(l);mob.setNoAi(true);mob.setPos(p);l.addFreshEntity(mob);
   var r=new FireworkRocketEntity(l,owner,p.x,p.y,p.z,item(Grade.MASTER.path(kind)));explode.invoke(r,l);
   check(mob.getHealth()==20,"Ornamental rocket has no native damage "+kind);mob.discard();
  }
  var mob=new Zombie(l);mob.setNoAi(true);mob.setPos(100,100,0);l.addFreshEntity(mob);
  var a=new StarArrowEntity(l,100,100,1,item("master_frost_arrow"));
  Effects.detonate(l,a,new Vec3(100,100,1),Kind.FROST_ARROW,Grade.MASTER);
  check(mob.hasEffect(MobEffects.SLOWNESS),"Frost effect applied");
  check(mob.getEffect(MobEffects.SLOWNESS).getDuration()==120,"Master frost duration 6s");mob.discard();
  // Actual ordinary blast must obey both states of the terrain setting.
  for(boolean enabled:new boolean[]{false,true}) {
   Starfall.config.terrainDamage=enabled;
   Vec3 p=new Vec3(112,100,0);
   for(BlockPos b:BlockPos.betweenClosed(BlockPos.containing(p).offset(-2,-1,-2),BlockPos.containing(p).offset(2,-1,2)))l.setBlock(b,Blocks.GLASS.defaultBlockState(),3);
   var r=new FireworkRocketEntity(l,owner,p.x,p.y,p.z,item("blast_rocket"));explode.invoke(r,l);
   int left=0;for(BlockPos b:BlockPos.betweenClosed(BlockPos.containing(p).offset(-2,-1,-2),BlockPos.containing(p).offset(2,-1,2)))if(l.getBlockState(b).is(Blocks.GLASS))left++;
   check(enabled?left<25:left==25,"Ordinary terrain switch "+enabled);
  }
  Starfall.config.terrainDamage=true;
  // Check the real EnergyExchange rule loader rather than just JSON syntax.
  if(net.fabricmc.loader.api.FabricLoader.getInstance().isModLoaded("energyexchange")) {
   Object rules=Class.forName("studio.ykz.energyexchange.EnergyExchange").getField("RULES").get(null);
   var require=rules.getClass().getMethod("require",String.class);
   for(String id:Starfall.ITEMS.keySet()) {
    Object rule=require.invoke(rules,"starfall:"+id);
    String actual=rule.getClass().getMethod("value").invoke(rule).toString();
    try(var in=ServerSmoke.class.getResourceAsStream("/data/starfall/energyexchange/values/"+id+".json")) {
     String expected=com.google.gson.JsonParser.parseReader(new java.io.InputStreamReader(in)).getAsJsonObject().get("value").getAsString();
     check(actual.equals(expected),"EnergyExchange price "+id+" = "+actual);
    }
   }
  }
  // Restore server config after test.
  check(Balance.breaksTerrain(false,true)&&!Balance.breaksTerrain(true,true),"Terrain gate respects safe identity");
 }
}
