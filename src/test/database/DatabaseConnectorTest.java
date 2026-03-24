package test.database;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class DatabaseConnectorTest {
    @Test
    public void testDatabaseConnectorSingleton() {
        // Grab the instance twice
        database.DatabaseConnector instance1 = database.DatabaseConnector.getInstance();
        database.DatabaseConnector instance2 = database.DatabaseConnector.getInstance();

        // assertSame checks if they occupy the exact same space in memory
        assertSame(instance1, instance2, "Both instances should be the exact same object in memory, proving the Singleton works.");
    }

    @Test
    public void testNewUserProfileInitialization() {
        // Create a fake new user
        ProfileCreationSystem.UserProfile newProfile = new ProfileCreationSystem.UserProfile("99", "Jaden", "secureHash123");

        // Verify stats are zeroed out
        assertEquals(0, newProfile.getScores(), "A brand new profile should always start with 0 score.");
        assertEquals(0, newProfile.getCampaignProgress(), "A brand new profile should start at 0 progress.");

        // Verify the list was initialized so it doesn't throw a NullPointerException later
        assertNotNull(newProfile.getSavedParties(), "The saved parties list should be empty, not null.");
        assertTrue(newProfile.getSavedParties().isEmpty(), "A brand new profile should not have any saved parties yet.");
    }
}
