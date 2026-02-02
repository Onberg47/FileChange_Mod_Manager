# FAQ

- ### What do I need to run FCMM?
    All you need is a Java runtime. Ideally version 25 or newer.<br>
    Changes in version 22 to `Java.nio` for Path handling made this project way cleaner but also not compatible with older runtimes.<br>
    All dependencies are very small (currently 1) and are hence bundled with the .jar file so you won't need anything else.
    
- ### How do I install and run FCMM?
    
    ### Installation
    1. Download the latest `fcmm-x.x.x.jar` file from the releases. (Download the icon too)
    2. Place the `.jar` file in a directory ideally where you want the working directory to be and therefore where you want the manager to store its own data.<br>
        Note: This is **not** necessarily where the Mods are stored, you decide that per a game.


    <details>
    <summary>3. On Windows:</summary>
    Create a new shortcut (this can be from scratch, no need to make it of the `.jar` file because you will need to edit the command anyways.)
    Edit the shortcut properties:
        1. Set the icon - Branding is important~
        3. Set the working directory if it is not set already. It can simply be where the `.jar` file is.
        2. CopyAsPath the `.jar` file and use it as the path for the command to run:
    
    For GUI:    (`javaw` will not make a terminal popup)
    ```bash
    javaw -jar "path/to/fcmm-x.x.x.jar" gui
    ```

    For CLI:
    ```bash
    java -jar "path/to/fcmm-x.x.x.jar"
    ```
    </details>
    <br>

    <details>
    <summary>3. On Linux:</summary>
    Simply make a custom application, using the java command and make sure to set the working directory.

    For GUI:    (`javaw` will not make a terminal popup)
    ```bash
    javaw -jar path/to/fcmm-x.x.x.jar gui
    ```

    For CLI:
    ```bash
    java -jar path/to/fcmm-x.x.x.jar
    ```
    </details>
    <br>

    <details>
    <summary>3. On Mac:</summary>
    Pfft, do games even run well on mac?
    Okay, but in all seriousness, this should be compatible with Mac but I never have or will test/tailor for it.
    </details>
    <br>
    

- ### Can I mod *any* game?
    Yes. (Assuming the game is mod-able)<br>
    This can be configured to deploy mods however you'd like. You just need to understand the process of mod deployment for that specific game.

- ### Should I use this instead of Vortex from Nexus?
    No -well, it depends.<br>
    Vortex uses a virtual file-system (VFS) to deploy games within. In short, this means that when a game want say, file *A* the VFS could point that path to anywhere else, leading it to file *B* without the game necessarily knowing.<br>
    Why does this matter? It is unquestionably safer, and way faster when running the mod launcher, simply because no files are actually moved or replaced. Its simply a different approach with different limitations.

- ### But when *should* I use FCMM?
    While using a virtual file-system is "better", its not always supported. Some games cannot run in a VFS or their anti-cheat flags it as a problem. Or Vortex might not have an extension for your game!<br>
    In those cases, I recommend using FCMM, that is what it is made for and what I personally use it for.

- ### I'm a modder, what do I need to know?
    For modder, FCMM makes it easy to update mods quickly and seamlessly.<br>
    If you have a custom mod, what you should do is add it to FCMM, so the launcher handles its deployment. Then when you want to make changes to the mod:
    - Edit the mod-data **inside** your Mod Storage, editting the compiled mod-data for FCMM.
    - Doing this, to update the mod simply click "Edit", *switch to "Update"* and "Save".
    - **Done!** The mod will be automatically re-compiled using the exsisting data and re-deployed to the game with the same load order as before.
