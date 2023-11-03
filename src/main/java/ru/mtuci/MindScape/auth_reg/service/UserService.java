/**
 * <p>Описание:</p>
 * Сервисный класс для управления пользователями. Позволяет выполнять операции поиска, создания и сохранения пользователей в соответствии с их ролями.
 * Поддерживает работу с пользователями общего типа, а также с пользователями со специальными ролями, такими как эксперты и исследователи.
 *
 * <p>Список методов:</p>
 * <ul>
 *     <li><b>findUserByEmail</b> - Осуществляет поиск пользователя по email среди всех типов пользователей, возвращая Optional пользователя, если он найден, и Optional.empty в противном случае.</li>
 *     <li><b>createUser</b> - Создает новый объект пользователя на основе указанной роли. Поддерживает роли "user", "expert" и "researcher".</li>
 *     <li><b>saveUser</b> - Сохраняет объект пользователя в соответствующий репозиторий, основываясь на его роли. Осуществляет приведение типа к нужному подклассу пользователя и вызывает метод сохранения соответствующего репозитория.</li>
 * </ul>
 */

package ru.mtuci.MindScape.auth_reg.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mtuci.MindScape.auth_reg.dto.BaseRegistrationDto;
import ru.mtuci.MindScape.user.model.BaseUser;
import ru.mtuci.MindScape.user.model.Expert;
import ru.mtuci.MindScape.user.model.Researcher;
import ru.mtuci.MindScape.user.model.User;
import ru.mtuci.MindScape.user.repository.ExpertRepository;
import ru.mtuci.MindScape.user.repository.ResearcherRepository;
import ru.mtuci.MindScape.user.repository.UserRepository;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class UserService {
    private UserRepository userRepository;
    private ExpertRepository expertRepository;
    private ResearcherRepository researcherRepository;

    public Optional<?> findUserByEmail(String email) {
        return Stream.<Function<String, Optional<?>>>of(
                        userRepository::findByEmail,
                        expertRepository::findByEmail,
                        researcherRepository::findByEmail)
                        .map(repoMethod -> repoMethod.apply(email))
                        .filter(Optional::isPresent)
                        .findFirst()
                        .orElse(Optional.empty());
    }

    public BaseUser createUser(String role) {
        if (role.equalsIgnoreCase("expert")) {
            return new Expert();
        } else if (role.equalsIgnoreCase("researcher")) {
            return new Researcher();
        } else {
            return new User();
        }
    }

    public void saveUser(String role, BaseUser entity) {
        if (role.equalsIgnoreCase("user")) {
            userRepository.save((User) entity);
        } else if (role.equalsIgnoreCase("expert")) {
            expertRepository.save((Expert) entity);
        } else if (role.equalsIgnoreCase("researcher")){
            researcherRepository.save((Researcher) entity);
        }
    }
}