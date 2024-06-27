package ufanet.practika.fitness_telegram_bot.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;

@Entity
@Data
@Table(name = "lesson_types")
public class LessonType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "place")
    private String place;

    @OneToMany(mappedBy = "lessonType")
    private Set<Lesson> lessons;

    @OneToMany(mappedBy = "lessonType")
    private Set<InstructorLessonType> instructorLessonTypes;

}
