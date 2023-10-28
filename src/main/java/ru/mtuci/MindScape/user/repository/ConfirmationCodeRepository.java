package ru.mtuci.MindScape.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import ru.mtuci.MindScape.user.model.ConfirmationCode;
import ru.mtuci.MindScape.user.model.User;

import java.util.List;
import java.util.UUID;

public interface ConfirmationCodeRepository extends JpaRepository<ConfirmationCode, UUID> {
    ConfirmationCode findByEmail(String email);

    @Transactional
    void deleteByEmail(String email);
}