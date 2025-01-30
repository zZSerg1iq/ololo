package ru.zinoviev.questbot.message_handlerservice.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.zinoviev.questbot.message_handlerservice.domain.entity.BotUser;

import java.util.Optional;

public interface UserRepository extends JpaRepository<BotUser, Long> {

    Optional<BotUser> findByTelegram(Long userId);

}
