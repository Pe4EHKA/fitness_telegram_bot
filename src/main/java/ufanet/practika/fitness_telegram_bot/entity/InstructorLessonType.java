package ufanet.practika.fitness_telegram_bot.entity;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "instructor_lesson_types")
public class InstructorLessonType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "lesson_type_id", nullable = false)
    private LessonType lessonType;

    @ManyToOne
    @JoinColumn(name = "instructor_id", nullable = false)
    private User instructor;
}
