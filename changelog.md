# Changelog
## v2.1.4
-----
### Changed
- Updated primal destroyer HUD to match new item texture
- Improved Creative Essentia Jar textures
- Improved Empowered Bow of Bone pull animation
- Improved Primal Arrow textures
### Fixed
- Fixed Catalyzation Chamber destroying unbreakable catalyst items
- Fixed rare issue where Void Portal would remain if Portal Anchor was destroyed
- Fixed Void Beacon playing activate and deactivate sounds when beacon structure was not formed
 
## v2.1.3
------
### Added
- Added new Avatar of Corruption ambient and death sound effects (courtesy of IcarussOne)
- Added Primal Destroyer sound effect when it bites the player (courtesy of IcarussOne)
- Added Inspiration Engine ambient sound effect (courtesy of IcarussOne)
- Added Madness Engine ambient sound effect (courtesy of IcarussOne)
### Changed
- Improved Void Beacon Vis regeneration logic to prioritize low vis chunks (made bacon more smarter)
- Rewrote Control Seal: Research Assistant handling to be much more intuitive 
### Fixed
- Fixed Voidcaller Armor model desync when crouching
- Fixed crash when wearing Void Walker Boots when Thaumic Augmentation is not installed
- Fixed Infernal Hedge and End Hedge duplication compatible recipes

## v2.1.2
------
### Fixed
- Fixed an issue that caused Eldritch Cluster aspects to not register correctly
- Fixed a serialization issue with the Portal Linker, Ore Diviner, and Portal Anchor. Existing Portal Anchors and Linkers will need to be reset after updating.
- Fixed a crash with the Coalescence Matrix caused by Thaumcraft not checking for null values
- Fixed Portal Linker sometimes desyncing on servers
- Fixed crash when attacking with Voidflame weapon
- Fixed crash when ThaumTweaks or TC4 Research Port is loaded
- Fixed Infernal and End Hedge Alchemy duplication recipe compatibility issues (courtesy of Keleut66666)

## v2.1.1
------
### Changed
- Modified Research Assistant Seal behavior to be a little bit smarter
### Fixed
- Fixed a syntax error resulting in an invalid alchemy research being registered
- Fixed server disconnect issue when using the Night Vision Goggles

## v2.1.0
------
### Added
- Added Void Beacon sound effects when it activates, deactivates and is running (courtesy of IcarussOne)
- Added Erythurgy crucible deconstruction recipes ported from NCR (courtesy of IcarussOne)
- Added Erythurgy gemstone ore refining
- Added Shulker Shell duplication recipe to End Hedge Alchemy
- Added Control Seal: Research Assistant, a golem seal that restocks paper in the Research Table, refills Scribing Tools, and provides needed research materials

### Changed
- Added new Disjunction Cloth texture (courtesy of IcarussOne)
- Added new Lethe Water texture (courtesy of IcarussOne)
- Improved Primal Destroyer texture (courtesy of IcarussOne)
- Heximite now uses the same placement and break sounds as TNT
- Structure Diviner no longer points to previously explored structures
- Structure Diviner recipe uses a Rare Earth in the place of Prismarine Crystals
- Void Beacon effect has been overhauled. It no longer produces items, but instead uses Auram and Rift Power to regenerate vis and provides Warp Ward
- Cleansing Charm flux pollution has been changed to have a 5% chance every 3 seconds (~2-3 flux per removal operation)
- Increased Quartz Eldritch Cluster smelting yield from 3 quartz to 4 quartz
- Changed the location of several research nodes for improved progression
- Dimensional Ripper how has a comparator output based on the amount of stored essentia

### Removed
- Removed Primal Arrows research note about them not working in the Automated Crossbow as it has been fixed externally in [Thaumcraft Fix](https://www.curseforge.com/minecraft/mc-mods/thaumcraftfix)
- Removed Void Beacon CraftTweaker and GroovyScript methods as they are no longer being used

### Fixed
- Fixed portal anchor right-click sometimes placing ghost blocks
- Fixed orphaned tooltip on Portal Generator that sometimes appeared
- Fixed Empowered Bow of Bone spamming re-equip animation when being recharged by the Amulet of Vis
- Fixed rare Primal Arrow client desync issue
- Fixed a few typos in the GroovyScript generated examples and wiki
- Fixed Primordial Pearl crucible reconstitution recipe requiring invalid research

## v2.0.0
------
### New Features
- Added Initiate's Band of Cleansing, a debuff cleansing ring with a few unique properties
- Added Sharing Tome with a few more features (includes config disable)
- Added Vis Capacitor, a high capacity vis storage and discharge block
- Added Void Walker Boots, upgraded Traveller's Boots (includes config disable)
- Added Voidflame potion effect, dealing 1 true damage every second and reducing healing received by 50%
- Added Voidcaller Armor, a powerful new set of casting robes
- Added Primordial Siphon, a rift siphon that creates Primordial Grains

### Added
- Added config options to adjust several Avatar of Corruption effects
- Added config options for Catalyst Stone durability, enchantabliity, and default flux chance
- Added config options for Cleansing Charm flux removal amount and processing time
- Added config options for Flying Carpet Vis storage/consumption and max flight speed
- Added config options for Meaty Orb cost and duration
- Added config options for Night Vision Goggles Vis storage/consumption and adaptive setting
- Added config options for Void Beacon base essentia cost
- Added config option to adjust vitium cost for Dimension Ripper effect
- Added Crafttweaker and GroovyScript methods for Catalyzation Chamber recipes
- Added Crafttweaker and GroovyScript methods for Meaty Orb entries
- Added Crafttweaker and GroovyScript methods Void Beacon entries
- Added Expanded Arcanum compat for Catalyzation Chamber
- Added Infusion Enchantment for Voidflame effect
- Added JEI integration for Catalyzation Chamber
- Added JEI integration for Meaty Orb
- Added JER integration for Avatar of Corruption
- Added Potion Effect for Voidflame, dealing unavoidable damage over its duration
- Added tooltip to Void Fortress Helm indicating it includes Goggles of Revealing and Sipping Fiend mask
- Added TOP tooltip for Flux Capacitor flux storage
- Added Primordial Siphon, a rift siphon that creates Primordial Grains
- Added config options to adjust the amount of lava produced and the vis drain per Everburning Urn fill operation

### Removed
- Removed Primordial Accelerator and all associated blocks, this has been replaced by a the new Primordial Siphon
- Removed Primordial Accretion Chamber and all associated blocks, this has been replaced by a new Primordial Pearl growth Infusion recipe

### Fixed
- Fixed Catalization Chamber voiding inventory when broken
- Fixed Disjunction Cloth crafting table recipe crash
- Fixed Flux Capacitor losing flux charge when broken by non-players
- Fixed Flying Carpet interaction duplication bug
- Fixed Flying Carpet entity losing stored vis on world/chunk reload
- Fixed Primal Destroyer missing infusion enchants when pulled from JEI or creative menu
- Fixed several dupe bugs [#77](https://github.com/daedalus4096/ThaumicWonders/pull/77)
- Fixed Void Beacon sometimes generating error items

### Changed
- Adjusted the position of several GUI's for more consistent appearance
- Avatar of Corruption now uses a loot table to generate drops
- Avatar of Corruption summon is no longer instant and now has a fancy animation
- Avatar of Corruption drops more experience on death
- Avatar of Corruption summoning platform has been modified to reduce the number of Void Metal Blocks required
- Avatar of Corruption spawns equipped with a new set of powerful armor
- Avatar of Corruption spawns has reduced base armor to adjust for new armor set
- Catalyzation Chamber now uses recipe structure
- Cinderpearl Seed recipe now produces 1 Cinderpearl Seed Stalk. Each stalk can be used to plant 8 Cinderpearl plants before being consumed.
- Shimmerleaf Seed recipe now produces 1 Shimmerleaft Seed Bulb. Each bulb can be used to plant 8 Shimmerleaf plants before being consumed.
- Vishroom Spore recipe now produces 1 Vishroom Spore Cluster. Each spore custer can be used to plant 8 Vishroom plants before being consumed.
- Creative Essentia Jars now display full when filled with essentia
- Creative Essentia Jars no longer pollute the aura when emptied
- Everburning Urn can now have fluid extracted from its base
- Flux Distiller now has a tighter hitbox
- Meaty Orb now uses recipe entry structure to generate drops
- Night Vision Goggles now have an adaptive on/off based on what the player is looking at (configurable)
- Primal Destroyer has been rebalanced to deal physical and void damage. Void damage bypasses armor and can not be mitigated by armor or potions
- Primal Destroyer Voidflame effect is now an infusion enchant that applies the Voidflame potion effect
- Updated Gui's to use updated GLStateMapper
- Void Beacon now uses recipe entry structure to generate drops
- Void Fortress Armor has 1 additional armor on all pieces
- New texture for Alkahest fluid
- New texture for Panacea
- New texture for Primal Destroyer
- New texture for Quicksilver fluid
- New texture and model for Ore Diviner
- Cleaned up numerous unneeded model files
- Significantly reduced the crafting cost of the Alienist Stone
- Increased the crafting cost of the Bow of Bone to match New Crimson Revelations
- Bow of Bone has been renamed to Empowered Bow of Bone to differentiate it from New Crimson Revelations Bow of Bone
- Primal Arrow recipes will now craft New Crimson Revelation arrows when NCR is loaded
- Primal Arrows have had their effects modified to match New Crimson Revelations, research entries have been adjusted to match
- Void Fortress Armor has been renamed to Warped Fortress Armor (item id's remain unchanged)
- Nearly every research entry has been rewritten, courtesy of [IcarussOne](https://www.curseforge.com/members/icarussone/projects)
- Ore Diviner logic has been completely overhauled, making it much more user-friendly
- Alkahest Vat can now connect directly to pipes and hoppers, fixing a few automation issues
- Changed the layout of the Eldritch portion of the research tree (left side)
- Reduced the crafting cost of several recipes to better match the new research layout and Thaumic Augmentation's balance
- New model and texture for the Portal Anchor
- Improved Disjunction Cloth onCrafted logic

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