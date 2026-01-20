# Add (Compile) a Mod

In order for the Mod Manager to use a Mod, it must first be compiled. This collects the files and creates a manifest for deployment and to store related information.
Once a Mod is compiled it doesn't need to be remade from scratch again, even for updates. (Updating is an automated recompile)

## General fields

- Mod Name : The display name and part of how the Mod ID is generated because of this you cannot *Edit* the name after creation, this is an *Update* operation.

- Description : A general, multi-line description of the Mod. While this intentionally has no line-limit, more that 4 line will not be visible on the ModCard for quick-asses. Avoid more than 14 lines as this is not intended for full Mod descriptions/install instructions but it can store an unlimited size of text in case it is required.

- Version : (Default "1.0") The current version of the Mod. Use the version provided by the Modder, do not just use the game version.

- Default load order : (Default: 1) The preferred load-order of a Mod. This is only the initial value when enabling the Mod. Use it as a guideline for categorising Mods into priority groups.

- Download source : The Source label that will be displayed on the ModCard. Only for referance.

- Download URL : Any URL or your choice. For mods with a repeatable download link, use that. Otherwise, for example with Nexus Mods, the download link is different for each version, so rather use the link to the page.

- Tags : Provided any comma separated tags for the Mod. Only for GUI filtering. Example: "textures, misc, resourcepack"

- Mod Files directory : Provide an *absolute* path to what will be the Mod's **root** directory with **pre-structured** mod files that are ready for *direct installation*.


## How to ready your Mod Files
As mentioned, the Mod Files need to be ready to install. What this means is they need to mimic the directory structure of the Game so each file is in the correct location already.
It is normal for mods to come pre-structured, but ensure you read the Mod's specific installation instructions as it is still up to you or the mod author to set this up.
Again, this is standard for mods.


### What this looks like (if you've never manually modded before)

Lets make an example mod.
The mods has 2 file: `custom_model.obj` and `sounds_pack.pak`
The mod instructions are: Put `custom_model.obj` in `/your_game_folder/data/models/` and `sounds_pack.pak` in `/your_game_folder/`

Then you need to make a fake game folder to put those files in the same places but not in your game.
So we'd make a new folder and it would have:
```text
/my_mod_folder
    sounds_pack.pak
    data/
        models/
            custom_model.obj
```
And that is ready! What the Mod Manager now want is where your `my_mod_folder` is!
So if the mod instructions say "Copy paste into game folder" then you don't need to do anything, just give it directly to the Manager.
