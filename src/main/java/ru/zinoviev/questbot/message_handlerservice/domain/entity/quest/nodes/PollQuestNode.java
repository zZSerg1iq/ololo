package ru.zinoviev.questbot.message_handlerservice.domain.entity.quest.nodes;

import jakarta.persistence.*;
import lombok.*;
import ru.zinoviev.questbot.message_handlerservice.domain.entity.quest.Quest;

import java.util.List;
import java.util.Map;

@Entity
@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class PollQuestNode implements QuestNode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer stepId;

    private String question;

    @ElementCollection
    private Map<Integer, String> options;

    @ElementCollection
    private List<Integer> correctOptionId;

    private Integer openPeriod;
    private String explanation;

    private String type;

    @ManyToOne
    private Quest quest;

    @OneToOne
    private PollQuestNode nextNode;

    @OneToOne
    private PollQuestNode prevNode;

    private Boolean isAnonymous;

    private Boolean allowMultipleAnswers;

    private Boolean isClosed;
    private String explanationParseMode;
    private Boolean protectContent;
}
