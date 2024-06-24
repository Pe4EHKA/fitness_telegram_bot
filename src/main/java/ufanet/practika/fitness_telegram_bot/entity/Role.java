package ufanet.practika.fitness_telegram_bot.entity;

import jakarta.persistence.*;
import lombok.Data;
import ufanet.practika.fitness_telegram_bot.service.UserRoles;

@Data
@Entity(name = "Roles")
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "role", nullable = false)
    private String role;
}
