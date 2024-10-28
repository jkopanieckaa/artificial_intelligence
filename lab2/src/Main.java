import sac.graph.GraphState;
import sac.graph.GraphStateImpl;

import java.util.ArrayList;
import java.util.List;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

        PuzzleState state= new PuzzleState(3);
        System.out.println(state.toString());
        System.out.println("isSolution: " + state.isSolution());
        expand(2,state);
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
    public PuzzleState(PuzzleState state) {
        super();
        this.state= new byte[state.n][state.n];
        this.n = state.n;
        this.columns = state.columns;
        this.rows = state.rows;
        this.empty = state.empty;

        for (int i = 0; i < state.state.length; i++) {
            for (int j = 0; j < state.state.length; j++) {
                this.state[i][j] = state.state[i][j];
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
                if(state[i][j] != i*n + j) {
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
}


