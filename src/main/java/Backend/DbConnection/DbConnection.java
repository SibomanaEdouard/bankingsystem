package Backend.DbConnection;

import lombok.*;

import java.sql.*;

@Setter
@Getter
@Builder
@Data
//@NoArgsConstructor
@RequiredArgsConstructor
public class DbConnection {

    // Establish database connection
    public static Connection getConnection() throws SQLException {
        String jdbcUrl = "jdbc:postgresql://localhost:5432/banking";
        String username = "edouard";
        String password = "edouard1234";

        Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
        if (connection != null) {
            System.out.println("Database connected successfully");
        } else {
            System.out.println("Failed to connect to the database");
        }
        return connection;
    }
}
