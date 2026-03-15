package com.maze.algorithm;

import com.maze.model.Grid;
import com.maze.model.SolveResult;

import java.util.*;

/**
 * Depth-First Search pathfinding algorithm.
 *
 * DFS explores as deep as possible along each branch before backtracking,
 * using a LIFO stack.
 * Key properties:
 *   - Does NOT guarantee the shortest path
 *   - Time complexity:  O(V + E) where V = rows × cols
 *   - Space complexity: O(V) worst case
 *   - Visually dramatic — explores long corridors before spreading out
 *
 * Contrast with BFS: BFS expands level-by-level (wave), DFS dives deep first.
 */
public class DFS implements PathfindingAlgorithm {

    private static final int[][] DIRECTIONS = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

    @Override
    public SolveResult solve(Grid grid, int startRow, int startCol, int endRow, int endCol) {

        long startTime = System.nanoTime();

        int rows = grid.getRows();
        int cols = grid.getCols();

        boolean[][] visited = new boolean[rows][cols];
        Map<String, int[]> parent = new HashMap<>();
        List<int[]> visitedOrder = new ArrayList<>();

        Deque<int[]> stack = new ArrayDeque<>();
        stack.push(new int[]{startRow, startCol});
        visited[startRow][startCol] = true;

        boolean pathFound = false;

        while (!stack.isEmpty()) {
            int[] current = stack.pop();
            int r = current[0];
            int c = current[1];

            visitedOrder.add(current); // record for animation

            // Check if we reached the goal
            if (r == endRow && c == endCol) {
                pathFound = true;
                break;
            }

            // Explore neighbours (reverse order so first direction is explored first)
            for (int i = DIRECTIONS.length - 1; i >= 0; i--) {
                int nr = r + DIRECTIONS[i][0];
                int nc = c + DIRECTIONS[i][1];

                if (grid.isTraversable(nr, nc) && !visited[nr][nc]) {
                    visited[nr][nc] = true;
                    parent.put(nr + "," + nc, current);
                    stack.push(new int[]{nr, nc});
                }
            }
        }

        List<int[]> path = reconstructPath(parent, startRow, startCol, endRow, endCol, pathFound);

        long timeTakenMs = (System.nanoTime() - startTime) / 1_000_000;

        return SolveResult.builder()
                .visitedOrder(visitedOrder)
                .shortestPath(path)
                .nodesExplored(visitedOrder.size())
                .pathLength(path.size())
                .timeTakenMs(timeTakenMs)
                .pathFound(pathFound)
                .algorithm(getName())
                .build();
    }

    /**
     * Walk the parent map from end → start to reconstruct the path found by DFS.
     * Note: This path is NOT necessarily the shortest.
     */
    private List<int[]> reconstructPath(Map<String, int[]> parent,
                                        int startRow, int startCol,
                                        int endRow, int endCol,
                                        boolean pathFound) {
        if (!pathFound) {
            return Collections.emptyList();
        }

        LinkedList<int[]> path = new LinkedList<>();
        int[] current = {endRow, endCol};

        while (!(current[0] == startRow && current[1] == startCol)) {
            path.addFirst(current);
            String key = current[0] + "," + current[1];
            current = parent.get(key);
            if (current == null) {
                return Collections.emptyList();
            }
        }
        path.addFirst(new int[]{startRow, startCol});
        return path;
    }

    @Override
    public String getName() {
        return "Depth-First Search";
    }
}
