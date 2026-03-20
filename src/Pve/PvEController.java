package Pve;

public class PvEController {

    private Campaign campaign;

    public PvEController(Party party) {
        this.campaign = new Campaign(party);
    }

    public void startGame() {
        campaign.start();
    }
}