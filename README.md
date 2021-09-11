# Super Decaying Simulator 2022

## Flower Decay

Edit `super-decaying-simulator-2022-common.toml`. You can:

* make any generating flower experience passive decay;
* set the decay time per-flower.

Note that the Hydroangeas can't be made a *not* passive flower, and its decay time can't be set longer than the default of 72000 ticks :) Everything else is fair game.

## Mana Statistics

In the background, this mod transparently tracks the total amount of mana generated in your world, as well as a breakdown per-flower.

Statistics can be examined using the `/super-decaying-simulator-2022 stats` command, or by pressing the "View Statistics" key. (The key is not bound to anything by default.)

## I'm a modder writing a Botania addon and I would like to add compat with my flowers. What should I do?

I tried to provide an event for this but god i hate Forge's bullshit lol

Mixin to the end of `SuperDecayingSimulator2022#registerGeneratingFlowers`, or get in touch if you know how to make an event I can fire at other mods during the very-very-early loading process