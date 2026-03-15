package com.maze.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents the 2D maze grid.
 * The grid is a 2D array of CellType values.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Grid {
    private int rows;
    private int cols;
    private CellType[][] cells;

    /**
     * Creates an empty grid with all cells set to EMPTY.
     */
    public Grid(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.cells = new CellType[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                cells[r][c] = CellType.EMPTY;
            }
        }
    }

    /**
     * Get the cell type at a specific position.
     */
    public CellType getCell(int row, int col) {
        return cells[row][col];
    }

    /**
     * Set the cell type at a specific position.
     */
    public void setCell(int row, int col, CellType type) {
        cells[row][col] = type;
    }

    /**
     * Check if a position is within grid bounds and not a wall.
     */
    public boolean isTraversable(int row, int col) {
        return row >= 0 && row < rows && col >= 0 && col < cols
                && cells[row][col] != CellType.WALL;
    }
}
