# Starfall Workshop

Download the [0.1.0-pre1 runtime JAR](downloads/starfall-mc26.2-0.1.0-pre1.jar).

[简体中文说明、配方与安装指南](README_zh-CN.md)

Minecraft Java 26.2 / Fabric, **0.1.0-pre1**. Five fireworks and four tactical arrows, each in three fixed material grades (27 items). Original voxel models, server-controlled settings, optional Mod Menu and EnergyExchange integration.

## Install

Use Java 25, Fabric Loader 0.19.3+, and Fabric API 0.159.0+26.2. Install the runtime JAR on both server and clients. Optional integrations tested against Mod Menu 20.0.2 (compiled) and YKZStudio/EnergyExchange 0.3.5 for 26.2 (dedicated-server price loading tested). No Cloth Config required.

## Progression

Base / Refined / Masterwork damage multipliers: 1 / 1.35 / 1.65. Radius and display scale: 1 / 1.15 / 1.30. Control durations: 1 / 1.5 / 2. All upgrades have distinct item IDs and exchange values. Upgrade one rocket or four arrows with 2 gunpowder + 2 copper ingots for Refined; upgrade again with 2 gunpowder + 2 amethyst shards + 1 gold nugget for Masterwork.

Breach rockets can break terrain; safe rockets and blast arrows never do. Halo, Willow and Spiral rockets are harmless. Frost, Pulse and Mark arrows add line-of-sight area control. Special arrows are consumed on impact and do not receive Infinity ammo savings; damage is controlled by grade/config, not Power, critical, Flame or piercing bonuses. Multishot retains vanilla consumption behavior. Combat rockets cannot boost elytra.

## Settings and integration

Mod Menu edits local/singleplayer settings. Dedicated-server administrators edit `config/starfall.json` then restart. Clients cannot override multiplayer rules. EnergyExchange prices use official per-item resources at `data/starfall/energyexchange/values/`; data packs can override them and `/reload` applies changes. See `docs/balance.json`. Prices are conservative against the referenced recipe costs; modified economy inputs require rebalancing.

## Build and validation

With full JDK 25: `./gradlew build` (Windows: `gradlew.bat build`). Regenerate with `python tools/generate_grades.py` (Pillow); validate using `python tools/validate_assets.py`. Model preview additionally needs NumPy and a font path.

The delivered JAR was directly compiled against official unobfuscated 26.2 classes and genuine dependencies because Loom's Unix socket probe is restricted in this environment. It loaded successfully on an actual 26.2 Fabric dedicated server. 144 server assertions, 3 pure unit tests and the 27-item asset/economy validation passed. No Minecraft classes or dependency libraries are bundled.

**Pre-release limitation:** graphical-client runtime, Mod Menu interaction and in-game model orientation/lighting have not been visually playtested. Client code compiles and the shipped geometry was rendered for inspection. `docs/model-preview.png` is a model-file render, not an in-game screenshot.

The temporary `src/smoke` test mod stops its server after testing; use only in a disposable local test world. Never install it on a normal server.
