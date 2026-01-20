# Usage for GameManager:
The Game manager is used to modify the configured games for the launcher to use.

```bash
```

# Usage for ModManager:
The Mod Manager is for managing the Mods of a single Game.

```bash
# Basic operations
java -jar ModManager.jar list --game ghost-recon --location game --s
java -jar ModManager.jar deploy --game ghost-recon --mod nexus-12345
java -jar ModManager.jar remove --game ghost-recon --mod nexus-12345

# Compile new mod from downloaded files
java -jar ModManager.jar compile --game ghost-recon --dir raw_sample_mod

# Batch operations
java -jar ModManager.jar batch --file deploy-list.txt

# GUI mode
java -jar ModManager.jar gui

java -jar GameManager.jar remove --id example_id
```

ModManager.jar {list | -L} --game [options]
ModManager.jar {deploy | -D} --game [options]

```bash
[console text]>{commands}                                # comment.

game_manager: > list                                     # List all managed games.
game_manager: > {add    | -A}                            # Add a new profile of a game to manage.
game_manager: > {remove | -R} [--ATOMIC]                 # Remove a game profile. `--ATOMIC`: will delete it after sucessfuly being trashing.
game_manager: > {update | -U} --id                       # Update a game profile. Only creates a backup if the ID is changed.
game_manager: > {mod    | -M} --id game_id               # Enter mod manager for a praticular game.
mod_manager: [game_id] > {list    | -L} [--s]            # list all mods deployed. `--s`: lists stored/available mods instead.
mod_manager: [game_id] > {deploy  | -D} --id             # install/deploys a mod to the game.
mod_manager: [game_id] > {remove  | -R} --id [--ATOMIC]  # uninstall a mod from game. `--ATOMIC`: will delete it after sucessfuly being trashing.
mod_manager: [game_id] > {compile | -C} --dir            # compiles a new mod for the manager to use
mod_manager: [game_id] > {game    | -G}                  # Takes you back to game manager
```