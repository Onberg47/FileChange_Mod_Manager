package Objects;

import java.util.Date;

/**
 * Represents a Mod.
 * 
 * @author Stephanos B
 */
public class Mod {
    private String name; // User-friendly name, doubles as the filename for the ModFile
    private String version;
    private Date downloadDate; // Used for update checks
    private String[] contentsArr; // Array of contents inside the ModFile.

    // non-essential fields
    private String description;
    private String downloadLink;

    /**
     * Empty constructor for Mod.
     */
    public Mod() {
        this.name = "Unnamed Mod";
        this.version = "0.0";
        this.downloadDate = new Date(0); // Epoch
        this.description = "No description provided.";
        this.downloadLink = "";
    }

    /**
     * Parameterized constructor for Mod without contents.
     * 
     * @param name         The user-friendly name, doubles as the filename for the
     *                     ModFile
     * @param version      The version of the Mod.
     * @param downloadDate The download date of the Mod.
     * @param description  The description of the Mod.
     * @param downloadLink The download link of the Mod.
     */
    public Mod(String name, String version, Date downloadDate, String description, String downloadLink) {
        this.name = name;
        this.version = version;
        this.downloadDate = downloadDate;
        this.description = description;
        this.downloadLink = downloadLink;
    }

    /**
     * Fully Parameterized constructor for Mod with contents.
     * 
     * @param name         The user-friendly name, doubles as the filename for the
     *                     ModFile
     * @param version      The version of the Mod.
     * @param downloadDate The download date of the Mod.
     * @param description  The description of the Mod.
     * @param downloadLink The download link of the Mod.
     * @param contentsArr  The contents array of the Mod.
     */
    public Mod(String name, String version, Date downloadDate, String description, String downloadLink,
            String[] contentsArr) {
        this(name, version, downloadDate, description, downloadLink);
        this.contentsArr = contentsArr;
    }

    /// /// /// Getters and Setters /// /// ///

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Date getDownloadDate() {
        return downloadDate;
    }

    public void setDownloadDate(Date downloadDate) {
        this.downloadDate = downloadDate;
    }

    public String[] getContentsArr() {
        return contentsArr;
    }

    public void setContentsArr(String[] contentsArr) {
        this.contentsArr = contentsArr;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDownloadLink() {
        return downloadLink;
    }

    public void setDownloadLink(String downloadLink) {
        this.downloadLink = downloadLink;
    }

    /// /// /// Functions /// /// ///

    /**
     * Prints the contents of the Mod.
     * 
     * @return A string representation of the contents of the Mod.
     */
    public String printContents() {
        String contents = "";
        if (contentsArr != null) {
            for (String content : contentsArr) {
                contents += content + "\n";
            }
        } else {
            contents = "No contents available.";
        }
        return contents;
    } // printContents()

    /**
     * Overrides toString() to provide a string representation of the Mod object.
     * 
     * @return A string representation of the Mod object.
     */
    @Override
    public String toString() {

        String contents = printContents();

        return "Mod [name=" + name + ", version=" + version + ", downloadDate=" + downloadDate + ", description="
                + description + ", downloadLink=" + downloadLink + ",contents=\n" + contents + "]";
    } // toString()

} // Class
