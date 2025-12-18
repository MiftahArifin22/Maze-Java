import java.awt.Color;
import java.util.*;

public class MazeSolver {
    private final MazePanel panel;
    private int visitedCount = 0;
    private String currentAlgo = "";

    public MazeSolver(MazePanel panel) {
        this.panel = panel;
    }

    public void solve(String algo) {
        this.currentAlgo = algo;
        panel.resetAlgoState();
        panel.isAnimating = true;
        visitedCount = 0;

        if (algo.equals("BFS")) solveBFS();
        else if (algo.equals("DFS")) solveDFS();
        else if (algo.equals("Dijkstra")) solveDijkstra();
        else if (algo.equals("A*")) solveAStar();

        panel.isAnimating = false;
    }

    private void solveBFS() {
        Queue<Cell> q = new LinkedList<>();
        initSearch(q, null, null);
        processSearch(q, null, null, false);
    }

    private void solveDFS() {
        Stack<Cell> s = new Stack<>();
        initSearch(null, s, null);
        processSearch(null, s, null, false);
    }

    private void solveDijkstra() {
        PriorityQueue<Cell> pq = new PriorityQueue<>(Comparator.comparingInt(c -> c.dist));
        initSearch(null, null, pq);
        processSearch(null, null, pq, false);
    }

    private void solveAStar() {
        PriorityQueue<Cell> pq = new PriorityQueue<>(Comparator.comparingInt(c -> c.fCost));
        initSearch(null, null, pq);
        panel.startNode.hCost = getHeuristic(panel.startNode, panel.endNode);
        panel.startNode.fCost = panel.startNode.dist + panel.startNode.hCost;
        processSearch(null, null, pq, true);
    }

    private void initSearch(Queue<Cell> q, Stack<Cell> s, PriorityQueue<Cell> pq) {
        panel.startNode.visited = true;
        panel.startNode.dist = 0;
        if (q != null) q.add(panel.startNode);
        else if (s != null) s.push(panel.startNode);
        else if (pq != null) pq.add(panel.startNode);
    }

    private void processSearch(Queue<Cell> q, Stack<Cell> s, PriorityQueue<Cell> pq, boolean isAStar) {
        while (true) {
            // Cek jika kosong -> Not Found
            if ((q != null && q.isEmpty()) || (s != null && s.isEmpty()) || (pq != null && pq.isEmpty())) {
                if (panel.mainFrame != null) panel.mainFrame.updateStats(visitedCount, -1, true);
                break;
            }

            Cell curr;
            if (q != null) curr = q.poll();
            else if (s != null) curr = s.pop();
            else curr = pq.poll();

            if (pq != null && curr.visited && curr != panel.startNode) continue;

            curr.visited = true;
            visitedCount++;
            if (panel.mainFrame != null) panel.mainFrame.updateStats(visitedCount, 0, false);

            if (curr == panel.endNode) {
                reconstructPath(curr);
                return;
            }

            curr.isCurrentHead = true;
            panel.repaint();
            try { Thread.sleep(3); } catch (Exception e) {}
            curr.isCurrentHead = false;

            for (Cell n : getNeighbors(curr)) {
                if (n.type != Cell.Type.WALL) {
                    if (q != null || s != null) {
                        if (!n.visited) {
                            n.visited = true;
                            n.parent = curr;
                            n.dist = curr.dist + 1;
                            if (q != null) q.add(n); else s.push(n);
                        }
                    } else {
                        int newDist = curr.dist + 1 + n.cost;
                        if (newDist < n.dist) {
                            n.dist = newDist;
                            n.parent = curr;
                            if (isAStar) {
                                n.hCost = getHeuristic(n, panel.endNode);
                                n.fCost = n.dist + n.hCost;
                            }
                            pq.remove(n); pq.add(n);
                        }
                    }
                }
            }
        }
    }

    private void reconstructPath(Cell end) {
        // Tentukan warna sesuai algoritma yang sedang jalan
        Color pathColor = Color.YELLOW; // Default BFS
        if (currentAlgo.equals("DFS")) pathColor = Color.MAGENTA;
        else if (currentAlgo.equals("Dijkstra")) pathColor = new Color(255, 140, 0); // Orange
        else if (currentAlgo.equals("A*")) pathColor = Color.RED;

        Cell c = end;
        int totalCost = end.dist;
        while (c != null) {
            c.isPath = true;
            c.pathColor = pathColor; // Set warna ke cell
            c = c.parent;
            panel.repaint();
            try { Thread.sleep(10); } catch (Exception e) {}
        }
        if (panel.mainFrame != null) panel.mainFrame.updateStats(visitedCount, totalCost, true);
    }

    private List<Cell> getNeighbors(Cell c) {
        List<Cell> l = new ArrayList<>();
        int[][] dirs = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        for (int[] d : dirs) {
            int nr = c.r + d[0], nc = c.c + d[1];
            if (nr >= 0 && nr < panel.rows && nc >= 0 && nc < panel.cols) {
                l.add(panel.grid[nr][nc]);
            }
        }
        return l;
    }

    private int getHeuristic(Cell a, Cell b) {
        return Math.abs(a.r - b.r) + Math.abs(a.c - b.c);
    }
}