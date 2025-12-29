package uz.test.bot.testBot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import uz.test.bot.testBot.bot.BotConfig;

@EnableConfigurationProperties(BotConfig.class)
@SpringBootApplication
public class TestBotApplication {

	public static void main(String[] args) {
		SpringApplication.run(TestBotApplication.class, args);
	}

}
