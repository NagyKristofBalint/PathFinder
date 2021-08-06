package Algorithms;

import Window.Square;
import Window.Table;

import java.util.LinkedList;

public class Moore extends AbstractAlgorithm {
    private final int[][] distance;
    private final LinkedList<Square> queue;

    public Moore(Table table, boolean crossDirectionEnabled) {
        super(table, crossDirectionEnabled);
        int size = squares.size();
        distance = new int[size][size];
        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                distance[i][j] = -1;
            }
        }
        queue = new LinkedList<>();
        queue.add(squares.get(table.getStartX()).get(table.getStartY()));
        distance[table.getStartX()][table.getStartY()] = 0;

        pathTracerSquare = squares.get(table.getFinishX()).get(table.getFinishY());
    }

    @Override
    protected void nextIteration() throws AlgorithmFinishedException, PathNotFoundException, InterruptedException {
        //Breath-first search
        Square current;
        if (!pathFound) {
            if (!queue.isEmpty()) {
                //get and pop
                current = queue.pollFirst();
                for (Square neighbour : getNeighboursOf(current)) {
                    if (distance[neighbour.x][neighbour.y] == -1) {
                        if (!isFinish(neighbour)) {
                            neighbour.setBackground(Table.MARKER_COLOR);
                        }
                        notifyCounterListeners();
                        Thread.sleep(delay * 2L);
                        queue.add(neighbour);
                        distance[neighbour.x][neighbour.y] = distance[current.x][current.y] + 1;
                        previous[neighbour.x][neighbour.y] = current;
                        neighbour.setBackground(Table.REMAINING_COLOR);
                        if (isStart(neighbour)) {
                            neighbour.setBackground(Table.START_COLOR);
                        } else if (isFinish(neighbour)) {
                            neighbour.setBackground(Table.FINISH_COLOR);
                        }
                        Thread.sleep(delay);
                        if (isFinish(neighbour)) {
                            pathFound = true;
                            break;
                        }
                    }
                }
            } else {
                throw new PathNotFoundException("Path not found");
            }
        } else {
            if (!backTraceFinished) {
                if (!isStart(pathTracerSquare)) {
                    pathTracerSquare = previous[pathTracerSquare.x][pathTracerSquare.y];
                    pathTracerSquare.setBackground(Table.PATH_COLOR);
                } else {
                    pathTracerSquare.setBackground(Table.START_COLOR);
                    backTraceFinished = true;
                }
                Thread.sleep(delay);
            } else {
                throw new AlgorithmFinishedException("Moore ended");
            }
        }
    }
}
