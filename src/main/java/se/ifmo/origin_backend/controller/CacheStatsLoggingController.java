package se.ifmo.origin_backend.controller;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import se.ifmo.origin_backend.cache.CacheStatsLoggingToggle;

@RestController
@RequestMapping("/cache/stats/logging")
@RequiredArgsConstructor
public class CacheStatsLoggingController {
    private final CacheStatsLoggingToggle toggle;

    @GetMapping
    public Map<String, Boolean> status() {
        return Map.of("enabled", toggle.isEnabled());
    }

    @PutMapping
    public Map<String, Boolean> setEnabled(@RequestParam boolean enabled) {
        toggle.setEnabled(enabled);
        return Map.of("enabled", toggle.isEnabled());
    }

}
