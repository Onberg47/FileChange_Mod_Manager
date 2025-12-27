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
```

```bash
ModManager.jar {list -L} --game [options]
ModManager.jar {deploy -D} --game [options]
```