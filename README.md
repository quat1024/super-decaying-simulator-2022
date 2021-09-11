# Super Decaying Simulator 2022

A port of the flower-decay and mana stats features from Botania Tweaks 1.12, now for Forge 1.16.

# Flower Decay

Edit `super-decaying-simulator-2022-common.toml`. You can:

* make any generating flower experience passive decay;
* set the decay time per-flower.

The Hydroangeas can't be made *not* passive, and its decay time can't be set longer than the default of 72000 ticks :), but everything else is fair game.

Addon flowers get piled together under `other_flowers`. This is a hack; get in touch if you want something better.

# Mana Statistics

In the background, this mod transparently tracks the total amount of mana generated in your world, breaking it down per-flower.

You can view statistics by pressing the "View Statistics" key (not bound to anything by default, it's under the "Botania" category in the key bindings menu), running `/super-decaying-simulator-2022 stats gui`, or print them using `/super-decaying-simulator-2022 stats show`. (The success count of `show` is the total amount of generated mana in the world.)

Level 2 ops can reset statistics using `/super-decaying-simulator-2022 stats reset`. The stats are stored in `mana-generation-statistics.dat` in the Overworld's `data` folder.

# Stat Advancements

An advancement criterion is available for rewarding players when the amount of generated mana crosses a threshold. Maybe modpacks will find this handy!

* Advancements are per-player (ofc), but mana statistics are global and shared across the entire server.
  * Every player will earn the advancements at the same time, and a player logging in after a mana goal has been reached will immediately earn the advancement.
* Advancement criterions are checked every 5 seconds, to avoid bogging down the server too much. This is configurable.

A worked example datapack is in the `example_datapack` folder of this repo.

The criterion is named `super-decaying-simulator-2022:generated_mana` (note dashes vs. underscore). Its conditions:

* `flower` - optional string
  * Which flower type to track.
  * Valid values are the same as the keys in the config file, `"hydroangeas"`, `shulk_me_not`, etc.
  * If this field is missing, the advancement will track the total amount of mana produced across all flowers.
* `mana` - positive long integer
  * The minimum amount of mana required for this advancement criteria to pass, inclusive.
  * You may specify `pools`, and it will multiply the number by 1 million. `"pools": 5` is the same as `"mana": 5000000`.
    * You may specify both, if you really want. They will get added together.

Examples:

* `{"flower": "shulk_me_not", "mana": 5}` would pass the first time a Shulk Me Not is used;
* `{"flower": "hydroangeas", "pools": 20}` would pass when 20 million mana is generated using Hydroangei;
* `{"pools": 100}` would pass when 100 million mana is generated across all flowers.

Migrating from Botania Tweaks:

* The criterion ID is `super-decaying-simulator-2022:generated_mana`, instead of `botania_tweaks:flower_generated_mana` and `botania_tweaks:total_generated_mana`.
* Total mana and per-flower mana use the same advancement criterion.
* The criterion parameters (`flower` and `pools`) are the same.
* `mana` field is available, instead of just `pools`.

# I'm a modder writing a Botania addon, and I would like to add compat with my flowers. What should I do?

I tried to provide an event for this so you could implement it in a Forgey way on your end, but god i hate Forge's red tape lol. I can't fire the event on the dang event bus, and I fear mod construction is too early for inter-mod stuff but creating the config file in common setup is too late. If you know how to do it, get in touch.

For now, patch the tail of `SuperDecayingSimulator2022#registerGeneratingFlowers` using Mixin or ASM or whatever. Create new `GeneratingFlowerType` instances and hand them off to the `Consumer`. You must provide:

* a name, to be used in the config file
* an `IItemProvider` (block or item), used as the icon in the stats GUI
* the `TileEntityType`(s) that belong to your flower.
  * Usually there will be only one `TileEntityType` per flower, but hey, if you added a petite version of your generating flower for some reason, who am I to judge.
* Call `.passive()` if your flower is already a passive flower.