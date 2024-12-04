import sac.game.GameState;
import sac.game.GameStateImpl;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        GameState gra = new Mlynek();

      //  testMlynek();
     //   testJumpingPhase();

       //  ((Mlynek) gra).state[0][1] = 1;
//
//        ((Mlynek) gra).state[0][1]=2;
//        ((Mlynek) gra).state[0][0]=1;
//        ((Mlynek) gra).state[1][0]=2;
//        ((Mlynek) gra).state[2][7]=1;
//        ((Mlynek) gra).state[2][3]=1;
//        ((Mlynek) gra).state[0][3]=1;
//        ((Mlynek) gra).state[2][5]=2;
//        ((Mlynek) gra).state[1][6]=1;
//        ((Mlynek) gra).state[1][5]=1;
//        ((Mlynek) gra).state[1][4]=1;
//        ((Mlynek) gra).state[0][5]=2;

        System.out.println(gra.toString());
        int depth = 6;
        expand(gra, depth);
    }

    public static void expand(GameState s, int d) {
        long[] v = new long[d];
        expand(s, v, 0);
        for (long val : v) {
            System.out.println(val);
        }
    }

    public static void expand(GameState s, long[] v, int d) {
        if (d >= v.length)
            return;
        for (GameState t : s.generateChildren()) {
            v[d]++;
            expand(t, v, d + 1);
        }
    }

    public static void testJumpingPhase() {
        Mlynek game = new Mlynek();

        // Set up a scenario where a player has 3 pieces left
        game.state[2][6] = 1; // White piece
        game.state[1][0] = 1; // White piece
        game.state[2][0] = 1; // White piece

        game.state[0][1] = 2; // Black piece
        game.state[1][1] = 2; // Black piece
        game.state[2][2] = 2; // Black piece

        game.white_count = 3;
        game.white_toplace = 0;

        // Print the initial state
        System.out.println("Initial State:");
        System.out.println(game.toString());

        // Execute jumping phase: jump from one piece to an empty spot
        List<int[]> jumps = game.jumpingstage();
        for (int[] jump : jumps) {
            System.out.println("Jumping to: " + Arrays.toString(jump));
            Mlynek newGameState = new Mlynek(game);
            newGameState.state[jump[0]][jump[1]] = 1; // White piece jumps
            newGameState.state[0][0] = 0; // Remove the original piece
            System.out.println("New State after Jump:");
            System.out.println(newGameState.toString());
        }
    }
    public static void testMlynek() {
        Mlynek game = new Mlynek();

        // Horizontal mill
        game.state[0][0] = 1;
        game.state[0][1] = 1;
        game.state[0][2] = 1;
        System.out.println("Horizontal Mill at (0, 1): " + game.isMlynek(0, 1)); // Expected: true

        // Vertical mill
        game.state[2][5] = 1;
        game.state[1][5] = 1;
        game.state[0][5] = 1;
        System.out.println("Vertical Mill at (0, 5): " + game.isMlynek(1, 1)); // Expected: true

        // No mill
        game.state[0][1] = 2; // Change piece to make no mill
        System.out.println("No mill at (1, 1): " + game.isMlynek(1, 1)); // Expected: false
        System.out.println("No mill at (0, 0): " + game.isMlynek(0, 0)); // Expected: false
        System.out.println("No mill at (1, 7): " + game.isMlynek(1, 7)); // Expected: false
    }

}

class Mlynek extends GameStateImpl {
    private static final byte empty = 0;
    private static final byte white = 1;
    private static final byte black = 2;

    byte[][] state;
    private final int rows = 3;
    private final int columns = 8;

    boolean maximazingturnnow;
    int white_toplace = 9;
    int black_toplace = 9;

    int white_count = 0;
    int black_count = 0;

    public Mlynek() {
        this.state = new byte[rows][columns];
        maximazingturnnow = true; //biale zaczynaja


        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                state[i][j] = empty;
            }
        }
    }

    public Mlynek(Mlynek other) {
        super();
        this.state = new byte[rows][columns];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                this.state[i][j] = other.state[i][j];
            }
        }

        this.maximazingturnnow = other.maximazingturnnow;
        this.white_toplace = other.white_toplace;
        this.black_toplace = other.black_toplace;
        this.white_count = other.white_count;
        this.black_count = other.black_count;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        //1
        sb.append(state[0][6]).append("***************").append(state[0][5]).append("***************").append(state[0][4]).append("\n");
        sb.append("*               *               *\n");

        //2
        sb.append("|     ").append(state[1][6]).append("*********").append(state[1][5]).append("*********").append(state[1][4]).append("     |\n");
        sb.append("|     |         |         |     |\n");

        //3
        sb.append("|     |     ").append(state[2][6]).append("***").append(state[2][5]).append("***").append(state[2][4]).append("     |     |\n");
        sb.append("|     |     |       |     |     |\n");

        //4
        sb.append(state[0][7]).append("*****").append(state[1][7]).append("*****").append(state[2][7]).append("       ")
                .append(state[2][3]).append("*****").append(state[1][3]).append("*****").append(state[0][3]).append("\n");
        sb.append("|     |     |       |     |     |\n");

        //5
        sb.append("|     |     ").append(state[2][0]).append("***").append(state[2][1]).append("***").append(state[2][2]).append("     |     |\n");
        sb.append("|     |         |         |     |\n");

        //6
        sb.append("|     ").append(state[1][0]).append("*********").append(state[1][1]).append("*********").append(state[1][2]).append("     |\n");
        sb.append("|               |               |\n");

        //7
        sb.append(state[0][0]).append("***************").append(state[0][1]).append("***************").append(state[0][2]).append("\n");

        return sb.toString();
    }

    public int hashCode() {
        return toString().hashCode();
    }

    boolean isMlynek(int row, int col) {
        //dla danej pozycji sprawdzamy czy jest mlynkiem
        //dwa rodzaje mlynka : poziomy i pionowy
        //poziomy
        //jezlei jest nieparzysty kolumna to sprawdzamy czy ma sasiadow (po lewej i prawej) i czy sa takie same kolory
        //jezlei jest parzysta to sprawdzamy sasiadow (dwoch po prawej lub dwoch po lewej) - np dla 0,0 to 0,1 i 0,2 a dla 0,6 to 0,5 i 0,4

        //pionowy
        //jezlei jest nieparzysty wiersz to sprawdzamy czy ma dwoch sasiadow na gorze lub na dile i czy sa takie same kolory
        //jezeli jest parzysty to sprawdzamy czy ma dwoch sasiadow na gorze lub na dole np. dla 0,0 to 0,7 i 0,6 a dla 0,2 o 0,3 i 0,4

        //jezeli ktorys z tych scenariuszy sie zgdza to zwracamy true

        if (row < 0 || row >= 3 || col < 0 || col >= 8) {
            return false;
        }

        if (col % 2 == 0 && state[row][col] != empty) {
            if (col == 0) {
                if (state[row][col] == state[row][1] && state[row][col] == state[row][2]) {
                    return true;
                }

                if (state[row][col] == state[row][7] && state[row][col] == state[row][6]) {
                    return true;
                }
            } else if (col == 2) {
                if (state[row][col] == state[row][col + 1] && state[row][col] == state[row][col + 2]) {
                    return true;
                }

                if (state[row][col] == state[row][col - 1] && state[row][col] == state[row][col - 2]) {
                    return true;
                }
            } else if (col == 4) {
                if (state[row][col] == state[row][col - 1] && state[row][col] == state[row][col - 2]) {
                    return true;
                }

                if (state[row][col] == state[row][col + 1] && state[row][col] == state[row][col + 2]) {
                    return true;
                }
            } else if (col == 6) {
                if (state[row][col] == state[row][col - 1] && state[row][col] == state[row][col - 2]) {
                    return true;
                }

                if (state[row][col] == state[row][col + 1] && state[row][col] == state[row][0]) {
                    return true;
                }
            }
        } else if(col % 2 == 1 && state[row][col] != empty) {

            if (row == 0) {
                if (col == 1 || col == 3 || col == 5) {
                    if (state[row][col] == state[row][col - 1] && state[row][col] == state[row][col + 1]) {
                        return true;
                    } else if (state[row][col] == state[1][col] && state[row][col] == state[2][col]) {
                        return true;
                    }
                } else if (col == 7) {
                    if (state[row][col] == state[row][0] && state[row][col] == state[row][6]) {
                        return true;
                    } else if (state[row][col] == state[1][col] && state[row][col] == state[2][col]) {
                        return true;
                    }
                }
            } else if (row == 1) {
                if (col == 1 || col == 3 || col == 5) {
                    if (state[row][col] == state[row][col - 1] && state[row][col] == state[row][col + 1]) {
                        return true;
                    } else if (state[row][col] == state[0][col] && state[row][col] == state[2][col]) {
                        return true;
                    }
                } else if (col == 7) {
                    if (state[row][col] == state[row][0] && state[row][col] == state[row][6]) {
                        return true;
                    } else if (state[row][col] == state[0][col] && state[row][col] == state[2][col]) {
                        return true;
                    }
                }
            } else if (row == 2) {
                if (col == 1 || col == 3 || col == 5) {
                    if (state[row][col] == state[row][col - 1] && state[row][col] == state[row][col + 1]) {
                        return true;
                    } else if (state[row][col] == state[0][col] && state[row][col] == state[1][col]) {
                        return true;
                    }
                } else if (col == 7) {
                    if (state[row][col] == state[row][0] && state[row][col] == state[row][6]) {
                        return true;
                    } else if (state[row][col] == state[0][col] && state[row][col] == state[1][col]) {
                        return true;
                    }
                }
            }

        }

        return false;
    }

    List<int[]> MlynekSolution(boolean maximazingturnnow) {
        //wywolywane jesli ktos zrobil mlynka
        //przeciwnik ktory utworzyl mlynek (czyli maximazingturnnow) ma mozliwosc usuniecia pionka przeciwnika
        //moze usunac kazdy pionek ktory NIE jest w mlynku
        //jezeli wzytskie znajduja sie w plynku to moze usunac dowolny
        //zwracamy liste mozliwych do usuniecia

        List<int[]> candelete = new ArrayList<>();
        byte opponent;

        if (maximazingturnnow) {
            opponent = black;
        } else {
            opponent = white;
        }


        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (state[i][j] != empty && state[i][j] == opponent && !isMlynek(i, j)) {
                    //DODAJEMY DO LISTY CANDELETE
                    candelete.add(new int[]{i, j});
                }
            }
        }

        if (candelete.isEmpty()) {
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    if (state[i][j] != empty && state[i][j] == opponent) {
                        //DODAJEMY DO LISTY CANDELETE
                        candelete.add(new int[]{i, j});
                    }
                }
            }
        }
        return candelete;
    }


    List<GameState> MlynekPerform(boolean maximazingturnnow) {
        //mamy liste z MlynekSolution
        //kto wygral ten ma mozliwosc usuniecia pionka przeciwnika TYLKO Z LISTY CANDELETE
        //zwracamy liste mozliwych stanow gry po usunieciu pionka przeciwnika? dobzre czaje baze?

        List<GameState> deleted = new ArrayList<>();
        List<int[]> candelete = MlynekSolution(maximazingturnnow);

        for (int[] i : candelete) {
            Mlynek newGameState = new Mlynek(this);
            newGameState.state[i[0]][i[1]] = empty;

            if (maximazingturnnow) {
                newGameState.black_count--;
            } else {
                newGameState.white_count--;
            }

            deleted.add(newGameState);
        }
        maximazingturnnow = !maximazingturnnow;
        return deleted;
    }


//    List<GameState> firstphase() {
//        //pierwsza faza gry
//        //kazdy gracz zaczyna z 9 pionkami
//        //faza konczy sie gdy kazdy z graczy ma 0 pionkow do ustawienia
//        //zwracamy liste mozliwych stanow gry po ustawieniu pionka
//        //sprawdzamy czy nie powstal mlynek wywolujac ismlynek
//
//        List<GameState> firstphase = new ArrayList<>();
//
//        for (int i = 0; i < rows; i++) {
//            for (int j = 0; j < columns; j++) {
//                if (state[i][j] == empty) {
//                    Mlynek newGameState = new Mlynek(this);
//
//                    if (newGameState.maximazingturnnow) {
//                        newGameState.state[i][j] = white;
//                        newGameState.white_toplace--;
//                        newGameState.white_count++;
//                    } else {
//                        newGameState.state[i][j] = black;
//                        newGameState.black_toplace--;
//                        newGameState.black_count++;
//                    }
//
//                    if (newGameState.isMlynek(i, j)) {
//                        List<GameState> mill_children = newGameState.MlynekPerform(newGameState.maximazingturnnow);
//                        firstphase.addAll(mill_children);
//                    } else {
//                        newGameState.maximazingturnnow = !newGameState.maximazingturnnow;
//                        firstphase.add(newGameState);
//                    }
//                }
//            }
//        }
//
//        return firstphase;
//    }
//
//
//    List<GameState> secondthirdphase() {
//        //druga faza
////        //gracze mogą przesuwać swoje pionki na sąsiednie wolne miejsca
////        //parzyste maja dwie mozliwosci
////        //nieparzyste maja trzy mozliwosci
////
////        //trzecia faza - zaczyna sie gdy ktorys z graczy ma 3 pionki
////        //gracz ktory ma 3 lub mniej pionkow moze skakac na dowolne wolne pole
////
////        //zwracamy wszytskie mozliwe stany gry dla fazy dwa i trzy
////
//        List<GameState> secondthirdphase = new ArrayList<>();
//
//        byte player;
//
//        if (maximazingturnnow) {
//            player = white;
//        } else {
//            player = black;
//        }
//
//        if ((white_toplace == 0 && black_toplace == 0) &&
//                ((maximazingturnnow && white_count > 3) || (!maximazingturnnow && black_count > 3))) {
//            for (int i = 0; i < rows; i++) {
//                for (int j = 0; j < columns; j++) {
//                    if (state[i][j] == player) {
//                        List<int[]> possibleMoves = possiblemoves();
//                        for (int[] move : possibleMoves) {
//                            Mlynek newGameState = new Mlynek(this);
//
//                            newGameState.state[move[0]][move[1]] = newGameState.state[i][j];
//                            newGameState.state[i][j] = empty;
//
//                            if (newGameState.isMlynek(move[0], move[1])) {
//                                List<GameState> mill_children = newGameState.MlynekPerform(newGameState.maximazingturnnow);
//                                secondthirdphase.addAll(mill_children);
//                            } else {
//                                secondthirdphase.add(newGameState);
//                            }
//                        }
//                    }
//                }
//            }
//        }
////trzecia faza
//        if ((maximazingturnnow && white_count <= 3 && white_toplace == 0 ) || (!maximazingturnnow && black_count <= 3 && black_toplace == 0)) {
//            for (int i = 0; i < rows; i++) {
//                for (int j = 0; j < columns; j++) {
//                    if (state[i][j] == player) {
//                        for (int k = 0; k < rows; k++) {
//                            for (int l = 0; l < columns; l++) {
//                                if (state[k][l] == empty) {
//                                    Mlynek newGameState = new Mlynek(this);
//
//                                    newGameState.state[k][l] = newGameState.state[i][j];
//                                    newGameState.state[i][j] = empty;
//
//                                    if (newGameState.isMlynek(k, l)) {
//                                        List<GameState> mill_children = newGameState.MlynekPerform(newGameState.maximazingturnnow);
//                                        secondthirdphase.addAll(mill_children);
//                                    } else {
//                                        // maximazingturnnow = !maximazingturnnow;
//                                        secondthirdphase.add(newGameState);
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//
//        return secondthirdphase;
//    }


    public List<int[]> getneighbors(int row, int col) {
        List<int[]> neighbors = new ArrayList<>();

        if (col % 2 == 0) {
            if (col == 0) {
                neighbors.add(new int[]{row, 1});
                neighbors.add(new int[]{row, 7});
            }

            if (col == 2 || col == 4 || col == 6) {
                neighbors.add(new int[]{row, col + 1});
                neighbors.add(new int[]{row, col - 1});
            }
        } else {
            if (row == 0) {
                neighbors.add(new int[]{1, col});
            }

            if (row == 1) {
                neighbors.add(new int[]{0, col});
                neighbors.add(new int[]{2, col});
            }

            if (row == 2) {
                neighbors.add(new int[]{1, col});
            }

            if (col == 1 || col == 3 || col == 5) {
                neighbors.add(new int[]{row, col - 1});
                neighbors.add(new int[]{row, col + 1});
            }

            if (col == 7) {
                neighbors.add(new int[]{row, 6});
                neighbors.add(new int[]{row, 0});
            }
        }

        return neighbors;
    }


    List<int[]> possiblemoves() {
        List<int[]> possiblemoves = new ArrayList<>();
        byte player;

        if (maximazingturnnow) {
            player = white;
        } else {
            player = black;
        }

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (state[i][j] == player) {
                    List<int[]> neighbors = getneighbors(i, j);
                    for (int[] neighbor : neighbors) {
                        if (state[neighbor[0]][neighbor[1]] == empty) {
                            possiblemoves.add(neighbor);
                        }
                    }

                }
            }
        }
        return possiblemoves;
    }

    List<int[]> jumpingstage(){
  //wszytskie wolne pola z planszy

        List<int[]> jumps = new ArrayList<>();

        for(int i = 0; i < rows; i++){
            for(int j = 0; j < columns; j++){
                if(state[i][j] == empty){
                    jumps.add(new int[]{i,j});
                }
            }
        }

        return jumps;
    }

    @Override
    public List<GameState> generateChildren() {
        List<GameState> children = new ArrayList<>();
        byte player;

        if (maximazingturnnow) {
            player = white;
        } else {
            player = black;
        }

//System.out.println("Maximazing turn now: " + maximazingturnnow);
//faza pierwsza
        if(white_toplace > 0 || black_toplace > 0){

        //    System.out.println("Placing phase");

            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    if (state[i][j] == empty) {
                        Mlynek newGameState = new Mlynek(this);

                        if (newGameState.maximazingturnnow) {
                            newGameState.state[i][j] = white;
                            newGameState.white_toplace--;
                            newGameState.white_count++;
                        } else {
                            newGameState.state[i][j] = black;
                            newGameState.black_toplace--;
                            newGameState.black_count++;
                        }

                        if (newGameState.isMlynek(i, j)) {
                            List<GameState> mill_children = newGameState.MlynekPerform(newGameState.maximazingturnnow);
                            children.addAll(mill_children);
                           // newGameState.maximazingturnnow = !newGameState.maximazingturnnow;
                        } else {
                            newGameState.maximazingturnnow = !newGameState.maximazingturnnow;
                            children.add(newGameState);
                        }
                    }
                }
            }
        }
        //faza druga
        else if ((white_toplace == 0 && black_toplace == 0) &&
                ((maximazingturnnow && white_count > 3) || (!maximazingturnnow && black_count > 3))) {

            System.out.println("Moving phase");

            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    if (state[i][j] == player) {
                        List<int[]> possibleMoves = possiblemoves();
                        for (int[] move : possibleMoves) {
                            Mlynek newGameState = new Mlynek(this);

                            newGameState.state[move[0]][move[1]] = newGameState.state[i][j];
                            newGameState.state[i][j] = empty;

                            if (newGameState.isMlynek(move[0], move[1])) {
                                List<GameState> mill_children = newGameState.MlynekPerform(newGameState.maximazingturnnow);
                                children.addAll(mill_children);
                            } else {
                                newGameState.maximazingturnnow = !newGameState.maximazingturnnow;
                                children.add(newGameState);
                            }
                        }
                    }
                }
            }

        }
        //faza trzecia
        else if ((maximazingturnnow && white_count <= 3 && white_toplace == 0 && black_toplace == 0) ||
                (!maximazingturnnow && black_count <= 3 && black_toplace == 0 && white_toplace == 0)) {


            System.out.println("Jumping phase");

            List<int[]> jumps = jumpingstage();  // Get all empty positions (jumping stage)

            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    if (state[i][j] == player) {  // Look for player's pieces
                        for (int[] jump : jumps) {
                            Mlynek newGameState = new Mlynek(this);  // Create a new game state

                            // Move the piece to the new position (jump)
                            newGameState.state[jump[0]][jump[1]] = newGameState.state[i][j];

                            // Set the original position to empty (ensure the original position is cleared)
                            newGameState.state[i][j] = empty;

                            // Print the new game state for debugging
                            System.out.println("Jumping to: " + Arrays.toString(jump));
                            System.out.println("New State after Jump:");
                            System.out.println(newGameState.toString());

                            // If a mill is formed after the move, perform the appropriate action
                            if (newGameState.isMlynek(jump[0], jump[1])) {
                                List<GameState> mill_children = newGameState.MlynekPerform(newGameState.maximazingturnnow);
                                children.addAll(mill_children);
                            } else {
                                // Otherwise, just add the new game state to the children list
                                newGameState.maximazingturnnow = !newGameState.maximazingturnnow;
                                children.add(newGameState);
                            }
                        }
                    }
                }
            }
        }


        return children;
    }

}

