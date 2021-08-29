package Algorithms;

import Window.Square;
import Window.Table;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;

import static java.lang.Math.sqrt;

public class AStar extends AbstractAlgorithm {

    private final int[][] distance;
    private boolean[][] used;
    private PriorityQueue<Element> priorityQueue;
    private Square current;
    private LinkedList<Square> neighbours;

    public AStar(Table table, boolean crossDirectionEnabled) {
        super(table, crossDirectionEnabled);
        int size = squares.size();
        used = new boolean[size][size];
        distance = new int[size][size];
        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                distance[i][j] = -1;
            }
        }
        current = squares.get(table.getStartX()).get(table.getStartY());

        priorityQueue = new PriorityQueue<>(size * size, DESCENDING_ORDER);
        priorityQueue.add(new Element(squares.get(table.getStartX()).get(table.getStartY())));
        distance[table.getStartX()][table.getStartY()] = 0;

        previous[table.getStartX()][table.getStartY()] = new Square(-1, -1);
        pathTracerSquare = squares.get(table.getFinishX()).get(table.getFinishY());
    }

    /*@Override
    protected void nextIteration() throws AlgorithmFinishedException, PathNotFoundException, InterruptedException {
        if (!pathFound) {
            if (!priorityQueue.isEmpty()) {
                neighbours = getNeighboursOf(current);
                for (Square neighbour : neighbours) {
                    priorityQueue.add(new Element(neighbour));
                    if (!isStart(neighbour) && !isFinish(neighbour) && previous[neighbour.x][neighbour.y] == null) {
                        neighbour.setBackground(Table.MARKER_COLOR);
                    }
                }
                Thread.sleep(delay * 2L);
                Square next = priorityQueue.poll().square;
                while (previous[next.x][next.y] != null) {
                    next.setBackground(Table.REMAINING_COLOR);
                    if (priorityQueue.isEmpty()) {
                        throw new PathNotFoundException("Path not found");
                    }
                    next = priorityQueue.poll().square;
                }
                notifyCounterListenersAndIncreaseCounter();
                previous[next.x][next.y] = current;
                distance[next.x][next.y] = distance[current.x][current.y] + 1;
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
    }*/

    @Override
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
                        previous[neighbour.x][neighbour.y] = current;
                }
                Thread.sleep(delay * 2L);
                Square next = priorityQueue.poll().square;
                while (used[next.x][next.y]/* || isStart(next)*/) {
                    //while (previous[next.x][next.y] != null) {
                    next.setBackground(Table.REMAINING_COLOR);
                    if (priorityQueue.isEmpty()) {
                        System.out.println("alma");
                        throw new PathNotFoundException("Path not found");
                    }
                    next = priorityQueue.poll().square;
                }
                notifyCounterListenersAndIncreaseCounter();
                used[next.x][next.y] = true;
                //previous[next.x][next.y] = current;
                distance[next.x][next.y] = distance[current.x][current.y] + 1;
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
                    pathTracerSquare = previous[pathTracerSquare.x][pathTracerSquare.y];
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
        public double f;
        public Square square;

        public Element(Square square) {
            this.square = square;
            f = distance[square.x][square.y] + euclideanHeuristic(square);
        }

        @Override
        public int compareTo(Element o) {
            return Double.compare(f, o.f);
        }
    }
}
