package Algorithms;

import Window.Square;
import Window.Table;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;

import static java.lang.Math.sqrt;

public class AStar extends AbstractAlgorithm {

    private final int[][] g;
    private final double[][] f;
    private boolean[][] used;
    private PriorityQueue<Element> priorityQueue;
    private Square current;
    private LinkedList<Square> neighbours;

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

        priorityQueue = new PriorityQueue<>(size * size, DESCENDING_ORDER);
        priorityQueue.add(new Element(getStart()));
        g[table.getStartX()][table.getStartY()] = 0;
        f[table.getStartX()][table.getStartY()] = euclideanHeuristic(getStart());

        cameFrom[table.getStartX()][table.getStartY()] = new Square(-1, -1);
        pathTracerSquare = getFinish();
    }

    /* @Override
    protected void nextIteration() throws AlgorithmFinishedException, PathNotFoundException, InterruptedException {
        if (!pathFound) {
            if (!priorityQueue.isEmpty()) {
                neighbours = getNeighboursOf(current);
                for (Square neighbour : neighbours) {
                    priorityQueue.add(new Element(neighbour));
                    if (neighbour.equals(current))
                        System.out.println("rossz szomszed");
                    if (!isStart(neighbour) && !isFinish(neighbour) && !used[neighbour.x][neighbour.y]) {
                        neighbour.setBackground(Table.MARKER_COLOR);
                    }
                    if (!used[neighbour.x][neighbour.y])
                        cameFrom[neighbour.x][neighbour.y] = current;
                }
                Thread.sleep(delay * 2L);
                Square next = priorityQueue.poll().square;
                while (used[next.x][next.y]) {
                    //while (used[next.x][next.y] || isStart(next)) {
                    //while (cameFrom[next.x][next.y] != null) {
                    next.setBackground(Table.REMAINING_COLOR);
                    if (priorityQueue.isEmpty()) {
                        System.out.println("alma");
                        throw new PathNotFoundException("Path not found");
                    }
                    next = priorityQueue.poll().square;
                }
                notifyCounterListenersAndIncreaseCounter();
                used[next.x][next.y] = true;
                //cameFrom[next.x][next.y] = current;
                g[next.x][next.y] = g[current.x][current.y] + 1;
                if (isFinish(next)) {
                    pathFound = true;
                } else {
                    current = next;
                    current.setBackground(Table.REMAINING_COLOR);
                    Thread.sleep(delay);
                }
            } else {
                throw new PathNotFoundException("Path not found");
            }
        } else {
            if (!backTraceFinished) {
                if (!isStart(pathTracerSquare)) {
                    System.out.println("" + pathTracerSquare.x + ' ' + pathTracerSquare.y);
                    pathTracerSquare = cameFrom[pathTracerSquare.x][pathTracerSquare.y];
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
 */
    @Override
    protected void nextIteration() throws AlgorithmFinishedException, PathNotFoundException, InterruptedException {
        if (!pathFound) {
            if (!priorityQueue.isEmpty()) {
                current = priorityQueue.poll().square;
                if (isFinish(current)) {
                    pathFound = true;
                } else {
                    notifyCounterListenersAndIncreaseCounter();
                    if (!isStart(current) && !isFinish(current))
                        current.setBackground(Table.REMAINING_COLOR);
                    used[current.x][current.y] = true;
                    Thread.sleep(delay * 2L);
                    neighbours = getNeighboursOf(current);
                    for (Square neighbour : neighbours) {
                        if (!isFinish(neighbour) && !used[neighbour.x][neighbour.y])
                            neighbour.setBackground(Table.MARKER_COLOR);
                        Thread.sleep(delay);
                        int tentativeG = g[current.x][current.y] + 1;
                        if (tentativeG < g[neighbour.x][neighbour.y]) {
                            g[neighbour.x][neighbour.y] = tentativeG;
                            cameFrom[neighbour.x][neighbour.y] = current;
                            f[neighbour.x][neighbour.y] = g[neighbour.x][neighbour.y] + euclideanHeuristic(neighbour);
                            priorityQueue.offer(new Element(neighbour));
                        }
                    }
                }
            } else {
                throw new PathNotFoundException("Path not found");
            }
        } else {
            if (!backTraceFinished) {
                if (!isStart(pathTracerSquare)) {
                    pathTracerSquare = cameFrom[pathTracerSquare.x][pathTracerSquare.y];
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

    private final Comparator<Element> DESCENDING_ORDER = new Comparator<Element>() {
        @Override
        public int compare(Element o1, Element o2) {
            return o1.compareTo(o2);
        }
    };

    class Element implements Comparable<Element> {
        public Square square;

        public Element(Square square) {
            this.square = square;
        }

        @Override
        public int compareTo(Element o) {
            return Double.compare(f[square.x][square.y], f[o.square.x][o.square.y]);
        }
        //Transform it into Dijkstra's algorithm
        /*public int compareTo(Element o) {
            return Double.compare(g[square.x][square.y], g[o.square.x][o.square.y]);
        }*/
    }
}
