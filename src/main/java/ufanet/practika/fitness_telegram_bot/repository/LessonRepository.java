package ufanet.practika.fitness_telegram_bot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ufanet.practika.fitness_telegram_bot.entity.Lesson;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface LessonRepository extends JpaRepository<Lesson, Integer> {
    List<Lesson> findByStartDateTime_Date(LocalDate startDateTime_date);
}
