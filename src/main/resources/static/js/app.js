/**
 * App — Main entry point. Wires together Grid, Visualizer, API, and UI controls.
 */
document.addEventListener('DOMContentLoaded', () => {

    // ===== DOM Elements =====
    const canvas = document.getElementById('maze-canvas');
    const algoSelect = document.getElementById('algorithm-select');
    const speedSlider = document.getElementById('speed-slider');
    const speedLabel = document.getElementById('speed-label');
    const gridSizeSelect = document.getElementById('grid-size');
    const btnSolve = document.getElementById('btn-solve');
    const btnGenerate = document.getElementById('btn-generate');
    const btnClearPath = document.getElementById('btn-clear-path');
    const btnReset = document.getElementById('btn-reset');
    const modeBtns = document.querySelectorAll('.mode-btn');

    // Stats elements
    const statAlgo = document.getElementById('stat-algo');
    const statNodes = document.getElementById('stat-nodes');
    const statPath = document.getElementById('stat-path');
    const statTime = document.getElementById('stat-time');
    const statFound = document.getElementById('stat-found');

    // ===== Initialize Grid & Visualizer =====
    const defaultSize = parseInt(gridSizeSelect.value);
    const mazeGrid = new MazeGrid(canvas, defaultSize, defaultSize);
    const visualizer = new MazeVisualizer(mazeGrid);

    // ===== Load Available Algorithms =====
    async function loadAlgorithms() {
        try {
            const algorithms = await MazeAPI.getAlgorithms();
            algoSelect.innerHTML = '';

            const keys = Object.keys(algorithms);
            if (keys.length === 0) {
                algoSelect.innerHTML = '<option value="" disabled selected>No algorithms yet</option>';
                btnSolve.disabled = true;
                return;
            }

            keys.forEach(key => {
                const option = document.createElement('option');
                option.value = key;
                option.textContent = algorithms[key];
                algoSelect.appendChild(option);
            });

            btnSolve.disabled = false;
        } catch (error) {
            console.warn('Could not load algorithms:', error.message);
            algoSelect.innerHTML = '<option value="" disabled selected>No algorithms yet</option>';
        }
    }
    loadAlgorithms();

    // ===== Speed Slider =====
    function getSpeed() {
        // Invert: slider 1 = slow (100ms), slider 100 = fast (1ms)
        return 101 - parseInt(speedSlider.value);
    }

    speedSlider.addEventListener('input', () => {
        speedLabel.textContent = getSpeed() + 'ms';
    });
    speedLabel.textContent = getSpeed() + 'ms';

    // ===== Draw Mode Buttons =====
    modeBtns.forEach(btn => {
        btn.addEventListener('click', () => {
            modeBtns.forEach(b => b.classList.remove('active'));
            btn.classList.add('active');
            mazeGrid.drawMode = btn.dataset.mode;
        });
    });

    // ===== Grid Size Change =====
    gridSizeSelect.addEventListener('change', () => {
        const size = parseInt(gridSizeSelect.value);
        mazeGrid.reset(size, size);
        clearStats();
    });

    // ===== Solve Button =====
    btnSolve.addEventListener('click', async () => {
        if (!mazeGrid.isReady()) {
            alert('Please place both a Start (🟢) and End (🔴) point on the grid.');
            return;
        }

        const algorithm = algoSelect.value;
        if (!algorithm) {
            alert('Please select an algorithm.');
            return;
        }

        // Clear previous path
        mazeGrid.clearPath();

        // Build request
        const request = {
            grid: mazeGrid.exportGrid(),
            algorithm: algorithm,
            startRow: mazeGrid.startPos.row,
            startCol: mazeGrid.startPos.col,
            endRow: mazeGrid.endPos.row,
            endCol: mazeGrid.endPos.col
        };

        // Disable controls during solve
        setControlsEnabled(false);

        try {
            const result = await MazeAPI.solve(request);

            // Update stats
            updateStats(result);

            // Animate
            await visualizer.animate(result, getSpeed(), () => {
                setControlsEnabled(true);
            });
        } catch (error) {
            alert('Error: ' + error.message);
            setControlsEnabled(true);
        }
    });

    // ===== Generate Maze Button =====
    btnGenerate.addEventListener('click', async () => {
        try {
            const size = parseInt(gridSizeSelect.value);
            const gridData = await MazeAPI.generateMaze(size, size);
            mazeGrid.importGrid(gridData);
            clearStats();
        } catch (error) {
            alert('Maze generation not available yet. Coming in a later step!');
        }
    });

    // ===== Clear Path Button =====
    btnClearPath.addEventListener('click', () => {
        visualizer.cancel();
        mazeGrid.clearPath();
        clearStats();
    });

    // ===== Reset Button =====
    btnReset.addEventListener('click', () => {
        visualizer.cancel();
        const size = parseInt(gridSizeSelect.value);
        mazeGrid.reset(size, size);
        clearStats();
    });

    // ===== Helper Functions =====
    function updateStats(result) {
        statAlgo.textContent = result.algorithm || '—';
        statNodes.textContent = result.nodesExplored || 0;
        statPath.textContent = result.pathFound ? result.pathLength : 'N/A';
        statTime.textContent = result.timeTakenMs + 'ms';
        statFound.textContent = result.pathFound ? '✅ Yes' : '❌ No';
        statFound.style.color = result.pathFound ? '#00e676' : '#ff5252';
    }

    function clearStats() {
        statAlgo.textContent = '—';
        statNodes.textContent = '—';
        statPath.textContent = '—';
        statTime.textContent = '—';
        statFound.textContent = '—';
        statFound.style.color = '';
    }

    function setControlsEnabled(enabled) {
        btnSolve.disabled = !enabled;
        btnGenerate.disabled = !enabled;
        btnReset.disabled = !enabled;
        algoSelect.disabled = !enabled;
    }
});
