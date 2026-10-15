# Starfall Workshop

Download the [0.1.0-pre3 runtime JAR](downloads/starfall-mc26.2-0.1.0-pre3.jar).

[简体中文说明、配方与安装指南](README_zh-CN.md)

Minecraft Java 26.2 / Fabric, **0.1.0-pre3**. Seven fireworks and six tactical arrows (48 items). All have Basic, Refined and Masterwork grades; Breach rockets, Safe-Blast rockets and Emberburst arrows additionally support Heavy IV, Siege V and Cataclysm VI. Also includes Star and Heart display rockets, Venom and Gravity arrows. Original voxel models, server-controlled settings, optional Mod Menu and EnergyExchange integration.

## Install

Use Java 25, Fabric Loader 0.19.3+, and Fabric API 0.159.0+26.2. Replace the previous runtime JAR on both server and clients. Optional integrations: Mod Menu 20.0.2 and YKZStudio/EnergyExchange 0.3.5 for 26.2; see the validation record for coverage. No Cloth Config required.

## Progression

Base / Refined / Masterwork damage multipliers: 1 / 1.35 / 1.65. Radius and display scale: 1 / 1.15 / 1.30. Control durations: 1 / 1.5 / 2. All upgrades have distinct item IDs and exchange values. Upgrade one rocket or four arrows with 2 gunpowder + 2 copper ingots for Refined; upgrade again with 2 gunpowder + 2 amethyst shards + 1 gold nugget for Masterwork.

Explosive ammunition can continue from Masterwork to Heavy (damage x4, radius x1.8), Siege (x12, x2.7), then Cataclysm (x40, x4). Each upgrade consumes one previous-grade rocket or four previous-grade arrows and yields the same count. Additional materials:

- Heavy: 2 TNT + 2 iron blocks + 1 diamond.
- Siege: 2 TNT + 2 diamond blocks + 1 gold block.
- Cataclysm: 2 TNT + 2 netherite ingots + 1 diamond block.

Default rocket damage caps grow from 16.5 HP at Masterwork to 40 / 120 / **400 HP**; arrow caps grow from 9.9 to 24 / 72 / **240 HP**, before armor and resistance. Cataclysm entity reach is 16 blocks for rockets and 9.6 for arrows with default explosion settings. High payloads increase actual damage, respecting range and cover; full cover does not amplify vanilla's minimum scratch damage. With `damageScale=2`, Cataclysm caps become 800 / 480 HP. Safe-Blast rockets and blast arrows preserve terrain at every grade. Other ammunition and displays remain at three grades.

Breach rockets can break terrain; safe rockets and blast arrows never do. Halo, Willow, Spiral, Star and Heart rockets are harmless. Frost, Pulse, Mark, Venom and Gravity arrows add line-of-sight area control on entity or block impact. Special arrows are consumed on impact and do not receive Infinity ammo savings; damage is controlled by grade/config, not Power, critical, Flame or piercing bonuses. Multishot retains vanilla consumption behavior. Combat rockets cannot boost elytra.

Venom arrows deal 4 base direct-hit HP and apply Poison I within 2.5 blocks for 3 / 4.5 / 6 seconds, respecting vanilla poison immunity and nonlethal poison behavior. Craft 4 arrows + 1 spider eye + 1 slime ball into 4 Venom arrows. Gravity arrows deal 4 base direct-hit HP and pull targets horizontally toward impact within 3 / 3.45 / 3.9 blocks; the impulse decreases with distance and never teleports targets or breaks terrain. Craft 4 arrows + 1 ender pearl + 1 amethyst shard into 4 Gravity arrows. Star rockets use a firework rocket + glowstone dust + amethyst shard; Heart rockets use a firework rocket + redstone + slime ball, each yielding one harmless display rocket.

## Settings and integration

Mod Menu edits local/singleplayer settings. Dedicated-server administrators edit `config/starfall.json` then restart. Clients cannot override multiplayer rules. EnergyExchange prices use official per-item resources at `data/starfall/energyexchange/values/`; data packs can override them and `/reload` applies changes. See `docs/balance.json`. Prices are conservative against the referenced recipe costs; modified economy inputs require rebalancing.

## Build and validation

With full JDK 25: `./gradlew build` (Windows: `gradlew.bat build`). Regenerate with `python tools/generate_grades.py` (Pillow); validate using `python tools/validate_assets.py`. Model preview additionally needs NumPy and a font path.

This version builds with JDK 25, Gradle 9.6.0 and Loom 1.17.12. All 428 dedicated-server assertions, five pure unit tests and the 48-item asset/economy validation passed, including all 48 EnergyExchange prices. See [the validation record](docs/VALIDATION_zh-CN.md) for coverage. No Minecraft classes or dependency libraries are bundled.

**Pre-release limitation:** graphical-client runtime, Mod Menu interaction and in-game model orientation/lighting have not been visually playtested. Client code compiles and the shipped geometry was rendered for inspection. `docs/model-preview.png` is a model-file render, not an in-game screenshot.

The `src/smoke` test mod stops its server after testing. Set up a disposable server in `build/smoke-server`, accept the Minecraft EULA, then run `./gradlew -PstarfallSmoke runSmokeServer`. Add EnergyExchange to that server's `mods` directory to also check all exchange prices. The test mod is enabled only with this explicit opt-in and is excluded from the runtime JAR. Never use it on a normal world.
