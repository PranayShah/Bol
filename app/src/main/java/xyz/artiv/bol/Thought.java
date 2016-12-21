package xyz.artiv.bol;


import java.util.Map;

public class Thought {
    public String by;
    public String downloadUri;
    public String uid;
    public String uuid;
    public String key;
    public String parent;
    public Map<String, Boolean> children;

    public Thought () {

    }

    public Thought(String by, String uid, String uuid, String downloadUri, String key, String parent, Map<String, Boolean> children) {
        this.by = by;
        this.downloadUri = downloadUri;
        this.uid = uid;
        this.uuid = uuid;
        this.key = key;
        this.parent = parent;
        this.children = children;
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

    public Map<String, Boolean> getChildren() {
        return children;
    }
}
