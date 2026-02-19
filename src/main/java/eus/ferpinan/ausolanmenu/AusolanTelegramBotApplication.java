package eus.ferpinan.ausolanmenu;

import eus.ferpinan.ausolanmenu.properties.AusolanProperties;
import eus.ferpinan.ausolanmenu.properties.TelegramProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties({AusolanProperties.class, TelegramProperties.class})
public class AusolanTelegramBotApplication {

	public static void main(String[] args) {

        SpringApplication.run(AusolanTelegramBotApplication.class, args);

	}

}
