package com.maze.service;

import com.maze.algorithm.AStar;
import com.maze.algorithm.BFS;
import com.maze.algorithm.DFS;
import com.maze.algorithm.Dijkstra;
import com.maze.algorithm.PathfindingAlgorithm;
import com.maze.model.Grid;
import com.maze.model.SolveRequest;
import com.maze.model.SolveResult;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Service layer that delegates maze solving to the appropriate algorithm.
 * Uses Strategy Pattern — algorithms are registered by name.
 */
@Service
public class MazeService {

    private final Map<String, PathfindingAlgorithm> algorithms = new HashMap<>();

    /**
     * Register all available algorithms on startup.
     */
    @PostConstruct
    public void init() {
        registerAlgorithm("bfs", new BFS());
        registerAlgorithm("dfs", new DFS());
        registerAlgorithm("dijkstra", new Dijkstra());
        registerAlgorithm("astar", new AStar());
    }

    /**
     * Register an algorithm so it can be selected by name.
     */
    public void registerAlgorithm(String key, PathfindingAlgorithm algorithm) {
        algorithms.put(key.toLowerCase(), algorithm);
    }

    /**
     * Get all registered algorithm names.
     */
    public Map<String, String> getAvailableAlgorithms() {
        Map<String, String> available = new HashMap<>();
        algorithms.forEach((key, algo) -> available.put(key, algo.getName()));
        return available;
    }

    /**
     * Solve the maze using the requested algorithm.
     */
    public SolveResult solve(SolveRequest request) {
        String algoKey = request.getAlgorithm().toLowerCase();
        PathfindingAlgorithm algorithm = algorithms.get(algoKey);

        if (algorithm == null) {
            throw new IllegalArgumentException(
                    "Unknown algorithm: " + request.getAlgorithm()
                    + ". Available: " + algorithms.keySet()
            );
        }

        return algorithm.solve(
                request.getGrid(),
                request.getStartRow(),
                request.getStartCol(),
                request.getEndRow(),
                request.getEndCol()
        );
    }
}
