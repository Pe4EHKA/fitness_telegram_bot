package ufanet.practika.fitness_telegram_bot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ufanet.practika.fitness_telegram_bot.entity.*;
import ufanet.practika.fitness_telegram_bot.repository.*;

import java.util.List;
import java.util.Optional;

@Service
public class ClientService {
    private final UserRepository userRepository;
    private final LessonsRegistrationRepository lessonsRegistrationRepository;
    private final LessonRepository lessonRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;

    @Autowired
    public ClientService(UserRepository userRepository,
                         LessonsRegistrationRepository lessonsRegistrationRepository,
                         LessonRepository lessonRepository,
                         UserRoleRepository userRoleRepository,
                         RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.lessonsRegistrationRepository = lessonsRegistrationRepository;
        this.lessonRepository = lessonRepository;
        this.userRoleRepository = userRoleRepository;
        this.roleRepository = roleRepository;
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
    public boolean isLessonExists(long chatId, int lessonId){
        User user = userRepository.findByChatId(chatId).get();
        Lesson lesson = lessonRepository.findById(lessonId);

        return lessonsRegistrationRepository.existsByUserAndLesson(user, lesson);
    }

    public Optional<User> getUser(Long chatId){
        return userRepository.findByChatId(chatId);
    }
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }
}