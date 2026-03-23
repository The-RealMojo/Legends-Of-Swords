package ProfileCreationSystem;

import java.sql.Connection;

public interface IDBConnection {
    void openConnection();
    Connection getConnection();
}
