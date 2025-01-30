package ru.zinoviev.questbot.message_handlerservice.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import ru.zinoviev.questbot.message_handlerservice.domain.entity.quest.Quest;
import ru.zinoviev.questbot.message_handlerservice.enums.RegistrationStatus;
import ru.zinoviev.questbot.message_handlerservice.enums.Scenario;

import java.util.List;

@Entity
@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class BotUser {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    /**
     * блок полей Телеграмма
     **/
    @Column(unique = true)
    private Long telegram;
    private String firstName;
    private String lastName;
    private String userName;
    private String languageCode;

    /**
     * имя, под которым пользователь будет известен в системе
     **/
    private String nickName;

    /**
     * пройдена ли регистрация пользователем
     **/
    @Enumerated(EnumType.STRING)
    private RegistrationStatus registrationStatus;

    /**
     * статус аккаунта: активен\ заблокирован
     **/
    @Enumerated(EnumType.STRING)
    private AccountStatus userAccountStatus;

    /**
     * текущий сценарий (роль) пользователя
     **/
    @Enumerated(EnumType.STRING)
    private Scenario scenario;

    /**
     * очки, заработанные пользователем за все время
     **/
    private int points;

    /**
     * друзья пользователя (функционал пока не раскрыт)
     **/
    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true,
            cascade = CascadeType.ALL, mappedBy = "questBotUser")
    @Fetch(FetchMode.JOIN)
    private List<UserFriend> friendList;

    /**
     * квесты пользователя
     **/
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    @Fetch(FetchMode.JOIN)
    private List<Quest> questList;


}
