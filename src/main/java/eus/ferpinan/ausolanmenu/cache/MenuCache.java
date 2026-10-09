package eus.ferpinan.ausolanmenu.cache;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

/**
 * Service component responsible for caching and managing menu information.
 * This class provides an in-memory storage to reduce redundant calls.
 */
@Service
@RequiredArgsConstructor
public class MenuCache {

    /**
     * Internal storage for the monthly menus.
     * The key represents the day/date identifier, and the value is the menu content.
     */
    private volatile Map<String, String> menus = Map.of();

    /**
     * Adds (or overrides) the given menus in the cache.
     *
     * @param newMenus Menus keyed by ISO date.
     */
    public synchronized void putMenus(Map<String, String> newMenus) {
        Map<String, String> merged = new TreeMap<>(menus);
        merged.putAll(newMenus);
        menus = Map.copyOf(merged);
    }

    /**
     * Returns every cached menu sorted by date.
     *
     * @return An unmodifiable map of menus keyed by ISO date.
     */
    public Map<String, String> getAllMenus() {
        return Collections.unmodifiableMap(new TreeMap<>(menus));
    }

    /**
     * Retrieves the menu for a specific day from the cache.
     *
     * @param day The string representation of the day to look up.
     * @return An {@link Optional} containing the menu content if present,
     *         or an empty Optional if no menu exists for the given day.
     */
    public Optional<String> getMenu(String day) {
        return Optional.ofNullable(menus.get(day));
    }
}