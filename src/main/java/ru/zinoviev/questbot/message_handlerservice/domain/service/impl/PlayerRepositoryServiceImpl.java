package ru.zinoviev.questbot.message_handlerservice.domain.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.zinoviev.questbot.message_handlerservice.domain.entity.BotUser;
import ru.zinoviev.questbot.message_handlerservice.domain.entity.Player;
import ru.zinoviev.questbot.message_handlerservice.domain.entity.PlayerStatus;
import ru.zinoviev.questbot.message_handlerservice.domain.entity.quest.nodes.PollQuestNode;
import ru.zinoviev.questbot.message_handlerservice.domain.repository.PlayerRepository;
import ru.zinoviev.questbot.message_handlerservice.domain.repository.UserRepository;
import ru.zinoviev.questbot.message_handlerservice.domain.service.PlayerRepositoryService;
import ru.zinoviev.questbot.message_handlerservice.domain.service.QuestRepositoryService;
import ru.zinoviev.questbot.message_handlerservice.dto.request.TGMessageDTO;
import ru.zinoviev.questbot.message_handlerservice.dto.request.TGUpdateDTO;
import ru.zinoviev.questbot.message_handlerservice.dto.response.ResponseMessageDTO;
import ru.zinoviev.questbot.message_handlerservice.dto.response.ResponsePollDTO;
import ru.zinoviev.questbot.message_handlerservice.enums.Scenario;
import ru.zinoviev.questbot.message_handlerservice.exception.EntityNotFound;
import ru.zinoviev.questbot.message_handlerservice.response.ReplyKeyboardService;
import ru.zinoviev.questbot.message_handlerservice.response.ResponseMessageService;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PlayerRepositoryServiceImpl implements PlayerRepositoryService {

    private final QuestRepositoryService questRepositoryService;
    private final PlayerRepository playerRepository;
    private final ResponseMessageService messageService;
    private final UserRepository userRepository;
    private final ReplyKeyboardService keyboardService;

    @Override
    @Transactional
    public List<ResponseMessageDTO> getNodeEventByPlayerId(TGMessageDTO messageDTO) {
        Long playerId = messageDTO.getChatId();
        Player player = playerRepository.findPlayerByUserTelegramId(playerId)
                .stream()
                .filter(p -> p.getPlayerStatus().equals(PlayerStatus.PLAYING))
                .findFirst()
                .orElseThrow(()-> new EntityNotFound("Пользователь не найден"));

        List<ResponseMessageDTO> nodes = new ArrayList<>();

        if (player.getCurrentPollNode() != null) {
            nodes = getPollNodeEvent(player, messageDTO);
        } else if (player.getCurrentLinearNode() != null) {
            // nodes = getLinearNodeEvent(player, messageDTO);
        } else {
            ResponseMessageDTO message = messageService.getEditMessage(messageDTO);
            message.setText("Вы уже завершили прохождение этого квеста, ваш результат: "+player.getPoints());

            ResponseMessageDTO message2 = messageService.getMessage(messageDTO);
            message2.setReplyMarkup(keyboardService.getUserReplyKeyboard());
            message2.setText("Попробуйте другой квест");
            return List.of(message, message2);
        }

        return nodes;
    }

    @Override
    @Transactional
    public List<ResponseMessageDTO> registerAnswerAndContinue(TGUpdateDTO update) {
        Long playerId = update.getFrom().getUserId();

        Player player = playerRepository.findPlayerByUserTelegramId(playerId)
                .stream()
                .filter(p -> p.getPlayerStatus().equals(PlayerStatus.PLAYING))
                .findFirst()
                .orElseThrow(()-> new EntityNotFound("Пользователь не найден"));

        List<ResponseMessageDTO> responseMessages = null;
        ResponseMessageDTO messageDTO = messageService.getMessage(update.getMessage());

        if (player.getCurrentPollNode() != null | player.getCurrentLinearNode() != null) {
            if (player.getCurrentPollNode() != null) {
                pollNodeRegisterAnswer(player, update);
                responseMessages = getPollNodeEvent(player, update.getMessage());
            } else if (player.getCurrentLinearNode() != null) {
                //linearNodeAnswerAction(player, update);
                //responseMessages = getLinearNodeEvent(player, update.getMessage());
            }
        } else {
            System.out.println("2222222222222222222222222222222222222");
            responseMessages = new ArrayList<>();
            messageDTO.setText("\uD83E\uDD73 \nВы завершили прохождение квеста!\nВаш результат: "+player.getPoints());
            messageDTO.setReplyMarkup(keyboardService.getUserReplyKeyboard());
            responseMessages.add(messageDTO);
            System.out.println(responseMessages);
        }
        return responseMessages;
    }

    @Override
    @Transactional
    public void quitQuest(Long playerId) {
        Player player = playerRepository.findPlayerByUserTelegramId(playerId)
                .stream()
                .filter(p -> p.getPlayerStatus().equals(PlayerStatus.PLAYING))
                .findFirst()
                .orElseThrow(()-> new EntityNotFound("Пользователь не найден"));


        player.setPlayerStatus(PlayerStatus.LEAVE);
        playerRepository.save(player);

        BotUser user = player.getUser();
        user.setScenario(Scenario.USER);
        userRepository.save(user);
    }

    private void pollNodeRegisterAnswer(Player player, TGUpdateDTO update) {
        PollQuestNode pollQuestNode = player.getCurrentPollNode();

        List<Integer> correctAnswers = new ArrayList<>(pollQuestNode.getCorrectOptionId());
        List<Integer> userAnswers = update.getPollAnswerDTO().getOptionIds();

        Iterator<Integer> iterator = userAnswers.iterator();

        while (iterator.hasNext()) {
            Integer answer = iterator.next();
            if (correctAnswers.contains(answer)) {
                player.setPoints(player.getPoints() + 1);
                correctAnswers.remove(answer);
            } else {
                player.setPoints(player.getPoints() - 1);
                if (player.getPoints() <= 0) {
                    player.setPoints(0);
                }
            }
            iterator.remove();
        }

        player.setCurrentPollNode(player.getCurrentPollNode().getNextNode());

        int penaltyPoints = 0;
        if (!correctAnswers.isEmpty()) {
            penaltyPoints += correctAnswers.size();
        }
        if (!userAnswers.isEmpty()) {
            penaltyPoints += correctAnswers.size();
        }

        player.setPoints(player.getPoints() - penaltyPoints);
        if (player.getPoints() < 0) {
            player.setPoints(0);
        }

        playerRepository.save(player);
    }

    private List<ResponseMessageDTO> getPollNodeEvent(Player player, TGMessageDTO messageDTO) {
        List<ResponseMessageDTO> pollNodeEventMessages = new ArrayList<>();

        if (player.getCurrentPollNode() == null) {
            BotUser user = player.getUser();
            user.setScenario(Scenario.USER);
            userRepository.save(user);
            player.setPlayerStatus(PlayerStatus.LEAVE);

            ResponseMessageDTO questEndMessage = messageService.getMessage(messageDTO);
            questEndMessage.setText("Поздавляем! Вы завершили квест!");
            pollNodeEventMessages.add(questEndMessage);

            ResponseMessageDTO keyboardMessage = messageService.getMessage(messageDTO);
            keyboardMessage.setText("Ваш результат: " + player.getPoints());
            keyboardMessage.setReplyMarkup(keyboardService.getUserReplyKeyboard());
            pollNodeEventMessages.add(keyboardMessage);

            return pollNodeEventMessages;
        }

        if (player.getCurrentPollNode().getPrevNode() == null){
            ResponseMessageDTO keyboardMessage = messageService.getDeleteMessage(messageDTO);
            pollNodeEventMessages.add(keyboardMessage);
        }


        PollQuestNode currentPollNode = player.getCurrentPollNode();
        ResponsePollDTO pollDTO = new ResponsePollDTO();

        pollDTO.setQuestion(currentPollNode.getQuestion());
        pollDTO.setOptions(new ArrayList<>(currentPollNode.getOptions().values()));
        pollDTO.setExplanation(currentPollNode.getExplanation());
        pollDTO.setType(currentPollNode.getType());

        if (currentPollNode.getType().equals("quiz")) {
            pollDTO.setCorrectOptionId(currentPollNode.getCorrectOptionId().get(0));
            pollDTO.setAllowMultipleAnswers(false);
        } else {
            pollDTO.setCorrectOptionId(null);
            pollDTO.setAllowMultipleAnswers(true);
        }

        ResponseMessageDTO nextQuestionMessage = messageService.getMessage(messageDTO);
        nextQuestionMessage.setText("Вопрос: " + pollDTO.getQuestion());
        nextQuestionMessage.setPollDTO(pollDTO);

        pollNodeEventMessages.add(nextQuestionMessage);
        return pollNodeEventMessages;
    }


}
