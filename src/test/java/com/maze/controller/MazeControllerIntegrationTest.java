package com.maze.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maze.model.CellType;
import com.maze.model.Grid;
import com.maze.model.SolveRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the MazeController REST endpoints.
 */
@SpringBootTest
@AutoConfigureMockMvc
class MazeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /api/algorithms returns all 4 algorithms")
    void getAlgorithms_returnsAll() throws Exception {
        mockMvc.perform(get("/api/algorithms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bfs").value("Breadth-First Search"))
                .andExpect(jsonPath("$.dfs").value("Depth-First Search"))
                .andExpect(jsonPath("$.dijkstra").value("Dijkstra's Algorithm"))
                .andExpect(jsonPath("$.astar").value("A* Search"));
    }

    @Test
    @DisplayName("POST /api/solve with BFS returns valid result")
    void solve_bfs_returnsResult() throws Exception {
        SolveRequest request = buildSolveRequest("bfs");

        mockMvc.perform(post("/api/solve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pathFound").value(true))
                .andExpect(jsonPath("$.algorithm").value("Breadth-First Search"))
                .andExpect(jsonPath("$.nodesExplored").isNumber())
                .andExpect(jsonPath("$.pathLength").isNumber())
                .andExpect(jsonPath("$.shortestPath").isArray())
                .andExpect(jsonPath("$.visitedOrder").isArray());
    }

    @Test
    @DisplayName("POST /api/solve with invalid algorithm returns 400 Bad Request")
    void solve_invalidAlgorithm_returns400() throws Exception {
        SolveRequest request = buildSolveRequest("unknown");

        mockMvc.perform(post("/api/solve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    @DisplayName("POST /api/generate returns a valid maze grid")
    void generate_returnsValidGrid() throws Exception {
        mockMvc.perform(post("/api/generate")
                        .param("rows", "15")
                        .param("cols", "15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rows").isNumber())
                .andExpect(jsonPath("$.cols").isNumber())
                .andExpect(jsonPath("$.cells").isArray());
    }

    @Test
    @DisplayName("GET / returns the index page (200)")
    void indexPage_returns200() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());
    }

    /**
     * Build a simple 5×5 solve request.
     */
    private SolveRequest buildSolveRequest(String algorithm) {
        Grid grid = new Grid(5, 5);
        grid.setCell(2, 2, CellType.WALL); // one wall

        SolveRequest request = new SolveRequest();
        request.setGrid(grid);
        request.setAlgorithm(algorithm);
        request.setStartRow(0);
        request.setStartCol(0);
        request.setEndRow(4);
        request.setEndCol(4);
        return request;
    }
}
