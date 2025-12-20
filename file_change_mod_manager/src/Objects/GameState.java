/*
 * Author: Stephanos B
 * Date: 19/12/2025
*/
package Objects;

import java.util.Date;
import java.util.List;

/**
 * Object that is stored within the Game's Manifest directory. Used for
 * quick-access data for both the ModManager and GUI.
 * 
 * @author Stephanos B
 */
public class GameState {

    public enum JsonFields {
        lastModified,
        deployedMods
    }

    private Date lastModified;
    private List<Mod> deployedMods;

    /// /// /// Getters and Setters /// /// ///

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date createdAt) {
        this.lastModified = createdAt;
    }

    public List<Mod> getDeployedMods() {
        return deployedMods;
    }

    public void setDeployedMods(List<Mod> deployedMods) {
        this.deployedMods = deployedMods;
    }

    /// ///

    public void addDeployedMod(Mod mod) {
        this.deployedMods.add(mod);
    }

    public void removeDeployedMod(int index) {
        this.deployedMods.remove(index);
    }

    public void removeDeployedMod(Mod mod) {
        this.deployedMods.remove(mod);
    }

    /// /// /// Methods /// /// ///

    /**
     * Sorts Mods Acending by LoadOrder. (Order for deployment)
     * Uses the simplest bubble sort.
     */
    public void sortDeployedMods() {
        Mod temp = new Mod();
        int i, j, n = deployedMods.size();

        boolean swapped;
        for (i = 0; i < n - 1; i++) {
            swapped = false;
            for (j = 0; j < n - i - 1; j++) {
                if (deployedMods.get(j).loadOrder > deployedMods.get(j + 1).loadOrder) {
                    temp = deployedMods.get(j);
                    deployedMods.set(j, deployedMods.get(j + 1));
                    deployedMods.set(j + 1, temp);
                    swapped = true;
                }
            } // for j

            // If no two elements were swapped by inner loop, then break.
            if (swapped == false)
                break;
        } // for i

    } // sortDeployedMods()

} // Class
