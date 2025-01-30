package ru.zinoviev.questbot.message_handlerservice.domain.entity.quest;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.zinoviev.questbot.message_handlerservice.domain.entity.BotUser;
import ru.zinoviev.questbot.message_handlerservice.domain.entity.Player;
import ru.zinoviev.questbot.message_handlerservice.domain.entity.quest.nodes.QuestNode;


import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
public class RunningQuest {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private LocalDateTime runningOn;

    @ManyToOne
    private BotUser owner;

    private QuestType questType;

    @OneToOne
    private Quest quest;

    @OneToMany(mappedBy = "runningQuest", cascade = CascadeType.ALL)
    private List<Player> players;

}
