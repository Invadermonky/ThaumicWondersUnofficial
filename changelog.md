# Changelog

## v2.0.0
------
### Added
- Added config options for Catalyst Stone durability, enchantabliity, and default flux chance
- Added config options for Cleansing Charm flux removal amount and processing time
- Added config options for Flying Carpet Vis storage/consumption and max flight speed
- Added config options for Meaty Orb cost and duration
- Added config options for Night Vision Goggles Vis storage/consumption and adaptive setting
- Added config options for Void Beacon base essentia cost
- Added Crafttweaker and GroovyScript methods for Catalyzation Chamber recipes
- Added Crafttweaker and GroovyScript methods for Meaty Orb entries
- Added Crafttweaker and GroovyScript methods Primordial Accretion Chamber recipes
- Added Crafttweaker and GroovyScript methods Void Beacon entries
- Added Expanded Arcanum compat for Catalyzation Chamber
- Added tooltip to Void Fortress Helm indicating it includes Goggles of Revealing and Sipping Fiend mask
- Added TOP tooltip for Flux Capacitor flux storage

### Fixed
- Fixed rare Disjunction Cloth crafting table recipe crash
- Fixed mod registering several fake blocks
- Fixed several dupe bugs [#77](https://github.com/daedalus4096/ThaumicWonders/pull/77)
- Fixed Flux Capacitor losing flux charge when broken by non-players
- Fixed Catalization Chamber voiding inventory when broken
- Fixed Primordial Accelerator voiding inventory when broken
- Fixed Primordial Accretion Chamber voiding inventory when broken
- Fixed Flying Carpet entity losing stored vis on world/chunk reload

### Changed
- Avatar of Corruption now uses a loot table to generate drops
- Catalyzation Chamber now uses recipe structure
- Cinderpearl Seed recipe now produces 1 Cinderpearl Seed Stalk. Each stalk can be used to plant 8 Cinderpearl plants before being consumed.
- Creative Essentia Jars now display full when filled with essentia
- Creative Essentia Jars no longer pollute the aura when emptied
- Meaty Orb now uses recipe entry structure to generate drops
- Night Vision Goggles now have an adaptive on/off based on what the player is looking at (configurable)
- Primordial Accretion Chamber now uses recipe structure
- Shimmerleaf Seed recipe now produces 1 Shimmerleaft Seed Bulb. Each bulb can be used to plant 8 Shimmerleaf plants before being consumed.
- Vishroom Spore recipe now produces 1 Vishroom Spore Cluster. Each spore custer can be used to plant 8 Vishroom plants before being consumed.
- Void Beacon now uses recipe entry structure to generate drops

## v1.8.4
------
* Reverted to Forge 14.23.5.2768

## v1.8.3
------
* Fixed a memory leak with Void Fortress Armor, thanks Aqua!

## v1.8.2
------
* Fixed a crash when using Panacea

## v1.8.1
------
* Coalescence Matrix has a new texture, thanks TechnoMysterio!
* Added Russian translations, thanks xRoBoTx!

## v1.8.0
------
* Added Flux Distiller, to clean out your capacitors
* Added Primordial Accelerator, to smash your Primordial Pearls to bits, for science!
* Added Primordial Accretion Chamber, to reconstitute your shattered pearls more efficiently
* Added Mystic Gardening, to cultivate your magical plant life
* Added Panacea, to cure what ails you
* Added Alkahest, to dissolve your excess stuff
* Added Lethe Water, for power at a price
* Added Coalescence Matrix, to test your mettle
* Set rarity for several items
* Fixed syncing bug when firing Primal Arrows from a Bone Bow
* Clarified dyeing process for the Flying Carpet
* Clarified how to place the Inspiration Engine for use with a Research Table
* Renamed Bone Bow to Bow of Bone
* Adjusted essentia costs for Void Beacon recipe

## v1.7.0
------
* Added Night-Vision Goggles, for a different kind of revealing
* Added Void Beacon, to conjure something from (almost) nothing
* Added Cleansing Charm, to help you with your Warp problem
* Added Bone Bow, revisiting a blast from the past
* Added Primal Arrows, to spice up your archery
* Flying Carpets can now be dyed different colors
* Fixed crafting recipe bug with Bone Bow and Hexamite

## v1.6.0
------
* Added Meaty Orb, for when the weather forecast should be "cloudy with a chance of meatballs"
* Added Structure Diviner, to track down interesting spots in the world
* Fixed a mod compatibility bug in ore detection logic
* Fixed an ore-matching issue with the Ore Diviner
* Fixed item duplication bug with Catalyzation Chamber
* Render tooltips in Catalyzation Chamber GUI
* Added visual effect for Void Portal destabilization
* Fixed Z-clipping issue with the Void Fortress Helmet and skins with hats
* Added star field effect to the business end of Dimensional Rippers

## v1.5.0
------
* Added Void Fortress Armor, the pinnacle of eldritch protection
* Added Meteorb, for manipulating meteorological phenomena
* Added Ore Diviner, to help you locate hard-to-find ores
* Dimensional Rippers can now be overcharged with additional essentia to create bigger rifts
* Dimensional Rippers can now enlarge existing flux rifts
* Portal Generators now show their last stability gain and loss
* Reduced Portal Generator load on Stabilizers; same stability gain over time, less flux from the Stabilizer
* Catalyst stones can now be enchanted with Unbreaking to improve their longevity
* Fixed a blockstate mod interaction bug with the Catalyzation Chamber

## v1.4.0
------
* Added Flux Capacitor, to help you manage your flux problem
* Added Transmuter's Stone, to allow your Catalyzation Chamber to turn one metal into another
* Added Alienist's Stone, for tripling your ore yields with the Catalyzation Chamber
* The Timewinder now generates significantly more flux, consumes vis charge, and incurs a 60-second cooldown on use
* Dimensional Rippers have a new model
* Dimensional Rippers now only connect to things (e.g. levers, essentia tubes) on their bottom face
* Dimensional Rippers now emit a particle effect when they find a matching, correctly-positioned ripper
* Portal Anchors and Portal Generators have new models
* Void Portals now destabilize over time; unstable portals will send you off-target and have other detrimental effects
* Fixed a bug preventing teleport sound when using void portals to travel long distances
* Portal Generators can now be disabled by applying a redstone signal
* Hexamite blast power reduced and vis/shard crafting cost increased
* Hexamite now damages the local aura when it affects open air instead of creating flux from destroyed blocks
* Fixed a bug where any filled universal bucket could be turned into eight quicksilver
* The Everburning Urn is now a standard fluid handler and can be drained with pipes
* Updated Thaumonomicon page's background image

## v1.3.0
------
* Added Void Portals, a means to teleport across space and dimensions
* Added Advanced Metal Purification, a more efficient way to double your ore
* Added Hexamite, for when you really need to ruin something's day
* Fixed crash bug when Primal Destroyer hunger fills

## v1.2.0
------
* Added Madness Engine, an essentia-powered source of eldritch inspiration
* Fixed another crash bug on server load

## v1.1.1
------
* Fixed crash bug on server load

## v1.1.0
------
* Added Timewinder, a device that lets you skip forward in time
* Added Inspiration Engine, an essentia-powered research aid
* The Primal Destroyer now truly hungers; keep it fed, or else
* Increased Primal Destroyer damage to compensate
* Reduced Flying Carpet speed and flight-time-per-vis.

## v1.0.0
------
* Added Everburning Urn, an infinite lava source
* Added Disjunction Cloth, which removes enchantments from items
* Added Creative Essentia Jar, an infinite essentia source only available in creative mode
* Added Dimensional Ripper, a means to create flux rifts
* Added Primal Destroyer, a powerful endgame weapon
* Added Flying Carpet, a flying vis-powered vehicle