package ru.zinoviev.questbot.message_handlerservice.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.zinoviev.questbot.message_handlerservice.domain.entity.quest.nodes.PollQuestNode;

import java.util.List;
import java.util.Optional;

public interface PollQuestNodeRepository extends JpaRepository<PollQuestNode, Long> {

    List<PollQuestNode> findAllByQuestId(Long questId);

    @Query(nativeQuery = true, value = """
                     select count(pqn.id) 
                     from poll_quest_node pqn
                     where pqn.quest_id = ?1
                    """)
    int getNodesCountByQuestId(long questId);
}
