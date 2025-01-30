package ru.zinoviev.questbot.message_handlerservice.domain.service;

import ru.zinoviev.questbot.message_handlerservice.dto.QuestInfo;

import java.util.List;

public interface RunningQuestRepositoryService {

    QuestInfo getQuestInfoByQuestId(Long userTelegramId);

    List<QuestInfo> findRunningUserQuestsByUserTelegramId(Long userTelegramId);

    QuestInfo getQuestInfoByPlayerId(Long questId);

    Long findRunningQuestCountByUserTelegramId(Long questId);

    void stopRunningQuest(long questId);
}
