package ru.zinoviev.questbot.message_handlerservice.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Entity
@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserFriend {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private Long userId;
    private String firstName;
    private String lastName;
    private String userName;

    @ManyToOne
    private BotUser questBotUser;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserFriend friend = (UserFriend) o;
        return Objects.equals(userId, friend.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(userId);
    }
}
