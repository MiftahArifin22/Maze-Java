import java.awt.*;

public class Cell {
    public int r, c;
    public Type type;
    public int cost;

    // Variabel Pathfinding
    public boolean visited = false;
    public boolean isCurrentHead = false;
    public Cell parent = null;
    public int dist = Integer.MAX_VALUE;
    public int hCost = 0;
    public int fCost = Integer.MAX_VALUE;

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
        isCurrentHead = false;
        parent = null;
        dist = Integer.MAX_VALUE;
        fCost = Integer.MAX_VALUE;
    }

    public void draw(Graphics g, int size) {
        // 1. Gambar Base Terrain
        g.setColor(type.color);
        g.fillRect(c * size, r * size, size, size);

        g.setColor(new Color(0, 0, 0, 50)); // Grid border tipis
        g.drawRect(c * size, r * size, size, size);

        // 2. Trace / Jejak Transparan
        if (visited && type != Type.WALL) {
            g.setColor(new Color(0, 255, 255, 60)); // Cyan Transparan (Alpha 60)
            g.fillRect(c * size, r * size, size, size);
        }

        // 3. Animasi Head (Titik yang sedang dicek)
        if (isCurrentHead) {
            g.setColor(Color.MAGENTA);
            g.fillRect(c * size + 4, r * size + 4, size - 8, size - 8);
        }

        // 4. Marker Start (Hijau)
        if (r == 1 && c == 1) {
            g.setColor(Color.GREEN);
            g.fillOval(c * size + 2, r * size + 2, size - 4, size - 4);
            g.setColor(Color.WHITE);
            g.drawOval(c * size + 2, r * size + 2, size - 4, size - 4);
        }
    }

    public void drawMarker(Graphics g, int s, Color color) {
        g.setColor(color);
        g.fillOval(c * s + 2, r * s + 2, s - 4, s - 4);
        g.setColor(Color.WHITE);
        g.drawOval(c * s + 2, r * s + 2, s - 4, s - 4);
    }
}