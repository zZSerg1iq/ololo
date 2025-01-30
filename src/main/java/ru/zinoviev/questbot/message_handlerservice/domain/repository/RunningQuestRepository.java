package ru.zinoviev.questbot.message_handlerservice.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.zinoviev.questbot.message_handlerservice.domain.entity.quest.RunningQuest;

import java.util.List;

public interface RunningQuestRepository extends JpaRepository<RunningQuest, Long> {

    @Query("""
        select rq from RunningQuest rq
        join fetch rq.quest
        join fetch rq.owner
        where rq.owner.telegram =:userTelegramId
        """)
    List<RunningQuest> findRunningUserQuestsByUserTelegramId(Long userTelegramId);


    @Query(nativeQuery = true, value = """
        select count(rq) from running_quest rq
        join bot_user bu on rq.owner_id = bu.id
        where bu.telegram =?1
        """)
    Long findRunningUserCountByUserTelegramId(Long userTelegramId);

    @Query("""
        select rq from RunningQuest rq
        join fetch rq.quest
        where rq.quest.id =:questId
        """)
    RunningQuest findRunningQuestsByQuestId(Long questId);
}
