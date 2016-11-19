package in.uchneech.bol;


public class Thought {
    String by;
    String downloadUri;
    String uid;
    String uuid;
    public Thought () {

    }

    public Thought(String by, String uid, String uuid, String downloadUri) {
        this.by = by;
        this.downloadUri = downloadUri;
        this.uid = uid;
        this.uuid = uuid;
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
}
