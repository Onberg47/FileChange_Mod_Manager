# Settings
Settings are important!

### Normal settings
Every user is expected to atleast look at these settings. Ideally you should set them!
- Default Mod storage directory: The auto-populated path when adding a new game. This is if you want to stroe your mods in a central location. (Recommended)<br>
For example if you wanted to do:
```text
Documents/
    Mods/
        cyberpunk/
        fallout-4/
        ghost-recon/
```
Then make this setting `Documents/Mods` so when you add a new game you can just add the game-specific direcotry.

- Trash warning:
    - (Full) Will warn you with a popup dialog prompting to clean when your trash size exceeds your limit.
    - (Light) Only a warning in the console will be logged when size is exceeded.
    - (Off) No warning **or** checks on trash. (default)

- Normalise Mods by Group:
    - (On) Recommended. This allows duplicate load order values. It assumes that duplicates are *grouped* and simply ensures that group numbers are sequential. [1,1,1,4,8,8] -> [1,1,1,2,3,3]<br>
    Drag-to-order will change to set load order to match where it was dropped. Will only normalise on commit.
    - (Off) This will normalise all Mods sequentially, ensuring no duplicate load orders and are counting from 1 up with no missing numbers. [0,4,5,12,100] -> [1,2,3,4,5]<br>
    This works best with drag-to-order instead of the load spinner. Will only normalise on commit and drag.

    What works best will depend on the specific game but it is recommended to set the default load order of mods by group and use groups when dealing with large-scale mod deployments that need a layered approach.
    For loose/unrelated mods, rather use sequential.

### Advanded settings
Changing these is for power users. It won't break **everything** but you must understand **why** you want to change it.
- Deployment Manager direcotry: This is where, inside the Game installation path, the Mod Manager will store all it's deployment files.
It won't break anything (expect all previous ModManager data in Games) Only set this if there is a conflict! (If a direcotry called `.mod_manager` already exsists in your game!?)

#### Expert settings (do not touch!)
These will break the program if incorrect and are not for regualr users! Changing these is only for very rare cases where you would want to re-organise key-parts of the program itself.<br>
The only useful scenario I can think of, is to store the *temp* and/or *trash* direcories on the same **physical** storage devices as your games. (An idea with merit...)<br>
Due to this, if you are curious about these settings, read the technical documentation on the program first. This user-manual will not cover them.<br>
These settings may be moved off the GUI in future.


## Trash managment
In the settings page is also the Trash manager. This is the only source of all true delete operations, if its deleted here, its gone!

### The manager offers:
- Displaying how many Megabytes of *actual* disk-space the trash currently is using.
- A percentage read-out of how close the trash space used is to your set limit.
- Setting a limit on the overall disk-size of trash.
- Setting a limit on the maximun age (days old) files are to qualify for cleaning.
- The ability to **Clean** or **Empty** trash.
- A shortcut to open the trash directory in the default direcotry application (file manager usually)

### Cleaning your trash
Cleaning is **not** done automatically to ensure data is retained unless explicity deleted. <br>
When a clean is performed it will delete all files older than the specified number of days. If the total size still exceeds the limit, starting from the oldest, files will be deleted until the maximun size is reached.

### Emptying your trash
**The entire trash directory will simply be nuked.**