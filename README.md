# Terranite
Terranite is a folia compatible World Edit plugin copy. It is based on the original World Edit plugin, with a few tweaks.

Check published version for easy download:<br/>
https://modrinth.com/plugin/terranite

### Supported versions
- Folia 1.21.4
- Paper 1.21.4

### Commands
These commands are available in the following aliases:<br/>
-`/s`<br/>
-`/selection`<br/>
-`/terra`<br/>
-`/terranite`

| Command                       | Description                                              |
|-------------------------------|----------------------------------------------------------|
| `/s wand`                     | Gives the player a Terra wand to make selections.        |
| `/s clear`                    | Clears the positions set of a selection.                 |
| `/s count <block>`            | Counts all (specified) blocks in a selection.            |
| `/s set <block>`              | Sets all blocks in a selection to a new block.           |
| `/s fill <block>`             | Fills all air blocks in a selection to a new block.      |
| `/s break <block>`            | Breaks all (specified) blocks in a selection.            |
| `/s replace <block> <block>`  | Replaces all blocks in a selection to another block.     |
| `/s pos <1-2> <x> <y> <z>`    | Sets position 1 or 2 of a selection to a specific coord. |
| `/s select <radius>`          | Selects a specific radius around the player.             |
| `/s copy`                     | Copies all blocks in a selection.                        |
| `/s cut`                      | Cuts all blocks in a selection.                          |
| `/s paste`                    | Pasts a copied/cut selection.                            |
| `/s undo`                     | Undo's the previous action done.                         |
| `/s paste`                    | Redo's the previous undo action done.                    |
| `/s generate <shape> <block>` | Generate a shape with a specific block.                  |
| `/s reload`                   | Reloads the config file. (Admin)                         |


### Config

| Var                  | Default | Description                                                                                        |
|----------------------|---------|----------------------------------------------------------------------------------------------------|
| `max_selection_size` | 500_000 | Maximum volume of blocks a selection can have. Used to limit excessive lag. -1 for unlimited size. |
| `blocked_blocks`     |         | List of blocks you can't set, fill, ...                                                            |

### Permissions

| Permission        | Default | Description                             |
|-------------------|---------|-----------------------------------------|
| `terranite.use`   | OP      | Basic usage of Terranite.               |
| `terranite.admin` | OP      | Access advanced commands of Terranite.  |
