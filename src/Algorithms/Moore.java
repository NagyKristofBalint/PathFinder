package Algorithms;
alma maci
import Window.Square;
import Window.Table;

import java.util.ArrayList;
import java.util.LinkedList;

public class Moore extends AbstractAlgorithm {
    private final ArrayList<ArrayList<Square>> squares;
    private final int size;
    private final int[][] distance;
    private final LinkedList<Square> queue;

    public Moore(Table table) {
        super(table);
        squares = table.squares;
        size = squares.size();
        previous = new Square[size][size];
        distance = new int[size][size];
        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                distance[i][j] = -1;
            }
        }
        queue = new LinkedList<>();
        //start
        queue.add(squares.get(table.getStartX()).get(table.getStartY()));
        distance[table.getStartX()][table.getStartY()] = 0;
    }

    @Override
    protected void nextIteration() throws AlgorithmFinishedException, PathNotFoundException, InterruptedException {
        //Breath-first search
        if (!pathFound) {
            Square current;
            if (!queue.isEmpty()) {
                //get and pop
                current = queue.pollFirst();
                for (Square neighbour : getNeighboursOf(current)) {
                    if (distance[neighbour.x][neighbour.y] == -1) {
                        /*if (!isFinish(neighbour)) {
                            neighbour.setBackground(Table.MARKER_COLOR);
                        }
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
                        }*/
                        if (!isFinish(neighbour)) {
                            neighbour.setBackground(Table.MARKER_COLOR);
                        }
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

            } else {
                throw new AlgorithmFinishedException("Moore ended");
            }
        }
    }

    private boolean isStart(Square current) {
        return current.equals(squares.get(table.getStartX()).get(table.getStartY()));
    }

    private boolean isFinish(Square current) {
        return current.equals(squares.get(table.getFinishX()).get(table.getFinishY()));
    }
}
