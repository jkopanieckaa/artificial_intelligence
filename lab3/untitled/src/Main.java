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
       //uzywamy algorytmu alphabeta


    }
}



class Mlynek extends GameStateImpl {
    byte[][] state;
    int rows = 3;
    int columns = 8;
    byte white = 1;
    byte black = 2;
    int countwhite_to_place = 9;
    int countblack_to_place = 9;//I faza
    int whitecount = 0; // zlicznie pionkow
    int blackcount = 0;
    boolean maximizingTurnNow;
    private byte player;




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
        //square - numer kwadratu i index to ideks w kwadracie
        byte board = state[square][index];
        if (board == 0) {
            return false;
        }

        //parzyste
        //prawo, prawo 2
        //lewo, lewo 2

        if (index % 2 == 0) {
            int next = (index + 1) % columns;
            int next2 = (index + 2) % columns;
            int prev = (index - 1 + columns) % columns;
            int prev2 = (index - 2 + columns) % columns;


            if ((state[square][next] == board && state[square][next2] == board) || (state[square][prev] == board && state[square][prev2] == board)) {
                return true;
            }
        }

        //nieparzyste - sasiedzi
        //prawo i lewo
        //gora i dol

        //poprzedmi kwadrat
        //nastepny kwadrat

//CZEMU TAK ZAPISANE
        if (index % 2 == 1) {
            int prevsquare = (square - 1 + rows) % rows;
            int nextsquare = (square + 1) % rows;
            int next = (index + 1) % columns;
            int prev = (index - 1 + columns) % columns;


            //miedzy kwadratami
            if ((state[prevsquare][index] == board && state[nextsquare][index] == board) || (state[square][next] == board && state[square][prev] == board)) {
                return true;
            }
        }
        return false;
    }


    public List<Mlynek> MlynekSolution(boolean maximizingTurnNow) {
        List<Mlynek> children = new ArrayList<>();
        byte opponent;
        boolean pieceRemoved = false;

        if(maximizingTurnNow) {
            player = white;
            opponent = black;
        }else{
            player = black;
            opponent = white;
        }


        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                if (state[row][col] == opponent && !isMlynek(row, col)) {
                    Mlynek child = new Mlynek(this);
                    child.state[row][col] = 0;  //czemu usuwamy pionek przeciwnika???
                    pieceRemoved = true;

                    // Aktualizacja liczby pionków przeciwnika
                    if (maximizingTurnNow) {
                        child.blackcount--;
                    } else {
                        child.whitecount--;
                    }

                    child.maximizingTurnNow = !maximizingTurnNow;
                    children.add(child);
                }
            }
        }


        if (!pieceRemoved) {
            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < columns; col++) {
                    if (state[row][col] == opponent) {
                        Mlynek child = new Mlynek(this); // Tworzenie kopii stanu
                        child.state[row][col] = 0; // Usunięcie pionka

                        // Aktualizacja liczby pionków przeciwnika
                        if (maximizingTurnNow) {
                            child.blackcount--;
                        } else {
                            child.whitecount--;
                        }

                        child.maximizingTurnNow = !maximizingTurnNow; // Zmiana tury
                        children.add(child); // Dodanie nowego stanu do listy dzieci
                    }
                }
            }
        }

        return children; // Zwrócenie wygenerowanych dzieci
    }


    public void placingpieces(int row, int col) { //zmien nazwe potem
        Scanner scanner = new Scanner(System.in);
        if(maximizingTurnNow){
            player = white;
        }else{
            player = black;
        }

        if (state[row][col] != 0) {
            System.out.println("Invalid move. Try again:");
            System.out.println("Which position do you choose? (row column): ");
            row = scanner.nextInt();
            col = scanner.nextInt();

        }

        state[row][col] = player;

        if (maximizingTurnNow) {
            countwhite_to_place--;
            whitecount++;
        } else {
            countblack_to_place--;
            blackcount++;
        }

    }

    public void movingpieces (int row, int col, int newrow, int newcol){
        Scanner scanner = new Scanner(System.in);

        if (maximizingTurnNow) {
            player = white;
        } else {
            player = black;
        }


        while(state[newrow][newcol] != 0){
            System.out.println("Invalid move - position taken:");
            System.out.println("Which position do you choose? (row column): ");
            newrow = scanner.nextInt();
            newcol = scanner.nextInt();

            if (newrow < 0 || newrow >= rows || newcol < 0 || newcol >= columns || state[newrow][newcol] != 0 || Math.abs(col - newcol) != 1 || Math.abs(row - newrow) != 1) {
                System.out.println("Invalid move - cannot move there:");
            }
        }

        state[row][col] = 0;
        state[newrow][newcol] = player;

        System.out.println("Moved piece to: [" + newrow + ", " + newcol + "]");

        }
    }


    public List<GameState> firstphase() {

        //pierwsza faza zaczyna sie kiedy count ==18 a konczy kiedy count jest 0

        Scanner scanner = new Scanner(System.in);
        boolean canjump = false;

        if(whitecount == 3 || blackcount == 3){
            canjump = true;
        }

        while (count != 0) {

            if (maximizingTurnNow) {
                player = white;
            } else {
                player = black;
            }

            System.out.println("Current state:");
            System.out.println(this);


            System.out.println("Which position do you choose? (row column): ");
            int row = scanner.nextInt();
            int col = scanner.nextInt();

            if (row < 0 || row > rows || col < 0 || col > columns || state[row][col] != 0) {
                System.out.println("Wrong position");
                continue;
            }

            state[row][col] = player;
            if (maximizingTurnNow) {
                count--;
            } else {
                count--;
            }

            if (isMlynek(row, col)) {
                System.out.println("Mlynek found");
                MlynekSolution(maximizingTurnNow);
            }

            isSolution();
            maximizingTurnNow = !maximizingTurnNow;

        }
        return Arrays.asList(this);
    }

    public List<GameState> secondandthirdphase() {
        //mamy plansze wypelniona po fazie pierwszej
        //na zmine gracze przesuwaja pionki o jedno pole
        // po kazdym ruchu sprawdzamy czy powstal mlynek
        boolean canjump = false;
        int playerpiecescount;
        Scanner scanner = new Scanner(System.in);

        while(!isSolution()) {
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

            System.out.println("Current state:");
            System.out.println(this);

            System.out.println("Position to move:");
            int row = scanner.nextInt();
            int col = scanner.nextInt();

            while (row < 0 || row >= rows || col < 0 || col >= columns || state[row][col] != player) {
                System.out.println("Wrong choice, try again");
                row = scanner.nextInt();
                col = scanner.nextInt();
            }

            System.out.println("Position to change: ");
            int newrow = scanner.nextInt();
            int newcol = scanner.nextInt();

            if (canjump) {
                while (newrow < 0 || newrow >= rows || newcol < 0 || newcol >= columns || state[newrow][newcol] != 0) {
                    System.out.println("Invalid move. Try again:");
                    newrow = scanner.nextInt();
                    newcol = scanner.nextInt();
                }
            } else {
                while (newrow < 0 || newrow >= rows || newcol < 0 || newcol >= columns || state[newrow][newcol] != 0 ||
                        Math.abs(col - newcol) != 1 || Math.abs(row - newrow) != 1) {
                    System.out.println("Invalid move. Try again:");
                    newrow = scanner.nextInt();
                    newcol = scanner.nextInt();
                }
            }


            state[row][col] = 0;
            state[newrow][newcol] = player;

            System.out.println("Moved piece to: [" + newrow + ", " + newcol + "]");

            if (isMlynek(newrow, newcol)) {
                System.out.println("Mlynek created");
                MlynekSolution(maximizingTurnNow);
            }

            isSolution();
            maximizingTurnNow = !maximizingTurnNow;

            System.out.println("Current state:");
            System.out.println(this);
        }

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

        if(stillcanmove.isEmpty() && count==0){
            System.out.println("The end, game over, kaput");
            return true;
        }

        return false;
    }

    // @Override
    public List<GameState> generateChildren() {

        if (count > 0) {
            return firstphase();
        } else {
            return secondandthirdphase();
        }
    }
}