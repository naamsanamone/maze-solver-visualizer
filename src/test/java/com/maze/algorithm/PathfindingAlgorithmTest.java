package com.maze.algorithm;

import com.maze.model.CellType;
import com.maze.model.Grid;
import com.maze.model.SolveResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for all pathfinding algorithms.
 * Each algorithm is tested for:
 *   1. Finding the shortest/valid path on a simple open grid
 *   2. Navigating around walls
 *   3. Handling no-path scenarios
 *   4. Handling start == end
 */
class PathfindingAlgorithmTest {

    private Grid openGrid;     // 5×5 grid, no walls
    private Grid walledGrid;   // 5×5 grid with walls requiring a detour
    private Grid blockedGrid;  // 5×5 grid where end is completely walled off

    @BeforeEach
    void setUp() {
        // Open grid — all cells traversable
        openGrid = new Grid(5, 5);

        // Walled grid — wall across the middle, with one gap
        // Layout (S=start, E=end, #=wall):
        //  . . . . .
        //  . . . . .
        //  # # # . .
        //  . . . . .
        //  . . . . E
        walledGrid = new Grid(5, 5);
        walledGrid.setCell(2, 0, CellType.WALL);
        walledGrid.setCell(2, 1, CellType.WALL);
        walledGrid.setCell(2, 2, CellType.WALL);

        // Blocked grid — end is surrounded by walls
        blockedGrid = new Grid(5, 5);
        blockedGrid.setCell(3, 3, CellType.WALL);
        blockedGrid.setCell(3, 4, CellType.WALL);
        blockedGrid.setCell(4, 3, CellType.WALL);
    }

    // =========== BFS Tests ===========

    @Test
    @DisplayName("BFS: finds shortest path on open grid")
    void bfs_openGrid_findsShortestPath() {
        SolveResult result = new BFS().solve(openGrid, 0, 0, 4, 4);
        assertTrue(result.isPathFound());
        assertEquals(9, result.getPathLength()); // Manhattan distance + 1 = 8 + 1
        assertEquals("Breadth-First Search", result.getAlgorithm());
        assertFalse(result.getVisitedOrder().isEmpty());
    }

    @Test
    @DisplayName("BFS: navigates around walls")
    void bfs_walledGrid_navigatesAroundWalls() {
        SolveResult result = new BFS().solve(walledGrid, 0, 0, 4, 4);
        assertTrue(result.isPathFound());
        assertTrue(result.getPathLength() >= 9, "Path should be at least Manhattan distance");
    }

    @Test
    @DisplayName("BFS: returns no path when blocked")
    void bfs_blockedGrid_noPath() {
        SolveResult result = new BFS().solve(blockedGrid, 0, 0, 4, 4);
        assertFalse(result.isPathFound());
        assertEquals(0, result.getPathLength());
        assertTrue(result.getShortestPath().isEmpty());
    }

    @Test
    @DisplayName("BFS: start equals end")
    void bfs_startEqualsEnd() {
        SolveResult result = new BFS().solve(openGrid, 2, 2, 2, 2);
        assertTrue(result.isPathFound());
        assertEquals(1, result.getPathLength()); // Just the cell itself
    }

    // =========== DFS Tests ===========

    @Test
    @DisplayName("DFS: finds a path on open grid")
    void dfs_openGrid_findsPath() {
        SolveResult result = new DFS().solve(openGrid, 0, 0, 4, 4);
        assertTrue(result.isPathFound());
        assertTrue(result.getPathLength() > 0);
        assertEquals("Depth-First Search", result.getAlgorithm());
    }

    @Test
    @DisplayName("DFS: navigates around walls")
    void dfs_walledGrid_navigatesAroundWalls() {
        SolveResult result = new DFS().solve(walledGrid, 0, 0, 4, 4);
        assertTrue(result.isPathFound());
        assertTrue(result.getPathLength() > 0);
    }

    @Test
    @DisplayName("DFS: returns no path when blocked")
    void dfs_blockedGrid_noPath() {
        SolveResult result = new DFS().solve(blockedGrid, 0, 0, 4, 4);
        assertFalse(result.isPathFound());
        assertEquals(0, result.getPathLength());
    }

    @Test
    @DisplayName("DFS: start equals end")
    void dfs_startEqualsEnd() {
        SolveResult result = new DFS().solve(openGrid, 2, 2, 2, 2);
        assertTrue(result.isPathFound());
        assertEquals(1, result.getPathLength());
    }

    // =========== Dijkstra Tests ===========

    @Test
    @DisplayName("Dijkstra: finds shortest path on open grid")
    void dijkstra_openGrid_findsShortestPath() {
        SolveResult result = new Dijkstra().solve(openGrid, 0, 0, 4, 4);
        assertTrue(result.isPathFound());
        assertEquals(9, result.getPathLength());
        assertEquals("Dijkstra's Algorithm", result.getAlgorithm());
    }

    @Test
    @DisplayName("Dijkstra: navigates around walls")
    void dijkstra_walledGrid_navigatesAroundWalls() {
        SolveResult result = new Dijkstra().solve(walledGrid, 0, 0, 4, 4);
        assertTrue(result.isPathFound());
        assertTrue(result.getPathLength() >= 9, "Path should be at least Manhattan distance");
    }

    @Test
    @DisplayName("Dijkstra: returns no path when blocked")
    void dijkstra_blockedGrid_noPath() {
        SolveResult result = new Dijkstra().solve(blockedGrid, 0, 0, 4, 4);
        assertFalse(result.isPathFound());
        assertEquals(0, result.getPathLength());
    }

    @Test
    @DisplayName("Dijkstra: start equals end")
    void dijkstra_startEqualsEnd() {
        SolveResult result = new Dijkstra().solve(openGrid, 2, 2, 2, 2);
        assertTrue(result.isPathFound());
        assertEquals(1, result.getPathLength());
    }

    // =========== A* Tests ===========

    @Test
    @DisplayName("A*: finds shortest path on open grid")
    void aStar_openGrid_findsShortestPath() {
        SolveResult result = new AStar().solve(openGrid, 0, 0, 4, 4);
        assertTrue(result.isPathFound());
        assertEquals(9, result.getPathLength());
        assertEquals("A* Search", result.getAlgorithm());
    }

    @Test
    @DisplayName("A*: explores fewer nodes than BFS on open grid")
    void aStar_exploresFewerNodesThanBFS() {
        SolveResult bfsResult = new BFS().solve(openGrid, 0, 0, 4, 4);
        SolveResult aStarResult = new AStar().solve(openGrid, 0, 0, 4, 4);

        // A* with heuristic should explore <= nodes than BFS
        assertTrue(aStarResult.getNodesExplored() <= bfsResult.getNodesExplored(),
                "A* explored " + aStarResult.getNodesExplored()
                + " vs BFS " + bfsResult.getNodesExplored());
    }

    @Test
    @DisplayName("A*: navigates around walls")
    void aStar_walledGrid_navigatesAroundWalls() {
        SolveResult result = new AStar().solve(walledGrid, 0, 0, 4, 4);
        assertTrue(result.isPathFound());
        assertTrue(result.getPathLength() >= 9, "Path should be at least Manhattan distance");
    }

    @Test
    @DisplayName("A*: returns no path when blocked")
    void aStar_blockedGrid_noPath() {
        SolveResult result = new AStar().solve(blockedGrid, 0, 0, 4, 4);
        assertFalse(result.isPathFound());
        assertEquals(0, result.getPathLength());
    }

    @Test
    @DisplayName("A*: start equals end")
    void aStar_startEqualsEnd() {
        SolveResult result = new AStar().solve(openGrid, 2, 2, 2, 2);
        assertTrue(result.isPathFound());
        assertEquals(1, result.getPathLength());
    }

    // =========== Cross-Algorithm: Shortest Path Consistency ===========

    @Test
    @DisplayName("BFS, Dijkstra, and A* agree on shortest path length (open grid)")
    void optimalAlgorithms_samePathLength_openGrid() {
        SolveResult bfs = new BFS().solve(openGrid, 0, 0, 4, 4);
        SolveResult dijkstra = new Dijkstra().solve(openGrid, 0, 0, 4, 4);
        SolveResult aStar = new AStar().solve(openGrid, 0, 0, 4, 4);

        assertEquals(bfs.getPathLength(), dijkstra.getPathLength());
        assertEquals(dijkstra.getPathLength(), aStar.getPathLength());
    }

    @Test
    @DisplayName("BFS, Dijkstra, and A* agree on shortest path length (walled grid)")
    void optimalAlgorithms_samePathLength_walledGrid() {
        SolveResult bfs = new BFS().solve(walledGrid, 0, 0, 4, 4);
        SolveResult dijkstra = new Dijkstra().solve(walledGrid, 0, 0, 4, 4);
        SolveResult aStar = new AStar().solve(walledGrid, 0, 0, 4, 4);

        assertEquals(bfs.getPathLength(), dijkstra.getPathLength());
        assertEquals(dijkstra.getPathLength(), aStar.getPathLength());
    }
}
