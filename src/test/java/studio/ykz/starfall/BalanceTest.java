package studio.ykz.starfall;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class BalanceTest {
 @Test void safeNeverBreaksTerrain() { assertFalse(Balance.breaksTerrain(true,true)); assertFalse(Balance.breaksTerrain(true,false)); assertFalse(Balance.breaksTerrain(false,false)); assertTrue(Balance.breaksTerrain(false,true)); }
 @Test void damageHasFiniteBounds() { assertEquals(1,Balance.falloff(0,4)); assertEquals(.5,Balance.falloff(2,4)); assertEquals(0,Balance.falloff(4,4)); assertEquals(0,Balance.falloff(9,4)); assertEquals(0,Balance.falloff(Double.NaN,4)); assertEquals(0,Balance.falloff(0,0)); }
 @Test void rejectsNonfiniteConfiguration() { assertEquals(1,Balance.clamp(Float.NaN,1,0,2)); assertEquals(1,Balance.clamp(Float.POSITIVE_INFINITY,1,0,2)); assertEquals(2,Balance.clamp(99,1,0,2)); }
 @Test void highPayloadActuallyRaisesDamageAndKeepsCover() {
  assertEquals(16.5f,Balance.explosionDamage(100,10,1.65f,false,1));
  assertEquals(400,Balance.explosionDamage(100,10,40,true,1));
  assertEquals(240,Balance.explosionDamage(100,6,40,true,1));
  assertEquals(800,Balance.explosionDamage(100,10,40,true,2));
  assertEquals(0,Balance.explosionDamage(100,10,40,true,0));
  assertEquals(0,Balance.explosionDamage(1,10,40,true,1));
  assertEquals(0,Balance.explosionDamage(0,10,40,true,1));
  assertTrue(Balance.explosionDamage(2,10,40,true,1)<Balance.explosionDamage(100,10,40,true,1));
 }
 @Test void extraGradesOnlyExistForExplosives() {
  for(Kind kind:Kind.values()) for(Grade grade:Grade.values())
   assertEquals(grade.ordinal()<=Grade.MASTER.ordinal()||kind.explosive(),grade.supports(kind));
 }
}
