package ufanet.practika.fitness_telegram_bot.repository;

import org.springframework.data.repository.CrudRepository;
import ufanet.practika.fitness_telegram_bot.entity.User;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByChatId(Long chatId);
}
