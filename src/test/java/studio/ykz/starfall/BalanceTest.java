package studio.ykz.starfall;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class BalanceTest {
 @Test void safeNeverBreaksTerrain() { assertFalse(Balance.breaksTerrain(true,true)); assertFalse(Balance.breaksTerrain(true,false)); assertFalse(Balance.breaksTerrain(false,false)); assertTrue(Balance.breaksTerrain(false,true)); }
 @Test void damageHasFiniteBounds() { assertEquals(1,Balance.falloff(0,4)); assertEquals(.5,Balance.falloff(2,4)); assertEquals(0,Balance.falloff(4,4)); assertEquals(0,Balance.falloff(9,4)); assertEquals(0,Balance.falloff(Double.NaN,4)); assertEquals(0,Balance.falloff(0,0)); }
 @Test void rejectsNonfiniteConfiguration() { assertEquals(1,Balance.clamp(Float.NaN,1,0,2)); assertEquals(1,Balance.clamp(Float.POSITIVE_INFINITY,1,0,2)); assertEquals(2,Balance.clamp(99,1,0,2)); }
}
