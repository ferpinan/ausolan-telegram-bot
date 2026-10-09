package eus.ferpinan.ausolanmenu.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import eus.ferpinan.ausolanmenu.cache.MenuCache;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/menus")
@RequiredArgsConstructor
public class MenuController {

    private final MenuCache menuCache;

    @GetMapping
    public Map<String, String> getMenus() {
        return menuCache.getAllMenus();
    }
}
