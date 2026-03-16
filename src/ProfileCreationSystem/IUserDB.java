package ProfileCreationSystem;

public interface IUserDB {
    boolean usernameExists(String username);
    void save(UserProfile user);
    UserProfile findUsername(String username);
}
