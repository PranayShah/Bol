package xyz.artiv.bol;


public class Thought {
    public String by;
    public String downloadUri;
    public String uid;
    public String uuid;
    public String key;
    public String parent;

    public Thought () {

    }

    public Thought(String by, String uid, String uuid, String downloadUri, String key, String parent) {
        this.by = by;
        this.downloadUri = downloadUri;
        this.uid = uid;
        this.uuid = uuid;
        this.key = key;
        this.parent = parent;
    }

    String getBy() {
        return by;
    }


    String getDownloadUri() {
        return downloadUri;
    }

    String getUid() {
        return uid;
    }

    String getUuid() {
        return uuid;
    }

    String getKey() {
        return key;
    }

    String getParent() {
        return parent;
    }
}
