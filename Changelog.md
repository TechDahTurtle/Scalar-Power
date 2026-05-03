# Changelog

## 26.1.1-1.0.0_beta
- Initial beta release for Minecraft 26.1.1.

## 26.1.1-1.0.1_beta
- Fixed broken tags:
  - `c\tags\item\ingots\redium.json`
  - `minecraft\tags\block\mineable\pickaxe.json`

## 26.1.1-1.0.2_beta
- Added Barometric Generator  
- Added Macerator (grinding with chance-based secondary output)  
  - Added Double Macerator (dual-lane upgraded maceration)  
  - Added Ore Chunks (intermediate product of maceration)
- Added Reinforced Fiber Glass Wires
- Cleaned up JEI Integration GUIs
- Moved to NeoForge 26.1.1.11-beta
- New Extractor Texture
- Reorganized Creative Tab
- Reorganized code base
- Started this Changelog.md file

## 26.1.1-1.0.3_beta
- Added Cobalt Block, Chunk, Dust, and Ingot
- Added Creative Battery
- Added Ender Alloy Block, Dust, and Ingot
- In GUI Textures for Multiple Outputs combined the output into one slot
- Nerfed the cold inputs for the Entropy Generator, added Cobalt Block and Ender Alloy Block as new top-tier cold inputs (for balance)
- New Barometric Generator Texture
- New Grinder Recipes: Blaze Powder from Blaze Rod, Bonemeal from Bone, Breeze Charge from Breeze Rod, and String from Wool
- Started work on [Wiki](https://scalarstudios.site/wikis/scalar_power.html)
- Updated README.md

## 26.1.2-1.0.4_beta
- Ported to 26.1.2

## 26.1.2-1.0.5_beta
- Added Ender Battery
- Added Geothermal Generator
- Added Redstone Clock
- Added Water Mill Generator
- Change Machine Frame Models + Texture to be Block instead of Isometric projection of a block
  - Also applied for Steel Machine Frame
- Renamed Glass Fiber wires to Fiber Glass Wires
- Updated NeoForge version to 26.1.2.7-beta

## 26.1.2-1.0.6_beta
- Added Device Frame
- Added Freezer machine (Lava → Obsidian, Water → Ice)
- Added Grinding recipe to turn Ice into Snowballs
- Added Infinite Water Source Block
- Added Liquifier machine with JEI recipes (Cobblestone, Stone, Basalt, Netherrack, and Magma Block into Lava)
- Added Netherite Scrap recipe in Macerator (more efficient than Grinder version)
- Added Wrench which cam be used to set the behavior of wires per side
- Changed Redstone Clock to be off 16 ticks, on for 4; instead of off 19 ticks, on for 1 
- Converted several literal components to translatable text components
- Fixed broken Macerator container translation
- General code cleanup and reorganization
- Swapped Fluid in GUIs to be handled by a TankRenderer instead of a Gradient
- Updated Redstone Clock texture and recipe to use Device Frame
- Updated Wire Textures

## 26.1.2-1.0.7_beta
- Added Charger Machine
- Added Cobalt Tool Suite (Powered Tools)
- Added Copper Pipe as a fluid transport block (supports vanilla and modded fluids)
- Added Steel Rod (Crafting Component)
- Added wrench side-configuration support for transport blocks (pipes and wires)
- Converted Synthetic Resin to Resin Pellet, 4 Resin Pellets can be crafted into 1 Vanilla Resin (better for balance)
- Updated Cobalt Textures
- Updated NeoForge version to 26.1.2.36-beta (most current, should get us past the "breaking changes")
- Refactored "wire" to "cable" because what we have more closely resembles a cable than a wire
- Removed code for scrapped features

## 26.1.2-1.0.8_beta (Upcoming)

## Planned Features
- Better SPU UI (not drawn in GUI)
- Crafting Recipe Revamp
- Finish Wiki
- Full Resource Sets (Less for us, more for cross-mod compat)
- Keep NeoForge up to date
- Possible Armor Suite
- README.md Update
- Texture Name and Model Cleanup
- Tooltips