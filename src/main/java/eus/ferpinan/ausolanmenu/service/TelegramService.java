package eus.ferpinan.ausolanmenu.service;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import eus.ferpinan.ausolanmenu.properties.TelegramProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * Service responsible for interacting with the Telegram Bot API.
 * <p>
 * This service provides methods to push notifications and media (such as menu images)
 * to a specific Telegram chat using the credentials defined in {@link TelegramProperties}.
 * </p>
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class TelegramService {

    private final RestTemplate restTemplate;
    private final TelegramProperties telegramProperties;

    /**
     * Sends a plain text message to the configured Telegram chat.
     *
     * @param message The text content to be sent.
     */
    public void sendMessage(String message) {
        String url = String.format(
                "https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s",
                telegramProperties.botToken(),
                telegramProperties.chatToken(),
                message
        );
        restTemplate.getForObject(url, String.class);
    }

    /**
     * Sends an image (provided as a byte array) to the configured Telegram chat.
     *
     * @param document The raw byte array of the image to be sent.
     */
    public void sendPhoto(byte[] document) {
        String url = String.format("https://api.telegram.org/bot%s/sendPhoto", telegramProperties.botToken());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        LinkedMultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("chat_id", telegramProperties.chatToken());
        body.add("photo", new ByteArrayResource(document) {
            @Override
            public String getFilename() {
                return "menu.png";
            }
        });

        restTemplate.postForObject(url, new HttpEntity<>(body, headers), String.class);
    }
}