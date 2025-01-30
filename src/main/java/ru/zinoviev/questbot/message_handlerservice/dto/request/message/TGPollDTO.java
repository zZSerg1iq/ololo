package ru.zinoviev.questbot.message_handlerservice.dto.request.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class TGPollDTO {
    private Long chatId; ///< Unique identifier for the target chat or username of the target channel (in the format @channelusername)
    private String question; ///< Poll question, 1-300 characters
    private List<TGPollOptionDTO> options; ///< List of answer options, 2-10 strings 1-100 characters each
    private Boolean isAnonymous; ///< Optional	True, if the poll needs to be anonymous, defaults to True
    private String type; ///< Optional	Poll type, “quiz” or “regular”, defaults to “regular”
    private Boolean allowMultipleAnswers; ///< Optional	True, if the poll allows multiple answers, ignored for polls in quiz mode, defaults to False
    private List<Integer> correctOptionId; ///< Optional	0-based identifier of the correct answer option, required for polls in quiz mode
    private Boolean isClosed; ///< Optional	Pass True, if the poll needs to be immediately closed
    private Integer openPeriod; ///< Optional. Amount of time in seconds the poll will be active after creation, 5-600. Can't be used together with close_date.
    private String explanation; ///< Optional. Text that is shown when a user chooses an incorrect answer or taps on the lamp icon in a quiz-style poll, 0-200 characters with at most 2 line feeds after entities parsing
    private String explanationParseMode; ///< Optional. Mode for parsing entities in the explanation. See formatting options for more details.
    private Boolean protectContent; ///< Optional. Protects the contents of sent messages from forwarding and saving
}
