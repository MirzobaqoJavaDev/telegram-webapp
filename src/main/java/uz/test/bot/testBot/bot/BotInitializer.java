package uz.test.bot.testBot.bot;


import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Component
public class BotInitializer {

    private final MyBot myBot;

    public BotInitializer(MyBot myBot) {
        this.myBot = myBot;
    }

    @PostConstruct
    public void init() {
        try {
            TelegramBotsApi api =
                    new TelegramBotsApi(DefaultBotSession.class);
            api.registerBot(myBot);
            System.out.println("BOT REGISTER BO‘LDI ✅");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
