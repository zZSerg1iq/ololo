package ru.zinoviev.questbot.message_handlerservice.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.zinoviev.questbot.message_handlerservice.domain.entity.quest.nodes.LinearQuestNode;

import java.util.List;

public interface LinearQuestNodeRepository extends JpaRepository<LinearQuestNode, Long> {

    List<LinearQuestNode> findAllByQuestId(Long questId);

    @Query("""
                     select count(lqn)
                     from LinearQuestNode lqn
                     where lqn.quest = ?1
                    """)
    int getNodesCountByQuestId(long questId);
}
