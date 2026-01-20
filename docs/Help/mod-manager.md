# Mod Mannager

The Mod Manager view is the primary page for managing and editting Mods for a game.

### Tips
- The name of the game your modding is in the window title.

- The top-row of buttons is the Utility panel. Below that is the Filters Panel.

- You don't have to close the console popup! It will display all user-logs, even though by processes that would normally hide their logs.<br>
    Also, It will stay where you move it or re-open at centre of the program if you close it.

---

### Add your first Mod
To add a new Mod, click the "Compile New Mod" button in the Utility Panel. THis will navigate to the Compile Mod page.

---
---

## The Mods Display
The Mod view is where you can see all Mods for a game, what state they are in and make all changes.
The view is split into two section:
    1. Enabled Mods: These will have green outlines and backgrounds.
    2. Disabled Mods: Outlined in red with a default background.
    Headers will denote the division of these categories.


### The ModCard
Mods are displayed as cards, providing all the information and functions you need for a Mod.
From left to right, the elements are:

- Enabled/Disabled toggle : This is a quick way to imidiately change the state of a Mod.

- The Load Order spinner :<br>
    Use the arrows to move the mod in the *direction* you want in the load order (up will decrease the load order to move the mod up)<br>
    Alternativly you can click and type directly into the spinner's number display to set the load order on enter.

- The drag handle : Click and drag the handle to use the **Drag-to-order** feature, explained further down.

- Mod title : The title of the Mod.

- Mod description : Displays a text area of the description.

- Version indicator : Displays the version of the Mod itself. (You can have multiple version of the same Mod)

- The Edit button : Click this to navigate to the page for making any changes to the data of that Mod. (Edit/Update/Delete)

- The Download source lable : This displays the Download source of the Mod with an embedded hyperlink to the download link, clicking it will open in your default browser.

---

### Drag-to-order
While using the Load Order spinner is great, dragging ModCards is far more satisfying and usually faster. (Plus I need to justify the effort it took to code)

To use it, click and drag the ModCard you want to move on its drag handle and drop it where you want, even into another sections!
#### Points of interest:
- The Mod being dragged will be outlined in yellow.
- You can Enable/Disable Mods by dropping them in differet sections.
- The direction you are dragging matters!
- Dropping onto a divider will place it at the top position of that divider's sections.
- Dropping on the last enabled Mod will *push-up* the exsisting mod, placing the dropped Mod last
- Dropping on white-space (spacers between cards) will cancel the move.


## Making changes
Any load order changes you make will not take affect until you click the "Apply Changed" button in the utility panel.
When you apply changes, a Console Popup will appear displaying the user-facing logs of all operations and indicate if the program is busy or idle.
