package com.maze.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request payload sent from the frontend to solve a maze.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolveRequest {
    private Grid grid;
    private String algorithm;  // "bfs", "dfs", "dijkstra", "astar"
    private int startRow;
    private int startCol;
    private int endRow;
    private int endCol;
}
