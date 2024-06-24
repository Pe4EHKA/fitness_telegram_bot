package ufanet.practika.fitness_telegram_bot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ufanet.practika.fitness_telegram_bot.entity.*;
import ufanet.practika.fitness_telegram_bot.repository.*;

import java.time.LocalDateTime;
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
    @Autowired
    private UserRoleRepository userRoleRepository;
    @Autowired
    private RoleRepository roleRepository;

    public List<Lesson> getAvailableLessonsByDate(LocalDateTime date) {
        List<Lesson> availableLessons = lessonRepository.findByStartDateTime(date);
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
    public void registrateUser(UserRole userRole){
        userRepository.save(userRole.getUser());
        userRoleRepository.save(userRole);
    }
    public Role getRole(String userRole){
        return roleRepository.findByRole(userRole);
    }
    public boolean isExistingUser(Long chatId){
        return userRepository.existsByChatId(chatId);
    }
    public boolean isExistingRole(String role){
        return roleRepository.existsByRole(role);
    }
    // Временный метод, для удобства разработки: сохраняет роль
    public void registrateRole(Role role){
        roleRepository.save(role);
    }
}