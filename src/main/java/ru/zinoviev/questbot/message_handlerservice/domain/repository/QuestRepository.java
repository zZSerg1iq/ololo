package ru.zinoviev.questbot.message_handlerservice.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.zinoviev.questbot.message_handlerservice.domain.entity.quest.Quest;

import java.util.List;

public interface QuestRepository extends JpaRepository<Quest, Long> {

//    @Query("select uq from UserQuest uq join fetch uq.author " +
//            "where uq.author =:authorId")
    List<Quest> findAllByAuthorTelegram(Long authorId);

    @Modifying
    @Query(nativeQuery = true,
            value = """
                    delete from quest q where q.id =?1
                    """)
    void deleteQuestById(long Id);
}
