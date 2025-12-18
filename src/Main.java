import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class Main extends JFrame {
    private static final int ROWS = 31;
    private static final int COLS = 45;
    private static final int CELL_SIZE = 25;

    private MazePanel mazePanel;
    private JLabel lblStatus, lblAlgoName, lblLiveCost, lblFinalCost;
    private JButton btnGenMaze, btnClearPaths;
    private JComboBox<String> cmbGenAlgo;

    public Main() {
        setTitle("Java Maze Pathfinder (Final Fix)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);

        mazePanel = new MazePanel(ROWS, COLS, CELL_SIZE, this);
        add(mazePanel, BorderLayout.CENTER);
        add(createControlPanel(), BorderLayout.EAST);

        pack();
        setLocationRelativeTo(null);
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(280, ROWS * CELL_SIZE));
        panel.setBackground(new Color(40, 40, 40));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Header
        JLabel title = new JLabel("CONTROL PANEL");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createVerticalStrut(20));

        // Legend
        panel.add(createLegend(Cell.Type.NORMAL, "Terrain (Cost 0)"));
        panel.add(createLegend(Cell.Type.GRASS, "Grass (Cost 1)"));
        panel.add(createLegend(Cell.Type.MUD, "Mud (Cost 5)"));
        panel.add(createLegend(Cell.Type.WATER, "Water (Cost 10)"));
        panel.add(Box.createVerticalStrut(20));

        // Stats
        JPanel stats = new JPanel(new GridLayout(3, 2));
        stats.setBackground(new Color(60, 60, 60));
        stats.setMaximumSize(new Dimension(250, 60));

        lblAlgoName = new JLabel("-"); lblAlgoName.setForeground(Color.CYAN);
        lblLiveCost = new JLabel("0"); lblLiveCost.setForeground(Color.YELLOW);
        lblFinalCost = new JLabel("-"); lblFinalCost.setForeground(Color.GREEN);

        addStatLabel(stats, "Algo:", lblAlgoName);
        addStatLabel(stats, "Check:", lblLiveCost);
        addStatLabel(stats, "Cost:", lblFinalCost);
        panel.add(stats);
        panel.add(Box.createVerticalStrut(15));

        // Status
        lblStatus = new JLabel("Ready");
        lblStatus.setForeground(Color.ORANGE);
        lblStatus.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(lblStatus);
        panel.add(Box.createVerticalStrut(15));

        // Buttons
        panel.add(createBtn("BFS (Yellow)", new Color(218, 165, 32), "BFS"));
        panel.add(Box.createVerticalStrut(5));
        panel.add(createBtn("DFS (Magenta)", new Color(142, 68, 173), "DFS"));
        panel.add(Box.createVerticalStrut(15));
        panel.add(createBtn("Dijkstra (Orange)", new Color(230, 126, 34), "Dijkstra"));
        panel.add(Box.createVerticalStrut(5));
        panel.add(createBtn("A* Star (Red)", new Color(231, 76, 60), "A*"));
        panel.add(Box.createVerticalStrut(10));

        // Clear Paths
        btnClearPaths = new JButton("Clear Paths Only");
        btnClearPaths.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnClearPaths.setBackground(Color.GRAY);
        btnClearPaths.setForeground(Color.WHITE);
        btnClearPaths.addActionListener(e -> mazePanel.clearPaths());
        panel.add(btnClearPaths);
        panel.add(Box.createVerticalStrut(20));

        // Generate
        JLabel lblGen = new JLabel("Generation Method:");
        lblGen.setForeground(Color.LIGHT_GRAY);
        lblGen.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(lblGen);

        String[] genMethods = { "Prim's Algorithm", "Kruskal's Algorithm" };
        cmbGenAlgo = new JComboBox<>(genMethods);
        cmbGenAlgo.setMaximumSize(new Dimension(250, 30));
        panel.add(cmbGenAlgo);
        panel.add(Box.createVerticalStrut(5));

        btnGenMaze = createBtn("Generate New Maze", new Color(39, 174, 96), "GEN");
        panel.add(btnGenMaze);

        return panel;
    }

    private void runAlgo(String type) {
        if (mazePanel.isAnimating) return;
        lblAlgoName.setText(type);
        lblStatus.setText("Running...");
        lblStatus.setForeground(Color.ORANGE); // Reset warna status
        lblLiveCost.setText("0");
        lblFinalCost.setText("-");
        new Thread(() -> mazePanel.solveMaze(type)).start();
    }

    public void updateStats(int checkCount, int finalCost, boolean finished) {
        SwingUtilities.invokeLater(() -> {
            if (!finished) {
                lblLiveCost.setText(String.valueOf(checkCount));
            } else {
                lblFinalCost.setText(String.valueOf(finalCost));
                lblStatus.setText("Done!");
                lblStatus.setForeground(Color.GREEN);
            }
        });
    }

    // --- SOLUSI ERROR: Method ini dipanggil oleh MazeSolver ---
    public void setStatus(String text, Color color) {
        SwingUtilities.invokeLater(() -> {
            lblStatus.setText(text);
            lblStatus.setForeground(color);
        });
    }

    private void addStatLabel(JPanel p, String title, JLabel val) {
        JLabel l = new JLabel(title);
        l.setForeground(Color.LIGHT_GRAY);
        p.add(l);
        p.add(val);
    }

    private JButton createBtn(String text, Color bg, String action) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(250, 30));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));

        btn.addActionListener(e -> {
            if (action.equals("GEN")) {
                String selected = (String) cmbGenAlgo.getSelectedItem();
                mazePanel.generateMaze(selected);
            }
            else runAlgo(action);
        });
        return btn;
    }

    private JPanel createLegend(Cell.Type type, String text) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(250, 20));
        JPanel box = new JPanel();
        box.setPreferredSize(new Dimension(12, 12));
        box.setBackground(type.color);
        box.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        JLabel l = new JLabel(text);
        l.setForeground(Color.LIGHT_GRAY);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        p.add(box); p.add(l);
        return p;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }
}