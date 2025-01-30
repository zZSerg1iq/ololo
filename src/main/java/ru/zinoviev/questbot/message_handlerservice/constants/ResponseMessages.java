package ru.zinoviev.questbot.message_handlerservice.constants;

import lombok.Getter;
import org.springframework.stereotype.Component;

@Component
@Getter
public class ResponseMessages {
    public static final String REGISTRATION_HALLO_MESSAGE = "Привет! Пожалуйста, введи своё имя";
    public static final String REGISTRATION_ENTER_NAME_CAPTION = "Вы ввели: ";
    public static final String REGISTRATION_COMPLETE_MESSAGE1 = "Поздравляем, вы успешно зарегистрированы как ";
    public static final String REGISTRATION_COMPLETE_MESSAGE2 = "Функции бота разблокированы";
    public static final String REGISTRATION_ON_ERROR = "Что то пошло не так";



//    public final String q = "";
//    public final String q = "";
//    public final String q = "";
//    public final String q = "";
//    public final String q = "";

}
