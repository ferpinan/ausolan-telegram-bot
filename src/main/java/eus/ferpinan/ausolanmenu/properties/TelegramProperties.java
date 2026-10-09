package eus.ferpinan.ausolanmenu.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for the Telegram Bot integration.
 * <p>
 * This record stores the necessary credentials and identifiers required to
 * communicate with the Telegram Bot API. These values are typically sourced
 * from the {@code application.properties} or {@code application.yml} file
 * using the {@code telegram} prefix.
 * </p>
 *
 * @param botToken  The unique authentication token provided by the BotFather
 *                  to authorize requests to the Telegram API.
 * @param chatToken The unique identifier for the specific chat or channel
 *                  where the bot will send messages.
 */
@ConfigurationProperties(prefix = "telegram")
public record TelegramProperties(
        String botToken,
        String chatToken
) {}