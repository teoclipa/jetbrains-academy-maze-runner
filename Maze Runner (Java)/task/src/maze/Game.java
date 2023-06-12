package maze;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;

public class Game {
    private MazeBoard board;
    private int size;
    private boolean mazeLoaded;
    private final Scanner scanner;
    private boolean escapeFound;

    public Game() {
        this.board = null;
        this.mazeLoaded = false;
        this.scanner = new Scanner(System.in);
        this.escapeFound = false;
    }

    public int setBoardSize() {
        System.out.println("Please enter the size of the maze:");

        return scanner.nextInt();

    }

    public void generateMaze() {
        size = setBoardSize();
        board = new MazeBoard(size);
        mazeLoaded = true;
    }

    public void loadMaze(String fileName) {
        try {
            Scanner scanner = new Scanner(new File(fileName));
            int size = scanner.nextInt();
            board = new MazeBoard(size);
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    board.setBoardCell(j, i, scanner.nextInt());
                }
            }
            mazeLoaded = true;
            //find and set the board start and end coordinates
            for (int i = 0; i < size; i++) {
                if (board.getBoardCell(i, 0) == MazeBoard.PATH) {
                    MazeBoard.startCoordinates = new int[]{i, 0};
                }
                if (board.getBoardCell(i, size - 1) == MazeBoard.PATH) {
                    MazeBoard.exitCoordinates = new int[]{i, size - 1};
                }
            }


            System.out.println("Maze loaded successfully.");
        } catch (FileNotFoundException e) {
            System.out.println("The file " + fileName + " does not exist.");
        } catch (Exception e) {
            System.out.println("Cannot load the maze. It has an invalid format." + e.getMessage());
        }
    }

    public void saveMaze(String fileName) {
        if (mazeLoaded) {
            try {
                PrintWriter writer = new PrintWriter(fileName);
                writer.println(size);
                for (int i = 0; i < size; i++) {
                    for (int j = 0; j < size; j++) {
                        writer.print(board.getBoardCell(j, i) + " ");
                    }
                    writer.println();
                }
                writer.close();
                System.out.println("Maze saved successfully.");
            } catch (FileNotFoundException e) {
                System.out.println("Cannot save the maze to " + fileName);
            }
        } else {
            System.out.println("No maze is currently loaded.");
        }
    }

    public void displayMaze() {
        if (mazeLoaded) {
            board.displayMaze();
        } else {
            System.out.println("No maze is currently loaded.");
        }
    }


    public void findEscape() {
        if (!mazeLoaded) {
            System.out.println("No maze is currently loaded.");
            return;
        }

        if (escapeFound) {
            System.out.println("Escape path already found.");
            return;
        }

        int startX = MazeBoard.startCoordinates[0];
        int startY = MazeBoard.startCoordinates[1];
        int exitX = MazeBoard.exitCoordinates[0];
        int exitY = MazeBoard.exitCoordinates[1];

        Stack<int[]> stack = new Stack<>();
        stack.push(new int[]{startX, startY});
        int[][] prevX = new int[board.size][board.size];
        int[][] prevY = new int[board.size][board.size];

        boolean[][] visited = new boolean[board.size][board.size];

        while (!stack.isEmpty()) {
            int[] current = stack.pop();
            int x = current[0];
            int y = current[1];

            if (visited[y][x]) {
                continue;
            }

            visited[y][x] = true;

            if (x == exitX && y == exitY) {
                escapeFound = true;
                break;
            }

            List<int[]> neighbors = board.getPathNeighbors(x, y);

            for (int[] neighbor : neighbors) {
                int nx = neighbor[0];
                int ny = neighbor[1];
                if (board.getBoardCell(nx, ny) == MazeBoard.PATH && !visited[ny][nx]) {
                    stack.push(new int[]{nx, ny});
                    prevX[ny][nx] = x;
                    prevY[ny][nx] = y;
                }
            }
        }

        if (escapeFound) {
            // Mark the escape path
            int currentX = exitX;
            int currentY = exitY;

            while (currentX != startX || currentY != startY) {
                int prevCellX = prevX[currentY][currentX];
                int prevCellY = prevY[currentY][currentX];
                board.setBoardCell(prevCellX, prevCellY, MazeBoard.ESCAPE_CELL);
                currentX = prevCellX;
                currentY = prevCellY;
            }
            board.setBoardCell(exitX, exitY, MazeBoard.ESCAPE_CELL);
            displayMaze();
        } else {
            System.out.println("Escape path not found.");
        }
    }


    public void play() {
        Scanner scanner = new Scanner(System.in);
        int option;

        do {
            System.out.println("=== Menu ===");
            System.out.println("1. Generate a new maze");
            System.out.println("2. Load a maze");
            if (mazeLoaded) {
                System.out.println("3. Save the maze");
                System.out.println("4. Display the maze");
                System.out.println("5. Find the escape");
            }
            System.out.println("0. Exit");
            option = scanner.nextInt();
            scanner.nextLine();

            switch (option) {
                case 1:
                    generateMaze();
                    displayMaze();
                    break;
                case 2:
                    System.out.println("Enter the file name:");
                    String fileName = scanner.nextLine();
                    loadMaze(fileName);
                    break;
                case 3:
                    System.out.println("Enter the file name:");
                    fileName = scanner.nextLine();
                    saveMaze(fileName);
                    break;
                case 4:
                    displayMaze();
                    break;
                case 5:
                    findEscape();
                    break;
                case 0:
                    System.out.println("Bye!");
                    break;
                default:
                    System.out.println("Incorrect option. Please try again.");
            }
            System.out.println();
        } while (option != 0);
    }
}
