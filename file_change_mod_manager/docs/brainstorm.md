Main program:

- Display what mods are installed.
- Allow the addition and removal of specific mods.

- Easily update mods: (streamlined clean addition)
    * Select a mod to update
    * Provide the new mod contents/files
    * preserves the Mod Name and removes the previous version's data automatically.

- Repack mods into a structured archive for ease of reuse.
    Mod pack:
    ./ModFile.txt
    ./#data/..
        contents

- Print out Mod list snapshots (for debugging, makes it easy to roll back to good mod setups)
- Warns when changes have been made after last build (prompts to compile game paks) //optional, most games do not require re-compiling

---

Game Data:
name : //user-facing name
game_directory_path : "C:\Program Files (x86)\Steam\steamapps\common\Ghost Recon Breakpoint\Extracted" // Path to where the mods are placed in the game (deployment location)
mod_directory_path : "" // Path to where the mod files are being stored outside the game (storage location)

icon // would be nice to have an icon for each game.
    Approach options:
        1. Have an "icon" folder in program directory and icons have the same name as the game for auto-detection
        2. path to the icon image is stored in the GameData.

---

Mod data:

file of mod:

    /// functional ///
    filename : STRING
    date_modified : DATE
    relative file path to each file contained in the mod : STRING[]
    load_order : int //incase of file conflicts, allowing manual control of the ordering is needed. (Many mods require being applied in order)

    /// optional / non-functional ///
    description : STRING
    download link : STRING

ModFile could be a JSON, sample data:
```json
{
    name : "Example Mod",
    date_modified : "13/12/2025 19:26",
    version : "1.1",
    description : "A few words about the mod",
    

    contents : [
        {
            dir : "/",
            contents : [
                {filename : "file_1"}
                ]
        },

        {
            dir : "/directory/",
            contents : [
                {filename : "file_1"},
                {filename : "file_2"}
                ]
        }
    ]
}
```

---

Storage options:

1. 
    Store a manifest per a game that stores first the `Game` data, followed by an Array of `Mod` objects.
    This would mean one JSON file per a game, which could also be copied as a complete snapshot of deployed mods of the game.
    Deployed mods are only listed in the file. This requires modifying a single file, add/removing `Mod` instances could become cumbersone? (Need more research on JSON usage)

2. 
    Store ModFiles seperate, one file per a Mod and have a single file for the `Game` data, storing all games in a single file.
    Only deployed mods have a ModFile, when a Mod is removed the file is deleted. When a Mod is deployed, a ModFile is created. Simplist way to handle JSON operations -as in, a file is never modified, files are simply discarded and created as needed.

## Important note: ##
Mods don't need to track what game they are associated with, as they are only deployed inside the game's directory and the game records where it's stored Mods are located.
