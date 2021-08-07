package Algorithms;

import Window.*;

import javax.swing.*;
import java.util.ArrayList;
import java.util.LinkedList;

abstract public class AbstractAlgorithm implements Runnable {
    protected Table table;
    protected int steps = 0;
    protected volatile Thread thread;
    protected ArrayList<ArrayList<Square>> squares;
    protected volatile boolean isSuspended = false;
    protected volatile boolean pathFound = false;
    protected volatile boolean backTraceFinished = false;
    protected Square[][] previous;
    protected volatile static int delay;
    protected Square pathTracerSquare;
    protected LinkedList<AlgorithmListener> algorithmListeners;
    private final boolean crossDirectionEnabled;

    public AbstractAlgorithm(Table table, boolean crossDirectionEnabled) {
        this.table = table;
        squares = table.squares;
        previous = new Square[squares.size()][squares.size()];
        algorithmListeners = new LinkedList<>();
        this.crossDirectionEnabled = crossDirectionEnabled;
    }

    abstract protected void nextIteration() throws AlgorithmFinishedException, PathNotFoundException, InterruptedException;

    @Override
    public void run() {
        //while (!thread.isInterrupted()) {
        while (true) {
            while (isSuspended) {
                synchronized (this) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        thread.interrupt();
                        break;
                    }
                }
            }

            if (thread.isInterrupted()) {
                break;
            }

            try {
                //////////////////////////////
                nextIteration();
                //////////////////////////////
            } catch (InterruptedException | AlgorithmFinishedException e) {
                System.out.println(e.getMessage());
                notifyAlgorithmStateListeners();
                break;
            } catch (PathNotFoundException e) {
                JOptionPane.showMessageDialog(table, e.getMessage());
                notifyAlgorithmStateListeners();
                break;
            }
        }
    }

    public void stop() {
        thread.interrupt();
    }

    public void start() {
        isSuspended = false;
        thread = new Thread(this);
        thread.start();
    }

    public void suspend() {
        isSuspended = true;
    }

    public synchronized void resume() {
        isSuspended = false;
        notifyAll();
    }

    protected synchronized void notifyCounterListenersAndIncreaseCounter() {
        for (AlgorithmListener listener : algorithmListeners) {
            listener.valueChanged(new CounterEvent(this, ++steps));
        }
    }

    private synchronized void notifyAlgorithmStateListeners() {
        for (AlgorithmListener listener : algorithmListeners) {
            listener.AlgorithmFinished();
        }
    }

    protected ArrayList<Square> getNeighboursOf(Square current) {
        int i = current.x;
        int j = current.y;
        ArrayList<Square> neighbours = new ArrayList<>();
        try {
            Square right = squares.get(i).get(j + 1);
            if (!right.isWall()) {
                neighbours.add(right);
            }
        } catch (IndexOutOfBoundsException ignored) {
        }

        try {
            Square left = squares.get(i).get(j - 1);
            if (!left.isWall()) {
                neighbours.add(left);
            }
        } catch (IndexOutOfBoundsException ignored) {
        }

        try {
            Square down = squares.get(i + 1).get(j);
            if (!down.isWall()) {
                neighbours.add(down);
            }
        } catch (IndexOutOfBoundsException ignored) {
        }

        try {
            Square up = squares.get(i - 1).get(j);
            if (!up.isWall()) {
                neighbours.add(up);
            }
        } catch (IndexOutOfBoundsException ignored) {
        }

        if (crossDirectionEnabled) {
            try {
                Square left_up = squares.get(i - 1).get(j - 1);
                if (!left_up.isWall()) {
                    neighbours.add(left_up);
                }
            } catch (IndexOutOfBoundsException ignored) {
            }

            try {
                Square right_up = squares.get(i - 1).get(j + 1);
                if (!right_up.isWall()) {
                    neighbours.add(right_up);
                }
            } catch (IndexOutOfBoundsException ignored) {
            }

            try {
                Square left_down = squares.get(i + 1).get(j - 1);
                if (!left_down.isWall()) {
                    neighbours.add(left_down);
                }
            } catch (IndexOutOfBoundsException ignored) {
            }

            try {
                Square right_down = squares.get(i + 1).get(j + 1);
                if (!right_down.isWall()) {
                    neighbours.add(right_down);
                }
            } catch (IndexOutOfBoundsException ignored) {
            }
        }

        return neighbours;
    }

    protected boolean isStart(Square current) {
        return current.equals(squares.get(table.getStartX()).get(table.getStartY()));
    }

    protected boolean isFinish(Square current) {
        return current.equals(squares.get(table.getFinishX()).get(table.getFinishY()));
    }

    public static void setDelay(int delay) {
        AbstractAlgorithm.delay = delay;
    }

    public synchronized void addAlgorithmListener(AlgorithmListener listener) {
        algorithmListeners.add(listener);
    }

    public synchronized void removeAlgorithmListener(AlgorithmListener listener) {
        algorithmListeners.remove(listener);
    }

    protected static class AlgorithmFinishedException extends Exception {
        public AlgorithmFinishedException(String errorMessage) {
            super(errorMessage);
        }
    }

    protected static class PathNotFoundException extends Exception {
        public PathNotFoundException(String errorMessage) {
            super(errorMessage);
        }
    }
}
