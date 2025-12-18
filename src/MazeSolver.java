import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class MazeSolver {
    private final MazePanel panel;
    private int visitedCount = 0;
    private String currentAlgoName = "";

    public MazeSolver(MazePanel panel) {
        this.panel = panel;
    }

    public void solve(String algo) {
        panel.resetAlgoState(); // Reset jejak visited lama
        this.currentAlgoName = algo;
        panel.isAnimating = true;
        visitedCount = 0;

        if (algo.equals("BFS")) solveBFS();
        else if (algo.equals("DFS")) solveDFS();
        else if (algo.equals("Dijkstra")) solveDijkstra();
        else if (algo.equals("A*")) solveAStar();

        panel.isAnimating = false;
    }

    // --- ALGORITMA SETUP ---
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
            // 1. Cek Antrian Kosong (Belum ketemu target tapi antrian habis)
            if ((q != null && q.isEmpty()) || (s != null && s.isEmpty()) || (pq != null && pq.isEmpty())) {
                break;
            }

            // 2. Ambil Node
            Cell curr;
            if (q != null) curr = q.poll();
            else if (s != null) curr = s.pop();
            else curr = pq.poll();

            if (pq != null && curr.visited && curr != panel.startNode) continue;

            // --- VISUALISASI ---
            curr.visited = true; // Akan digambar warna Cyan transparan di Cell.draw()
            visitedCount++;

            if (panel.mainFrame != null) panel.mainFrame.updateStats(visitedCount, 0, false);

            // 3. Target Found
            if (curr == panel.endNode) {
                reconstructPath(curr);
                return;
            }

            // 4. Animasi Head
            curr.isCurrentHead = true;
            panel.repaint();
            try { Thread.sleep(5); } catch (Exception e) {}
            curr.isCurrentHead = false;

            // 5. Cek Tetangga
            for (Cell n : getNeighbors(curr)) {
                if (n.type != Cell.Type.WALL) {
                    if (q != null || s != null) { // Unweighted
                        if (!n.visited) {
                            n.visited = true;
                            n.parent = curr;
                            n.dist = curr.dist + 1;
                            if (q != null) q.add(n); else s.push(n);
                        }
                    } else { // Weighted
                        int newDist = curr.dist + 1 + n.cost;
                        if (newDist < n.dist) {
                            n.dist = newDist;
                            n.parent = curr;
                            if (isAStar) {
                                n.hCost = getHeuristic(n, panel.endNode);
                                n.fCost = n.dist + n.hCost;
                            }
                            pq.remove(n);
                            pq.add(n);
                        }
                    }
                }
            }
        }

        // --- SAFETY CHECK (NO PATH) ---
        // Panggil method resmi 'setStatus' agar tidak error
        if (panel.mainFrame != null) {
            panel.mainFrame.setStatus("No Path Found!", Color.RED);
        }
    }

    private void reconstructPath(Cell end) {
        List<Cell> pathList = new ArrayList<>();
        Cell c = end;
        int totalCost = end.dist;
        while (c != null) {
            pathList.add(c);
            c = c.parent;
        }
        Collections.reverse(pathList);

        panel.addPath(currentAlgoName, pathList);
        if (panel.mainFrame != null) panel.mainFrame.updateStats(visitedCount, totalCost, true);
    }

    private List<Cell> getNeighbors(Cell c) {
        List<Cell> l = new ArrayList<>();
        int[][] dirs = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        for (int[] d : dirs) {
            int nr = c.r + d[0], nc = c.c + d[1];
            if (nr >= 0 && nr < panel.rows && nc >= 0 && nc < panel.cols) l.add(panel.grid[nr][nc]);
        }
        return l;
    }

    private int getHeuristic(Cell a, Cell b) {
        return Math.abs(a.r - b.r) + Math.abs(a.c - b.c);
    }
}