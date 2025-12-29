package uz.test.bot.testBot.bot;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;
import java.util.Map;


@Component
public class MyBot extends TelegramLongPollingBot {

    private final BotConfig botConfig;
    private final ExcelService excelService;

    public MyBot(BotConfig botConfig, ExcelService excelService) {
        this.botConfig = botConfig;
        this.excelService = excelService;
    }

    private final Map<Long, String> steps = new HashMap<>();
    private final Map<Long, UserTemp> users = new HashMap<>();

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {

            Long chatId = update.getMessage().getChatId();
            String text = update.getMessage().getText();

            if (text.equals("/start")) {
                send(chatId, "Assalomu alaykum!" +
                        "\nFamiliyangizni kiriting:");
                steps.put(chatId, "LASTNAME");
                return;
            }

            if ("LASTNAME".equals(steps.get(chatId))) {
                users.put(chatId, new UserTemp(text, null, null));
                steps.put(chatId, "FIRSTNAME");
                send(chatId, "Ismingizni kiriting:");
                return;
            }

            if ("FIRSTNAME".equals(steps.get(chatId))) {
                users.get(chatId).setFirstName(text);
                steps.put(chatId, "PHONE");
                send(chatId, "Telefon raqamingizni kiriting:");
                return;
            }

            if ("PHONE".equals(steps.get(chatId))) {
                users.get(chatId).setPhone(text);
                steps.put(chatId, "CLASS");
                send(chatId, "Sinf raqamini kiriting (1–11):");
                return;
            }

            if ("CLASS".equals(steps.get(chatId))) {

                int classNumber;

                try {
                    classNumber = Integer.parseInt(text);
                    if (classNumber < 1 || classNumber > 11) {
                        send(chatId, "Sinf 1 dan 11 gacha bo‘lishi kerak.");
                        return;
                    }
                } catch (NumberFormatException e) {
                    send(chatId, "Iltimos, faqat raqam kiriting.");
                    return;
                }

                UserTemp u = users.get(chatId);

                excelService.saveToExcel(
                        u.getLastName(),
                        u.getFirstName(),
                        u.getPhone(),
                        classNumber
                );

                send(chatId, "Ma’lumot saqlandi! Rahmat.");

                steps.remove(chatId);
                users.remove(chatId);
            }
        }
    }


    @Override
    public String getBotUsername() {
        return botConfig.getUsername();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    private void send(Long chatId, String text) {
        SendMessage msg = new SendMessage();
        msg.setChatId(chatId.toString());
        msg.setText(text);
        try {
            execute(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

