package uz.test.bot.testBot.bot;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.webapp.WebAppInfo;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Component
public class MyBot extends TelegramLongPollingBot {

    private final BotConfig botConfig;
    private final ExcelService excelService;

    public MyBot(BotConfig botConfig, ExcelService excelService) {
        this.botConfig = botConfig;
        this.excelService = excelService;
    }


    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            if (update.getMessage().getText().equals("/start")) {
                sendWebAppButton(update.getMessage().getChatId());
            }
        }

    }
    private void sendWebAppButton(Long chatId){
        InlineKeyboardButton webAppButton = InlineKeyboardButton.builder()
                .text("📝 Javoblarni belgilash")
                .webApp(new WebAppInfo("https://alaya-undeductive-subsuperficially.ngrok-free.app/test.html"))
                .build();
        InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder()
                .keyboard(List.of(List.of(webAppButton)))
                .build();

        SendMessage message= new SendMessage(chatId.toString(),
                "Milliy sertifikat test javoblarini belgilang:");
        message.setReplyMarkup(markup);

        try {
            execute(message);
        }catch (TelegramApiException e){
            e.printStackTrace();
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

    private void sendText(Long chatId, String text) {
        SendMessage ms = new SendMessage();
        ms.setChatId(chatId.toString());
        ms.setText(text);
        try {
            execute(ms);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
