package pvp;

public class Invite {
    private String senderUsername;
    private String receiverUsername;
    private String senderParty;
    private String receiverParty;
    private boolean accepted;

    public Invite(String senderUsername, String receiverUsername) {
        this.senderUsername = senderUsername;
        this.receiverUsername = receiverUsername;
        this.accepted = false;
    }

    public String getSenderUsername() {
        return senderUsername;
    }

    public String getReceiverUsername() {
        return receiverUsername;
    }

    public String getSenderParty() {
        return senderParty;
    }

    public void setSenderParty(String senderParty) {
        this.senderParty = senderParty;
    }

    public String getReceiverParty() {
        return receiverParty;
    }

    public void setReceiverParty(String receiverParty) {
        this.receiverParty = receiverParty;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void accept() {
        accepted = true;
    }

    public void decline() {
        accepted = false;
    }
}