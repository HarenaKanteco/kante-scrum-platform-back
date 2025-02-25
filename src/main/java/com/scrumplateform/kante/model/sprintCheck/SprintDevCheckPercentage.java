package com.scrumplateform.kante.model.sprintCheck;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SprintDevCheckPercentage {
    private long totalTasks;
    private long completedTasks;
    private double percentage;
}
