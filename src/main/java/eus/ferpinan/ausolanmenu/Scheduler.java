package eus.ferpinan.ausolanmenu;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import eus.ferpinan.ausolanmenu.cache.MenuCache;
import eus.ferpinan.ausolanmenu.service.TelegramService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Component
@Log4j2
@RequiredArgsConstructor
public class Scheduler {

    private final TelegramService telegramService;
    private final MenuCache menuCache;

    @Scheduled(cron = "0 0 7 * * *")
    public void senMenuTask() {
        sendMenu();
    }

    @Scheduled(cron = "0 0 9 * * 0")
    public void resetMenuTask() {
        menuCache.resetMenus();
    }

    @PostConstruct
    public void startService() {
        menuCache.resetMenus();
        sendMenu();
    }

    private void sendMenu() {
        String todayDate = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        String tomorrowDate = LocalDate.now().plusDays(1).format(DateTimeFormatter.ISO_DATE);

        String message = Stream.of(
                        Map.entry(todayDate, "Gaurko menua"),
                        Map.entry(tomorrowDate, "Biharko menua")
                )
                .map(e -> menuCache.getMenu(e.getKey())
                        .map(menu -> String.format("%s (%s):%n%n%s", e.getValue(), e.getKey(), menu)))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.joining("\n\n"));

        if (message.isEmpty()) {
            log.info("No menus found for dates {} and {}", todayDate, tomorrowDate);
        } else {
            log.info("Sending telegram message...\n{}", message);
            telegramService.sendMessage(message);
        }
    }
}
