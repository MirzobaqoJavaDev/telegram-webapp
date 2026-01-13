package uz.test.bot.testBot.bot;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MyBot extends TelegramLongPollingBot {

    private final BotConfig botConfig;
    private final ExcelService excelService;

    private static final String CHANNEL_USERNAME = "@matematika_Buxoro1";

    // User chatId → step/state
    private final Map<Long, String> steps = new HashMap<>();
    // User chatId → temporary user data
    private final Map<Long, UserTemp> users = new HashMap<>();

    public MyBot(BotConfig botConfig, ExcelService excelService) {
        this.botConfig = botConfig;
        this.excelService = excelService;
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {
            handleMessage(update);
        }

        if (update.hasCallbackQuery()) {
            handleCallback(update.getCallbackQuery());
        }
    }

    private void handleMessage(Update update) {
        Long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();

        // /start
        if (text.equals("/start")) {
            sendSubscriptionMessage(chatId);
            steps.put(chatId, "AWAIT_SUB");
            return;
        }

        // Steps state machine
        String step = steps.get(chatId);

        if (step == null) return; // Step bo‘lmasa — ignore

        switch (step) {
            case "LASTNAME" -> {
                users.put(chatId, new UserTemp(text, null, null));
                steps.put(chatId, "FIRSTNAME");
                send(chatId, "Ismingizni kiriting:");
            }
            case "FIRSTNAME" -> {
                users.get(chatId).setFirstName(text);
                steps.put(chatId, "PHONE");
                send(chatId, "Telefon raqamingizni kiriting:");
            }
            case "PHONE" -> {
                users.get(chatId).setPhone(text);
                steps.put(chatId, "CLASS");
                send(chatId, "Sinf raqamini kiriting (1–11):");
            }
            case "CLASS" -> {
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

                // Excel saqlash
                excelService.saveToExcel(u.getLastName(), u.getFirstName(), u.getPhone(), classNumber);

                send(chatId, "Ma’lumot saqlandi! Rahmat.");

                // Clear user data
                steps.remove(chatId);
                users.remove(chatId);
            }
            case "AWAIT_SUB" -> send(chatId, "Iltimos, avval obuna bo‘ling va ✅ tugmasini bosing.");
        }
    }

    private void handleCallback(CallbackQuery cb) {
        Long chatId = cb.getMessage().getChatId();
        Long userId = cb.getFrom().getId();

        // Har doim javob qaytarish
        try {
            execute(AnswerCallbackQuery.builder()
                    .callbackQueryId(cb.getId())
                    .build());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

        if ("CHECK_SUB".equals(cb.getData())) {
            if (isSubscribed(userId)) {
                // Obuna bo‘lsa
                steps.put(chatId, "LASTNAME"); // Stepni boshlash
                send(chatId, "✅ Rahmat! Siz kanalga obuna bo‘lgansiz.\nAssalomu aleykum!\nFamiliyangizni kiriting:");
            } else {
                // Obuna bo‘lmagan
                send(chatId, "❌ Siz hali kanalga obuna bo‘lmadingiz!\nIltimos, avval obuna bo‘ling.");
            }
        }
    }

    private void sendSubscriptionMessage(Long chatId) {
        SendMessage msg = new SendMessage();
        msg.setChatId(chatId.toString());
        msg.setText("🔔 Botdan foydalanish uchun kanalimizga obuna bo‘ling:\n\n" +
                "👉 https://t.me/matematika_Buxoro1");

        InlineKeyboardButton btn = new InlineKeyboardButton();
        btn.setText("✅ Obuna bo‘ldim");
        btn.setCallbackData("CHECK_SUB");

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(List.of(List.of(btn)));
        msg.setReplyMarkup(markup);

        try {
            execute(msg);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private boolean isSubscribed(Long userId) {
        try {
            GetChatMember getChatMember = new GetChatMember();
            getChatMember.setChatId(CHANNEL_USERNAME);
            getChatMember.setUserId(userId);

            ChatMember member = execute(getChatMember);
            String status = member.getStatus();
            // Status tekshirish
            return status.equals("member") ||
                    status.equals("administrator") ||
                    status.equals("creator");

        } catch (Exception e) {
            return false;
        }
    }

    private void send(Long chatId, String text) {
        SendMessage msg = new SendMessage();
        msg.setChatId(chatId.toString());
        msg.setText(text);
        try {
            execute(msg);
        } catch (TelegramApiException e) {
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
}
