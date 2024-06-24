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

    @Column(name = "first_Name", nullable = false)
    private String firstName;

    @Column(name = "last_Name", nullable = false)
    private String lastName;

    @Column(name = "telegram_User_Name", nullable = false)
    private String telegramUserName;

    @Column(name = "chat_Id", nullable = false)
    private Long chatId;

    @Column(name = "bio")
    private String bio;

    @Column(name = "registration_Date")
    private Timestamp registrationDate;

    @OneToOne(mappedBy = "user")
    private UserRole userRoles;

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
