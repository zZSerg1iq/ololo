package ru.zinoviev.questbot.message_handlerservice.mapper;

import org.mapstruct.Mapper;
import ru.zinoviev.questbot.message_handlerservice.domain.entity.quest.Quest;
import ru.zinoviev.questbot.message_handlerservice.dto.QuestInfo;

@Mapper(componentModel = "spring")
public interface QuestDataMapper {

    QuestInfo getQuestData(Quest quest);
}
