import java.util.*;

public class MazeGenerator {
    private final MazePanel panel;
    private final Random rand = new Random();

    public MazeGenerator(MazePanel panel) {
        this.panel = panel;
    }

    private void initGrid() {
        int rows = panel.rows;
        int cols = panel.cols;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) panel.grid[r][c] = new Cell(r, c, Cell.Type.WALL);
        }
    }

    // --- 1. PRIM'S ALGORITHM ---
    public void generatePrim() {
        initGrid();
        Cell[][] grid = panel.grid;

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
        finalizeMaze();
    }

    // --- 2. KRUSKAL'S ALGORITHM ---
    public void generateKruskal() {
        initGrid();
        Cell[][] grid = panel.grid;
        int rows = panel.rows;
        int cols = panel.cols;

        List<Edge> edges = new ArrayList<>();
        int[] parent = new int[rows * cols];
        for(int i=0; i<parent.length; i++) parent[i] = i;

        for (int r = 1; r < rows - 1; r += 2) {
            for (int c = 1; c < cols - 1; c += 2) {
                grid[r][c].setType(Cell.Type.NORMAL);
                if (c + 2 < cols - 1) edges.add(new Edge(r, c, r, c + 2, r, c + 1));
                if (r + 2 < rows - 1) edges.add(new Edge(r, c, r + 2, c, r + 1, c));
            }
        }
        Collections.shuffle(edges);

        for (Edge e : edges) {
            int id1 = e.r1 * cols + e.c1;
            int id2 = e.r2 * cols + e.c2;
            if (find(parent, id1) != find(parent, id2)) {
                union(parent, id1, id2);
                grid[e.wallR][e.wallC].setType(Cell.Type.NORMAL);
            }
        }
        finalizeMaze();
    }

    private int find(int[] parent, int i) {
        if (parent[i] == i) return i;
        return parent[i] = find(parent, parent[i]);
    }
    private void union(int[] parent, int i, int j) {
        parent[find(parent, i)] = find(parent, j);
    }
    private static class Edge {
        int r1, c1, r2, c2, wallR, wallC;
        public Edge(int r1, int c1, int r2, int c2, int wr, int wc) {
            this.r1=r1; this.c1=c1; this.r2=r2; this.c2=c2; this.wallR=wr; this.wallC=wc;
        }
    }

    private void finalizeMaze() {
        int rows = panel.rows;
        int cols = panel.cols;
        Cell[][] grid = panel.grid;

        // Add Loops
        int loops = (rows * cols) / 15;
        for (int i = 0; i < loops; i++) {
            int r = rand.nextInt(rows - 2) + 1;
            int c = rand.nextInt(cols - 2) + 1;
            if (grid[r][c].type == Cell.Type.WALL && r > 0 && r < rows - 1 && c > 0 && c < cols - 1) {
                grid[r][c].setType(Cell.Type.NORMAL);
            }
        }

        // Random Terrain
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
            if (isValid(nr, nc) && grid[nr][nc].type == Cell.Type.WALL) walls.add(grid[nr][nc]);
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