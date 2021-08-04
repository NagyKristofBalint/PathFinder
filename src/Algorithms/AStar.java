package Algorithms;

import Window.Square;
import Window.Table;

import java.util.ArrayList;

public class AStar extends AbstractAlgorithm {
    public AStar(Table table, ArrayList<ArrayList<Square>> squares) {
        super(table,squares);
    }

    @Override
    protected void nextIteration() throws AlgorithmFinishedException, PathNotFoundException, InterruptedException {

    }
}
