package ufanet.practika.fitness_telegram_bot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ufanet.practika.fitness_telegram_bot.entity.UserRole;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

}
