package ufanet.practika.fitness_telegram_bot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ufanet.practika.fitness_telegram_bot.entity.Lesson;
import ufanet.practika.fitness_telegram_bot.entity.LessonRegistration;
import ufanet.practika.fitness_telegram_bot.entity.User;
import ufanet.practika.fitness_telegram_bot.repository.LessonRepository;
import ufanet.practika.fitness_telegram_bot.repository.LessonsRegistrationRepository;
import ufanet.practika.fitness_telegram_bot.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClientService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LessonsRegistrationRepository lessonsRegistrationRepository;
    @Autowired
    private LessonRepository lessonRepository;

    public List<Lesson> getAvailableLessonsByDate(LocalDate date) {
        List<Lesson> availableLessons = lessonRepository.findByStartDateTime_Date(date);
        return availableLessons.stream()
                .filter(el -> el.getOccupiedPlaces() < el.getPlaces())
                .collect(Collectors.toList());
    }
    public List<Lesson> getAllClientLessons(User user){
        List<LessonRegistration> registrations = lessonsRegistrationRepository.findByUser(user);
        return registrations.stream().map(LessonRegistration::getLesson).toList();
    }
    public void singUpLesson(Lesson lesson, User user) {
        LessonRegistration lessonRegistration = new LessonRegistration();
        lessonRegistration.setLesson(lesson);
        lessonRegistration.setUser(user);

        lessonsRegistrationRepository.save(lessonRegistration);
    }
    public void cancelLesson(Lesson lesson) {
        throw new RuntimeException();
    }
}