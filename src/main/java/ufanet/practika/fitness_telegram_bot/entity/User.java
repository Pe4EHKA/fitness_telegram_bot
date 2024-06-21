package ufanet.practika.fitness_telegram_bot.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.util.Set;

@Data
@Entity(name = "Users")
@Table( name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "firstName", nullable = false)
    private String firstName;

    @Column(name = "lastName", nullable = false)
    private String lastName;

    @Column(name = "telegramUserName", nullable = false)
    private String telegramUserName;

    @Column(name = "chatId", nullable = false)
    private Long chatId;

    @Column(name = "bio")
    private String bio;

    @Column(name = "registrationDate")
    private Timestamp registrationDate;

    @OneToMany(mappedBy = "user")
    private Set<UserRole> userRoles;

    @OneToMany(mappedBy = "user")
    private Set<LessonRegistration> lessonRegistrations;


    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", userName='" + telegramUserName + '\'' +
                '}';
    }
}
