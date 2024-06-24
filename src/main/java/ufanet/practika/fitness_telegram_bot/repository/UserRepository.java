package ufanet.practika.fitness_telegram_bot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ufanet.practika.fitness_telegram_bot.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByChatId(Long chatId);
}
