import java.awt.*;

public class Cell {
    public int r, c;
    public Type type;
    public int cost;

    // Variabel Pathfinding
    public boolean visited = false;
    public boolean isPath = false;
    public boolean isCurrentHead = false;
    public Cell parent = null;
    public int dist = Integer.MAX_VALUE;
    public int hCost = 0;
    public int fCost = Integer.MAX_VALUE;

    // Warna Path Dinamis (Diubah oleh Solver)
    public Color pathColor = Color.YELLOW;

    public enum Type {
        WALL(new Color(20, 20, 20), 0),
        NORMAL(new Color(220, 220, 220), 0),
        GRASS(new Color(46, 204, 113), 1),
        MUD(new Color(211, 84, 0), 5),
        WATER(new Color(52, 152, 219), 10);

        public final Color color;
        public final int weight;
        Type(Color c, int w) { this.color = c; this.weight = w; }
    }

    public Cell(int r, int c, Type t) {
        this.r = r;
        this.c = c;
        setType(t);
    }

    public void setType(Type t) {
        this.type = t;
        this.cost = t.weight;
    }

    public void resetAlgo() {
        visited = false;
        isPath = false;
        isCurrentHead = false;
        parent = null;
        dist = Integer.MAX_VALUE;
        fCost = Integer.MAX_VALUE;
        pathColor = Color.YELLOW; // Reset warna ke default
    }

    public void draw(Graphics g, int size) {
        // 1. Gambar Base Terrain
        g.setColor(type.color);
        g.fillRect(c * size, r * size, size, size);

        g.setColor(new Color(0, 0, 0, 50)); // Grid border tipis
        g.drawRect(c * size, r * size, size, size);

        // 2. Gambar Jalur Final (Warna Dinamis)
        if (isPath) {
            g.setColor(pathColor);
            g.fillRect(c * size + 6, r * size + 6, size - 12, size - 12);
            g.setColor(Color.BLACK);
            g.drawRect(c * size + 6, r * size + 6, size - 12, size - 12);
        }

        // 3. Animasi Head (Titik yang sedang dicek)
        if (isCurrentHead) {
            g.setColor(Color.MAGENTA);
            g.fillRect(c * size + 4, r * size + 4, size - 8, size - 8);
        }

        // 4. Marker Start (Hijau)
        if (r == 1 && c == 1) {
            drawMarker(g, size, Color.GREEN);
        }
    }

    // Helper menggambar lingkaran marker
    public void drawMarker(Graphics g, int s, Color color) {
        g.setColor(color);
        g.fillOval(c * s + 2, r * s + 2, s - 4, s - 4);
        g.setColor(Color.WHITE);
        g.drawOval(c * s + 2, r * s + 2, s - 4, s - 4);
    }
}