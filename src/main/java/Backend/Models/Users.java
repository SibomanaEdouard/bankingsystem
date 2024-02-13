package Backend.Models;

import Backend.DbConnection.DbConnection;
import lombok.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@AllArgsConstructor
@Builder
@Data
@Getter
@Setter
@NoArgsConstructor

public class Users {
    private String username;
    private String email;
    private String phone;
    private String age;
    private String password;


}
