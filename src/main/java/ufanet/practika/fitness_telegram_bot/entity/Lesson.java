package ufanet.practika.fitness_telegram_bot.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Data
@Table(name = "lessons")
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "lesson_id", nullable = false)
    LessonType lessonType;

    @ManyToOne
    @JoinColumn(name = "instructor_id", nullable = false)
    private User instructor;

    @Column(name = "start_datetime", nullable = false)
    private LocalDateTime startDateTime;

    @Column(name = "end_datetime", nullable = false)
    private LocalDateTime endDateTime;

    @Column(name = "places", nullable = false)
    private Integer places;

    @Column(name = "occupaid_places", nullable = false)
    private Integer occupiedPlaces;

    @OneToMany(mappedBy = "lesson")
    private Set<LessonRegistration> lessonRegistrations;
}
