package com.maze.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response payload containing the solving result.
 * - visitedOrder: cells visited in order (drives the visualization animation)
 * - shortestPath: the final path from start to end
 * - stats: performance metrics
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SolveResult {
    private List<int[]> visitedOrder;    // Each int[] is {row, col}
    private List<int[]> shortestPath;    // Each int[] is {row, col}
    private int nodesExplored;
    private int pathLength;
    private long timeTakenMs;
    private boolean pathFound;
    private String algorithm;
}
