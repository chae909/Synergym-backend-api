// org/synergym/backendapi/controller/AchievementController.java
package org.synergym.backendapi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.synergym.backendapi.service.AchievementService;

import java.util.Map;

@RestController
@RequestMapping("/api/achievements/{user_id}")
@RequiredArgsConstructor
public class AchievementController {

    private final AchievementService achievementService;

    @PostMapping
    public ResponseEntity<String> awardAchievement(
            @PathVariable int user_id,
            @RequestBody Map<String, String> payload) {
        
        String badgeName = payload.get("badge_name");
        String badgeDescription = payload.get("badge_description");

        achievementService.awardBadgeToUser(user_id, badgeName, badgeDescription);

        return ResponseEntity.ok("Achievement awarded successfully.");
    }
}