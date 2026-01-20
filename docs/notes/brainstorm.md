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

---
---

Read more on handling file paths and listing directory contents here: https://www.javaspring.net/blog/file-java-path/

```java

/**
 * Example of how I would normally do this using an Array and for each.
 * @snippet
 */
List<ModManifest> deployedMods = new ArrayList<ModManifest>();
File[] files = Path.of(game.getInstallPath(), MANIFEST_DIR).toFile().listFiles();
for (File i : files) {
    try {
        deployedMods.add(new ModManifest(ModIO.readModManifest(i)));
    } catch (Exception e) {
        System.err.println("\t\t❌ No exsisting deployed mods found!");
        return false;
    }
} // for each File

/**
 * Using Stream to avoid having an array to store Files[] for reading.
 * @snippet
 */
try (Stream<Path> paths = Files.list(Path.of(game.getInstallPath(), MANIFEST_DIR))) {
    deployedMods = paths.map(path -> {
        try {
            return new ModManifest(ModIO.readModManifest(path.toFile()));
        } catch (Exception e) {
            System.err.println("\t\t❌ No existing deployed mods found!");
            return null;
        }
    }).filter(Objects::nonNull).toList();
} catch (Exception e) {
    System.err.println("\t\t❌ Error reading manifest directory!");
    return false;
}
```

---
---

Ideas for how to rollback file versions when removing a mod:

# Option 1.
## Part 1
When a mod is deployed, a file-conflict is found and the new mod is going to overwrite:
if the file does NOT belong to another mod:
true -> create a backup of that file.

The idea is only the original game files need backups because I'm just going to accept using the Mod Storage directory for retrieving mod Files. I'll do Hash checks so if the file in storage no longer is what the deployed version of that mod expects then it'll know.

So I'm thinking of storing the backups in either: `game_root/.backups/` or with my Mod Manifests as: `game_root/.mod_manifests/backup_files/`
Then with the backups I plan to only store them as files (not with per-structed directories like the mods.) and a `manifest.json` that will store data about each file, reusing my ModFile class. The Path will be stored, so I can restore it from that.

---

## Part 2
During the trash process, just before a ModFile is to be trashed...
- Check if the hash is the same as the manifest of the mod being trashed
-> If not the same leave it alone.
   <<- // stop further checks
-> If the same:
   Check the deployed mod Manifests for ALL other mod with the same file.
   - Find which of those mods has highest load priority and replace with that one's version of the file from Mod_Storage.
   <<- // stop further checks
If no deployed Mod has the file, Does the file have a backup in the `backup_manifest.json`?
true -> replace with that backup and update backup_manifest to stop storing the now current file.

---

# Option 2. (Maybe better?)

Part 1. Stays the same, we keep a backup of non-mod files that are overridden.

Part 2. 
Same hash check to verify if the current mod owns the file, if not we leave it alone.

We now Store a `file-change-log` This tracks whenever a file is written or removed by a mod in chronological order.
When we want to trash a mod, we fetch all logs for the file we're checking. If the last (most recent) write of that file is the same as the mod then we know we own it. (our hash check should ensure this is the case)
Then when we want to know what Mod's version of the file to apply, we simply grab the modID of the previous mod to write that file.
This hinges on trusting our Mods are deployed with correct loadOrder, a process I've already done.

---

Option 2, just seems like more work, keeping a JSON log (because we need to extract data), adding yet another JSON to manage ontop of the backup manifest. Whereas option 1 uses more computational head to determine through process.

---
---

Possible mod ID conflicts:

If we want to have a mod with varients and we assume we want to use the same base version for all its varients then we have this problem:

The origonal/parent > Mod A:
ParentMod; version 1.0 --> id `source-Parent-{hash of "1.0"}`

Mod A.1:
ParentMod_Varient1; version 1.0 --> id `source-Parent-{hash of "1.0"}`
Mod A.2:
ParentMod_Varient2; version 1.0 --> id `source-Parent-{hash of "1.0"}`
Mod A.3: (Lets assume this is a custom-mod make by the user)
ParentMod_Varient3; version 1.0 --> id `custom-Parent-{hash of "1.0"}`

These would all have idential IDs, since it makes sense that varients would be named with the parent name first.
And the Mod download sources would be idential if they are offical varients. If the user makes their own varient then it would make sense to rather change the source to something like "custom" but is on the user to do that.
It also makes sense that varients would refer to the version of the base mod they are from.

So instead we could id lie this:
current ID format: `{source}-{first 6 chars of name}-{version hash}`
revised: `{first 6 of name}-{name hash}-{version hash}`