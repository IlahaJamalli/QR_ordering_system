package com.qrrestaurant.backend.service;

import com.qrrestaurant.backend.dto.MenuItemDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class MenuItemCacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String MENU_ITEMS_KEY = "menu:items";
    private static final long CACHE_DURATION = 1; // 1 hour

    public void cacheMenuItems(List<MenuItemDTO> menuItems) {
        redisTemplate.opsForValue().set(MENU_ITEMS_KEY, menuItems, CACHE_DURATION, TimeUnit.HOURS);
    }

    @SuppressWarnings("unchecked")
    public List<MenuItemDTO> getCachedMenuItems() {
        return (List<MenuItemDTO>) redisTemplate.opsForValue().get(MENU_ITEMS_KEY);
    }

    public void clearMenuItemsCache() {
        redisTemplate.delete(MENU_ITEMS_KEY);
    }
}
