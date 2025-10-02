# Terranite
Terranite is a folia compatible World Edit plugin copy. It is based on the original World Edit plugin, with a few tweaks. This repository is focussed on paper development. Same functionalities as the original folia variant.

Check published version for easy download:<br/>
https://modrinth.com/plugin/terranite

### Supported versions
- Folia 1.21.4
- Paper 1.21.4

### Commands
These commands are available in the following aliases:<br/>
-`//` <br/>
-`/s`<br/>
-`/selection`<br/>
-`/terra`<br/>
-`/terranite`

| Command                                  | Description                                                         |
|------------------------------------------|---------------------------------------------------------------------|
| `//wand`                                 | Gives the player a Terra wand to make selections.                   |
| `//bind`                                 | Binds an item to Terranite.                                         |
| `//unbind`                               | Unbinds an item to Terranite. WARNING: Removes all lore.            |
| `//clear`                                | Clears the positions set of a selection.                            |
| `//count <block> <...>`                  | Counts all (specified) blocks in a selection.                       |
| `//set <block> [#preview]`               | Sets all blocks in a selection to a new block.                      |
| `//fill <block>`                         | Fills all air blocks in a selection to a new block.                 |
| `//mask [block]`                         | Sets the mask of the //fill command (default: air)                  |
| `//center <block>`                       | Sets a block on the center of the selection.                        |
| `//break <block> <...>`                  | Breaks all (specified) blocks in a selection.                       |
| `//replace <block> <block>`              | Replaces all blocks in a selection to another block.                |
| `//replacenear <radius> <block> <block>` | Replaces all blocks in a radius around the player to another block. |
| `//pos <1-2> <x> <y> <z>`                | Sets position 1 or 2 of a selection to a specific coord.            |
| `//select <radius>`                      | Selects a specific radius around the player.                        |
| `//copy`                                 | Copies all blocks in a selection.                                   |
| `//cut`                                  | Cuts all blocks in a selection.                                     |
| `//paste`                                | Pasts a copied/cut selection.                                       |
| `//undo`                                 | Undoes the previous undo action done.                               |
| `//redo`                                 | Redoes the previous undo action done.                               |
| `//generate <shape> <block>`             | Generate a shape with a specific block.                             |
| `//teleport`                             | Teleports the player to the center of a selection.                  |
| `//apply`                                | Applies the preview of an action.                                   |
| `//cancel`                               | Cancels the preview of an action.                                   |
| `//move <direction> <amount>`            | Moves the selection to a certain direction.                         |
| `//shrink <direction> <amount>`          | Shrinks the selection to a certain direction.                       |
| `//extend <direction> <amount>`          | Extends the selection to a certain direction.                       |
| `//schematic <delete/list/save>`         | All commands related to schematics.                                 |
| `//config <reload/info>`                 | Reloads the config file. (Admin)                                    |


### Config

| Var                                      | Default                            | Description                                                                                                                                                                    |
|------------------------------------------|------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `lockdown`                               | false                              | Blocks the usage of Terranite for all users, even users with the `terranite.admin` permnode.                                                                                   |
| `exclude_blocked_blocks`                 | false                              | When true, all blocks will be blocked, except for those listed in blocked_blocks.                                                                                              |
| `exclude_notified_blocks`                | false                              | When true, all blocks will be notified, except for those listed in notified_blocks.                                                                                            |
| `blocked_blocks`                         | [...]                              | List of blocks you can't set, fill, ...                                                                                                                                        |
| `notified_blocks`                        | [...]                              | List of blocks that give a notification to the console when being used.                                                                                                        |
| `log_notifications`                      | false                              | If the notifications should be logged to server files.                                                                                                                         |
| `safe_delete_schematics`                 | true                               | If everyone should be able to delete any saved schematic. On true, can only the player who saved the schematic, delete it. `terranite.admin` permission bypasses this setting. |
| `max_selection_size`                     | 500_000                            | Maximum volume of blocks a selection can have. Used to limit excessive lag. -1 for unlimited size.                                                                             |
| `command_cooldown`                       | 0                                  | Cooldown of command usage. (Not sure why this would be useful)                                                                                                                 |
| `clear_selection_after_command`          | false                              | If the selection should clear itself after a command is executed.                                                                                                              |
| `hide_selection_when_holding_other_item` | true                               | If the selection should hide when the player holds a different item.                                                                                                           |
| `wand_material`                          | FEATHER                            | Material of the wand. Only 2 basic materials supported. Use one of these for maximum security.                                                                                 |
| `wand_name`                              | Terra Wand                         | Name of the wand.                                                                                                                                                              |
| `wand_description`                       | This is a tool used for Terranite. | Description of the wand.                                                                                                                                                       |
| `wand_color`                             | GOLD                               | Color of the wand.                                                                                                                                                             |
| `allow_multiple_wands`                   | false                              | If a player should be able to summon multiple wands.                                                                                                                           |
| `delete_wand_on_drop`                    | true                               | If the wand should delete itself when dropped.                                                                                                                                 |
| `delete_wand_on_store`                   | true                               | If the wand should delete itself when the player stores it in a container.                                                                                                     |
| `delete_wand_on_pickup`                  | true                               | If the wand should delete itself when any player picks it up.                                                                                                                  |
| `delete_wand_on_shot`                    | true                               | If the wand should delete itself when shot with a (cross)bow. (ARROW material security)                                                                                        |
| `select_effect_color`                    | FUCHSIA                            | Effect color of selecting.                                                                                                                                                     |
| `outline_effect_color`                   | FUCHSIA                            | Effect color of the outline.                                                                                                                                                   |
| `outline_effect_speed`                   | 4                                  | How fast the outline should tick - Between 1 and 4 (1 = slowest, 4 = fastest).                                                                                                 |
| `play_sound`                             | false                              | If selecting should make sound.                                                                                                                                                |
| `select_sound`                           | BLOCK_NOTE_BLOCK_PLING             | Sound of selecting.                                                                                                                                                            |

### Permissions

| Permission                        | Default | Description                                                                         |
|-----------------------------------|---------|-------------------------------------------------------------------------------------|
| `terranite.use`                   | OP      | Basic usage of Terranite.                                                           |
| `terranite.admin`                 | OP      | List of blocks you can't set, fill, ...                                             |
| `terranite.teleport`              | OP      | Allows player to use the //teleport command. (included in `terranite.admin`)        |
| `terranite.bind`                  | OP      | Allows player to use the //bind & //unbind command. (included in `terranite.admin`) |
| `terranite.exempt.*`              | OP      | Covers all subpermissions.                                                          |
| `terranite.exempt.selection`      | OP      | Exempt from the limit of blocks you can select at once.                             |
| `terranite.exempt.blockedBlocks`  | OP      | Exempt from not be able to use blocked blocks.                                      |
| `terranite.exempt.notifiedBlocks` | OP      | Exempt from being notified to console.                                              |
| `terranite.exempt.cooldown`       | OP      | Exempt from the cooldown of commands.                                               |

### Full config file
This is the default file, for those who are on a previous version and need the most recent one.

```yaml
# +-------------------------------------------------------------+
# |                                                             |
# |                         Terranite                           |
# |    A folia compatible world editing plugin for Minecraft    |
# |                                                             |
# +-------------------------------------------------------------+

# +-------------------------------------------------------------+
# |                     Security settings                       |
# +-------------------------------------------------------------+

# Lockdown mode of the wand.
# When true, no one can use the wand, including players with terranite.admin permission.
lockdown: false

# Exclude listed blocks from blocked_blocks.
# When true, all blocks will be blocked, except for those listed in blocked_blocks.
exclude_blocked_blocks: false

# Exclude listed blocks from notified_blocks.
# When true, all blocks will be notified, except for those listed in notified_blocks.
exclude_notified_blocks: false

# Blocks that cannot be pasted, set, filled, replaced, etc.
blocked_blocks:
  - bedrock
  - barrier
  - end_portal_frame
  - command_block
  - repeating_command_block
  - chain_command_block
  - structure_block
  - structure_void
  - spawner
  - dragon_egg
  - dragon_head

# Blocks that give a notification to the console when used.
notified_blocks:
  - bedrock
  - barrier
  - end_portal_frame
  - command_block
  - repeating_command_block
  - chain_command_block
  - structure_block
  - structure_void
  - spawner
  - dragon_egg
  - dragon_head

# Log notifications to a file.
log_notifications: false

# If everyone should be able to delete any saved schematic.
# If true, only the player who saved the schematic can delete it.
# terranite.admin permission bypasses this setting.
safe_delete_schematics: true

# +-------------------------------------------------------------+
# |                     Selection settings                      |
# +-------------------------------------------------------------+

# Maximum amount of blocks that can be selected at once. -1 for no limit.
max_selection_size: 500_000

# Command cooldown in seconds.
command_cooldown: 0

# If the selection should be cleared after a command is executed.
clear_selection_after_command: false

# If the selection should hide when the player holds a different item.
hide_selection_when_holding_other_item: true

# +-------------------------------------------------------------+
# |                       Wand settings                         |
# +-------------------------------------------------------------+

# Material of the wand - Default: ARROW.
# Supported materials: ARROW, FEATHER
wand_material: FEATHER

# Name of the wand
wand_name: "Terra Wand"

# Description of the wand
# null for no description.
wand_description: "This is a tool used for Terranite."

# Color of the wand - Default: GOLD.
# Full list: https://jd.advntr.dev/api/latest/net/kyori/adventure/text/format/NamedTextColor.html
wand_color: GOLD

# If a player should be able to summon multiple wands.
allow_multiple_wands: false

# If the wand should delete itself when dropped.
delete_wand_on_drop: true

# If the wand should delete itself when the player stores it in a container.
delete_wand_on_store: true

# If the wand should delete itself when any player picks it up.
delete_wand_on_pickup: true

# If the wand should delete itself when shot with a (cross)bow.
delete_wand_on_shot: true

# Color of selection - Default: FUCHSIA.
select_effect_color: FUCHSIA

# Color of constant outline - Default: FUCHSIA.
outline_effect_color: FUCHSIA

# How fast the outline should tick - Between 1 and 4 (1 = slowest, 4 = fastest).
outline_effect_speed: 4

# Play sound when a selection is made.
play_sound: false

# Sound of selection - Default: BLOCK_NOTE_BLOCK_PLING.
# Full list: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Sound.html
select_sound: BLOCK_NOTE_BLOCK_PLING
```