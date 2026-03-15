package com.maze.algorithm;

import com.maze.model.Grid;
import com.maze.model.SolveResult;

import java.util.*;

/**
 * Breadth-First Search pathfinding algorithm.
 *
 * BFS explores cells level by level using a FIFO queue.
 * Key properties:
 *   - Guarantees the shortest path in an unweighted grid
 *   - Time complexity:  O(V + E) where V = rows × cols
 *   - Space complexity: O(V) for the visited set and queue
 *
 * The visitedOrder list records every cell in the order it is explored,
 * which the frontend uses to drive the step-by-step animation.
 */
public class BFS implements PathfindingAlgorithm {

    // Cardinal directions: up, down, left, right
    private static final int[][] DIRECTIONS = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

    @Override
    public SolveResult solve(Grid grid, int startRow, int startCol, int endRow, int endCol) {

        long startTime = System.nanoTime();

        int rows = grid.getRows();
        int cols = grid.getCols();

        boolean[][] visited = new boolean[rows][cols];
        // parent map: key = "row,col" → value = int[]{parentRow, parentCol}
        Map<String, int[]> parent = new HashMap<>();
        List<int[]> visitedOrder = new ArrayList<>();

        Queue<int[]> queue = new LinkedList<>();
        queue.offer(new int[]{startRow, startCol});
        visited[startRow][startCol] = true;

        boolean pathFound = false;

        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int r = current[0];
            int c = current[1];

            visitedOrder.add(current); // record for animation

            // Check if we reached the goal
            if (r == endRow && c == endCol) {
                pathFound = true;
                break;
            }

            // Explore neighbours
            for (int[] dir : DIRECTIONS) {
                int nr = r + dir[0];
                int nc = c + dir[1];

                if (grid.isTraversable(nr, nc) && !visited[nr][nc]) {
                    visited[nr][nc] = true;
                    parent.put(nr + "," + nc, current);
                    queue.offer(new int[]{nr, nc});
                }
            }
        }

        // Reconstruct path by walking the parent map backwards
        List<int[]> shortestPath = reconstructPath(parent, startRow, startCol, endRow, endCol, pathFound);

        long timeTakenMs = (System.nanoTime() - startTime) / 1_000_000;

        return SolveResult.builder()
                .visitedOrder(visitedOrder)
                .shortestPath(shortestPath)
                .nodesExplored(visitedOrder.size())
                .pathLength(shortestPath.size())
                .timeTakenMs(timeTakenMs)
                .pathFound(pathFound)
                .algorithm(getName())
                .build();
    }

    /**
     * Walk the parent map from end → start to reconstruct the shortest path.
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
                return Collections.emptyList(); // safety guard
            }
        }
        path.addFirst(new int[]{startRow, startCol}); // include start
        return path;
    }

    @Override
    public String getName() {
        return "Breadth-First Search";
    }
}
