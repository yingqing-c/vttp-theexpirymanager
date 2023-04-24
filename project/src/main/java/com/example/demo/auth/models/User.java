package com.example.demo.auth.models;

import com.example.demo.features.models.Item;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data // includes @Getter and @Setter
@Entity
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "username"),
                @UniqueConstraint(columnNames = "email")
        })
public class User {

    //org.hibernate.id.IdentifierGenerationException: ids for this class must
    // be manually assigned before calling save(): com.example.demo.auth.models.User
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    @NotBlank
    private String email;

    private Set<String> roles = new HashSet<>();

    @OneToMany(mappedBy="user")
    private List<Item> items;

    public static User create(SqlRowSet rs) {
        User user = new User();
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        return user;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", roles=" + roles +
                '}';
    }
}
