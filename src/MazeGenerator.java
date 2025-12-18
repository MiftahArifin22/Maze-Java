import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MazeGenerator {
    private final MazePanel panel;
    private final Random rand = new Random();

    public MazeGenerator(MazePanel panel) {
        this.panel = panel;
    }

    public void generate() {
        int rows = panel.rows;
        int cols = panel.cols;
        Cell[][] grid = panel.grid;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) grid[r][c] = new Cell(r, c, Cell.Type.WALL);
        }

        int sr = 1, sc = 1;
        grid[sr][sc].setType(Cell.Type.NORMAL);
        ArrayList<Cell> walls = new ArrayList<>();
        addWalls(sr, sc, walls, grid);

        while (!walls.isEmpty()) {
            int idx = rand.nextInt(walls.size());
            Cell w = walls.remove(idx);
            List<Cell> neighbors = getNeighborsDist2(w.r, w.c, grid);
            List<Cell> connected = new ArrayList<>();
            for (Cell n : neighbors) if (n.type != Cell.Type.WALL) connected.add(n);

            if (connected.size() == 1) {
                w.setType(Cell.Type.NORMAL);
                grid[(w.r + connected.get(0).r) / 2][(w.c + connected.get(0).c) / 2].setType(Cell.Type.NORMAL);
                addWalls(w.r, w.c, walls, grid);
            }
        }

        int loops = (rows * cols) / 15;
        for (int i = 0; i < loops; i++) {
            int r = rand.nextInt(rows - 2) + 1;
            int c = rand.nextInt(cols - 2) + 1;
            if (grid[r][c].type == Cell.Type.WALL && r > 0 && r < rows - 1 && c > 0 && c < cols - 1) {
                grid[r][c].setType(Cell.Type.NORMAL);
            }
        }

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (grid[r][c].type != Cell.Type.WALL) {
                    double p = rand.nextDouble();
                    if (p < 0.15) grid[r][c].setType(Cell.Type.GRASS);
                    else if (p < 0.25) grid[r][c].setType(Cell.Type.MUD);
                    else if (p < 0.30) grid[r][c].setType(Cell.Type.WATER);
                    else grid[r][c].setType(Cell.Type.NORMAL);
                }
            }
        }

        panel.startNode = grid[1][1]; panel.startNode.setType(Cell.Type.NORMAL);
        panel.endNode = grid[rows - 2][cols - 2]; panel.endNode.setType(Cell.Type.NORMAL);
    }

    private void addWalls(int r, int c, ArrayList<Cell> walls, Cell[][] grid) {
        int[][] dirs = {{-2, 0}, {2, 0}, {0, -2}, {0, 2}};
        for (int[] d : dirs) {
            int nr = r + d[0], nc = c + d[1];
            if (isValid(nr, nc) && grid[nr][nc].type == Cell.Type.WALL) {
                walls.add(grid[nr][nc]);
            }
        }
    }

    private List<Cell> getNeighborsDist2(int r, int c, Cell[][] grid) {
        List<Cell> list = new ArrayList<>();
        int[][] dirs = {{-2, 0}, {2, 0}, {0, -2}, {0, 2}};
        for (int[] d : dirs) {
            int nr = r + d[0], nc = c + d[1];
            if (isValid(nr, nc)) list.add(grid[nr][nc]);
        }
        return list;
    }

    private boolean isValid(int r, int c) {
        return r >= 0 && r < panel.rows && c >= 0 && c < panel.cols;
    }
}