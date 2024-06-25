package ufanet.practika.fitness_telegram_bot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ufanet.practika.fitness_telegram_bot.entity.Lesson;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface LessonRepository extends JpaRepository<Lesson, Integer> {
    List<Lesson> findByStartDateTime_Date(int startDateTime_date);
    Lesson findById(long id);
}
