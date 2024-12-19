import sac.State;
import sac.StateFunction;
import sac.graph.AStar;
import sac.graph.GraphSearchAlgorithm;
import sac.graph.GraphState;
import sac.graph.GraphStateImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

        PuzzleState state= new PuzzleState(3);
        System.out.println(state.toString());
        System.out.println("isSolution: " + state.isSolution());
//        expand(2,state);

        GraphSearchAlgorithm gs = new AStar(state);
        PuzzleState p = new PuzzleState(3);
        double czas_misplaced = 0 ;
        int zopen_misplaced=0;
        int zclosed_misplaced=0;
        int dlugosc_misplaced = 0;

        double czas_manhattan =0;
        int zopen_manhattan=0;
        int zclosed_manhattan=0;
        int dlugosc_manhattan=0;


        for(int i=0; i<100; i++){
            gs.setInitial(p.mix(1000));

            PuzzleState.setHFunction(new HFunctionMisplaced());
            gs.execute();
            czas_misplaced += gs.getDurationTime();
            zopen_misplaced += gs.getOpenSet().size();
            zclosed_misplaced +=gs.getClosedStatesCount();
            dlugosc_misplaced += gs.getSolutions().get(0).getPath().size();

            PuzzleState.setHFunction(new HFunctionManhattan());
            gs.execute();

            czas_manhattan += gs.getDurationTime();
            zopen_manhattan += gs.getOpenSet().size();
            zclosed_manhattan += gs.getClosedStatesCount();
            dlugosc_manhattan += gs.getSolutions().get(0).getPath().size();

        }

        System.out.println("sredni czas misplaced " + czas_misplaced/100);
        System.out.println("sredni zopen misplaced " + zopen_misplaced/100);
        System.out.println("sredni zclosed misplaced " + zclosed_misplaced/100);
        System.out.println("srednia dllugosc sciezki misplaced "+ dlugosc_misplaced/100 + '\n');

        System.out.println("sredni czas manhattan " + czas_manhattan/100);
        System.out.println("sredni zopen manhattann "+ zopen_manhattan/100);
        System.out.println("sredni zclosed manhattan " + zclosed_manhattan/100);
        System.out.println("srednia dllugosc sciezki manhattan "+ dlugosc_manhattan/100);


    }

    public static void expand(int d, GraphState s){
        if(d<=0)
            System.out.println(s);
        else
            for(GraphState t:s.generateChildren())
                expand(d-1,t);

    }


}



class PuzzleState extends GraphStateImpl {
    private byte[][] state;
    private int n;
    private int columns, rows;
    private int empty;

    //konstruktor
    public PuzzleState(int n) {
        state = new byte[n][n];
        this.n = n;
        empty = state[0][0];

        for (int i = 0; i < state.length; i++) {
            for (int j = 0; j < state.length; j++) {
                state[i][j] = (byte) (i * n + j);
            }
        }
    }

    //konstruktor kopiujacy
    public PuzzleState(PuzzleState statecopy) {
        super();
        this.state= new byte[statecopy.n][statecopy.n];
        this.n = statecopy.n;
        this.columns = statecopy.columns;
        this.rows = statecopy.rows;
        this.empty = statecopy.empty;

        for (int i = 0; i < statecopy.state.length; i++) {
            for (int j = 0; j < statecopy.state.length; j++) {
                this.state[i][j] = statecopy.state[i][j];
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < state.length; i++) {
            for (int j = 0; j < state.length; j++) {
                s.append(state[i][j]);
            }
            s.append('\n');
        }
        return s.toString();
    }

    public boolean isSolution() {
        for(int i = 0; i < n; i++) {
            for(int j = 0; j < n; j++) {
                if(state[i][j] != i*n + j) { //jak nie na swoim miejscu to karamba
                    return false;
                }
            }
        }
        return true;
    }

    public List<GraphState> generateChildren(){
        List<GraphState> children = new ArrayList<>();

        if(rows != 0){
            PuzzleState down = new PuzzleState(this);
            down.state[rows][columns] = down.state[rows-1][columns];
            down.state[rows-1][columns] = 0;
            down.rows=rows-1;
            down.setMoveName("down");
            children.add(down);
        }

        if(rows != n-1){
            PuzzleState up = new PuzzleState(this);
            up.state[rows][columns] = up.state[rows+1][columns];
            up.state[rows+1][columns] = 0;
            up.rows=rows+1;
            up.setMoveName("up");
            children.add(up);
        }

        if(columns != 0){
            PuzzleState left = new PuzzleState(this);
            left.state[rows][columns] = left.state[rows][columns-1];
            left.state[rows][columns-1] = 0;
            left.columns=columns-1;
            left.setMoveName("left");
            children.add(left);
        }

        if(columns != n-1){
            PuzzleState right = new PuzzleState(this);
            right.state[rows][columns] = right.state[rows][columns+1];
            right.state[rows][columns+1] = 0;
            right.columns=columns+1;
            right.setMoveName("right");
            children.add(right);
        }

        return children;
    }

    public int hashCode(){
        return toString().hashCode();
    }

    public int misplaced_tiles() {
        int misplaced = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if(state[i][j] != 0 && state[i][j] != i*n + j) {
                    misplaced++;
                }
            }
        }
        return misplaced;
    }

    public int manhattan() {
        int manhattan = 0;

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if(state[i][j] != 0 && state[i][j] != i*n + j) {
                manhattan += Math.abs(i - state[i][j]/n) + Math.abs(j - state[i][j]% n);
                }
            }
        }
        return manhattan;
    }


    public GraphState mix(int n){
        GraphState s = this;
        Random r = new Random();
        for (int i = 0; i < n; i++) {
                List<GraphState> c = s.generateChildren();
                s = c.get(r.nextInt(c.size()));
        }
        return s;
    }


}

class HFunctionMisplaced extends StateFunction {
    public double calculate(State s){
        if (s instanceof PuzzleState) {
            PuzzleState ss = (PuzzleState) s;
            return ss.misplaced_tiles();
        }
        return Double.NaN;
    }
}

class HFunctionManhattan extends StateFunction {
    public double calculate(State s){
        if (s instanceof PuzzleState) {
            PuzzleState ss = (PuzzleState) s;
            return ss.manhattan();
        }
        return Double.NaN;
    }
}

