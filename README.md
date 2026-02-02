# FileChange_ModManager (FCMM)
A Java program for managing Mods that require manual integration with the game files instead of in a dedicated Mod directory but works for both cases.

* **Want to use my program? Then skim-read this first: [User-manual](docs/Manual.md)**<br>

* **Got any general questions? Check this first: [FAQ](docs/FAQ.md)**

<details>
<summary>A personal side note:</summary>
The goal is to create a practical project for both real-world usage but also as practice and a showcase of my Java "know-how". This is why I opted to not use a virtual file system (VFS) (how platforms such as Vortex from Nexus works) as I wanted the added challenge of manual file integrity management, whereas with a VFS there would be no need to worry about file conflicts or even moving files.
</details>


<br>

---
### Overview

* #### Key features
    - Manages multiple games.
    - Multi-platform (Windows and Linux)

* #### 🔒 Safe by Design
    - Game deployment data is self-enclosed
    - All operations happen in temporary directories first
    - Only finalises after successful completion
    - Automatic backups of original game files
    - Nothing touches your game until everything is verified
    - Complete transaction rollback on failure possible

* #### 📊 Smart File Management
    - File hash verification to avoid unnecessary copies
    - Load-order aware conflict resolution
    - Mods can be reordered without redeploying everything
    - Batch operations with minimal I/O

* #### 🎨 User Experience
    - Clean CLI with visual feedback
    - GUI with real-time console output
    - Comprehensive logging


<br>

---
# Features
### General: 
- Safe by design. Operations all take place within a temporary directory. Only when all operations have been successful are final moves made and temp/ cleaned. This is also to ensure working files are not externally altered during operation. Should an error occur at any stage no modifications will have taken place on the game files, even in delete operations.
- Thorough error checking and comprehensive logging.
  - Detects missing files when attempting to deploy Mods and will halt.
  - Verify files Hashes match to detect changes and avoid unnecessary file system operations.
- In case of final write failures, data stored in temp is recoverable (temp/ cleaning only takes place when no errors occur)
- Visual feedback (Console) Actions are carefully logged, allowing exact failure pinpointing.
- Trash utility: Manage the max size and number of days old trash files can be with automatic warnings and prompts to clean or empty trash files.

### Mods: 
- Track, add, remove and update Mods deployed into a game.
- Mods can be deployed individually to a game while automatically backing up game files and tracking mod-overrides.
- Mods can be re-deployed individually with different load orders, fully handling new load priority **without** re-building from scratch.
- Individual mods can be removed (safely trashed) from the game restoring original files if needed.
- Keep meta data for each mod:
  - Display name.
  - Description.
  - Version number.
  - Download information. Store a download URL and tag the download source (eg: Nexus, ModDB, Steam, custom...)
  - Add tags to mods for GUI sorting and filtering.
- Intuative updating. Mods can be updated easily at anytime, handling re-deployment automatically.
  Updates can provide new files or the exsisting files will be re-compiled.
  For modders, this makes tweaking/updating mods on the go for testing fast and painless. See the [FAQ](docs/FAQ.md) for more.

### Games: 
- Stores a current GameState with the game, all data needed to track and remove deployed Mods is stored within the game directory.
- Track multiple Games.
- Flexible Mod management, deployment directory and Mod file storage are customisation per a game. This provides support for games with unique mod file locations.
- Store non-installed mods in a dedicated directory. Stored mods are ready to be deployed or can be packed. (TODO: archive file contents but leave manifest for ease of access)

### GameStates:
- Keep snapshots of deployed mod lists.
- Design, record and deploy GameStates. Allows making major changes quickly, automating the re-building process.

### Future:
- For my planned updates/features see: [TODO](docs/TODO.md)


<br>

---
# Interfaces

### GUI
- Tile view of games with icons.
- Manage Mods with a Card design to display mod info clearly.
- Interactive way to reorder mods by dragging mod tiles.
- Enable/disable mods by dragging them to and from the deployed/stored containers.
- Download link for mod are a clickable hyper-link.
- All file/directory input fields allow Drag-and-Drop.
- Write changes to a temp GameState or pick from saved profiles. Then Apply the GameState when ready.

### CLI Usage:
The CLI offers an interactive interface to perform all the core operations for managing Games and Mods.
While this is fully capable, it lacks the bulk-operation (temporary GameState) handling that the GUI offers, simply because it would be too tedious for a CLI.

**See full commands here: [CLI Commands](docs/CLI_Commands.md)**

<br>

---
# Behind the scenes...

In the docs for this project, I have created diagrams to fully explain the logical steps taken for various key methods and architecture designs.<br>

Mods are split into two types: Regular Mods have less fields to be more lightweight for easier storage and more efficient usage when the GUI needs to fetch Mod data to display but still needs the Mod_Id for functioning.
ModManifests are a child Mod that has the additional functionality of tracking Files, which themselves are stored as ModFile instances, storing integrity data.

### File integrity and order of operations:

Operations need to track file overrides, backups, mod-ownership, and load order.
- A `Mod Manifest` which tracks what files and hashes of those file a Mod has.
- Next, a `File Lineage` for each file deployed in the game is kept. This is a Stack of file-owners and the hashes each owner expects. (Stack is order with "highest load priority" / "current owner" on top)

With these, when a new mod is deployed and a file already exists we can determine who owns the file and how to respond with these questions:
1. Does the file have a Lineage?
- Y. Then another mod owns it.
- N. Then we are the first mod to try override it, therefore it must be a game-file. Back it up and list the GAME as the first owner.

2. What is the load order of the current owner against the current mod being operated on?
- If current is lower: It has not got priority and must insert itself into the stack after all higher priority entries. This now **Wants to own** the file.
- If current is higher: It has priority and can override the file and push itself as the new top owner on the stack. This now **Owns** the file.

**Read more on the technical aspects in: [Technical](docs/Technical.md)**