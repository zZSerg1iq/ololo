package ru.zinoviev.questbot.message_handlerservice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.zinoviev.questbot.message_handlerservice.domain.entity.quest.QuestStatus;
import ru.zinoviev.questbot.message_handlerservice.domain.entity.quest.QuestType;

import java.time.LocalDate;
import java.util.Objects;

@Getter
@Setter
@Builder
@ToString
public class QuestInfo {

    private QuestType questType;
    private QuestStatus questStatus;
    private Long id;
    private String description;
    private String questName;
    private LocalDate createdDate;

    public QuestInfo(QuestType questType, QuestStatus questStatus, Long id, String description, String questName, LocalDate createdDate) {
        this.questType = questType;
        this.questStatus = questStatus;
        this.id = id;
        this.description = description;
        this.questName = questName;
        this.createdDate = createdDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QuestInfo questInfo = (QuestInfo) o;
        return questType == questInfo.questType && Objects.equals(id, questInfo.id) && Objects.equals(createdDate, questInfo.createdDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(questType, id, createdDate);
    }
}
