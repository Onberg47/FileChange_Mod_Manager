# Adding a Game

To add a game, simply full out the fields and click "Add Game". Clicking cancel will discard any data.

### Fields

Game Name :<br>
    The display name of the Game to add.

Game ID :<br>
    This is an internal ID for the program to identify the Game by. If you want to use the CLI, this id is what you would use. Just set it to a lower-case only with no white-spaces version of the name. (This is done by default)

Release Version :<br>
    Set this to the version of the game or the BuildID in Steam properties if you cannot find the actual version.

Game Install Directory :<br>
    This is the *absolute* path from which Mods are *installed* to the game, depending on the game this might not be the game's root-directory.<br>

Mod Storage Directory :<br>
    This is the *absolute* path where Mods that are compiled for a game will be stored.<br>
    By default, this is populated to your default Mod storage directory with an example game ID added. If you have not set this in settings yet, go do that first!<br>
#### GAMES CANNOT SHARE THIS DIRECOTRY!

Icon File :<br>
    Set this to an image (.png or .jpg) to set the Game's icon. This will only make a copy of that file in the program's files with an auto-generated name, not moving the original file.
