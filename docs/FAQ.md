# FAQ

- ### What do I need to run FCMM?
    All you need is a Java runtime. Ideally version 25 or newer.<br>
    Changes in version 22 to `Java.nio` for Path handling made this project way cleaner but also not compatable with older runtimes.<br>
    All dependencies are very small (currently 1) and are hence bundled with the .jar file.
    
- ### How do I install and run FCMM?
    //
    

- ### Can I mod *any* game?
    Yes. (Assuming the game is moddable)<br>
    This can be configured to deploy mods however you'd like. You just need to understand the process of mod deployment for that specific game.

- ### Should I use this instead of Vortex from Nexus?
    No -well, it depends.<br>
    Vortex uses a virtual file-system (VFS) to deploy games within. In short, this means that when a game want say, file *A* the VFS could point that path to anywhere else, leading it to file *B* without the game nesessarily knowing.<br>
    Why does this matter? It is unquestionably safer, and way faster when running the mod launcher simply because no files are actually moved or replaced. Its simply a different approach with different limitations.

- ### But when *should* I use FCMM?
    While using a virtual file-system is "better", its not always supported. Some games cannot run in a VFS or their anti-cheat flags it as a problem. Or Vortex might not have an extension for your game!<br>
    In those cases, I recommend using FCMM, that is what it is made for and what I personally use it for.
