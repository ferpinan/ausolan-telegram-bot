package eus.ferpinan.ausolanmenu.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.stereotype.Service;
import eus.ferpinan.ausolanmenu.cache.MenuCache;
import lombok.RequiredArgsConstructor;

/**
 * Service for building menu messages.
 */
@Service
@RequiredArgsConstructor
public class MenuMessageService {

    private static final String DAY_MENU_TEMPLATE = """
            %s (%s):
            
            %s
            """;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_DATE;

    private final MenuCache menuCache;

    /**
     * Builds a daily menu message for today and tomorrow.
     *
     * @param today Today's date
     * @param tomorrow Tomorrow's date
     * @return Optional containing the formatted message, or empty if no menus found
     */
    public Optional<String> buildDailyMenuMessage(LocalDate today, LocalDate tomorrow) {
        String message = Stream.of(
                        buildMenuEntry(today, "Gaurko menua"),
                        buildMenuEntry(tomorrow, "Biharko menua")
                )
                .flatMap(Optional::stream)
                .collect(Collectors.joining("\n\n"));

        return message.isEmpty() ? Optional.empty() : Optional.of(message);
    }

    /**
     * Builds a single menu entry for a specific date.
     *
     * @param date The date
     * @param label The label in Basque
     * @return Optional containing the formatted entry, or empty if no menu found
     */
    private Optional<String> buildMenuEntry(LocalDate date, String label) {
        String dateStr = date.format(DATE_FORMATTER);
        return menuCache.getMenu(dateStr)
                .map(menu -> DAY_MENU_TEMPLATE.formatted(label, dateStr, menu));
    }
}