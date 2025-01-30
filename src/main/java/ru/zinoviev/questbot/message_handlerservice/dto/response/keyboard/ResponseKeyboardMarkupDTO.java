package ru.zinoviev.questbot.message_handlerservice.dto.response.keyboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.zinoviev.questbot.message_handlerservice.enums.KeyboardType;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ResponseKeyboardMarkupDTO {

    private KeyboardType keyboardType;
    private List<ResponseKeyboardRowDTO> keyboardRows;
    private Integer rowWidth;
    private boolean resizeKeyboard;
    private boolean removeKeyboard;

    public ResponseKeyboardMarkupDTO(KeyboardType keyboardType) {
        this.keyboardType = keyboardType;
        this.keyboardRows = new ArrayList<>();
    }

    public void addRow(ResponseKeyboardRowDTO buttonsRow) {
        check();
        keyboardRows.add(buttonsRow);
    }

    public void addInlineButton(String buttonCaption, String callbackData) {
        check();
        ResponseKeyboardRowDTO row = new ResponseKeyboardRowDTO();
        row.addInlineButton(buttonCaption, callbackData);
        keyboardRows.add(row);
    }

    public void addReplyButton(String buttonCaption) {
        check();
        ResponseKeyboardRowDTO row = new ResponseKeyboardRowDTO();
        row.addReplyButton(buttonCaption);
        keyboardRows.add(row);
    }

    private void check(){
        if (this.keyboardRows == null) {
            this.keyboardRows = new ArrayList<>();
        }
    }
}
