package ufanet.practika.fitness_telegram_bot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ufanet.practika.fitness_telegram_bot.entity.Lesson;
import ufanet.practika.fitness_telegram_bot.entity.LessonRegistration;
import ufanet.practika.fitness_telegram_bot.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface LessonsRegistrationRepository extends JpaRepository<LessonRegistration, Integer> {
    List<LessonRegistration> findByUser(User user);

    boolean existsByUserAndLesson(User user, Lesson lesson);

    Optional<LessonRegistration> findByLessonAndUser(Lesson lesson, User user);
}
