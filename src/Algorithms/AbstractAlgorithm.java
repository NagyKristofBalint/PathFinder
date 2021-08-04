package Algorithms;

import Window.AlgorithmListener;
import Window.Square;
import Window.Table;

import javax.swing.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedList;

abstract public class AbstractAlgorithm implements Runnable {
    protected Table table;
    protected volatile Thread thread;
    protected ArrayList<ArrayList<Square>> squares;
    protected volatile boolean isSuspended = false;
    protected volatile boolean pathFound = false;
    protected volatile boolean backTraceFinished = false;
    protected Square[][] previous;
    protected volatile static int delay;
    protected Square pathTracerSquare;
    protected LinkedList<AlgorithmListener> AlgorithmListeners;
    protected Instant time;

    public AbstractAlgorithm(Table table) {
        this.table = table;
        squares = table.squares;
        previous = new Square[squares.size()][squares.size()];
        AlgorithmListeners = new LinkedList<>();
    }

    abstract protected void nextIteration() throws AlgorithmFinishedException, PathNotFoundException, InterruptedException;

    @Override
    public void run() {
        time = Instant.now();
        Thread thisThread = Thread.currentThread();
        while (thread == thisThread) {
            while (isSuspended) {
                synchronized (this) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }
            try {
                nextIteration();
            } catch (InterruptedException | AlgorithmFinishedException e) {
                System.out.println(e.getMessage());
                for (AlgorithmListener listener : AlgorithmListeners) {
                    listener.AlgorithmFinished();
                }
                break;
            } catch (PathNotFoundException e) {
                System.out.println(e.getMessage());
                JOptionPane.showMessageDialog(table, "Path not found");
                synchronized (this) {
                    for (AlgorithmListener listener : AlgorithmListeners) {
                        listener.AlgorithmFinished();
                    }
                }
                break;
            }
        }
    }

    public void stop() {
        Thread tmp = thread;
        thread = null;
        tmp.interrupt();
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
        AlgorithmListeners.add(listener);
    }

    public synchronized void removeAlgorithmListener(AlgorithmListener listener) {
        AlgorithmListeners.remove(listener);
    }

    protected class AlgorithmFinishedException extends Exception {
        public AlgorithmFinishedException(String errorMessage) {
            super(errorMessage);
        }
    }

    protected class PathNotFoundException extends Exception {
        public PathNotFoundException(String errorMessage) {
            super(errorMessage);
        }
    }
}
