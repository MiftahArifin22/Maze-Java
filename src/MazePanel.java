import javax.swing.*;
import java.awt.*;

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

    public MazePanel(int rows, int cols, int cellSize, Main frame) {
        this.rows = rows;
        this.cols = cols;
        this.cellSize = cellSize;
        this.mainFrame = frame;
        this.grid = new Cell[rows][cols];

        // Penting: Set ukuran agar tidak blank
        this.setPreferredSize(new Dimension(cols * cellSize, rows * cellSize));
        this.setBackground(Color.BLACK);

        this.generator = new MazeGenerator(this);
        this.solver = new MazeSolver(this);

        generateMaze();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (grid[r][c] != null) {
                    grid[r][c].draw(g, cellSize);

                    // Manual Draw End Node agar selalu terlihat paling atas
                    if (grid[r][c] == endNode) {
                        grid[r][c].drawMarker(g, cellSize, Color.RED);
                    }
                }
            }
        }
    }

    public void generateMaze() {
        generator.generate();
        resetAlgoState();
        repaint();
    }

    public void solveMaze(String algo) {
        solver.solve(algo);
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