package ru.zinoviev.questbot.message_handlerservice.domain.entity.quest;

import jakarta.persistence.*;
import lombok.*;
import ru.zinoviev.questbot.message_handlerservice.domain.entity.BotUser;
import ru.zinoviev.questbot.message_handlerservice.domain.entity.quest.nodes.QuestNode;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Quest {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String questName;
    private String description;

    @Enumerated(EnumType.STRING)
    private QuestType questType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(referencedColumnName = "telegram")
    private BotUser author;

    @Enumerated(EnumType.STRING)
    private QuestStatus questStatus;

    private LocalDate createdDate;

    private Boolean publicQuest;

    private Integer price;

    private Boolean running;

    @Transient
    private List<QuestNode> questNodes;

}