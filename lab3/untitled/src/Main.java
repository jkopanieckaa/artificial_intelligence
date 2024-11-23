import sac.game.GameSearchAlgorithm;
import sac.game.GameState;
import sac.game.GameStateImpl;
import sac.game.MinMax;

import java.util.*;
import java.util.Scanner;


public class Main {
    public static void main(String[] args) {
        Mlynek gra = new Mlynek();  // Initialize the game state
        GameSearchAlgorithm alg = new MinMax(gra);  // Use the MinMax algorithm for AI decisions
        Scanner scanner = new Scanner(System.in);

        while (!gra.isWinTerminal() && !gra.isNonWinTerminal()) {
            // Player's move
            System.out.println("Twój ruch: ");
            System.out.println(gra);
            gra.generateChildren();  // Generate all possible player moves

            // Read player's move
            System.out.println("Which position do you choose? (row column): ");
            int row = scanner.nextInt();
            int col = scanner.nextInt();

            // Validate and apply player's move
            while (row < 0 || row >= gra.rows || col < 0 || col >= gra.columns || gra.state[row][col] != 0) {
                System.out.println("Wrong position, try again: ");
                row = scanner.nextInt();
                col = scanner.nextInt();
            }
            gra.state[row][col] = gra.maximizingTurnNow ? gra.white : gra.black;
            gra.count--;

            // Check if the game ended after the player's move
            if (gra.isWinTerminal() || gra.isNonWinTerminal()) {
                break;
            }

            // Computer's move
            System.out.println("Ruch komputera...");
            gra.generateChildren();  // Generate states after the computer's move
            alg.setInitial(gra);  // Set the current state in the MinMax algorithm
            alg.execute();  // Run the MinMax algorithm
            // Get the best move for AI
            String ruch = alg.getFirstBestMove();  // Get the best move for AI

// Ensure the move string is in the correct format before attempting to execute it
            if (ruch == null || ruch.split(" ").length != 4) {
                System.out.println("Invalid move format. Trying again...");
                continue;  // Skip to the next loop iteration if the move is invalid
            }

// Execute the AI move
            gra.executemove(ruch);  // Execute the computer's move
            System.out.println("Komputer wybrał ruch: " + ruch);
            // Execute the computer's move


            // Check if the game ended after the computer's move
            if (gra.isWinTerminal() || gra.isNonWinTerminal()) {
                break;
            }
        }

        // End of the game
        if (gra.isWinTerminal()) {
            System.out.println("Wygrałeś!");
        } else if (gra.isNonWinTerminal()) {
            System.out.println("Gra zakończona remisem.");
        }
    }

}






    class Mlynek extends GameStateImpl {
        byte[][] state;
        int rows = 3;
        int columns = 8;
        byte white = 1;
        byte black = 2;
        int count = 18; //I faza
        int whitecount = 9; // zlicznie pozostałych i usuwanie przy mlynkach
        int blackcount = 9;
        boolean maximizingTurnNow;
        private byte player;
        private Scanner scanner = new Scanner(System.in); // Instance variable


        public Mlynek() {
            state = new byte[rows][columns];
            maximizingTurnNow = true; //białe zaczynaja? CZY KOCHANY TO TU  MA BYC????? CZY W KONSTRRUKTORZE BEDZIE DOBRZE? I CZY MA BYC TEZ W KOPIUJACYM?
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    state[i][j] = 0;
                }
            }
        }

        public Mlynek(Mlynek m) {
            super();
            this.state = new byte[rows][columns];

            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    this.state[i][j] = m.state[i][j];
                }
            }
        }

        public String toString() { // co jest z tym
            StringBuilder s = new StringBuilder();
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    s.append(state[i][j] == 0 ? "." : (state[i][j] == white ? "W" : "B"));
                    if (j < columns - 1) s.append(" "); // Separator pól
                }
                s.append('\n');
            }
            return s.toString();
        }


        public int hashCode() {
            return toString().hashCode();
        }

        public boolean isMlynek(int square, int index) {
            byte board = state[square][index];

            if (board == 0) {
                return false;
            }

            // Check horizontal mills
            if (index % 2 == 0) {
                int next = (index + 1) % columns;
                int next2 = (index + 2) % columns;
                int prev = (index - 1 + columns) % columns;
                int prev2 = (index - 2 + columns) % columns;

                if ((state[square][next] == board && state[square][next2] == board) ||
                        (state[square][prev] == board && state[square][prev2] == board)) {
                    return true;
                }
            }

            // Check vertical mills
            if (index % 2 == 1) {
                int prevSquare = (square - 1 + rows) % rows;
                int nextSquare = (square + 1) % rows;
                int next = (index + 1) % columns;
                int prev = (index - 1 + columns) % columns;

                if ((state[prevSquare][index] == board && state[nextSquare][index] == board) ||
                        (state[square][next] == board && state[square][prev] == board)) {
                    return true;
                }
            }

            return false;
        }


        public List<GameState> MlynekSolution(boolean maximizingTurnNow) {
            byte opponent;
            boolean check;
            List<int[]> removablepieces = new ArrayList<>();
            List<int[]> mlynekpieces = new ArrayList<>();

            if (maximizingTurnNow) {
                opponent = black;
            } else {
                opponent = white;
            }

            // Loop through the board and check for mills or removable pieces
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    if (state[i][j] == opponent) {
                        check = isMlynek(i, j); // Check if a mill is formed
                        if (check) {
                            mlynekpieces.add(new int[]{i, j});
                        } else {
                            removablepieces.add(new int[]{i, j}); // Add to removable if no mill is formed
                        }
                    }
                }
            }

            List<int[]> availablePieces;
            if (removablepieces.isEmpty()) {
                System.out.println("U can delete one of these (mills):");
                availablePieces = mlynekpieces; // If no removable pieces, use mill pieces
            } else {
                System.out.println("U can delete one of these (no mill):");
                availablePieces = removablepieces; // If there are removable pieces, show those
            }

            // Output the available pieces for deletion
            for (int i = 0; i < availablePieces.size(); i++) {
                int[] piece = availablePieces.get(i);
                System.out.println(i + ": [" + piece[0] + ", " + piece[1] + "]");
            }

            // Ask player to choose which piece to delete
            Scanner scanner = new Scanner(System.in);
            System.out.println("Choose which one u want to delete (int):");
            int choice = scanner.nextInt();

            // Ensure the choice is valid
            while (choice < 0 || choice >= availablePieces.size()) {
                System.out.println("Wrong choice, try again: ");
                choice = scanner.nextInt();
            }

            // Delete the selected piece
            int[] selectedPiece = availablePieces.get(choice);
            System.out.println("Deleting: [" + selectedPiece[0] + ", " + selectedPiece[1] + "]");
            state[selectedPiece[0]][selectedPiece[1]] = 0;

            // Update the count based on the player whose piece was deleted
            if (maximizingTurnNow) {
                blackcount--;
            } else {
                whitecount--;
            }

            isSolution(); // Check the solution after deleting a piece
            return Arrays.asList(this);
        }



        public boolean isSolution() {
            // jezeli jeden z gracz ma zero pionkow to koniec the end
            // jezeli brak ruchow ma jeden z graczy to tez kaputo
            int playerpiecescount;
            boolean canjump = false;
            List<int[]> stillcanmove = new ArrayList<>();


            if (whitecount == 0 || blackcount == 0) {
                System.out.println("The end, game over");
                return true;
            }

            //przejsc przez wszytskie pionki gracza
            //sprawdzic czy mozna sie ruszyc w prawo lewo gora dol (jezeli wiecej niz 3)
            //jezeli nie to przegral

            if (maximizingTurnNow) {
                player = white;
                playerpiecescount = whitecount;
            } else {
                player = black;
                playerpiecescount = blackcount;
            }

            if (playerpiecescount <= 3) {
                canjump = true;
            }

            //wsumie moge zrobic tak ze jak nie mozna wykonac secondandthirdphase to koniec
            //ale to chyba


            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    if (state[i][j] == player && !canjump) {
                        if (j + 1 < columns && state[i][j + 1] != 0 && j - 1 >= 0 && state[i][j - 1] != 0 && i + 1 < rows && state[i + 1][j] != 0 && i - 1 >= 0 && state[i - 1][j] != 0) {
                            stillcanmove.add(new int[]{i, j});
                        }
                    }
                }
            }

            if (stillcanmove.isEmpty() && count == 0) {
                System.out.println("The end, game over, kaput");
                return true;
            }

            return false;
        }

        // @Override
        public List<GameState> generateChildren() {
            List<GameState> children = new ArrayList<>();

            if (count > 0) {
                // Phase 1: Adding pieces
                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < columns; j++) {
                        if (state[i][j] == 0) {
                            Mlynek child = new Mlynek(this);
                            child.state[i][j] = maximizingTurnNow ? white : black;
                            child.count--;
                            child.maximizingTurnNow = !maximizingTurnNow;
                            children.add(child);
                        }
                    }
                }
            } else {
                // Phase 2 and 3: Moving pieces
                byte player = maximizingTurnNow ? white : black;
                boolean canJump = (maximizingTurnNow && whitecount <= 3) || (!maximizingTurnNow && blackcount <= 3);

                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < columns; j++) {
                        if (state[i][j] == player) {
                            if (canJump) {
                                // Jumping move
                                for (int ni = 0; ni < rows; ni++) {
                                    for (int nj = 0; nj < columns; nj++) {
                                        if (state[ni][nj] == 0) {
                                            Mlynek child = new Mlynek(this);
                                            child.state[i][j] = 0;
                                            child.state[ni][nj] = player;
                                            child.maximizingTurnNow = !maximizingTurnNow;
                                            children.add(child);
                                        }
                                    }
                                }
                            } else {
                                // Normal move
                                int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
                                for (int[] dir : directions) {
                                    int ni = i + dir[0];
                                    int nj = j + dir[1];
                                    if (ni >= 0 && ni < rows && nj >= 0 && nj < columns && state[ni][nj] == 0) {
                                        Mlynek child = new Mlynek(this);
                                        child.state[i][j] = 0;
                                        child.state[ni][nj] = player;
                                        child.maximizingTurnNow = !maximizingTurnNow;
                                        children.add(child);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            return children;
        }


        public void executemove(String move) {
            // Parse the move string to get the source and destination positions
            String[] parts = move.split(" ");

            // Check if it's a phase 1 move (placing a piece), or phase 2/3 (moving a piece)
            if (parts.length == 2) { // Phase 1 (place piece)
                int row = Integer.parseInt(parts[0]);
                int col = Integer.parseInt(parts[1]);
                byte player = maximizingTurnNow ? white : black;
                state[row][col] = player;  // Place the piece
            } else if (parts.length == 4) { // Phase 2/3 (move piece)
                int srcRow = Integer.parseInt(parts[0]);
                int srcCol = Integer.parseInt(parts[1]);
                int destRow = Integer.parseInt(parts[2]);
                int destCol = Integer.parseInt(parts[3]);

                // Move the piece on the board
                byte player = state[srcRow][srcCol];
                state[srcRow][srcCol] = 0;
                state[destRow][destCol] = player;
            } else {
                System.out.println("Invalid move format.");
            }
        }


    }
