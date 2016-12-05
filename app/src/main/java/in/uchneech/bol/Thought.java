package in.uchneech.bol;


public class Thought {
    private String by;
    private String downloadUri;
    private String uid;
    private String uuid;
    private String key;
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
