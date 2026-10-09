package eus.ferpinan.ausolanmenu.scheduler;

import java.time.LocalDate;
import java.time.YearMonth;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import eus.ferpinan.ausolanmenu.service.GascaMenuService;
import eus.ferpinan.ausolanmenu.service.GascaService;
import eus.ferpinan.ausolanmenu.service.MenuMessageService;
import eus.ferpinan.ausolanmenu.service.TelegramService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * Scheduler for automated menu notifications via Telegram.
 * Handles daily menu messages, monthly menu images, and weekly cache resets.
 */
@Component
@Log4j2
@RequiredArgsConstructor
public class MenuScheduler {

    private final TelegramService telegramService;
    private final MenuMessageService menuMessageService;
    private final GascaService gascaService;
    private final GascaMenuService gascaMenuService;

    /**
     * Sends daily menu message at 7:00 AM.
     */
    @Scheduled(cron = "0 0 7 * * *")
    public void sendDailyMenu() {
        log.info("Executing daily menu task");
        sendMenuForDates(LocalDate.now(), LocalDate.now().plusDays(1));
    }

    @Scheduled(cron = "0 55 5 1 * *")
    public void reloadMenuCache() {
        YearMonth targetMonth = YearMonth.now();
        byte[] pdf = gascaService.downloadMonthlyPdf(targetMonth);
        gascaMenuService.storeMenus(pdf, targetMonth);
    }

    /**
     * Initializes service on startup.
     */
    @PostConstruct
    public void initialize() {
        reloadMenuCache();
        sendDailyMenu();
    }

    /**
     * Sends menu for specified dates.
     *
     * @param today Today's date
     * @param tomorrow Tomorrow's date
     */
    private void sendMenuForDates(LocalDate today, LocalDate tomorrow) {
        menuMessageService.buildDailyMenuMessage(today, tomorrow)
                .ifPresentOrElse(
                        message -> {
                            log.info("Sending telegram message:\n{}", message);
                            telegramService.sendMessage(message);
                        },
                        () -> log.warn("No menus found for dates {} and {}", today, tomorrow)
                );
    }
}