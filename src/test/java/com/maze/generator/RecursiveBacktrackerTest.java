package com.maze.generator;

import com.maze.model.CellType;
import com.maze.model.Grid;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Recursive Backtracker maze generator.
 */
class RecursiveBacktrackerTest {

    private final RecursiveBacktracker generator = new RecursiveBacktracker();

    @Test
    @DisplayName("Generated maze has odd dimensions")
    void generatedMaze_hasOddDimensions() {
        Grid grid = generator.generate(20, 20);
        // Generator forces odd dimensions for the wall pattern
        assertEquals(1, grid.getRows() % 2, "Rows should be odd");
        assertEquals(1, grid.getCols() % 2, "Cols should be odd");
    }

    @Test
    @DisplayName("Generated maze has START and END cells")
    void generatedMaze_hasStartAndEnd() {
        Grid grid = generator.generate(15, 15);
        boolean hasStart = false;
        boolean hasEnd = false;

        for (int r = 0; r < grid.getRows(); r++) {
            for (int c = 0; c < grid.getCols(); c++) {
                if (grid.getCell(r, c) == CellType.START) hasStart = true;
                if (grid.getCell(r, c) == CellType.END) hasEnd = true;
            }
        }

        assertTrue(hasStart, "Maze should have a START cell");
        assertTrue(hasEnd, "Maze should have an END cell");
    }

    @Test
    @DisplayName("Generated maze has walls on the border")
    void generatedMaze_hasWallBorder() {
        Grid grid = generator.generate(15, 15);
        int rows = grid.getRows();
        int cols = grid.getCols();

        // Top and bottom borders should be walls
        for (int c = 0; c < cols; c++) {
            assertEquals(CellType.WALL, grid.getCell(0, c), "Top border should be wall at col " + c);
            assertEquals(CellType.WALL, grid.getCell(rows - 1, c), "Bottom border should be wall at col " + c);
        }

        // Left and right borders should be walls
        for (int r = 0; r < rows; r++) {
            assertEquals(CellType.WALL, grid.getCell(r, 0), "Left border should be wall at row " + r);
            assertEquals(CellType.WALL, grid.getCell(r, cols - 1), "Right border should be wall at row " + r);
        }
    }

    @Test
    @DisplayName("Generated maze has passages (not all walls)")
    void generatedMaze_hasPassages() {
        Grid grid = generator.generate(15, 15);
        int emptyCount = 0;

        for (int r = 0; r < grid.getRows(); r++) {
            for (int c = 0; c < grid.getCols(); c++) {
                CellType type = grid.getCell(r, c);
                if (type == CellType.EMPTY || type == CellType.START || type == CellType.END) {
                    emptyCount++;
                }
            }
        }

        assertTrue(emptyCount > 0, "Maze should have at least some passages");
    }

    @Test
    @DisplayName("Generated maze is solvable (BFS finds a path)")
    void generatedMaze_isSolvable() {
        Grid grid = generator.generate(15, 15);

        // Find start and end positions
        int startRow = -1, startCol = -1, endRow = -1, endCol = -1;
        for (int r = 0; r < grid.getRows(); r++) {
            for (int c = 0; c < grid.getCols(); c++) {
                if (grid.getCell(r, c) == CellType.START) { startRow = r; startCol = c; }
                if (grid.getCell(r, c) == CellType.END) { endRow = r; endCol = c; }
            }
        }

        assertNotEquals(-1, startRow, "START not found");
        assertNotEquals(-1, endRow, "END not found");

        // Use BFS to verify the maze is solvable
        var result = new com.maze.algorithm.BFS().solve(grid, startRow, startCol, endRow, endCol);
        assertTrue(result.isPathFound(), "Generated maze should always be solvable");
        assertTrue(result.getPathLength() > 1, "Path should be longer than 1 cell");
    }

    @Test
    @DisplayName("Generator produces different mazes on consecutive calls")
    void generator_producesDifferentMazes() {
        Grid maze1 = generator.generate(11, 11);
        Grid maze2 = generator.generate(11, 11);

        // Compare cell patterns — extremely unlikely to be identical
        boolean identical = true;
        for (int r = 0; r < maze1.getRows() && identical; r++) {
            for (int c = 0; c < maze1.getCols() && identical; c++) {
                if (maze1.getCell(r, c) != maze2.getCell(r, c)) {
                    identical = false;
                }
            }
        }

        assertFalse(identical, "Two generated mazes should differ");
    }
}
