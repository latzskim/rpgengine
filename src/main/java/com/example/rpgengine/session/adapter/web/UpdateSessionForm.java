package com.example.rpgengine.session.adapter.web;

import com.example.rpgengine.session.domain.valueobject.DifficultyLevel;
import com.example.rpgengine.session.domain.valueobject.Visibility;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSessionForm {
    private String title;
    private String description;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime startDate;

    private Integer durationInMinutes;
    private DifficultyLevel difficultyLevel;
    private Visibility visibility;
    private Integer minPlayers;
    private Integer maxPlayers;
    private List<String> requirements;
    private String customRequirements;
}
