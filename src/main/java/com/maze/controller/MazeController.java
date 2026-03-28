package com.maze.controller;

import com.maze.model.SolveRequest;
import com.maze.model.SolveResult;
import com.maze.service.MazeService;
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
     * REST endpoint: Get available algorithms.
     */
    @GetMapping("/api/algorithms")
    @ResponseBody
    public ResponseEntity<Map<String, String>> getAlgorithms() {
        return ResponseEntity.ok(mazeService.getAvailableAlgorithms());
    }
}
