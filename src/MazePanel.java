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

    // Menyimpan Jalur (Persistent Paths)
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

        // Definisi Warna Jalur
        pathColors.put("BFS", Color.YELLOW);
        pathColors.put("DFS", Color.MAGENTA);
        pathColors.put("Dijkstra", new Color(255, 140, 0));
        pathColors.put("A*", Color.RED);

        generateMaze("Prim's Algorithm");
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 1. Gambar Grid & Cell (termasuk visualisasi visited)
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (grid[r][c] != null) grid[r][c].draw(g, cellSize);
            }
        }

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 2. Gambar Jalur Permanen (Saved Paths)
        for (Map.Entry<String, List<Cell>> entry : savedPaths.entrySet()) {
            String algo = entry.getKey();
            List<Cell> path = entry.getValue();
            Color c = pathColors.getOrDefault(algo, Color.WHITE);

            g2.setColor(c);

            for (Cell cell : path) {
                if (cell == startNode || cell == endNode) continue;

                // Offset agar garis tidak saling menimpa total
                int offset = 0;
                if (algo.equals("BFS")) offset = 6;       // Kuning (Tengah)
                if (algo.equals("DFS")) offset = 8;       // Magenta (Lebih kecil)
                if (algo.equals("Dijkstra")) offset = 10; // Oranye (Paling kecil)
                if (algo.equals("A*")) offset = 4;        // Merah (Paling besar/luar)

                g2.fillRect(cell.c * cellSize + offset, cell.r * cellSize + offset,
                        cellSize - (offset*2), cellSize - (offset*2));

                g2.setColor(Color.BLACK);
                g2.drawRect(cell.c * cellSize + offset, cell.r * cellSize + offset,
                        cellSize - (offset*2), cellSize - (offset*2));
                g2.setColor(c);
            }
        }
    }

    public void generateMaze(String method) {
        clearPaths(); // Hapus jalur jika maze baru di-generate
        if (method != null && method.contains("Prim")) generator.generatePrim();
        else generator.generateKruskal();
        resetAlgoState();
        repaint();
    }

    public void solveMaze(String algo) {
        solver.solve(algo);
    }

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
                if (grid[r][c] != null) grid[r][c].resetAlgo();
            }
        }
        repaint();
    }
}