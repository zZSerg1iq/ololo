package ru.zinoviev.questbot.message_handlerservice.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.zinoviev.questbot.message_handlerservice.domain.entity.quest.QuestRemoveMarker;

public interface QuestRemoveRepository extends JpaRepository<QuestRemoveMarker, Long> {
}
