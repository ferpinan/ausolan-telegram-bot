package eus.ferpinan.ausolanmenu.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "telegram")
public record TelegramProperties(
        String botToken,
        String chatToken
) {}
