package eus.ferpinan.ausolanmenu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

import eus.ferpinan.ausolanmenu.properties.GascaProperties;
import eus.ferpinan.ausolanmenu.properties.TelegramProperties;

/**
 * The main entry point for the Ausolan Telegram Bot application.
 * <p>
 * This class initializes the Spring Boot application context and enables core
 * framework features required for the application's operation:
 * <ul>
 *   <li><b>Scheduling:</b> Allows the execution of periodic tasks (e.g., daily menu updates).</li>
 *   <li><b>Configuration Properties:</b> Enables strongly-typed mapping of external settings
 *       for both Ausolan and Telegram services.</li>
 * </ul>
 * </p>
 */
@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties({GascaProperties.class, TelegramProperties.class})
public class AusolanTelegramBotApplication {

	/**
	 * Main method that launches the Spring Boot application.
	 *
	 * @param args Command-line arguments passed to the application.
	 */
	public static void main(String[] args) {
		SpringApplication.run(AusolanTelegramBotApplication.class, args);
	}

}