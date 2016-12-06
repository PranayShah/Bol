package in.uchneech.bol;


public class Thought {
    public String by;
    public String downloadUri;
    public String uid;
    public String uuid;
    public String key;
    public Thought () {

    }

    public Thought(String by, String uid, String uuid, String downloadUri, String key) {
        this.by = by;
        this.downloadUri = downloadUri;
        this.uid = uid;
        this.uuid = uuid;
        this.key = key;
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
}
