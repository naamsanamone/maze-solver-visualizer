package com.maze.algorithm;

import com.maze.model.Grid;
import com.maze.model.SolveResult;

/**
 * Strategy Pattern interface for pathfinding algorithms.
 * Each algorithm implements this interface, making it easy to swap algorithms.
 */
public interface PathfindingAlgorithm {

    /**
     * Solve the maze and return the result with visited order for visualization.
     *
     * @param grid     the maze grid
     * @param startRow start position row
     * @param startCol start position column
     * @param endRow   end position row
     * @param endCol   end position column
     * @return SolveResult containing visited order, shortest path, and stats
     */
    SolveResult solve(Grid grid, int startRow, int startCol, int endRow, int endCol);

    /**
     * Returns the display name of this algorithm.
     */
    String getName();
}
