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
import eus.ferpinan.ausolanmenu.service.AusolanService;
import eus.ferpinan.ausolanmenu.service.TelegramService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Component
@Log4j2
@RequiredArgsConstructor
public class Scheduler {

    public static final String DAY_MENU_TEMPLATE = """
            %s (%s):
            
            %s
            """;
    private final TelegramService telegramService;
    private final MenuCache menuCache;
    private final AusolanService ausolanService;

    @Scheduled(cron = "0 0 7 * * *") // every day at 7:00
    public void senMenuTask() {
        sendMenu();
    }

    @Scheduled(cron = "0 55 6 1 * *") // 6:55 of first day of month
    public void sendMonthlyMenuImageTask() {
        sendMonthlyMenuImage();
    }

    @Scheduled(cron = "0 0 9 * * 0") // every sunday at 9:00
    public void resetMenuTask() {
        menuCache.resetMenus();
    }

    @PostConstruct
    public void startService() {
        menuCache.resetMenus();
        sendMenu();
    }

    private void sendMonthlyMenuImage() {
        byte[] monthlyMenuImage = ausolanService.getMonthlyMenuImage();
        telegramService.sendDocument(monthlyMenuImage);
    }

    private void sendMenu() {
        String todayDate = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        String tomorrowDate = LocalDate.now().plusDays(1).format(DateTimeFormatter.ISO_DATE);

        String message = Stream.of(Map.entry(todayDate, "Gaurko menua"), Map.entry(tomorrowDate, "Biharko menua"))
            .map(e -> menuCache.getMenu(e.getKey()).map(menu -> DAY_MENU_TEMPLATE.formatted(e.getValue(), e.getKey(), menu)))
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
