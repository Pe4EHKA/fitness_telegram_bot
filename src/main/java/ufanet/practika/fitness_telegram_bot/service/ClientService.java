package ufanet.practika.fitness_telegram_bot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ufanet.practika.fitness_telegram_bot.entity.*;
import ufanet.practika.fitness_telegram_bot.repository.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.PatternSyntaxException;

@Slf4j
@Service
public class ClientService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LessonsRegistrationRepository lessonsRegistrationRepository;
    @Autowired
    private LessonRepository lessonRepository;
    @Autowired
    private UserRoleRepository userRoleRepository;
    @Autowired
    private RoleRepository roleRepository;


    public boolean isSignUp(String callBackData) {
        String[] data = new String[0];
        try {
            data = callBackData.split("/");
            return data[0].equals(TelegramBot.SIGN_UP_LESSON);
        } catch (PatternSyntaxException e) {
            log.error("Error occurred during parsing callBackData " + e.getMessage(), e);
        }
        return false;
    }

    public boolean isBackToLessons_Schedule_Day(String callBackData) {
        String[] data;
        try {
            data = callBackData.split("/");
        } catch (PatternSyntaxException e) {
            log.error("Error occurred during split callBackData " + e.getMessage(), e);
            return false;
        }
        return data[0].equals(TelegramBot.BACK_TO_LESSONS_SCHEDULE_DAY);
    }

    public boolean containsLessonId(String callBackData) {
        int lessonId;
        try {
            lessonId = Integer.parseInt(callBackData);
        } catch (NumberFormatException e) {
            log.error("Error occurred during parsing callBackData to Integer " + e.getMessage(), e);
            return false;
        }
        return lessonRepository.findAllLessonIds().contains(lessonId);
    }

    public List<Lesson> getAllAvailableLessonsByDay(String callBackData) {
        List<Lesson> lessonsList = List.of();
        try {
            LocalDateTime day = LocalDateTime.parse(callBackData);
            LocalDateTime endDay = day.with(LocalTime.of(23, 59));
            lessonsList = lessonRepository.findByStartDateTimeBetweenAndOccupiedPlacesLessThanPlaces(day, endDay);
        } catch (DateTimeParseException e) {
            log.error("Error during parsing DateTime " + e.getMessage(), e);
        }
        return lessonsList;
    }
    /*
    Выводит все занятия конкретного клиента
     */
    public List<Lesson> getAllClientLessons(User user){
        List<LessonRegistration> registrations = lessonsRegistrationRepository.findByUser(user);
        return registrations.stream().map(LessonRegistration::getLesson).toList();
    }
    public Lesson getLesson(long id){
        return lessonRepository.findById(id);
    }
    public void singUpLesson(Lesson lesson, User user) {
        LessonRegistration lessonRegistration = new LessonRegistration();
        lessonRegistration.setLesson(lesson);
        lessonRegistration.setUser(user);

        lessonsRegistrationRepository.save(lessonRegistration);
    }

    public void cancelLesson(User user, Lesson lesson) {
        Optional<LessonRegistration> lessonRegistration = lessonsRegistrationRepository.findByLessonAndUser(lesson, user);
        if (lessonRegistration.isPresent()) {
            Lesson lessonChanged = lessonRegistration.get().getLesson();
            Integer occupaiedPlaces = lessonChanged.getOccupiedPlaces();
            lessonChanged.setOccupiedPlaces(occupaiedPlaces - 1);

            lessonsRegistrationRepository.delete(lessonRegistration.get());
            lessonRepository.save(lessonChanged);
        }
    }

    public void registrateUser(UserRole userRole){
        userRepository.save(userRole.getUser());
        userRoleRepository.save(userRole);
    }
    public Role getRole(String userRole){
        return roleRepository.findByRole(userRole);
    }
    public boolean isLessonExists(long chatId, String callBackData){
        long lessonId;
        try {
            lessonId = Integer.parseInt(callBackData);
        } catch (NumberFormatException e) {
            log.error("Error occured during parsing callBackData to Integer " + e.getMessage(), e);
            return false;
        }
        User user = userRepository.findByChatId(chatId).get();
        Lesson lesson = lessonRepository.findById(lessonId);

        return lessonsRegistrationRepository.existsByUserAndLesson(user, lesson);
    }
    public boolean isExistingUser(Long chatId){
        return userRepository.existsByChatId(chatId);
    }

    public Optional<User> getUser(Long chatId){
        return userRepository.findByChatId(chatId);
    }
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    // Временный метод, для удобства разработки: проверяет существование роли
    public boolean isExistingRole(String role){
        return roleRepository.existsByRole(role);
    }
    // Временный метод, для удобства разработки: сохраняет роль
    public void registrateRole(Role role){
        roleRepository.save(role);
    }
}