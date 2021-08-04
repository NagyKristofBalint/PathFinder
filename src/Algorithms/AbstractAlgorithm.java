package Algorithms;

import Window.Square;
import Window.Table;

import javax.swing.*;
import java.util.ArrayList;

abstract public class AbstractAlgorithm implements Runnable {
    protected Table table;
    protected volatile Thread thread;
    protected volatile boolean isSuspended = false;
    protected volatile boolean pathFound = false;
    protected volatile boolean backTraceFinished = false;
    protected Square[][] previous;
    protected volatile static int delay;
    private static String errorMessage;

    public static String getErrorMessage() {
        return errorMessage;
    }

    public AbstractAlgorithm(Table table) {
        this.table = table;
    }

    abstract protected void nextIteration() throws AlgorithmFinishedException, PathNotFoundException, InterruptedException;

    @Override
    public void run() {
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
                errorMessage = "Done";
                break;
            } catch (PathNotFoundException e) {
                System.out.println(e.getMessage());
                errorMessage = "Path not found";
                //JOptionPane.showMessageDialog(, "You just opened a dialog.", DIALOG_TITLE, DIALOG_ICON);
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
            Square right = table.squares.get(i).get(j + 1);
            if (!right.isWall()) {
                neighbours.add(right);
            }
        } catch (IndexOutOfBoundsException ignored) {
        }

        try {
            Square left = table.squares.get(i).get(j - 1);
            if (!left.isWall()) {
                neighbours.add(left);
            }
        } catch (IndexOutOfBoundsException ignored) {
        }

        try {
            Square down = table.squares.get(i + 1).get(j);
            if (!down.isWall()) {
                neighbours.add(down);
            }
        } catch (IndexOutOfBoundsException ignored) {
        }

        try {
            Square up = table.squares.get(i - 1).get(j);
            if (!up.isWall()) {
                neighbours.add(up);
            }
        } catch (IndexOutOfBoundsException ignored) {
        }

        return neighbours;
    }

    public static void setDelay(int delay) {
        AbstractAlgorithm.delay = delay;
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
