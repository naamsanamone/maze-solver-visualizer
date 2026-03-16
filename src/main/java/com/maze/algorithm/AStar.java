package com.maze.algorithm;

import com.maze.model.Grid;
import com.maze.model.SolveResult;

import java.util.*;

/**
 * A* (A-Star) pathfinding algorithm.
 *
 * Combines Dijkstra's guaranteed optimality with a heuristic that guides
 * the search toward the goal, exploring far fewer nodes.
 *
 * f(n) = g(n) + h(n)
 *   - g(n) = actual cost from start to n
 *   - h(n) = heuristic estimate from n to goal (Manhattan distance)
 *
 * Key properties:
 *   - Guarantees shortest path when h(n) is admissible (never overestimates)
 *   - Manhattan distance is admissible for 4-directional grids
 *   - Typically explores fewer nodes than Dijkstra or BFS
 *   - Time complexity: O(V log V) worst case, but much faster in practice
 *   - Widely used in games, GPS navigation, and robotics
 */
public class AStar implements PathfindingAlgorithm {

    private static final int[][] DIRECTIONS = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

    @Override
    public SolveResult solve(Grid grid, int startRow, int startCol, int endRow, int endCol) {

        long startTime = System.nanoTime();

        int rows = grid.getRows();
        int cols = grid.getCols();

        // g(n): actual cost from start to each cell
        int[][] gScore = new int[rows][cols];
        for (int[] row : gScore) {
            Arrays.fill(row, Integer.MAX_VALUE);
        }
        gScore[startRow][startCol] = 0;

        boolean[][] visited = new boolean[rows][cols];
        Map<String, int[]> parent = new HashMap<>();
        List<int[]> visitedOrder = new ArrayList<>();

        // PriorityQueue ordered by f(n) = g(n) + h(n)
        // Entries: {row, col, fScore}
        PriorityQueue<int[]> openSet = new PriorityQueue<>(Comparator.comparingInt(a -> a[2]));
        int initialF = manhattanDistance(startRow, startCol, endRow, endCol);
        openSet.offer(new int[]{startRow, startCol, initialF});

        boolean pathFound = false;

        while (!openSet.isEmpty()) {
            int[] current = openSet.poll();
            int r = current[0];
            int c = current[1];

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

            // Explore neighbours
            for (int[] dir : DIRECTIONS) {
                int nr = r + dir[0];
                int nc = c + dir[1];

                if (grid.isTraversable(nr, nc) && !visited[nr][nc]) {
                    int edgeWeight = 1;
                    int tentativeG = gScore[r][c] + edgeWeight;

                    if (tentativeG < gScore[nr][nc]) {
                        gScore[nr][nc] = tentativeG;
                        int f = tentativeG + manhattanDistance(nr, nc, endRow, endCol);
                        parent.put(nr + "," + nc, new int[]{r, c});
                        openSet.offer(new int[]{nr, nc, f});
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

    /**
     * Manhattan distance heuristic — admissible for 4-directional movement.
     * |r1 - r2| + |c1 - c2| never overestimates the true shortest path.
     */
    private int manhattanDistance(int r1, int c1, int r2, int c2) {
        return Math.abs(r1 - r2) + Math.abs(c1 - c2);
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
        return "A* Search";
    }
}
