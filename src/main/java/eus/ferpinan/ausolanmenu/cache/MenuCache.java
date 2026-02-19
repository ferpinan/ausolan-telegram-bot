package eus.ferpinan.ausolanmenu.cache;

import eus.ferpinan.ausolanmenu.service.MenuService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
public class MenuCache {

    private final MenuService menuService;

    private static Map<String, String> menuCache = new HashMap<>();

    public void resetMenus(){
        menuCache = menuService.getMonthlyMenu();
    }

    public Optional<String> getMenu(String day){
        return Optional.ofNullable(menuCache.get(day));
    }


}
