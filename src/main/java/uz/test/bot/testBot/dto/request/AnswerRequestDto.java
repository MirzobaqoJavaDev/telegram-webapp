package uz.test.bot.testBot.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;
@Getter
@Setter
public class AnswerRequestDto {
    private Long telegramId;
    private Map<Integer, String> answers;
}
