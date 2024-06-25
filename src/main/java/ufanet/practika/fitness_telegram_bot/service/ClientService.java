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
        /*if (lessonsRegistrationRepository.existsByUserAndLesson(user, lesson)) {
            Optional<LessonRegistration> lessonRegistrationToDelete = Optional.empty();

            Set<LessonRegistration> lessonRegistrationSet = lesson.getLessonRegistrations();

            for (LessonRegistration lessonRegistration : lessonRegistrationSet) {
                if (lessonRegistration.getUser().equals(user)) {
                    lessonRegistrationToDelete = Optional.of(lessonRegistration);
                    lessonsRegistrationRepository.delete(lessonRegistration);
                    break;
                }
            }
            lessonRegistrationToDelete.ifPresent(lessonRegistrationSet::remove);
            lesson.setOccupiedPlaces(lesson.getOccupiedPlaces() - 1);
            lessonRepository.save(lesson);
        }*/
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