package com.maze.algorithm;

import com.maze.model.Grid;
import com.maze.model.SolveResult;

import java.util.*;

/**
 * Dijkstra's Algorithm for weighted shortest-path finding.
 *
 * Uses a min-heap (PriorityQueue) ordered by cumulative distance from start.
 * Key properties:
 *   - Guarantees the shortest path in both weighted and unweighted grids
 *   - Time complexity:  O(V log V) with a binary heap
 *   - Space complexity: O(V)
 *
 * In an unweighted grid (all edge weights = 1) it behaves identically to BFS,
 * but this implementation is ready for weighted cells if the Grid model is
 * extended with per-cell weights in the future.
 */
public class Dijkstra implements PathfindingAlgorithm {

    private static final int[][] DIRECTIONS = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

    @Override
    public SolveResult solve(Grid grid, int startRow, int startCol, int endRow, int endCol) {

        long startTime = System.nanoTime();

        int rows = grid.getRows();
        int cols = grid.getCols();

        // dist[r][c] = shortest known distance from start to (r,c)
        int[][] dist = new int[rows][cols];
        for (int[] row : dist) {
            Arrays.fill(row, Integer.MAX_VALUE);
        }
        dist[startRow][startCol] = 0;

        boolean[][] visited = new boolean[rows][cols];
        Map<String, int[]> parent = new HashMap<>();
        List<int[]> visitedOrder = new ArrayList<>();

        // PriorityQueue entries: {row, col, distance}
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[2]));
        pq.offer(new int[]{startRow, startCol, 0});

        boolean pathFound = false;

        while (!pq.isEmpty()) {
            int[] current = pq.poll();
            int r = current[0];
            int c = current[1];
            int d = current[2];

            // Skip if we've already finalized this cell with a shorter distance
            if (visited[r][c]) {
                continue;
            }
            visited[r][c] = true;
            visitedOrder.add(new int[]{r, c});

            // Check if we reached the goal
            if (r == endRow && c == endCol) {
                pathFound = true;
                break;
            }

            // Relax neighbours
            for (int[] dir : DIRECTIONS) {
                int nr = r + dir[0];
                int nc = c + dir[1];

                if (grid.isTraversable(nr, nc) && !visited[nr][nc]) {
                    int edgeWeight = 1; // uniform weight; extend here for weighted grids
                    int newDist = d + edgeWeight;

                    if (newDist < dist[nr][nc]) {
                        dist[nr][nc] = newDist;
                        parent.put(nr + "," + nc, new int[]{r, c});
                        pq.offer(new int[]{nr, nc, newDist});
                    }
                }
            }
        }

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
        return "Dijkstra's Algorithm";
    }
}
