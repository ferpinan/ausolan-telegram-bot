package eus.ferpinan.ausolanmenu.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import lombok.Data;

@Data
@ConfigurationProperties(prefix = "telegram")
public class TelegramProperties {

    private String botToken;
    private String chatToken;
}
