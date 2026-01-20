## Program commands

```bash
Program Commands:
gui             | Launch the GUI program
info            | Show general info about the program and config
-h , help       | Show this help
-t , trash      | trash cleaning tool
            --h | Specific help
exit / quit     | Exit the program
```

## Game commands
```bash
Game Manager Commands:
-L , list       | List all game profiles
-A , add        | Add a new game profile
-R , remove     | Remove a game profile
  --id <target> | target game id
     [--atomic] | removed files will not be left in trash
-U , update     | Update a game profile
  --id <target> | target game id
-M , mod        | Enter mod manager for a game
  --id <target> | target game id
```

## Mod Manager commands
```bash
-L , list       | List all available Mods
          [--i] | Only installed Mods
          [--u] | Only uninstalled Mods
-D , deploy     | Deploy a mod to game files
-R , remove     | Remove a mod from game files
  --id <target> | target mod id
          --all | removes all mod from game files
     [--atomic] | removed files will not be left in trash
-o , order      | reorder a mod
  --id <target> | target mod id
   --n <number> | new load order
-c , compile    | Compile a new mod
   --dir <name> | name-only of the directory within temp
delete          | Delete a mod from storage, cannot be installed
  --id <target> | target mod id
     [--atomic] | removed files will not be left in trash
-G , game       | Return to game manager
```

## Trash Utility commands
```bash
-t , trash [option]  | trash cleaning tool
    -h,              | Specific help.
    -c, clean        | Cleans the Trash direcotry based on file LastModified dates and a maxiumon size.
        --s <number> | maximum size in Megabytes of direcotry
        --d <number> | Days older than to clean out
    -e, empty        | Empties the entire Trash direcotry.
```