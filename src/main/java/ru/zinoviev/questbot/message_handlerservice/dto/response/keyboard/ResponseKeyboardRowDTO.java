package ru.zinoviev.questbot.message_handlerservice.dto.response.keyboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ResponseKeyboardRowDTO {

    private List<ResponseKeyboardButtonDTO> buttons = null;

    public void addReplyButton(String buttonName){
        if (buttons == null) {
            buttons = new ArrayList<>();
        }
        buttons.add(new ResponseKeyboardButtonDTO(buttonName));
    }

    public void addInlineButton(String buttonName, String callbackData){
        if (buttons == null) {
            buttons = new ArrayList<>();
        }

        buttons.add(new ResponseKeyboardButtonDTO(buttonName, callbackData));
    }

}
