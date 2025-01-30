package ru.zinoviev.questbot.message_handlerservice.domain.entity.quest;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class QuestRemoveMarker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate deleteDate;

    @OneToOne
    private Quest quest;
}
