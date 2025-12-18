import java.awt.*;

public class Cell {
    public int r, c;
    public Type type;
    public int cost;

    // State untuk algoritma
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
        public final Color color; public final int weight;
        Type(Color c, int w) { this.color = c; this.weight = w; }
    }

    public Cell(int r, int c, Type t) {
        this.r = r; this.c = c; setType(t);
    }

    public void setType(Type t) {
        this.type = t; this.cost = t.weight;
    }

    public void resetAlgo() {
        visited = false;
        isCurrentHead = false;
        parent = null;
        dist = Integer.MAX_VALUE;
        fCost = Integer.MAX_VALUE;
    }

    public void draw(Graphics g, int size) {
        // 1. Terrain
        g.setColor(type.color);
        g.fillRect(c * size, r * size, size, size);
        g.setColor(new Color(0, 0, 0, 50));
        g.drawRect(c * size, r * size, size, size);

        // 2. VISUALISASI "VISITED" (JEJAK PENCARIAN) - Cyan Transparan
        if (visited && type != Type.WALL) {
            g.setColor(new Color(0, 200, 255, 60)); // Cyan transparan
            g.fillRect(c * size, r * size, size, size);
            g.setColor(new Color(0, 200, 255, 120)); // Border sedikit lebih tebal
            g.drawRect(c * size, r * size, size, size);
        }

        // 3. Head (Animasi kotak berjalan)
        if (isCurrentHead) {
            g.setColor(Color.MAGENTA);
            g.fillRect(c * size + 4, r * size + 4, size - 8, size - 8);
        }

        // 4. Start Marker
        if (r == 1 && c == 1) drawMarker(g, size, Color.GREEN);
    }

    private void drawMarker(Graphics g, int s, Color color) {
        g.setColor(color);
        g.fillOval(c * s + 2, r * s + 2, s - 4, s - 4);
        g.setColor(Color.WHITE);
        g.drawOval(c * s + 2, r * s + 2, s - 4, s - 4);
    }
}