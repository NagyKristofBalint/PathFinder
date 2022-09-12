package Algorithms;

import Window.Square;
import Window.Table;

import java.util.PriorityQueue;

public class Dijkstra extends AbstractAlgorithm {
    private final int[][] distance;
    private final PriorityQueue<PrioQElement> priorityQueue;
    private Square current;
    private boolean[][] used;

    public Dijkstra(Table table, boolean crossDirectionEnabled) {
        super(table, crossDirectionEnabled);
        int size = squares.size();
        distance = new int[size][size];
        used = new boolean[size][size];
        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                distance[i][j] = Integer.MAX_VALUE;
            }
        }
        priorityQueue = new PriorityQueue<>();
        priorityQueue.add(new PrioQElement(table.getStart()));
        distance[table.getStartX()][table.getStartY()] = 0;

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
                        int tentativeDistance = distance[current.x][current.y] + 1;
                        if (tentativeDistance < distance[neighbour.x][neighbour.y]) {
                            distance[neighbour.x][neighbour.y] = tentativeDistance;
                            cameFrom.put(neighbour, current);
                            priorityQueue.offer(new PrioQElement(neighbour));
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
                throw new AlgorithmFinishedException("Dijkstra ended");
            }
        }
    }

    private class PrioQElement implements Comparable<PrioQElement> {
        public Square square;

        public PrioQElement(Square s) {
            this.square = s;
        }

        @Override
        public int compareTo(PrioQElement o) {
            return Integer.compare(distance[square.x][square.y], distance[o.square.x][o.square.y]);
        }

    }
}
