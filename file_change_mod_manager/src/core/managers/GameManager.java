/**
 * Author Stephanos B
 * Date TODO when I work on this
 */
package core.managers;

import core.objects.Game;

/**
 * For performing core operations for Game managment.
 * 
 * @author Stephanos B
 */
public class GameManager {

    /// /// /// Core Methods /// /// ///

    /// /// /// Core Heler Methods /// /// ///

    /// /// /// Public Helper Methods /// /// ///

    /**
     * Fetch a Game instance by its Id. Handles various checks internally.
     * 
     * @param gameId Id of the game to read. (The name of the json file)
     * @return Complete Game instance if successful. Null on fail.
     */
    public static Game getGameById(String gameId) {
        Game game = new Game();

        // TODO
        game = new Game("game_1", "Test Game", "tst_fs/game_root", "mod_manager/mod_storage/game_1");

        return game;
    }
} // Class
