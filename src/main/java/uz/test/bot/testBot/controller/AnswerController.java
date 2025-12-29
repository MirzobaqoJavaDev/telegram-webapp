package uz.test.bot.testBot.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.test.bot.testBot.dto.request.AnswerRequestDto;
import uz.test.bot.testBot.entity.Answer;
import uz.test.bot.testBot.repository.AnswerRepository;

@RestController
@RequestMapping("/api")
public class AnswerController {
    private final AnswerRepository answerRepository;

    public AnswerController(AnswerRepository answerRepository) {
        this.answerRepository = answerRepository;
    }
    @PostMapping("/answers")
    public void save(@RequestBody AnswerRequestDto req) {
        req.getAnswers().forEach((q, a) -> {
            Answer e = new Answer();
            e.setTelegramId(req.getTelegramId());
            e.setQuestion(q);
            e.setAnswer(a);
            answerRepository.save(e);
        });
    }
}
