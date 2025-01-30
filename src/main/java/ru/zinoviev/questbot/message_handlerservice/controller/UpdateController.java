package ru.zinoviev.questbot.message_handlerservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.zinoviev.questbot.message_handlerservice.dto.request.TGUpdateDTO;
import ru.zinoviev.questbot.message_handlerservice.dto.response.ResponseUpdateDTO;
import ru.zinoviev.questbot.message_handlerservice.response.RequestUpdateHandlerService;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UpdateController {

    private final RequestUpdateHandlerService requestService;

    @PostMapping("/telegram")
    public ResponseUpdateDTO processUpdate(@RequestBody TGUpdateDTO update) {
        return requestService.getResponse(update);
    }


}

