/**
 * <p>Описание:</p>
 * Сервисная реализация интерфейса UserDetailsService для работы с данными пользователей.
 *
 * <p>Поля:</p>
 * <ul>
 *     <li>userRepository: Репозиторий для работы с пользователями.</li>
 *     <li>expertRepository: Репозиторий для работы с экспертами.</li>
 *     <li>researcherRepository: Репозиторий для работы с исследователями.</li>
 * </ul>
 *
 * <p>Методы:</p>
 * <ul>
 *     <li><b>loadUserByUsername</b> - Загрузка данных пользователя по электронной почте.</li>
 * </ul>
 */

package ru.mtuci.MindScape.security;

import org.springframework.beans.factory.annotation.Autowired;
import ru.mtuci.MindScape.user.model.Expert;
import ru.mtuci.MindScape.user.model.Researcher;
import ru.mtuci.MindScape.user.model.User;
import ru.mtuci.MindScape.user.repository.ExpertRepository;
import ru.mtuci.MindScape.user.repository.ResearcherRepository;
import ru.mtuci.MindScape.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExpertRepository expertRepository;

    @Autowired
    private ResearcherRepository researcherRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            return UserDetailsImplFactory.fromUser(userOpt.get());
        }

        Optional<Expert> expertOpt = expertRepository.findByEmail(email);
        if (expertOpt.isPresent()) {
            return UserDetailsImplFactory.fromExpert(expertOpt.get());
        }

        Optional<Researcher> researcherOpt = researcherRepository.findByEmail(email);
        if (researcherOpt.isPresent()) {
            return UserDetailsImplFactory.fromResearcher(researcherOpt.get());
        }

        throw new UsernameNotFoundException("User not found with email: " + email);
    }
}