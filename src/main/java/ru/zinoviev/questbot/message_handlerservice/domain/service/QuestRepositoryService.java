package ru.zinoviev.questbot.message_handlerservice.domain.service;

import ru.zinoviev.questbot.message_handlerservice.domain.entity.quest.QuestType;
import ru.zinoviev.questbot.message_handlerservice.dto.QuestInfo;
import ru.zinoviev.questbot.message_handlerservice.dto.UserInfo;
import ru.zinoviev.questbot.message_handlerservice.dto.request.TGMessageDTO;
import ru.zinoviev.questbot.message_handlerservice.dto.request.message.TGPollDTO;

import java.util.List;

public interface QuestRepositoryService {

    void startQuestCreating(Long userId, QuestType type);
    boolean finishQuestCreating(Long questId);

    boolean addPollQuestNode(TGPollDTO poll, Long questId);
    boolean addLinearQuestNode(TGMessageDTO message, Long questId);

    void addQuestName(Long questId, String questName);
    void addQuestDescriptionAndStopCreatingQuest(Long questId, String description);

    List<QuestInfo> getQuestInfoListByUserId(Long userTelegramId);

    QuestInfo getQuestInfoByUserId(Long userId);

    void removeEmptyQuest(Long userId, Long questId);

    QuestInfo runQuest(long questId, Long userTelegramId);

    void removeQuest(long questId);

    UserInfo playQuest(Long userTelegramId, long questId);
}
