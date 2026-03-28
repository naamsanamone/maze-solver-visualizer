package com.maze.generator;

import com.maze.model.CellType;
import com.maze.model.Grid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Recursive Backtracker maze generation algorithm (randomized DFS).
 *
 * How it works:
 *   1. Start with a grid where ALL cells are walls
 *   2. Pick a starting cell, mark it as a passage
 *   3. Randomly choose an unvisited neighbour 2 steps away
 *   4. Carve a passage to that neighbour (remove the wall between them)
 *   5. Recurse from the new cell
 *   6. If no unvisited neighbours, backtrack (stack unwinds)
 *
 * This produces a "perfect maze" — exactly one path between any two cells,
 * with long winding corridors (great for visualization).
 *
 * The "2-step" approach ensures walls remain between passages,
 * creating the classic maze look with thick walls.
 */
public class RecursiveBacktracker implements MazeGenerator {

    private static final int[][] DIRECTIONS = {{-2, 0}, {2, 0}, {0, -2}, {0, 2}};
    private final Random random = new Random();

    @Override
    public Grid generate(int rows, int cols) {
        // Ensure odd dimensions so the wall/passage pattern works cleanly
        if (rows % 2 == 0) rows++;
        if (cols % 2 == 0) cols++;

        Grid grid = new Grid(rows, cols);

        // Step 1: Fill everything with walls
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                grid.setCell(r, c, CellType.WALL);
            }
        }

        // Step 2: Carve passages using recursive backtracking from (1,1)
        boolean[][] visited = new boolean[rows][cols];
        carve(grid, visited, 1, 1);

        // Step 3: Place START at top-left passage and END at bottom-right passage
        grid.setCell(1, 1, CellType.START);

        // Find the bottom-right-most passage cell for END
        int endRow = rows - 2;
        int endCol = cols - 2;
        // Ensure end position is a passage (should be, but safety check)
        while (endRow > 0 && endCol > 0 && grid.getCell(endRow, endCol) == CellType.WALL) {
            endCol -= 2;
            if (endCol < 1) {
                endCol = cols - 2;
                endRow -= 2;
            }
        }
        grid.setCell(endRow, endCol, CellType.END);

        return grid;
    }

    /**
     * Recursively carve passages through the maze using iterative DFS
     * (avoids stack overflow on large grids).
     */
    private void carve(Grid grid, boolean[][] visited, int startRow, int startCol) {
        // Use an explicit stack to avoid stack overflow on large grids
        List<int[]> stack = new ArrayList<>();
        stack.add(new int[]{startRow, startCol});
        visited[startRow][startCol] = true;
        grid.setCell(startRow, startCol, CellType.EMPTY);

        while (!stack.isEmpty()) {
            int[] current = stack.get(stack.size() - 1);
            int r = current[0];
            int c = current[1];

            // Get unvisited neighbours (2 steps away)
            List<int[]> neighbours = getUnvisitedNeighbours(grid, visited, r, c);

            if (neighbours.isEmpty()) {
                // Backtrack
                stack.remove(stack.size() - 1);
            } else {
                // Pick a random neighbour
                int[] next = neighbours.get(random.nextInt(neighbours.size()));
                int nr = next[0];
                int nc = next[1];

                // Carve the wall between current and next
                int wallRow = r + (nr - r) / 2;
                int wallCol = c + (nc - c) / 2;
                grid.setCell(wallRow, wallCol, CellType.EMPTY);

                // Mark next as passage and visited
                grid.setCell(nr, nc, CellType.EMPTY);
                visited[nr][nc] = true;

                stack.add(new int[]{nr, nc});
            }
        }
    }

    /**
     * Get all unvisited neighbours that are 2 cells away (for wall carving).
     */
    private List<int[]> getUnvisitedNeighbours(Grid grid, boolean[][] visited, int row, int col) {
        List<int[]> neighbours = new ArrayList<>();

        for (int[] dir : DIRECTIONS) {
            int nr = row + dir[0];
            int nc = col + dir[1];

            if (nr > 0 && nr < grid.getRows() - 1 && nc > 0 && nc < grid.getCols() - 1
                    && !visited[nr][nc]) {
                neighbours.add(new int[]{nr, nc});
            }
        }

        // Shuffle for randomness
        Collections.shuffle(neighbours, random);
        return neighbours;
    }

    @Override
    public String getName() {
        return "Recursive Backtracker";
    }
}
