Updated `ModFile.json` contents:

```json
{
  "name": "Example Mod",
  "id": "example-mod-unique-id",
  "version": "1.1.0",
  "author": "Mod Author",
  "description": "A few words about the mod",
  "installDate": "2024-12-13T19:26:00Z", // ISO 8601
  "loadOrder": 5,
  "gameId": "example_game_id", // Reference to game
  "files": [
    {
      "relativePath": "data/config.xml",
      "hash": "a1b2c3d4..." // SHA-256 of file contents
    },
    {
      "relativePath": "textures/weapon.dds",
      "hash": "e5f67890..."
    }
  ]
}
```

---

Mod ID planning:
  NexusMods format: `nexus-12345`
  Manual format: `custom-modname`
  Generated fallback: `gen-a1b2c3d4`

---

Program working direcotry:

```text
  .modmanager/
├── config.json                    # Global settings
├── games/                         # All games
│   └── ghost-recon-breakpoint/    # Game ID as directory name
│       ├── game.json              # Game config (your GameData)
│       ├── mods/                  # Active mods
│       │   ├── nexus-12345.json   # Mod file
│       │   └── custom-weapon.json
│       ├── disabled/              # Disabled mods (just move .json here)
│       ├── snapshots/             # Your rollback feature
│       │   ├── 2024-12-13-good/   # Each snapshot is a full state!
│       │   │   ├── game.json
│       │   │   └── mods/
│       │   └── last-working/
│       └── archives/              # Downloaded/original mod zips
├── icons/                         # Game icons
│   └── ghost-recon-breakpoint.png
└── temp/                          # Working directory
```

---

```text
.game_root/
  # Mod data is deploy here.

.mod_storage/
  # Undeployed Mods are kept here.
  ./Mod_id/
    Mod_id.json
    # Mod data is here.

.program_dir/
  config.json # Any program settings.
  ./temp/
    # Data is deployed from here.

  ./games/
    game_id.json # game's json file, kept outside the game's directory to make it easier to scan all known games.
    ./game_id/
      mod.json # all currently deployed Mod's Mod.JSON files are here.

  ./icons/
    game_id.png # could also store these as `../games/game_id/icon.png` instead.
```