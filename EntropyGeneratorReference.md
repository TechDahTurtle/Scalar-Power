# Entropy Generator Reference
Quick reference for the Entropy Generator temperature map and generation formula.

## Temperature Map

### Hot Values
- Lava: `+150`
- Magma Block: `+120`
- Soul Fire: `+110`
- Fire: `+100`
- Soul Campfire: `+70`
- Campfire: `+60`
- Soul Lantern: `+30`
- Jack o'Lantern: `+25`
- Lantern: `+20`
- Torch: `+15`
- Wall Torch: `+15`

### Cold Values
- Water: `-15`
- Powder Snow: `-60`
- Snow Block: `-80`
- Ice: `-100`
- Packed Ice: `-120`
- Blue Ice: `-150`

Any block not listed above counts as `0`.

## Side Sampling
- The generator checks the block on its **left** and **right** sides based on the block's facing direction.
- Left value is shown in GUI as `L`, right value as `R`.
- Generation only happens when one side is hot (`> 0`) and the other side is cold (`< 0`).
- If the left side is negative and the right side is positive, flow is treated as left-to-right.

## Generation Formula
Given:  
- `left = left side temperature`
- `right = right side temperature`

Compute:

```text
validPair = (left > 0 and right < 0) or (left < 0 and right > 0)
if not validPair: SPU/t = 0

delta = abs(left - right)
combined = abs(left) + abs(right)
multiplier = 1.0 + (combined / 1000.0)
SPU/t = round(delta * multiplier)
```

If either side is `0`, or both sides are hot, or both sides are cold, generation is `0`.

### Example
- Left = Lava (`150`), Right = Blue Ice (`-150`)
- `delta = abs(150 - (-150)) = 300`
- `combined = 150 + 150 = 300`
- `multiplier = 1.3`
- `SPU/t = round(300 * 1.3) = 390`

