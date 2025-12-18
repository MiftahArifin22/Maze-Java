import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class MazePanel extends JPanel {
    public final int rows;
    public final int cols;
    public final int cellSize;
    public Cell[][] grid;

    public Cell startNode, endNode;
    public Main mainFrame;
    public boolean isAnimating = false;

    private final MazeGenerator generator;
    private final MazeSolver solver;

    // --- FITUR BARU: Persistent Paths ---
    public Map<String, List<Cell>> savedPaths = new HashMap<>();
    public Map<String, Color> pathColors = new HashMap<>();

    public MazePanel(int rows, int cols, int cellSize, Main frame) {
        this.rows = rows;
        this.cols = cols;
        this.cellSize = cellSize;
        this.mainFrame = frame;
        this.grid = new Cell[rows][cols];

        this.setPreferredSize(new Dimension(cols * cellSize, rows * cellSize));
        this.setBackground(Color.BLACK);

        this.generator = new MazeGenerator(this);
        this.solver = new MazeSolver(this);

        // Setup Warna Awal (Mungkin masih ada yang tabrakan di tahap ini)
        pathColors.put("BFS", Color.CYAN);
        pathColors.put("DFS", Color.MAGENTA);
        pathColors.put("Dijkstra", new Color(255, 140, 0));
        pathColors.put("A*", Color.RED);

        generateMaze();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 1. Gambar Grid & Trace (via Cell.draw)
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (grid[r][c] != null) grid[r][c].draw(g, cellSize);
            }
        }

        // 2. FITUR BARU: Gambar Jalur Permanen (Saved Paths)
        Graphics2D g2 = (Graphics2D) g;
        for (Map.Entry<String, List<Cell>> entry : savedPaths.entrySet()) {
            String algo = entry.getKey();
            List<Cell> path = entry.getValue();
            Color c = pathColors.getOrDefault(algo, Color.WHITE);

            g2.setColor(c);

            for (Cell cell : path) {
                if (cell == startNode || cell == endNode) continue;

                // Offset agar jalur bertumpuk terlihat semua (Layering)
                int offset = 0;
                if (algo.equals("BFS")) offset = 6;
                if (algo.equals("DFS")) offset = 8;
                if (algo.equals("Dijkstra")) offset = 10;
                if (algo.equals("A*")) offset = 4;

                g2.fillRect(cell.c * cellSize + offset, cell.r * cellSize + offset,
                        cellSize - (offset*2), cellSize - (offset*2));

                g2.setColor(Color.BLACK);
                g2.drawRect(cell.c * cellSize + offset, cell.r * cellSize + offset,
                        cellSize - (offset*2), cellSize - (offset*2));
                g2.setColor(c);
            }
        }

        // 3. Gambar End Node Manual (Agar selalu di atas path)
        if (endNode != null) endNode.drawMarker(g, cellSize, Color.RED);
    }

    public void generateMaze() {
        clearPaths(); // Hapus jalur lama saat generate
        generator.generate();
        resetAlgoState();
        repaint();
    }

    public void solveMaze(String algo) {
        solver.solve(algo);
    }

    // Method baru untuk menyimpan jalur dari Solver
    public void addPath(String algo, List<Cell> path) {
        savedPaths.put(algo, path);
        repaint();
    }

    public void clearPaths() {
        savedPaths.clear();
        resetAlgoState();
        repaint();
    }

    public void resetAlgoState() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if(grid[r][c] != null) grid[r][c].resetAlgo();
            }
        }
        repaint();
    }
}