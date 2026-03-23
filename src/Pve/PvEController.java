package Pve;

import battle.Hero;
import java.util.List;

public class PvEController {

    private Campaign campaign;

    // Start a new PvE campaign
    public void startCampaign(List<Hero> heroes) {
        if (heroes == null || heroes.isEmpty()) {
            System.out.println("Cannot start campaign: no heroes provided.");
            return;
        }

        campaign = new Campaign(heroes);
        campaign.start();
    }

    // Get current campaign used for UI
    public Campaign getCampaign() {
        return campaign;
    }

    //Continue campaign for database integration
    public void continueCampaign(Campaign savedCampaign) {
        if (savedCampaign == null) {
            System.out.println("No saved campaign found.");
            return;
        }

        this.campaign = savedCampaign;
        System.out.println("Resuming campaign...");
        campaign.start();
    }
}