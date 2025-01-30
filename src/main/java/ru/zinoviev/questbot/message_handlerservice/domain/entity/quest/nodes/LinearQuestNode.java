package ru.zinoviev.questbot.message_handlerservice.domain.entity.quest.nodes;

import jakarta.persistence.*;
import lombok.*;
import ru.zinoviev.questbot.message_handlerservice.domain.entity.quest.Quest;

import java.util.List;

@Entity
@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class LinearQuestNode implements QuestNode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer stepId;

    @ManyToOne
    private Quest quest;

    @OneToOne
    private PollQuestNode nextNode;

    @OneToOne
    private PollQuestNode prevNode;

    @ElementCollection
    private List<String> questMessages;

    @ElementCollection
    private List<String> expectedUserAnswers;
    private int requiredAnswersCount;

    @ElementCollection
    private List<String> correctAnswersReactMessages;
    private boolean reactOnCorrectAnswer;

    private boolean reactOnWrongAnswer;
    private boolean isRandom;

    @ElementCollection
    private List<String> wrongAnswersReactMessages;

    @OneToOne
    private GeoPoint geoPoint;

    private boolean switchToNextNodeWhenPointReached;
    private int pauseBeforeSwitching;

}
