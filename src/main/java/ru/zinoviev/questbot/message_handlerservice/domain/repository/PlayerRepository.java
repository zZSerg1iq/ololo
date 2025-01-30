package ru.zinoviev.questbot.message_handlerservice.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.zinoviev.questbot.message_handlerservice.domain.entity.Player;

import java.util.List;
import java.util.Optional;

public interface PlayerRepository extends JpaRepository<Player, Long> {

    @Query("""
        select p from Player p
        where p.userTelegramId =:userTelegramId 
        and p.playerStatus = "PLAYING"
        """)
    List<Player> findPlayerByUserTelegramId(Long userTelegramId);
}
