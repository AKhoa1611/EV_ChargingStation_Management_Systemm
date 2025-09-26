package swp391.code.swp391.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.util.List;

@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "full_name", nullable = false)
    private String fullName;
    @Column(name = "email", unique = true, nullable = false)
    private String email;
    @Column(name = "password", nullable = false)
    private String password;
    @Column(name = "phone", unique = true, nullable = false)
    private String phone;
    @Column(name = "date_Of_Birth", nullable = false)
    private Date dateOfBirth;
    @Column(name = "role", nullable = false)
    private UserRole role = UserRole.DRIVER;
    @Column(name = "address", nullable = false)
    private String address;
    @Enumerated
    @Column(name = "status",nullable = false)
    private UserStatus status = UserStatus.ACTIVE;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Vehicle> vehicles;

    public User(String fullName, String email, String password, String phone, Date dateOfBirth, String address) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
    }

    public enum UserStatus {
        ACTIVE,
        INACTIVE,
        BANNED
    }

    public enum UserRole {
        DRIVER,
        ADMIN,
        STAFF
    }
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Vehicle> vehicles;
}
