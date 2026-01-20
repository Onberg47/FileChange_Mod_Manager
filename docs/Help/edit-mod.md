# Edit/Update/Delete a Mod
When in the edit Mod page, there is an additional option to toggle between *Edit* or *Update* modes. This is because some data requires the mod to be recompiled (which will automatically disable, re-compile, and re-enable)
From a user's point of view, this simply means that updating can take longer for very large Mods, whereas editing will always be imidiate and require no changes to the game.<br>
**Take note: switching between Edit and Update mode will discard changes.**

## Editing
When in Edit mode, the Mod Description, Default load order, Download Source and URL, and tags may only be changed.
If only changing these, it is recommended to only edit.

## Updating
Mod updating or changing any of the non-edit fields is only provided with an Update operation.
To do this there are some important notes:
- Leaving the Mod File field empty will re-use the existing data.
- To update the files of a Mod (regular update) only then do you provide a path to a new **pre-structured** directory, just like compiling a Mod. (Remember to update the version)
- If you want to update a Mod but *keep* the old version, then this is **not** and update, instead compile it as a new version. Updating will delete the old version automatically.


## Deleting
While editing, the delete button allows deleting of Mods that are not deployed. If a mod is deployed it will not allow deletion as a safety measure.

- Tip: Remember that all "delete" operations actually only trash the data. If you make a mistake you can retrieve it, the console or log will tell you **exactly** where the trashed files are!