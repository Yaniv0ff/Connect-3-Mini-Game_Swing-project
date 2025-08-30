package Connect3_Game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class Connect3App extends JFrame {
    // Game window size
    static final int ROWS = 6, COLS = 7, CELL = 90, PADDING_TOP = 100, DISC_MARGIN = 10;
    private final Board board = new Board(ROWS, COLS);
    private final GamePanel panel = new GamePanel(COLS, ROWS, CELL, PADDING_TOP, DISC_MARGIN);
    private Color playerColor = new Color(220, 40, 60);
    private Color botColor = new Color(40, 90, 210);
    private boolean playerTurn = true;
    private boolean gameOver   = false;
    private volatile boolean dropping = false; // disc drop animation

    private int playerWins = 0, botWins = 0;
    private final Random rng = new Random();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Connect3App::new);
    }

    public Connect3App() {
        super("Connect-3 Mini Game!");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        pickColorsDialog(); // Opening window (explanation & customization)

        // UI: Game board + column buttons
        JPanel root = new JPanel(new BorderLayout(0, 6));
        panel.setPreferredSize(new Dimension(COLS * CELL, PADDING_TOP + ROWS * CELL + 40));
        panel.setBoard(board);
        panel.setPlayerColors(playerColor, botColor);

        JPanel buttonsBar = new JPanel(new GridLayout(1, COLS, 4, 0));
        for (int c = 0; c < COLS; c++) {
            final int col = c;
            JButton b = new JButton("▼");
            b.setFocusable(false);
            b.setFont(b.getFont().deriveFont(Font.BOLD, 20f));
            b.addActionListener(e -> dropDisc(col));
            buttonsBar.add(b);
        }

        // Keyboard shortcuts: N - new game , R - reset scoreboard
        addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                char ch = e.getKeyChar();
                if (ch == 'n' || ch == 'N') newGame();
                if (ch == 'r' || ch == 'R') resetScoreboard();
            }
        });
        refreshHeader();
        root.add(panel, BorderLayout.CENTER);
        root.add(buttonsBar, BorderLayout.SOUTH);
        setContentPane(root);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Dropping disc in this column
    private void dropDisc(int col) {
        if (gameOver || dropping) return;
        int row = board.findFreeRow(col);
        if (row < 0) return; // full column
        discAnimation(col, 1);
    }

    private int chooseRandomColumn() {
        for (int i=0; i<50; i++) {
            int c = rng.nextInt(COLS);
            if (board.findFreeRow(c) >= 0) return c;
        }
        for (int c=0; c<COLS; c++) if (board.findFreeRow(c) >= 0) return c;
        return 0;
    }

    // Disc drop animation
    private void discAnimation(int col, int who) {
        if (dropping) return;
        int targetRow = board.findFreeRow(col);
        if (targetRow < 0) return;
        dropping = true;

        new Thread(() -> {
            try {
                int x = col * CELL + DISC_MARGIN;
                int startY = PADDING_TOP + DISC_MARGIN;
                int endY   = PADDING_TOP + targetRow * CELL + DISC_MARGIN;
                int size   = CELL - 2 * DISC_MARGIN;
                Color color = (who == 1 ? playerColor : botColor);
                panel.setFalling(x, startY, size, color, true);
                int y = startY;
                while (y < endY) {
                    y += 12;
                    if (y > endY) y = endY;
                    panel.setFalling(x, y, size, color, true);
                    try { Thread.sleep(10); } catch (InterruptedException ignored) {}
                }

                // Disc Landed
                board.setCell(targetRow, col, who);
                panel.setFallingInactive();

                // Check for winners
                if (board.isWin(who)) {
                    gameOver = true;
                    if (who == 1) playerWins++; else botWins++;
                    refreshHeader();
                    SwingUtilities.invokeLater(() -> {
                        String msg = (who == 1) ? "Player Won!!!" : "PC Won!";
                        JOptionPane.showMessageDialog(this, msg, "Game Ended", JOptionPane.INFORMATION_MESSAGE);
                        newGame();
                    });
                } else if (board.isFull()) {
                    gameOver = true;
                    refreshHeader();
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this, "Draw!", "Game Ended", JOptionPane.INFORMATION_MESSAGE);
                        newGame();
                    });
                } else {
                    // No winners yet - switch turns
                    if (who == 1) {
                        playerTurn = false;
                        refreshHeader();
                        panel.repaint();
                        int botCol = chooseRandomColumn();
                        SwingUtilities.invokeLater(() -> discAnimation(botCol, 2));
                    } else {
                        playerTurn = true;
                        refreshHeader();
                        panel.repaint();
                    }
                }
            } finally {
                dropping = false;
            }
        }).start();
    }

    // clear game board
    private void newGame() {
        board.clear();
        playerTurn = true;
        gameOver   = false;
        refreshHeader();
        panel.repaint();
    }

    // reset scores
    private void resetScoreboard() {
        playerWins = 0; botWins = 0;
        refreshHeader();
        panel.repaint();
    }

    // Stats headline
    private void refreshHeader() {
        String score = "Player Wins:  " + playerWins + "    |    PC Wins:  " + botWins + "            'N'-New Game , 'R'-Reset Score";
        String turnStr = gameOver ? "Round End" : (playerTurn ? "Player's Turn!" : "PC Turn...");
        panel.setHeader(score, turnStr);
        panel.setPlayerColors(playerColor, botColor);
    }

    // Opening screen: Explanation % Customization
    private void pickColorsDialog() {
        Color[] options = new Color[]{Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW};
        String[] names  = new String[]{"Red","Blue","Green", "Yellow"};
        JComboBox<String> p1 = new JComboBox<>(names);
        JComboBox<String> p2 = new JComboBox<>(names);
        p2.setSelectedIndex(1);

        JLabel title = new JLabel("Welcome to Connect-3 Mini Game!", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));
        title.setOpaque(true);
        title.setBackground(new Color(235, 242, 255));
        title.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Explanation box
        JTextArea info = new JTextArea(
                        "In this game, the players need to connect a row / column / diagonal line\n" +
                        "Featuring 3 of their personal discs, by pressing the column buttons (▼)\n\n" +
                        "Latest Patch Additions :D\n" +
                        "1. Added Customization:\n   - Players can choose their favorite disc color\n" +
                        "2. Added Stats:\n   - Scoreboard will be displayed above the game area\n" +
                        "3. Added Keyboard Flexibility:\n   - Press 'N' for New game, " +
                        "Press 'R' to reset the scoreboard\n"
        );
        info.setEditable(false);
        info.setLineWrap(true);
        info.setWrapStyleWord(true);
        info.setFont(info.getFont().deriveFont(Font.PLAIN, 14f));
        info.setBackground(new Color(240,248,255));
        info.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80,120,220), 2),
                BorderFactory.createEmptyBorder(10,10,10,10)
        ));

        JPanel choices = new JPanel(new GridLayout(0,1,6,6));
        choices.add(new JLabel("Player disc color:"));
        choices.add(p1);
        choices.add(new JLabel("PC disc color:"));
        choices.add(p2);

        JPanel content = new JPanel(new BorderLayout(0, 10));
        content.add(title, BorderLayout.NORTH);
        content.add(info,  BorderLayout.CENTER);
        content.add(choices, BorderLayout.SOUTH);
        content.setPreferredSize(new Dimension(480, 400));

        while (true) {
            int ans = JOptionPane.showConfirmDialog(
                    this, content, "Pre-Game Screen",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
            );
            if (ans != JOptionPane.OK_OPTION) System.exit(0);

            int i1 = p1.getSelectedIndex();
            int i2 = p2.getSelectedIndex();
            if (i1 == i2) {
                JOptionPane.showMessageDialog(this, "Cant choose same colors.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                playerColor = options[i1];
                botColor = options[i2];
                break;
            }
        }
    }
}










