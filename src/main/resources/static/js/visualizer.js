/**
 * Visualizer — Handles step-by-step animation of the solving process.
 */
class MazeVisualizer {
    constructor(grid) {
        this.grid = grid; // MazeGrid instance
        this.isRunning = false;
        this.isCancelled = false;
    }

    /**
     * Animate the solving result step by step.
     * @param {Object} result - SolveResult from the backend
     * @param {number} speed - Delay in ms between each step
     * @param {Function} onComplete - Callback when animation finishes
     */
    async animate(result, speed, onComplete) {
        this.isRunning = true;
        this.isCancelled = false;

        // Phase 1: Animate visited cells (exploration wave)
        if (result.visitedOrder) {
            for (let i = 0; i < result.visitedOrder.length; i++) {
                if (this.isCancelled) {
                    this.isRunning = false;
                    return;
                }

                const [row, col] = result.visitedOrder[i];

                // Don't overwrite start/end cells
                if (this.grid._isStart(row, col) || this.grid._isEnd(row, col)) {
                    continue;
                }

                // Gradient color from light cyan to deeper blue as time progresses
                const progress = i / result.visitedOrder.length;
                const r = Math.round(0 + progress * 20);
                const g = Math.round(188 - progress * 80);
                const b = Math.round(212 - progress * 40);
                this.grid.drawCell(row, col, `rgb(${r}, ${g}, ${b})`);
                this.grid.grid[row][col] = 'VISITED';

                await this._sleep(speed);
            }
        }

        // Small pause between phases
        await this._sleep(Math.max(speed * 5, 200));

        // Phase 2: Animate shortest path (golden trail)
        if (result.pathFound && result.shortestPath) {
            for (let i = 0; i < result.shortestPath.length; i++) {
                if (this.isCancelled) {
                    this.isRunning = false;
                    return;
                }

                const [row, col] = result.shortestPath[i];

                if (this.grid._isStart(row, col) || this.grid._isEnd(row, col)) {
                    continue;
                }

                this.grid.drawCell(row, col, '#ffd700');
                this.grid.grid[row][col] = 'PATH';

                await this._sleep(speed * 3); // Slower for emphasis
            }
        }

        this.isRunning = false;
        if (onComplete) onComplete();
    }

    /**
     * Cancel the current animation.
     */
    cancel() {
        this.isCancelled = true;
    }

    /**
     * Helper: sleep for a given number of milliseconds.
     */
    _sleep(ms) {
        return new Promise(resolve => setTimeout(resolve, ms));
    }
}
