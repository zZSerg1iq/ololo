package ru.zinoviev.questbot.message_handlerservice.dto.response.keyboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ResponseKeyboardButtonDTO {
    private String text;
    private String url;
    private String callbackData;

    public ResponseKeyboardButtonDTO(String text) {
        this.text = text;
    }

    public ResponseKeyboardButtonDTO(String text, String callbackData) {
        this.text = text;
        this.callbackData = callbackData;
    }
}
