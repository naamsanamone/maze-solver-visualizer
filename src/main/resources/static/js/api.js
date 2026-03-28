/**
 * API module — Handles all HTTP requests to the Spring Boot backend.
 */
const MazeAPI = {

    /**
     * Send the grid to the backend for solving.
     * @param {Object} solveRequest - { grid, algorithm, startRow, startCol, endRow, endCol }
     * @returns {Promise<Object>} SolveResult
     */
    async solve(solveRequest) {
        const response = await fetch('/api/solve', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(solveRequest)
        });

        if (!response.ok) {
            const error = await response.text();
            throw new Error(`Solve failed: ${error}`);
        }

        return response.json();
    },

    /**
     * Get the list of available algorithms.
     * @returns {Promise<Object>} Map of algorithm key → display name
     */
    async getAlgorithms() {
        const response = await fetch('/api/algorithms');
        if (!response.ok) {
            throw new Error('Failed to load algorithms');
        }
        return response.json();
    },

    /**
     * Request the backend to generate a maze.
     * @param {number} rows
     * @param {number} cols
     * @returns {Promise<Object>} Grid data
     */
    async generateMaze(rows, cols) {
        const response = await fetch(`/api/generate?rows=${rows}&cols=${cols}`, {
            method: 'POST'
        });
        if (!response.ok) {
            throw new Error('Failed to generate maze');
        }
        return response.json();
    }
};
