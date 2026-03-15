package com.maze.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a single cell in the maze grid.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cell {
    private int row;
    private int col;
    private CellType type;

    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
        this.type = CellType.EMPTY;
    }
}
