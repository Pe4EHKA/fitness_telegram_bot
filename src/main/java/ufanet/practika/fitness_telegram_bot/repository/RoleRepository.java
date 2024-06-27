package ufanet.practika.fitness_telegram_bot.repository;

import org.springframework.data.repository.CrudRepository;
import ufanet.practika.fitness_telegram_bot.entity.Role;

public interface RoleRepository extends CrudRepository<Role, Long> {
    Role findByRole(String role);
    boolean existsByRole(String role);
}
