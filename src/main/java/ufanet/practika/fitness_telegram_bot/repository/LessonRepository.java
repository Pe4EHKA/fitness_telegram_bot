package ufanet.practika.fitness_telegram_bot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ufanet.practika.fitness_telegram_bot.entity.Lesson;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface LessonRepository extends JpaRepository<Lesson, Integer> {
    // boolean existsByLessonId(Integer Lesson);
    // List<Lesson> findByStartDateTimeBetween(Timestamp start, Timestamp end);
    Lesson findById(long id);

    List<Lesson> findByStartDateTimeBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT l FROM Lesson l WHERE l.startDateTime BETWEEN :startDateTime AND " +
            ":endDateTime AND l.occupiedPlaces < l.places")
    List<Lesson> findByStartDateTimeBetweenAndOccupiedPlacesIsLessThanPlaces(LocalDateTime startDateTime, LocalDateTime endDateTime);

    /*@Query("SELECT e.id FROM Lesson e")
    List<Integer> findAllLessonIds();*/

    boolean existsById(long id);
}
