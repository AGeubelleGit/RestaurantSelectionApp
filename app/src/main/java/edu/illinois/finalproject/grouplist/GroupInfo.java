package edu.illinois.finalproject.grouplist;

/**
 * Created by alexandregeubelle on 12/7/17.
 */

/**
 * Class for information for groups stored in firebase.
 * Name: The name of the group
 * Id: The Id that is the key for the group under the root reference
 * Key: the firebase key for this group
 * Password: the group password
 * Creator: the firebase Uid of the person that created the group.
 */
public class GroupInfo {
    private String Name;
    private String Id;
    private String Key;
    private String Password;
    private String Creator;

    public GroupInfo() {
        this.Name = null;
        this.Id = null;
        this.Key = null;
        this.Password = null;
        this.Creator = null;
    }

    public GroupInfo(String name, String id, String key, String password, String creator) {
        this.Name = name;
        this.Id = id;
        this.Key = key;
        this.Password = password;
        this.Creator = creator;
    }

    public String getName() {
        return Name;
    }

    public String getId() {
        return Id;
    }

    public String getKey() {
        return Key;
    }

    public String getPassword() {
        return Password;
    }

    public String getCreator() {
        return Creator;
    }
}