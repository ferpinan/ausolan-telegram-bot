package eus.ferpinan.ausolanmenu.cache;

import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import eus.ferpinan.ausolanmenu.service.AusolanService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MenuCache {

    private final AusolanService ausolanService;

    private Map<String, String> menus = Map.of();

    public void resetMenus(){
        menus = ausolanService.getMonthlyMenu();
    }

    public Optional<String> getMenu(String day){
        return Optional.ofNullable(menus.get(day));
    }
}
