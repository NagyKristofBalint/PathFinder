package Algorithms;

import Window.Square;
import Window.Table;

import java.util.PriorityQueue;

import static java.lang.Math.sqrt;

public class AStar extends AbstractAlgorithm {

    private final int[][] g;
    private final double[][] f;
    private boolean[][] used;
    private PriorityQueue<prioQElement> priorityQueue;
    private Square current;

    public AStar(Table table, boolean crossDirectionEnabled) {
        super(table, crossDirectionEnabled);
        int size = squares.size();
        used = new boolean[size][size];
        g = new int[size][size];
        f = new double[size][size];
        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                g[i][j] = Integer.MAX_VALUE;
                f[i][j] = Double.MAX_VALUE;
            }
        }

        priorityQueue = new PriorityQueue<>();
        priorityQueue.add(new prioQElement(table.getStart()));
        g[table.getStartX()][table.getStartY()] = 0;
        f[table.getStartX()][table.getStartY()] = euclideanHeuristic(table.getStart());

        cameFrom.put(table.getStart(), new Square(-1, -1));
        pathTracerSquare = table.getFinish();
    }

    @Override
    protected void nextIteration() throws AlgorithmFinishedException, PathNotFoundException, InterruptedException {
        if (!pathFound) {
            if (!priorityQueue.isEmpty()) {
                current = priorityQueue.poll().square;
                if (current.isFinish()) {
                    pathFound = true;
                } else {
                    notifyCounterListenersAndIncreaseCounter();
                    if (!current.isStart() && !current.isFinish())
                        current.setBackground(Table.REMAINING_COLOR);
                    used[current.x][current.y] = true;
                    Thread.sleep(delay * 2L);
                    for (Square neighbour : getNeighboursOf(current)) {
                        if (!neighbour.isFinish() && !used[neighbour.x][neighbour.y])
                            neighbour.setBackground(Table.MARKER_COLOR);
                        Thread.sleep(delay);
                        int tentativeG = g[current.x][current.y] + 1;
                        if (tentativeG < g[neighbour.x][neighbour.y]) {
                            g[neighbour.x][neighbour.y] = tentativeG;
                            cameFrom.put(neighbour, current);
                            f[neighbour.x][neighbour.y] = g[neighbour.x][neighbour.y] + euclideanHeuristic(neighbour);
                            priorityQueue.offer(new prioQElement(neighbour));
                        }
                    }
                }
            } else {
                throw new PathNotFoundException("Path not found");
            }
        } else {
            if (!backTraceFinished) {
                if (!pathTracerSquare.isStart()) {
                    pathTracerSquare = cameFrom.get(pathTracerSquare);
                    pathTracerSquare.setBackground(Table.PATH_COLOR);
                } else {
                    pathTracerSquare.setBackground(Table.START_COLOR);
                    backTraceFinished = true;
                }
                Thread.sleep(delay);
            } else {
                throw new AlgorithmFinishedException("AStar ended");
            }
        }
    }

    private double euclideanHeuristic(Square s) {
        int xDifference = s.x - table.getFinishX();
        int yDifference = s.y - table.getFinishY();
        return sqrt(xDifference * xDifference + yDifference * yDifference);
    }

    class prioQElement implements Comparable<prioQElement> {
        public Square square;

        public prioQElement(Square square) {
            this.square = square;
        }

        @Override
        public int compareTo(prioQElement o) {
            return Double.compare(f[square.x][square.y], f[o.square.x][o.square.y]);
            //Transform it into Dijkstra's algorithm
            //return Double.compare(g[square.x][square.y], g[o.square.x][o.square.y]);

        }

    }
}
