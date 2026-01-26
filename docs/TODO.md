# My TODO (In order of priority/favor)

## Upcoming changes

- Add an option to get a trash size-limit warning when the Trash dir reaches the set limit.
    I added this feature to the user-manual but does not exists yet.

- Resolve the drag-and-drop not working on Windows 11 (Works on Linux flawlessly) Error appears to be Windows is not passing any contents.
    May have to make a seperate system for Windows that instead auto-copies the dropped files to `temp/` and gets the path manually.

- Finally get around to adding FlatLaf themes! (Needs user preferences first)


## Low priority improvements

- GameState could use a lighter version of a Mod, it has more info than needed.

- Add/test Game and Mod edit and update features in the CLI. These are not fully supported in the CLI currently.

- Add pereference to reduce user-facing logging to only a progress display.


## Longer-term changes/features

- GameState profile saving. Add a drop-down to select a Mod profile to apply and save/manage profiles.

- As an alternative to Mod dependencies (or could work in tandem) add **Mod Groups**, allowing a group to be applied which contains multiple Mods in sequence.
    Instead of displaying all the contained mods, rather make the Group be displayed as a single card.
    This will make it easy to apply large groups of mods quickly, such as grouping Mods that support each other or related categories such as Core/Back-end Mods that are prerequisites.

- Mod dependencies. Mods should be able to specify other Mods they require.
    This is tricky because it will be version dependant and will require extra logic for the Mod Update process to also update dependencies.
    It also needs to be handled intelligently to reduce the amount of user-steps when updating. Avoid having the user need to re-define dependencies when a mod is updated.
    Or, could do that but provided an advanced GUI selection for dependencies to make it easy.

