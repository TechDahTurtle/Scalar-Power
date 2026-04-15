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

## 26.1.2-1.0.6_beta (Upcoming)
- Added Liquifier machine with JEI recipes (Cobblestone, Stone, Basalt, Netherrack, and Magma Block into Lava)
- General code cleanup and reorganization
- Swapped Lava in GUIs to be handled by a TankRenderer in LavaTankRenderer instead of a Gradient
  - This was extended to also work for Water (and theoretically other fluids if extended)
- Updated Wire Textures

## Planned Features
- Battery Sidedness (I/O Faces)
- Better SPU UI (not drawn in GUI)
- Crafting Recipe Revamp
- Finish Wiki
- Freezer Machine (Water → Ice → Packed Ice → Blue Ice)
- Infinite Water Source Block
- Texture Name and Model Cleanup
- Tooltips
- Wrench / Pipe IO