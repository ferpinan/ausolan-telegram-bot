package eus.ferpinan.ausolanmenu.service;

import eus.ferpinan.ausolanmenu.properties.TelegramProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Log4j2
public class TelegramService {

    private final RestTemplate restTemplate;
    private final TelegramProperties telegramProperties;

    public void sendMessage(String message) {
        String url = String.format(
                "https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s",
                telegramProperties.getBotToken(),
                telegramProperties.getChatToken(),
                message
        );
        restTemplate.getForObject(url, String.class);
    }
}
