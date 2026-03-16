package com.maze.generator;

import com.maze.model.Grid;

/**
 * Strategy Pattern interface for maze generation algorithms.
 */
public interface MazeGenerator {

    /**
     * Generate a maze grid with walls and passages.
     * The generated grid will have START and END cells placed automatically.
     *
     * @param rows number of rows
     * @param cols number of columns
     * @return a Grid with walls carved out to form a solvable maze
     */
    Grid generate(int rows, int cols);

    /**
     * Returns the display name of this generator.
     */
    String getName();
}
