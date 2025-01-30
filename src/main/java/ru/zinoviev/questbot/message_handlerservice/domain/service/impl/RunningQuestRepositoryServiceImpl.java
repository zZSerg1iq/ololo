package ru.zinoviev.questbot.message_handlerservice.domain.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.zinoviev.questbot.message_handlerservice.domain.entity.BotUser;
import ru.zinoviev.questbot.message_handlerservice.domain.entity.Player;
import ru.zinoviev.questbot.message_handlerservice.domain.entity.PlayerStatus;
import ru.zinoviev.questbot.message_handlerservice.domain.entity.quest.Quest;
import ru.zinoviev.questbot.message_handlerservice.domain.entity.quest.RunningQuest;
import ru.zinoviev.questbot.message_handlerservice.domain.entity.quest.nodes.LinearQuestNode;
import ru.zinoviev.questbot.message_handlerservice.domain.entity.quest.nodes.PollQuestNode;
import ru.zinoviev.questbot.message_handlerservice.domain.entity.quest.nodes.QuestNode;
import ru.zinoviev.questbot.message_handlerservice.domain.repository.PlayerRepository;
import ru.zinoviev.questbot.message_handlerservice.domain.repository.RunningQuestRepository;
import ru.zinoviev.questbot.message_handlerservice.domain.repository.UserRepository;
import ru.zinoviev.questbot.message_handlerservice.domain.service.QuestRepositoryService;
import ru.zinoviev.questbot.message_handlerservice.domain.service.RunningQuestRepositoryService;
import ru.zinoviev.questbot.message_handlerservice.dto.QuestInfo;
import ru.zinoviev.questbot.message_handlerservice.enums.Scenario;
import ru.zinoviev.questbot.message_handlerservice.exception.EntityNotFound;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RunningQuestRepositoryServiceImpl implements RunningQuestRepositoryService {

    private final RunningQuestRepository runningQuestRepository;
    private final UserRepository userRepository;
    private final PlayerRepository playerRepository;

    @Override
    public List<QuestInfo> findRunningUserQuestsByUserTelegramId(Long userTelegramId) {
        return runningQuestRepository.findRunningUserQuestsByUserTelegramId(userTelegramId)
                .stream()
                .map(rq -> {
                    Quest quest = rq.getQuest();
                    return QuestInfo.builder()
                            .description(quest.getDescription())
                            .id(rq.getId())
                            .questStatus(quest.getQuestStatus())
                            .questType(quest.getQuestType())
                            .questName(quest.getQuestName())
                            .createdDate(quest.getCreatedDate())
                            .build();
                })
                .toList();
    }

    @Override
    public Long findRunningQuestCountByUserTelegramId(Long userTelegramId) {
        return runningQuestRepository.findRunningUserCountByUserTelegramId(userTelegramId);
    }

    @Override
    public QuestInfo getQuestInfoByPlayerId(Long playerId) {
        Player player = playerRepository.findPlayerByUserTelegramId(playerId)
                .stream()
                .filter(p -> p.getPlayerStatus().equals(PlayerStatus.PLAYING))
                .findFirst()
                .orElseThrow(()-> new EntityNotFound("Пользователь не найден"));

        RunningQuest runningQuest = player.getRunningQuest();

        return QuestInfo.builder()
                .questName(runningQuest.getQuest().getQuestName())
                .id(runningQuest.getId())
                .build();
    }

    @Override
    public QuestInfo getQuestInfoByQuestId(Long questId) {
        RunningQuest runningQuest = runningQuestRepository.findRunningQuestsByQuestId(questId);
        if (runningQuest == null){
            return null;
        }
        return QuestInfo.builder()
                .questName(runningQuest.getQuest().getQuestName())
                .id(runningQuest.getId())
                .build();
    }

    @Override
    @Transactional
    public void stopRunningQuest(long questId) {
        RunningQuest runningQuest = runningQuestRepository.findById(questId)
                .orElseThrow(() -> new EntityNotFoundException("Не найден запущенный квест id:" + questId));

        List<Player> players = runningQuest.getPlayers();

        for (Player player : players) {
            BotUser botUser = player.getUser();
            botUser.setPoints(botUser.getPoints() + player.getPoints());
            botUser.setScenario(Scenario.USER);
            userRepository.save(botUser);
        }

        runningQuestRepository.deleteById(runningQuest.getId());
    }

//    @Override
//    @Transactional
//    public boolean playQuest(final Long userId, final long questId) {
//        RunningQuest runningQuest = runningQuestRepository.findById(questId)
//                .orElseThrow(() -> new EntityNotFound("Запущенный квест " + questId + " не найден, регистрация на квест невозможна"));
//
//        BotUser user = userRepository.findByTelegram(userId)
//                .orElseThrow(()-> new EntityNotFound("Пользователь не найден: "+userId));
//
//        List<Player> players = runningQuest.getPlayers();
//
//        Player userPlayer = players.stream()
//                .filter(player -> player.getUserTelegramId().equals(userId))
//                .findFirst()
//                .orElseGet(null);
//
//        QuestNode firstNode = runningQuest.getQuest().getQuestNodes().get(0);
//
//        if (userPlayer != null) {
//            switch (runningQuest.getQuestType()){
//                case LINEAR -> {
//                    if (userPlayer.getCurrentLinearNode() == null) {
//                        user.setScenario(Scenario.USER);
//                        userRepository.save(user);
//                        return false;
//                    }
//                }
//                case POLL -> {
//                    if (userPlayer.getCurrentPollNode() == null) {
//                        user.setScenario(Scenario.USER);
//                        userRepository.save(user);
//                        return false;
//                    }
//                }
//            }
//
//
//            userPlayer.setPlayerStatus(PlayerStatus.PLAYING);
//        } else {
//            userPlayer = new Player();
//            userPlayer.setUserTelegramId(userId);
//            userPlayer.setPoints(0);
//            userPlayer.setRunningQuest(runningQuest);
//            userPlayer.setPlayerStatus(PlayerStatus.PLAYING);
//
//            switch (runningQuest.getQuestType()){
//                case LINEAR -> userPlayer.setCurrentLinearNode((LinearQuestNode) firstNode);
//                case POLL -> userPlayer.setCurrentPollNode((PollQuestNode) firstNode);
//            }
//        }
//
//        if (runningQuest.getOwner().getTelegram().equals(userId)){
//            user.setScenario(Scenario.ADMIN);
//        } else {
//            user.setScenario(Scenario.PLAYER);
//        }
//        userRepository.save(user);
//        playerRepository.save(userPlayer);
//        return true;
//    }
}
