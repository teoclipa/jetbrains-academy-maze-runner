package maze;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MazeBoard implements Serializable {
    public int size;
    public static final int ESCAPE_CELL = 2;
    public static final int WALL = 1;
    public static final int PATH = 0;
    public static int[] startCoordinates;
    public static int[] exitCoordinates;

    public int[][] board;

    public MazeBoard(int size) {
        this.size = size;
        this.board = new int[size][size];
        generateMaze();
    }

    public int getBoardCell(int x, int y) {
        return board[y][x];
    }

    public void setBoardCell(int x, int y, int value) {
        board[y][x] = value;
    }

    private void generateMaze() {
        // Set all cells as walls initially
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                board[i][j] = WALL;
            }
        }

        Random random = new Random();
        int startX = random.nextInt(size - 2) + 1; // Exclude the border cells
        int startY = 0;
        int exitX = random.nextInt(size - 2) + 1; // Exclude the border cells
        int exitY = size - 1;
        startCoordinates = new int[]{startX, startY};
        exitCoordinates = new int[]{exitX, exitY};

        board[startY][startX] = PATH;
        board[exitY][exitX] = PATH;

        generatePath(startX, startY, exitX, exitY);
        // Making sure the exit is clear by removing the one or two walls above the exit
        if (board[exitY - 1][exitX] == WALL) {
            board[exitY - 1][exitX] = PATH;
        }
        if (board[exitY - 2][exitX] == WALL) {
            board[exitY - 2][exitX] = PATH;
        }
    }


    private void generatePath(int x, int y, int targetX, int targetY) {
        board[y][x] = PATH;

        while (x != targetX || y != targetY) {
            List<int[]> neighbors = getNeighbors(x, y);

            if (!neighbors.isEmpty()) {
                Random random = new Random();
                int[] randomNeighbor = neighbors.get(random.nextInt(neighbors.size()));
                int nx = randomNeighbor[0];
                int ny = randomNeighbor[1];

                int wallX = (x + nx) / 2;
                int wallY = (y + ny) / 2;
                board[wallY][wallX] = PATH;
                board[ny][nx] = PATH;

                generatePath(nx, ny, targetX, targetY);
            } else {
                return;
            }
        }
    }


    List<int[]> getNeighbors(int x, int y) {
        List<int[]> neighbors = new ArrayList<>();
        int[][] directions = {{2, 0}, {0, 2}, {-2, 0}, {0, -2}};

        for (int[] direction : directions) {
            int nx = x + direction[0];
            int ny = y + direction[1];

            if (nx >= 1 && nx < size - 1 && ny >= 1 && ny < size - 1 && board[ny][nx] == WALL) {
                if (!hasIsland(nx, ny)) {
                    int[] neighbor = {nx, ny};
                    neighbors.add(neighbor);
                }
            }
        }
        return neighbors;
    }

    List<int[]> getPathNeighbors(int x, int y) {
        List<int[]> neighbors = new ArrayList<>();
        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

        for (int[] direction : directions) {
            int nx = x + direction[0];
            int ny = y + direction[1];

            if (nx >= 0 && nx < size && ny >= 0 && ny < size && board[ny][nx] == PATH) {
                int[] neighbor = {nx, ny};
                neighbors.add(neighbor);
            }
        }

        return neighbors;
    }


    private boolean hasIsland(int x, int y) {
        // Check if the 3x3 block centered at (x, y) contains only walls
        for (int i = y - 1; i <= y + 1; i++) {
            for (int j = x - 1; j <= x + 1; j++) {
                if (board[i][j] == PATH) {
                    return true;
                }
            }
        }
        return false;
    }


    public void displayMaze() {
        for (int[] row : board) {
            for (int cell : row) {
                if (cell == WALL) {
                    System.out.print("\u2588\u2588");
                } else if (cell == PATH) {
                    System.out.print("  ");
                } else if (cell == ESCAPE_CELL) {
                    System.out.print("//");
                }
            }
            System.out.println();
        }
    }
}