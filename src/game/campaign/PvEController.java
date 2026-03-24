package game.campaign;

import db.GameSaveDAO;
import game.battle.Hero;
import game.campaign.Campaign;
import java.util.List;

public class PvEController {
    private Campaign campaign;

    public void startCampaign(List<Hero> heroes) {
        if (heroes==null||heroes.isEmpty()) return;
        campaign = new Campaign(heroes); campaign.start();
    }
    public void startCampaign(List<Hero> heroes, GameSaveDAO dao, int userId, String partyName) {
        if (heroes==null||heroes.isEmpty()) return;
        campaign = new Campaign(heroes, dao, userId, partyName); campaign.start();
    }

    public void continueCampaign(List<Hero> heroes, GameSaveDAO dao, int userId, String partyName) {
        int[] state = dao.loadCampaignProgress(userId, partyName);
        campaign = new Campaign(heroes, dao, userId, partyName);
        if (state!=null) {
            campaign.setCurrentRoom(state[0]);
            campaign.setGold(state[1]);
            campaign.setLastInnRoom((state[0] / 5) * 5);
        }
        campaign.start();
    }
    public Campaign getCampaign() { return campaign; }
}
