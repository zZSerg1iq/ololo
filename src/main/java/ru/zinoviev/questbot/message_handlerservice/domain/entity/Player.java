package ru.zinoviev.questbot.message_handlerservice.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.zinoviev.questbot.message_handlerservice.domain.entity.quest.nodes.LinearQuestNode;
import ru.zinoviev.questbot.message_handlerservice.domain.entity.quest.nodes.PollQuestNode;
import ru.zinoviev.questbot.message_handlerservice.domain.entity.quest.RunningQuest;


@Getter
@Setter
@Entity
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne
    private BotUser user;

    @Enumerated(EnumType.STRING)
    private PlayerStatus playerStatus;

    private Long userTelegramId;

    private int points;

    @ManyToOne
    private RunningQuest runningQuest;

    @ManyToOne
    private PollQuestNode currentPollNode;

    @ManyToOne
    private LinearQuestNode currentLinearNode;

    @Override
    public String toString() {
        return "Player{" +
                "id=" + id +
                ", user=" + user.getId() +
                ", playerStatus=" + playerStatus +
                ", userTelegramId=" + userTelegramId +
                ", points=" + points +
                ", runningQuest=" + runningQuest.getId() +
                ", currentPollNode=" + currentPollNode.getId() +
                ", currentLinearNode=" + currentLinearNode +
                '}';
    }
}