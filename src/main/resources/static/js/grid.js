/**
 * MazeGrid — Handles the HTML Canvas grid rendering and mouse interaction.
 * Responsible for drawing cells, managing grid state, and handling wall drawing.
 */
class MazeGrid {
    constructor(canvas, rows, cols) {
        this.canvas = canvas;
        this.ctx = canvas.getContext('2d');
        this.rows = rows;
        this.cols = cols;
        this.grid = []; // 2D array of CellType strings

        // Colors matching CSS variables
        this.colors = {
            EMPTY: '#1a1a2e',
            WALL: '#2d2d2d',
            START: '#00e676',
            END: '#ff5252',
            VISITED: '#00bcd4',
            PATH: '#ffd700',
            GRID_LINE: 'rgba(255, 255, 255, 0.06)'
        };

        this.startPos = null; // {row, col}
        this.endPos = null;   // {row, col}
        this.isDrawing = false;
        this.drawMode = 'wall'; // 'wall', 'start', 'end', 'erase'

        this._initGrid();
        this._resize();
        this._bindEvents();
    }

    /**
     * Initialize the grid with all EMPTY cells.
     */
    _initGrid() {
        this.grid = [];
        for (let r = 0; r < this.rows; r++) {
            this.grid[r] = [];
            for (let c = 0; c < this.cols; c++) {
                this.grid[r][c] = 'EMPTY';
            }
        }
        this.startPos = null;
        this.endPos = null;
    }

    /**
     * Resize the canvas to fit its container.
     */
    _resize() {
        const container = this.canvas.parentElement;
        const maxWidth = container.clientWidth - 20;
        const maxHeight = container.clientHeight - 20;
        this.cellSize = Math.max(4, Math.floor(Math.min(maxWidth / this.cols, maxHeight / this.rows)));
        this.canvas.width = this.cellSize * this.cols;
        this.canvas.height = this.cellSize * this.rows;
        this.render();
    }

    /**
     * Bind mouse events for interactive wall drawing.
     */
    _bindEvents() {
        this.canvas.addEventListener('mousedown', (e) => {
            this.isDrawing = true;
            this._handleCellClick(e);
        });

        this.canvas.addEventListener('mousemove', (e) => {
            if (this.isDrawing) {
                this._handleCellClick(e);
            }
        });

        this.canvas.addEventListener('mouseup', () => {
            this.isDrawing = false;
        });

        this.canvas.addEventListener('mouseleave', () => {
            this.isDrawing = false;
        });

        // Handle window resize
        window.addEventListener('resize', () => this._resize());
    }

    /**
     * Get the grid cell from a mouse event.
     */
    _getCellFromEvent(e) {
        const rect = this.canvas.getBoundingClientRect();
        const x = e.clientX - rect.left;
        const y = e.clientY - rect.top;
        const col = Math.floor(x / this.cellSize);
        const row = Math.floor(y / this.cellSize);
        if (row >= 0 && row < this.rows && col >= 0 && col < this.cols) {
            return { row, col };
        }
        return null;
    }

    /**
     * Handle a cell click based on current draw mode.
     */
    _handleCellClick(e) {
        const cell = this._getCellFromEvent(e);
        if (!cell) return;

        const { row, col } = cell;

        switch (this.drawMode) {
            case 'wall':
                if (this._isStartOrEnd(row, col)) return;
                this.grid[row][col] = 'WALL';
                break;
            case 'start':
                if (this.startPos) {
                    this.grid[this.startPos.row][this.startPos.col] = 'EMPTY';
                }
                this.grid[row][col] = 'START';
                this.startPos = { row, col };
                break;
            case 'end':
                if (this.endPos) {
                    this.grid[this.endPos.row][this.endPos.col] = 'EMPTY';
                }
                this.grid[row][col] = 'END';
                this.endPos = { row, col };
                break;
            case 'erase':
                if (this._isStart(row, col)) this.startPos = null;
                if (this._isEnd(row, col)) this.endPos = null;
                this.grid[row][col] = 'EMPTY';
                break;
        }

        this.render();
    }

    _isStart(row, col) {
        return this.startPos && this.startPos.row === row && this.startPos.col === col;
    }

    _isEnd(row, col) {
        return this.endPos && this.endPos.row === row && this.endPos.col === col;
    }

    _isStartOrEnd(row, col) {
        return this._isStart(row, col) || this._isEnd(row, col);
    }

    /**
     * Render the entire grid on the canvas.
     */
    render() {
        const ctx = this.ctx;
        const size = this.cellSize;

        // Clear canvas
        ctx.clearRect(0, 0, this.canvas.width, this.canvas.height);

        for (let r = 0; r < this.rows; r++) {
            for (let c = 0; c < this.cols; c++) {
                const x = c * size;
                const y = r * size;
                const type = this.grid[r][c];

                // Fill cell
                ctx.fillStyle = this.colors[type] || this.colors.EMPTY;
                ctx.fillRect(x, y, size, size);

                // Grid lines
                ctx.strokeStyle = this.colors.GRID_LINE;
                ctx.lineWidth = 0.5;
                ctx.strokeRect(x, y, size, size);
            }
        }
    }

    /**
     * Draw a single cell with a specific color (used during animation).
     */
    drawCell(row, col, color) {
        const x = col * this.cellSize;
        const y = row * this.cellSize;
        const ctx = this.ctx;

        ctx.fillStyle = color;
        ctx.fillRect(x + 1, y + 1, this.cellSize - 2, this.cellSize - 2);
    }

    /**
     * Clear only the visited/path cells, keep walls, start, and end.
     */
    clearPath() {
        for (let r = 0; r < this.rows; r++) {
            for (let c = 0; c < this.cols; c++) {
                if (this.grid[r][c] === 'VISITED' || this.grid[r][c] === 'PATH') {
                    this.grid[r][c] = 'EMPTY';
                }
            }
        }
        this.render();
    }

    /**
     * Reset the entire grid.
     */
    reset(newRows, newCols) {
        if (newRows) this.rows = newRows;
        if (newCols) this.cols = newCols;
        this._initGrid();
        this._resize();
    }

    /**
     * Export the grid state for the backend API.
     * Converts to the format expected by SolveRequest.
     */
    exportGrid() {
        // Convert string types to the CellType enum values
        const cells = [];
        for (let r = 0; r < this.rows; r++) {
            cells[r] = [];
            for (let c = 0; c < this.cols; c++) {
                cells[r][c] = this.grid[r][c];
            }
        }
        return {
            rows: this.rows,
            cols: this.cols,
            cells: cells
        };
    }

    /**
     * Import a grid from the backend (e.g., generated maze).
     */
    importGrid(gridData) {
        this.rows = gridData.rows;
        this.cols = gridData.cols;
        this.startPos = null;
        this.endPos = null;

        for (let r = 0; r < this.rows; r++) {
            for (let c = 0; c < this.cols; c++) {
                this.grid[r][c] = gridData.cells[r][c];
                if (this.grid[r][c] === 'START') {
                    this.startPos = { row: r, col: c };
                } else if (this.grid[r][c] === 'END') {
                    this.endPos = { row: r, col: c };
                }
            }
        }
        this._resize();
    }

    /**
     * Check if the grid is ready to solve (has start and end).
     */
    isReady() {
        return this.startPos !== null && this.endPos !== null;
    }
}
