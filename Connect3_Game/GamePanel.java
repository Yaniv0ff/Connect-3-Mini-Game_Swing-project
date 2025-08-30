package Connect3_Game;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel {
    private final int cols, rows, cell, padTop, margin;

    private Board board;
    private Color playerColor = Color.RED, botColor = Color.BLUE;
    private String headerLine1 = "", headerLine2 = "";

    // "Falling disc" layer
    private boolean fallingActive = false;
    private int fx, fy, fsize;
    private Color fcolor = Color.BLACK;

    public GamePanel(int cols, int rows, int cell, int padTop, int margin) {
        this.cols = cols; this.rows = rows; this.cell = cell; this.padTop = padTop; this.margin = margin;
        setBackground(new Color(245, 248, 255)); // Board background
    }

    public void setBoard(Board b) { this.board = b; }

    public void setPlayerColors(Color p, Color b) {
        this.playerColor = p;
        this.botColor = b;
        repaint();
    }

    public void setHeader(String line1, String line2) {
        this.headerLine1 = line1;
        this.headerLine2 = line2;
        repaint();
    }

    // Animation layer control
    public void setFalling(int x, int y, int size, Color color, boolean active) {
        this.fx = x; this.fy = y; this.fsize = size; this.fcolor = color; this.fallingActive = active;
        repaint();
    }
    public void setFallingInactive() {
        this.fallingActive = false;
        repaint();
    }

    // Game board design graphics
    @Override protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Headlines (scoreboard + turns)
        g2.setColor(new Color(40, 50, 70));
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 18f));
        g2.drawString(headerLine1, 12, 28);
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 14f));
        g2.drawString(headerLine2, 12, 52);

        // Game board color
        int w = cols * cell, h = rows * cell;
        g2.setColor(new Color(90, 155, 235));
        g2.fillRoundRect(0, padTop, w, h, 20, 20);

        // Board "holes" & discs
        if (board != null) {
            for (int r=0;r<rows;r++) {
                for (int c=0;c<cols;c++) {
                    int cx = c * cell + margin;
                    int cy = padTop + r * cell + margin;
                    int size = cell - 2 * margin;

                    // bright "holes"
                    g2.setColor(new Color(230, 235, 245));
                    g2.fillOval(cx, cy, size, size);

                    int val = board.getCell(r,c);
                    if (val != 0) {
                        g2.setColor(val==1 ? playerColor : botColor);
                        g2.fillOval(cx, cy, size, size);
                        g2.setColor(new Color(30, 30, 30, 80));
                        g2.drawOval(cx, cy, size, size);
                    }
                }
            }
        }

        // Falling disc layer (animation)
        if (fallingActive) {
            g2.setColor(fcolor);
            g2.fillOval(fx, fy, fsize, fsize);
            g2.setColor(new Color(30, 30, 30, 80));
            g2.drawOval(fx, fy, fsize, fsize);
        }

        g2.dispose();
    }
}



