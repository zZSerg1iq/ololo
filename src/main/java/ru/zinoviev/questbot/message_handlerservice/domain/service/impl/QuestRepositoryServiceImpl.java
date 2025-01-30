package ru.zinoviev.questbot.message_handlerservice.domain.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.zinoviev.questbot.message_handlerservice.domain.entity.BotUser;
import ru.zinoviev.questbot.message_handlerservice.domain.entity.Player;
import ru.zinoviev.questbot.message_handlerservice.domain.entity.PlayerStatus;
import ru.zinoviev.questbot.message_handlerservice.domain.entity.quest.*;
import ru.zinoviev.questbot.message_handlerservice.domain.entity.quest.nodes.LinearQuestNode;
import ru.zinoviev.questbot.message_handlerservice.domain.entity.quest.nodes.PollQuestNode;
import ru.zinoviev.questbot.message_handlerservice.domain.entity.quest.nodes.QuestNode;
import ru.zinoviev.questbot.message_handlerservice.domain.repository.*;
import ru.zinoviev.questbot.message_handlerservice.domain.service.QuestRepositoryService;
import ru.zinoviev.questbot.message_handlerservice.dto.QuestInfo;
import ru.zinoviev.questbot.message_handlerservice.dto.UserInfo;
import ru.zinoviev.questbot.message_handlerservice.dto.request.TGMessageDTO;
import ru.zinoviev.questbot.message_handlerservice.dto.request.message.TGPollDTO;
import ru.zinoviev.questbot.message_handlerservice.dto.request.message.TGPollOptionDTO;
import ru.zinoviev.questbot.message_handlerservice.enums.Scenario;
import ru.zinoviev.questbot.message_handlerservice.exception.EntityNotFound;
import ru.zinoviev.questbot.message_handlerservice.mapper.QuestDataMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
public class QuestRepositoryServiceImpl implements QuestRepositoryService {

    private final PollQuestNodeRepository pollQuestNodeRepository;
    private final LinearQuestNodeRepository linearQuestNodeRepository;
    private final QuestRepository questRepository;
    private final UserRepository userRepository;
    private final RunningQuestRepository runningQuestRepository;
    private final PlayerRepository playerRepository;
    private final QuestRemoveRepository questRemoveRepository;

    private final QuestDataMapper questDataMapper;

    @Override
    public void startQuestCreating(Long telegramId, QuestType type) {
        BotUser user = getUserByTelegramId(telegramId);
        user.setScenario(Scenario.CREATING);

        Quest quest = Quest.builder()
                .questType(type)
                .author(user)
                .questStatus(QuestStatus.UNDER_CONSTRUCTION)
                .build();
        questRepository.save(quest);
    }


    @Override
    @Transactional
    public boolean finishQuestCreating(Long questId) {
        Quest quest = getQuestById(questId);

        int nodesCount = switch (quest.getQuestType()) {
            case POLL -> pollQuestNodeRepository.getNodesCountByQuestId(questId);
            case LINEAR -> linearQuestNodeRepository.getNodesCountByQuestId(questId);
        };

        if (nodesCount == 0) {
            return false;
        }

        quest.setQuestStatus(QuestStatus.EDITING_NAME);
        questRepository.save(quest);
        return true;
    }

    @Override
    @Transactional
    public boolean addPollQuestNode(TGPollDTO poll, Long questId) {
        Quest quest = getQuestById(questId);

        List<PollQuestNode> questNodeList = getPollQuestNodes(questId);

        //последняя нода квеста для установки ссылки на добавляемую ноду
        PollQuestNode lastNode = null;
        if (!questNodeList.isEmpty()) {
            lastNode = questNodeList.get(questNodeList.size() - 1);
        }

        PollQuestNode newNode = new PollQuestNode();
        if (lastNode != null) {
            newNode.setPrevNode(lastNode);
        }

        Map<Integer, String> optionsMap = new HashMap<>();
        List<String> list = poll.getOptions().stream()
                .map(TGPollOptionDTO::getText)
                .toList();

        IntStream.range(0, list.size())
                .forEach(i -> optionsMap.put(i, list.get(i)));

        newNode.setExplanation(poll.getExplanation());
        newNode.setOpenPeriod(poll.getOpenPeriod());
        newNode.setQuest(quest);
        newNode.setOptions(optionsMap);
        newNode.setStepId(1);
        newNode.setCorrectOptionId(poll.getCorrectOptionId());
        newNode.setAllowMultipleAnswers(poll.getAllowMultipleAnswers());
        newNode.setIsAnonymous(poll.getIsAnonymous());
        newNode.setIsClosed(poll.getIsClosed());
        newNode.setType(poll.getType());
        newNode.setExplanation(poll.getExplanation());
        newNode.setProtectContent(poll.getProtectContent());

        if (lastNode != null) {
            lastNode.setNextNode(newNode);
            newNode.setStepId(lastNode.getStepId() + 1);
        }
        newNode.setQuestion("Вопрос "+newNode.getStepId()+":\n"+poll.getQuestion());
        pollQuestNodeRepository.save(newNode);
        return true;
    }

    @Override
    @Transactional
    public boolean addLinearQuestNode(TGMessageDTO message, Long questId) {
        // List<LinearQuestNode> questNodes = getLinearQuestNodes(questId);
        return true;
    }


    @Override
    @Transactional
    public void addQuestName(Long questId, String questName) {
        Quest quest = getQuestById(questId);

        quest.setQuestStatus(QuestStatus.EDITING_DESCRIPTION);
        quest.setQuestName(questName);
        questRepository.save(quest);
    }

    @Override
    @Transactional
    public void addQuestDescriptionAndStopCreatingQuest(Long questId, String description) {
        Quest quest = getQuestById(questId);

        long userId = quest.getAuthor().getTelegram();

        BotUser user = userRepository.findByTelegram(userId)
                .orElseThrow();

        user.setScenario(Scenario.USER);
        userRepository.save(user);

        quest.setQuestStatus(QuestStatus.COMPLETE);
        quest.setDescription(description);
        quest.setCreatedDate(LocalDate.now());
        quest.setPublicQuest(false);
        quest.setPrice(0);
        quest.setRunning(false);
        questRepository.save(quest);
    }


    @Override
    public List<QuestInfo> getQuestInfoListByUserId(Long userTelegramId) {
        List<Quest> runningQuests = runningQuestRepository.findRunningUserQuestsByUserTelegramId(userTelegramId)
                .stream()
                .map(RunningQuest::getQuest)
                .toList();

        return questRepository.findAllByAuthorTelegram(userTelegramId).stream()
                .filter(quest -> quest.getQuestStatus().equals(QuestStatus.COMPLETE))
                .sorted(Comparator.comparing(Quest::getCreatedDate))
                .map(quest -> {
                            String questName = runningQuests.contains(quest) ?
                                    quest.getQuestName() + " ( запущен \uFE0F )" :
                                    quest.getQuestName();

                            return QuestInfo.builder()
                                    .description(quest.getDescription())
                                    .id(quest.getId())
                                    .questStatus(quest.getQuestStatus())
                                    .questType(quest.getQuestType())
                                    .questName(questName)
                                    .createdDate(quest.getCreatedDate())
                                    .build();
                        }
                )
                .collect(Collectors.toList());
    }

    @Override
    public QuestInfo getQuestInfoByUserId(Long authorId) {
        return questRepository.findAllByAuthorTelegram(authorId)
                .stream()
                .filter(quest -> !quest.getQuestStatus().equals(QuestStatus.COMPLETE))
                .map(quest -> {
                    return QuestInfo.builder()
                            .description(quest.getDescription())
                            .id(quest.getId())
                            .questStatus(quest.getQuestStatus())
                            .questType(quest.getQuestType())
                            .questName(quest.getQuestName())
                            .createdDate(quest.getCreatedDate())
                            .build();
                })
                .findFirst()
                .orElseThrow();
    }


    private List<PollQuestNode> getPollQuestNodes(Long questId) {
        return pollQuestNodeRepository
                .findAllByQuestId(questId).stream()
                .sorted(Comparator.comparingInt(PollQuestNode::getStepId))
                .toList();
    }

    private List<LinearQuestNode> getLinearQuestNodes(Long questId) {
        return linearQuestNodeRepository
                .findAllByQuestId(questId).stream()
                .sorted(Comparator.comparingInt(LinearQuestNode::getStepId))
                .toList();
    }

    @Override
    @Transactional
    public void removeEmptyQuest(Long telegramId, Long questId) {
        BotUser user = getUserByTelegramId(telegramId);
        user.setScenario(Scenario.USER);
        userRepository.save(user);
        questRepository.deleteQuestById(questId);
    }

    @Override
    @Transactional
    public QuestInfo runQuest(long questId, Long userTelegramId) {
        BotUser user = getUserByTelegramId(userTelegramId);

        RunningQuest runningQuest = new RunningQuest();
        Player player = new Player();

        player.setRunningQuest(runningQuest);
        player.setUser(user);
        player.setPlayerStatus(PlayerStatus.PLAYING);
        player.setUserTelegramId(userTelegramId);

        Quest quest = getQuestById(questId);
        quest.setQuestNodes(getQuestNodesByQuestType(questId, quest.getQuestType()));
        if (quest.getQuestType().equals(QuestType.POLL)) {
            player.setCurrentPollNode((PollQuestNode) quest.getQuestNodes().get(0));
        } else {
            player.setCurrentLinearNode((LinearQuestNode) quest.getQuestNodes().get(0));
        }

        runningQuest.setRunningOn(LocalDateTime.now());
        runningQuest.setQuest(quest);
        runningQuest.setOwner(user);
        runningQuest.setQuestType(quest.getQuestType());
        runningQuest.setPlayers(List.of(player));
        long runningQuestId = runningQuestRepository.save(runningQuest).getId();

//        user.setScenario(Scenario.ADMIN);
//        userRepository.save(user);

        return QuestInfo.builder()
                .questName(quest.getQuestName())
                .id(runningQuestId)
                .build();
    }

    private List<QuestNode> getQuestNodesByQuestType(Long questId, QuestType questType) {
        return switch (questType) {
            case POLL -> getPollQuestNodes(questId).stream()
                    .map(QuestNode.class::cast)
                    .toList();

            case LINEAR -> getLinearQuestNodes(questId).stream()
                    .map(QuestNode.class::cast)
                    .toList();
        };
    }

    @Override
    @Transactional
    public void removeQuest(long questId) {
        Quest quest = getQuestById(questId);

        QuestRemoveMarker questRemoveMarker = new QuestRemoveMarker();
        questRemoveMarker.setQuest(quest);
        questRemoveMarker.setDeleteDate(LocalDate.now().plusMonths(2));
        questRemoveRepository.save(questRemoveMarker);
    }

    @Override
    @Transactional
    public UserInfo playQuest(final Long userId, final long questId) {
        RunningQuest runningQuest = runningQuestRepository.findById(questId)
                .orElseThrow(() -> new EntityNotFound("Запущенный квест " + questId + " не найден, регистрация на квест невозможна"));

        BotUser user = userRepository.findByTelegram(userId)
                .orElseThrow(() -> new EntityNotFound("Пользователь не найден: " + userId));

        List<Player> players = runningQuest.getPlayers();

        Player userPlayer = players.stream()
                .filter(player -> player.getUserTelegramId().equals(userId))
                .findFirst()
                .orElseGet(() -> null);

        QuestNode firstNode = getQuestNodesByQuestType(runningQuest.getQuest().getId(), runningQuest.getQuestType()).get(0);

        if (userPlayer != null) {
            switch (runningQuest.getQuestType()) {
                case LINEAR -> {
                    if (userPlayer.getCurrentLinearNode() == null) {
                        return null;
                    }
                }
                case POLL -> {
                    if (userPlayer.getCurrentPollNode() == null) {
                        return null;
                    }
                }
            }

            userPlayer.setPlayerStatus(PlayerStatus.PLAYING);
        } else {
            userPlayer = new Player();
            userPlayer.setUserTelegramId(userId);
            userPlayer.setPoints(0);
            userPlayer.setUser(user);
            userPlayer.setRunningQuest(runningQuest);
            userPlayer.setPlayerStatus(PlayerStatus.PLAYING);
            switch (runningQuest.getQuestType()) {
                case LINEAR -> userPlayer.setCurrentLinearNode((LinearQuestNode) firstNode);
                case POLL -> userPlayer.setCurrentPollNode((PollQuestNode) firstNode);
            }
        }

        if (runningQuest.getOwner().getTelegram().equals(userId)) {
            user.setScenario(Scenario.ADMIN);
        } else {
            user.setScenario(Scenario.PLAYER);
        }

        System.out.println(userPlayer);

        userRepository.save(user);
        playerRepository.save(userPlayer);

        return UserInfo.builder()
                .scenario(user.getScenario())
                .build();
    }

    private BotUser getUserByTelegramId(Long telegramId) {
        return userRepository.findByTelegram(telegramId)
                .orElseThrow(() -> new EntityNotFound("Пользователь не найден: " + telegramId));
    }

    private Quest getQuestById(Long questId) {
        return questRepository.findById(questId)
                .orElseThrow(() -> new EntityNotFound("Пользователь не найден: " + questId));
    }
}
