# Flowstone

Flowstone is a mod that makes ores renewable resources.

In vanilla Minecraft, lava flowing over water always generates a stone block. With Flowstone, there is a chance that it will generate a random ore block instead.

By default, all eight vanilla overworld ore blocks (coal, copper, iron, gold, lapis, redstone, emerald, and diamond) can be generated with a 1% chance each.

![Flowstone Showcase](img/Flowstone_Showcase_1.gif)

(In the GIF, each ore block has a 100% chance for example purposes.)

## Recipes

As per Flowstone 5.1 onwards, there is no longer a configuration file for configuring which blocks to generate and with what chances.

Instead, Flowstone loads such configurations from `/data/<mod id or datapack name>/flowstone/generators`, where each file represents a block that Flowstone may use to replace that Minecraft would normally generate.

<details>
<summary>Example: stone_to_coal_ore.json</summary>

```json
{
    "replace": "minecraft:stone", // The block to be replaced
    "with": "minecraft:coal_ore", // The block to replace the above one with
    "chance": 0.01 // The replacement chance
}
```

</details>

This way, you can easily extend Flowstone to, for instance, account for modded ores through datapacks.

## Planned Features

I also want to mod cobblestone and basalt generators, but I can't figure out how. If one of you fellow modders have some clue, let me know.
