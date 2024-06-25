package ufanet.practika.fitness_telegram_bot.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

@Data
@Entity(name = "Users")
@Table( name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "telegram_user_name", nullable = false)
    private String telegramUserName;

    @Column(name = "chat_Id", nullable = false)
    private Long chatId;

    @Column(name = "bio", nullable = true)
    private String bio;

    @Column(name = "registration_Date")
    private LocalDateTime registrationDate;

    @OneToOne(mappedBy = "user")
    private UserRole userRoles;

    @OneToMany(mappedBy = "user")
    private Set<LessonRegistration> lessonRegistrations;


    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", userName='" + telegramUserName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id)
                && Objects.equals(telegramUserName, user.telegramUserName)
                && Objects.equals(chatId, user.chatId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, telegramUserName, chatId);
    }
}
