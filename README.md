# Flowstone

Flowstone is a mod that makes ores renewable resources.

In vanilla Minecraft, lava flowing over water always generates a stone block. With Flowstone, there is a configurable chance that it will generate an ore block.

By default, all eight ore blocks of any vanilla overworld (coal, copper, iron, gold, lapis, redstone, emerald, and diamond) can be generated with a 1% chance each.

![Flowstone Showcase](img/Flowstone_Showcase_1.gif)

(In the GIF, each ore block has a 100% chance for example purposes.)

## Configuration

If one isn't present, Flowstone creates a default `flowstone.json` configuration file inside the `config` folder, which is under the Minecraft folder. The file looks like the following snippet.

<details>
<summary>Default Configuration File</summary>

```json
{
  "enabled": true,
  "recipes": [
    {
      "block": "minecraft:coal_ore",
      "chance": 0.01
    },
    {
      "block": "minecraft:copper_ore",
      "chance": 0.01
    },
    {
      "block": "minecraft:iron_ore",
      "chance": 0.01
    },
    {
      "block": "minecraft:gold_ore",
      "chance": 0.01
    },
    {
      "block": "minecraft:lapis_ore",
      "chance": 0.01
    },
    {
      "block": "minecraft:redstone_ore",
      "chance": 0.01
    },
    {
      "block": "minecraft:emerald_ore",
      "chance": 0.01
    },
    {
      "block": "minecraft:diamond_ore",
      "chance": 0.01
    }
  ]
}
```

</details>

You can add new blocks and relative chances to the recipes list, maybe more ore blocks from other mods, or perhaps you want to generate andesite, diorite, and granite, too.

## Planned Features

I also want to mod cobblestone and basalt generators, but I can't figure out how. If one of you fellow modders have some clue, let me know.
