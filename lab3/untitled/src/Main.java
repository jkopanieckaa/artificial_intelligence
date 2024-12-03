import sac.State;
import sac.StateFunction;
import sac.game.GameSearchAlgorithm;
import sac.game.GameState;
import sac.game.GameStateImpl;
import sac.game.MinMax;
import sac.graph.AStar;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        GameState state = new Mlynek();
        int depth = 6;
        expand(state, depth);
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

    //prosze sprawdz czy tak moze byc wsensie czy tu dwa razy nie kopiuje planszy?
    public Mlynek(byte[][] state) {
        super();
        this.state = state;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                this.state[i][j] = state[i][j];
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        //1
        sb.append(state[0][6]).append("***************").append(state[0][5]).append("***************").append(state[0][4]).append("\n");
        sb.append("|               |               |\n");

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

    //to tak sie to robilo?
    @Override
    public int hashCode() {
        return super.hashCode();
    }

    private boolean isMlynek(int row, int col) {
        //dla danej pozycji sprawdzamy czy jest mlynkiem
        //dwa rodzaje mlynka : poziomy i pionowy
        //poziomy
        //jezlei jest nieparzysty kolumna to sprawdzamy czy ma sasiadow (po lewej i prawej) i czy sa takie same kolory
        //jezlei jest parzysta to sprawdzamy sasiadow (dwoch po prawej lub dwoch po lewej) - np dla 0,0 to 0,1 i 0,2 a dla 0,6 to 0,5 i 0,4

        //pionowy
        //jezlei jest nieparzysty wiersz to sprawdzamy czy ma dwoch sasiadow na gorze lub na dile i czy sa takie same kolory
        //jezeli jest parzysty to sprawdzamy czy ma dwoch sasiadow na gorze lub na dole np. dla 0,0 to 0,7 i 0,6 a dla 0,2 o 0,3 i 0,4

        //jezeli ktorys z tych scenariuszy sie zgdza to zwracamy true

        if (col % 2 == 0) {
            if (col == 0) {
                if (state[row][col] == state[row][col + 1] && state[row][col] == state[row][col + 2]) {
                    return true;
                }

                if (state[row][col] == state[row][columns + 7] && state[row][col] == state[row][columns + 6]) {
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

                if (state[row][col] == state[row][col + 1] && state[row][col] == state[row][col - 6]) {
                    return true;
                }
            }
        } else {
            if (row == 0) {
                if (state[row][col] == state[row + 1][col] && state[row][col] == state[row + 2][col]) {
                    return true;
                }
            } else if (row == 1) {
                if (state[row][col] == state[row - 1][col] && state[row][col] == state[row + 1][col]) {
                    return true;
                }
            } else if (row == 2) {
                if (state[row][col] == state[row - 1][col] && state[row][col] == state[row - 2][col]) {
                    return true;
                }
            } else if (col == 1 || col == 3 || col == 5 || col == 7) {
                if (state[row][col] == state[row][col - 1] && state[row][col] == state[row][col + 1]) {
                    return true;
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
            byte[][] newstate = new byte[rows][columns];
            for (int j = 0; j < rows; j++) {
                for (int k = 0; k < columns; k++) {
                    newstate[j][k] = state[j][k];
                }
            }
            newstate[i[0]][i[1]] = empty;
            deleted.add(new Mlynek(newstate));

            if (maximazingturnnow) {
                black_count--;
            } else {
                white_count--;
            }
        }

        return deleted;
    }

    List<GameState> firstphase() {
        //pierwsza faza gry
        //kazdy gracz zaczyna z 9 pionkami
        //faza konczy sie gdy kazdy z graczy ma 0 pionkow do ustawienia
        //zwracamy liste mozliwych stanow gry po ustawieniu pionka
        //sprawdzamy czy nie powstal mlynek wywolujac ismlynek

        List<GameState> firstphase = new ArrayList<>();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (state[i][j] == empty) {
                    byte[][] newstate = new byte[rows][columns];
                    for (int k = 0; k < rows; k++) {
                        for (int l = 0; l < columns; l++) {
                            newstate[k][l] = state[k][l];
                        }
                    }
                    if (maximazingturnnow) {
                        newstate[i][j] = white;
                        white_toplace--;
                        white_count++;
                    } else {
                        newstate[i][j] = black;
                        black_toplace--;
                        white_count++;
                    }

                    if (isMlynek(i, j)) {
                        firstphase.addAll(MlynekPerform(maximazingturnnow));
                    } else {
                        firstphase.add(new Mlynek(newstate));
                    }
                }
            }
        }
        return firstphase;

    }
//
//    List<GameState> firstphase() {
//        // Lista możliwych stanów gry
//        List<GameState> firstphase = new ArrayList<>();
//
//        for (int i = 0; i < rows; i++) {
//            for (int j = 0; j < columns; j++) {
//                if (state[i][j] == empty) {  // Sprawdzamy puste pole
//                    // Tworzymy nowy stan gry
//                    byte[][] newstate = new byte[rows][columns];
//                    for (int k = 0; k < rows; k++) {
//                        newstate[k] = Arrays.copyOf(state[k], columns);
//                    }
//
//                    // Ustawiamy nowy pionek
//                    newstate[i][j] = maximazingturnnow ? white : black;
//
//
//// Aktualizacja liczników
//                    if (maximazingturnnow) {
//                        white_toplace--; // Gracz W umieścił pionek
//                        white_count++;   // Zwiększamy licznik pionków na planszy dla W
//                    } else {
//                        black_toplace--; // Gracz B umieścił pionek
//                        black_count++;   // Zwiększamy licznik pionków na planszy dla B
//                    }
//
//                    // Sprawdzamy, czy powstał młynek
//                    if (isMlynek(i, j)) {
//                        firstphase.addAll(MlynekPerform(maximazingturnnow));
//                    } else {
//                        firstphase.add(new Mlynek(newstate));
//                    }
//                }
//            }
//        }
//
//        return firstphase;
//    }

    List<GameState> secondthirdphase() {
        //druga faza
        //gracze mogą przesuwać swoje pionki na sąsiednie wolne miejsca
        //parzyste maja dwie mozliwosci
        //nieparzyste maja trzy mozliwosci

        //trzecia faza - zaczyna sie gdy ktorys z graczy ma 3 pionki
        //gracz ktory ma 3 lub mniej pionkow moze skakac na dowolne wolne pole

        //zwracamy wszytskie mozliwe stany gry dla fazy dwa i trzy

        List<GameState> secondthirdphase = new ArrayList<>();
        byte player;

        if (maximazingturnnow) {
            player = white;
        } else {
            player = black;
        }

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (state[i][j] != empty && state[i][j] == player) {
                    if (j % 2 == 0) {
                        if (j == 0) {
                            if (state[i][j + 1] == empty) {
                                byte[][] newstate = new byte[rows][columns];
                                for (int k = 0; k < rows; k++) {
                                    for (int l = 0; l < columns; l++) {
                                        newstate[k][l] = state[k][l];
                                    }
                                }
                                newstate[i][j + 1] = newstate[i][j];
                                newstate[i][j] = empty;
                                secondthirdphase.add(new Mlynek(newstate));
                            }

                            if (state[i][j + 7] == empty) {
                                byte[][] newstate = new byte[rows][columns];
                                for (int k = 0; k < rows; k++) {
                                    for (int l = 0; l < columns; l++) {
                                        newstate[k][l] = state[k][l];
                                    }
                                }
                                newstate[i][j + 2] = newstate[i][j];
                                newstate[i][j] = empty;
                                secondthirdphase.add(new Mlynek(newstate));
                            }

                        } else if (j == 2 || j == 4 || j == 6) {
                            if (state[i][j + 1] == empty) {
                                byte[][] newstate = new byte[rows][columns];
                                for (int k = 0; k < rows; k++) {
                                    for (int l = 0; l < columns; l++) {
                                        newstate[k][l] = state[k][l];
                                    }
                                }
                                newstate[i][j + 1] = newstate[i][j];
                                newstate[i][j] = empty;
                                secondthirdphase.add(new Mlynek(newstate));
                            }

                            if (state[i][j - 1] == empty) {
                                byte[][] newstate = new byte[rows][columns];
                                for (int k = 0; k < rows; k++) {
                                    for (int l = 0; l < columns; l++) {
                                        newstate[k][l] = state[k][l];
                                    }
                                }
                                newstate[i][j - 1] = newstate[i][j];
                                newstate[i][j] = empty;
                                secondthirdphase.add(new Mlynek(newstate));
                            }
                        } else {
                            if (j == 1 || j == 3 || j == 5) {
                                if (state[i][j - 1] == empty) {
                                    byte[][] newstate = new byte[rows][columns];
                                    for (int k = 0; k < rows; k++) {
                                        for (int l = 0; l < columns; l++) {
                                            newstate[k][l] = state[k][l];
                                        }
                                    }
                                    newstate[i][j - 1] = newstate[i][j];
                                    newstate[i][j] = empty;
                                    secondthirdphase.add(new Mlynek(newstate));
                                }

                                if (state[i][j + 1] == empty) {
                                    byte[][] newstate = new byte[rows][columns];
                                    for (int k = 0; k < rows; k++) {
                                        for (int l = 0; l < columns; l++) {
                                            newstate[k][l] = state[k][l];
                                        }
                                    }
                                    newstate[i][j + 1] = newstate[i][j];
                                    newstate[i][j] = empty;
                                    secondthirdphase.add(new Mlynek(newstate));
                                }
                            } else if (j == 7) {
                                if (state[i][j - 1] == empty) {
                                    byte[][] newstate = new byte[rows][columns];
                                    for (int k = 0; k < rows; k++) {
                                        for (int l = 0; l < columns; l++) {
                                            newstate[k][l] = state[k][l];
                                        }
                                    }
                                    newstate[i][j - 1] = newstate[i][j];
                                    newstate[i][j] = empty;
                                    secondthirdphase.add(new Mlynek(newstate));
                                }

                                if (state[i][0] == empty) {
                                    byte[][] newstate = new byte[rows][columns];
                                    for (int k = 0; k < rows; k++) {
                                        for (int l = 0; l < columns; l++) {
                                            newstate[k][l] = state[k][l];
                                        }
                                    }
                                    newstate[i][0] = newstate[i][j];
                                    newstate[i][j] = empty;
                                    secondthirdphase.add(new Mlynek(newstate));
                                }
                            }

                            if (i == 0) {
                                if (state[i + 1][j] == empty) {
                                    byte[][] newstate = new byte[rows][columns];
                                    for (int k = 0; k < rows; k++) {
                                        for (int l = 0; l < columns; l++) {
                                            newstate[k][l] = state[k][l];
                                        }
                                    }
                                    newstate[i + 1][j] = newstate[i][j];
                                    newstate[i][j] = empty;
                                    secondthirdphase.add(new Mlynek(newstate));
                                }
                            }

                            if (i == 1) {
                                if (state[i + 1][j] == empty) {
                                    byte[][] newstate = new byte[rows][columns];
                                    for (int k = 0; k < rows; k++) {
                                        for (int l = 0; l < columns; l++) {
                                            newstate[k][l] = state[k][l];
                                        }
                                    }
                                    newstate[i + 1][j] = newstate[i][j];
                                    newstate[i][j] = empty;
                                    secondthirdphase.add(new Mlynek(newstate));
                                }

                                if (state[i - 1][j] == empty) {
                                    byte[][] newstate = new byte[rows][columns];
                                    for (int k = 0; k < rows; k++) {
                                        for (int l = 0; l < columns; l++) {
                                            newstate[k][l] = state[k][l];
                                        }
                                    }
                                    newstate[i - 1][j] = newstate[i][j];
                                    newstate[i][j] = empty;
                                    secondthirdphase.add(new Mlynek(newstate));
                                }
                            }

                            if (i == 2) {
                                if (state[i - 1][j] == empty) {
                                    byte[][] newstate = new byte[rows][columns];
                                    for (int k = 0; k < rows; k++) {
                                        for (int l = 0; l < columns; l++) {
                                            newstate[k][l] = state[k][l];
                                        }
                                    }
                                    newstate[i - 1][j] = newstate[i][j];
                                    newstate[i][j] = empty;
                                    secondthirdphase.add(new Mlynek(newstate));
                                }
                            }
                        }

                    }

                }
            }
        }

        //faza trzecia

        if(white_count <= 3 && player == white){
            for(int i = 0; i < rows; i++){
                for(int j = 0; j < columns; j++){
                    if(state[i][j] == player){
                        for(int k = 0; k < rows; k++){
                            for(int l = 0; l < columns; l++){
                                if(state[k][l] == empty){
                                    byte[][] newstate = new byte[rows][columns];
                                    for(int m = 0; m < rows; m++){
                                        for(int n = 0; n < columns; n++){
                                            newstate[m][n] = state[m][n];
                                        }
                                    }
                                    newstate[k][l] = newstate[i][j];
                                    newstate[i][j] = empty;
                                    secondthirdphase.add(new Mlynek(newstate));
                                }
                            }
                        }
                    }
                }
            }
        } else if (black_count <= 3 && player == black){
            for(int i = 0; i < rows; i++){
                for(int j = 0; j < columns; j++){
                    if(state[i][j] == player){
                        for(int k = 0; k < rows; k++){
                            for(int l = 0; l < columns; l++){
                                if(state[k][l] == empty){
                                    byte[][] newstate = new byte[rows][columns];
                                    for(int m = 0; m < rows; m++){
                                        for(int n = 0; n < columns; n++){
                                            newstate[m][n] = state[m][n];
                                        }
                                    }
                                    newstate[k][l] = newstate[i][j];
                                    newstate[i][j] = empty;
                                    secondthirdphase.add(new Mlynek(newstate));
                                }
                            }
                        }
                    }
                }
            }

        }
        return secondthirdphase;
    }

    @Override
    public List<GameState> generateChildren() {
        if (white_toplace > 0 || black_toplace > 0) {
            return firstphase();
        } else {
            return secondthirdphase();
        }
    }
}
