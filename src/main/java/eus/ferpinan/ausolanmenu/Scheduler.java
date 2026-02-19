package eus.ferpinan.ausolanmenu;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

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
        String currentDate = LocalDate.now().format(DateTimeFormatter.ISO_DATE);

        log.info("Getting menu...");
        Optional<String> menu = menuCache.getMenu(currentDate);

        if (menu.isPresent()){
            log.info("Sending telegram message...");
            log.info(menu.get());
            telegramService.sendMessage(menu.get());
        }else{
            log.info("No menu found");
        }
    }
}
