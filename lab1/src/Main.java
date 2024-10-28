import sac.State;
import sac.StateFunction;
import sac.graph.BestFirstSearch;
import sac.graph.GraphSearchConfigurator;
import sac.graph.GraphState;
import sac.graph.GraphStateImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.


        SudokuState state = new SudokuState(3);
        state.fromString("........85...3..9..s.4.2....62..1.........4..9....5...69..1.523.4....3.....329.56.");

      //  System.out.println(state);
      //  System.out.println(state.isValid());
      //  System.out.println(java.util.Arrays.toString((state.generateChildren()).toArray()));
        SudokuState.setHFunction(new HFunction());

        BestFirstSearch bfs = new BestFirstSearch();

        GraphSearchConfigurator gsc = new GraphSearchConfigurator();
        gsc.setWantedNumberOfSolutions(Integer.MAX_VALUE);
        bfs.setConfigurator(gsc);

        bfs.setInitial(state);
        bfs.execute();

        System.out.println(bfs.getSolutions().size());
        System.out.println("Rozmiar zbioru open: "+bfs.getOpenSet().size());
        System.out.println("Rozmiar zbioru closed: "+bfs.getClosedStatesCount());
        System.out.println("Czas trwania: "+bfs.getDurationTime());



    }
}


class SudokuState extends GraphStateImpl{

    private byte[][]state;
    private int n, n_puste;

    public SudokuState(int n){
        state=new byte[n*n][n*n];
        this.n=n;
        n_puste=n*n*n*n;
    }

    @Override
    public String toString(){
        StringBuilder s=new StringBuilder();
        for(int i=0;i<state.length;i++){
            for(int j=0;j<state.length;j++){
                s.append(state[i][j]);
            }
            s.append('\n');
        }
        return s.toString();
    }

    void fromString(String s){
        n=3;
        n_puste=0;
        int indeks=0;
        state=new byte[9][9];
        for(int i=0;i< state.length;i++){
            for(int j=0;j<state.length;j++){
                if(s.charAt(indeks)!='.')
                    state[i][j]=(byte)(s.charAt(indeks)-'0');
                else
                    n_puste++;
                indeks++;
            }
        }
    }

    public boolean isValid(){
        HashSet<Byte> set = new HashSet<>();

        for(int i=0;i<state.length;i++){
            set.clear();
            for(int j=0;j<state.length;j++){
              if(state[i][j]==0)
                  continue;
              else if(set.contains(state[i][j]))
                  return false;
              else
                  set.add(state[i][j]);
            }
        }

        //column
        for(int i=0;i<state.length;i++){
            set.clear();
            for(int j=0;j<state.length;j++){
                if(state[j][i]==0)
                    continue;
                else if(set.contains(state[j][i]))
                    return false;
                else
                    set.add(state[j][i]);
            }
        }
//kwadraty
        for(int i=0;i<state.length;i+=n) {
            for (int j = 0; j < state.length; j += n) {
                set.clear();
                for (int k = 0; k < n; k++) {
                    for (int l = 0; l < n; l++) {
                        if (state[i + k][j + l] == 0)
                            continue;
                        else if (set.contains(state[i + k][j + l]))
                            return false;
                        else
                            set.add(state[i + k][j + l]);
                        }
                    }
                }
            }
        return true;
    }

    //konstruktor kopiujący

    public SudokuState(SudokuState state){
        super();
        this.state=new byte[state.state.length][state.state.length];
        this.n=state.n;
        this.n_puste=state.n_puste;
        for(int i=0;i<state.state.length;i++){
            for(int j=0;j<state.state.length;j++){
                this.state[i][j]=state.state[i][j];
            }
        }
    }


    @Override
    public int hashCode(){
        return toString().hashCode();
    }

    @Override
    public List<GraphState> generateChildren() {

        List<GraphState> list =new ArrayList<>();

        for(int i=0;i<state.length;i++){
            for(int j=0;j<state.length;j++){
                if(state[i][j]==0){
                    for(int k=1;k<=state.length;k++){
                        state[i][j]=(byte) k;
                        n_puste--;
                        if(isValid()){
                            list.add(new SudokuState(this));
                        }
                        n_puste++;
                        state[i][j]=0;
                    }
                    return list;
                }
            }
        }
        return list;
    }

    @Override
    public boolean isSolution() {
        return n_puste==0;
    }

    public int getN_puste() {
        return n_puste;
    }
}

class HFunction extends StateFunction{
    public double calculate(State s){
        if (s instanceof SudokuState) {
            SudokuState ss = (SudokuState) s;
            return ss.getN_puste();
        }
        return Double.NaN;
    }
}

