package com.maze.controller;

import com.maze.model.Grid;
import com.maze.model.SolveRequest;
import com.maze.model.SolveResult;
import com.maze.service.MazeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller that serves the Thymeleaf UI page and handles API requests.
 */
@Controller
public class MazeController {

    private final MazeService mazeService;

    public MazeController(MazeService mazeService) {
        this.mazeService = mazeService;
    }

    /**
     * Serves the main visualizer page.
     */
    @GetMapping("/")
    public String index() {
        return "index";
    }

    /**
     * REST endpoint: Solve the maze with the given algorithm.
     */
    @PostMapping("/api/solve")
    @ResponseBody
    public ResponseEntity<SolveResult> solve(@RequestBody SolveRequest request) {
        SolveResult result = mazeService.solve(request);
        return ResponseEntity.ok(result);
    }

    /**
     * REST endpoint: Generate a random maze.
     */
    @PostMapping("/api/generate")
    @ResponseBody
    public ResponseEntity<Grid> generate(@RequestParam int rows, @RequestParam int cols) {
        Grid maze = mazeService.generateMaze(rows, cols);
        return ResponseEntity.ok(maze);
    }

    /**
     * REST endpoint: Get available algorithms.
     */
    @GetMapping("/api/algorithms")
    @ResponseBody
    public ResponseEntity<Map<String, String>> getAlgorithms() {
        return ResponseEntity.ok(mazeService.getAvailableAlgorithms());
    }

    /**
     * Handle invalid algorithm or bad request errors gracefully.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    public ResponseEntity<Map<String, String>> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", ex.getMessage()));
    }
}
