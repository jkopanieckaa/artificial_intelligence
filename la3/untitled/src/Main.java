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

    }
}

class młynek extends GraphStateImpl{
    private byte[][] state;
    int white = 0;
    int black = 0;

    public mlynek(){
        state = new byte[3][8];
    }
}