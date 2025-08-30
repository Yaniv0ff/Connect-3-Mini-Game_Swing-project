package Connect3_Game;


public class Board {
    private final int rows, cols;
    private final int[][] grid; // 0 empty, 1 player, 2 pc

    public Board(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.grid = new int[rows][cols];
    }

    public int getRows() { return rows; }
    public int getCols() { return cols; }

    // clear grid
    public void clear() {
        for (int r=0;r<rows;r++) for (int c=0;c<cols;c++) grid[r][c]=0;
    }

    public int getCell(int r, int c) { return grid[r][c]; }

    public void setCell(int r, int c, int who) { grid[r][c] = who; }

    public int findFreeRow(int col) {
        for (int r = rows - 1; r >= 0; r--) {
            if (grid[r][col] == 0) return r;
        }
        return -1;
    }

    // check if columns are full
    public boolean isFull() {
        for (int c=0;c<cols;c++) if (grid[0][c]==0) return false;
        return true;
    }

    // check if someone won
    public boolean isWin(int who) {
        // width check
        for (int r=0;r<rows;r++)
            for (int c=0;c<=cols-3;c++)
                if (grid[r][c]==who && grid[r][c+1]==who && grid[r][c+2]==who) return true;

        // height check
        for (int c=0;c<cols;c++)
            for (int r=0;r<=rows-3;r++)
                if (grid[r][c]==who && grid[r+1][c]==who && grid[r+2][c]==who) return true;

        // left diagonal check "\"
        for (int r=0;r<=rows-3;r++)
            for (int c=0;c<=cols-3;c++)
                if (grid[r][c]==who && grid[r+1][c+1]==who && grid[r+2][c+2]==who) return true;

        // right diagonal check "/"
        for (int r=0;r<=rows-3;r++)
            for (int c=2;c<cols;c++)
                if (grid[r][c]==who && grid[r+1][c-1]==who && grid[r+2][c-2]==who) return true;

        return false;
    }
}


