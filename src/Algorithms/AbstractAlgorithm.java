package Algorithms;

import Util.AlgorithmListener;
import Util.CounterEvent;
import Util.MyAudioPlayer;
import Window.*;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

abstract public class AbstractAlgorithm implements Runnable {
    protected Table table;
    protected int steps = 0;
    protected volatile Thread thread;
    protected ArrayList<ArrayList<Square>> squares;
    protected volatile boolean isSuspended = false;
    protected volatile boolean pathFound = false;
    protected volatile boolean backTraceFinished = false;
    protected HashMap<Square, Square> cameFrom;
    protected volatile static int delay;
    protected Square pathTracerSquare;
    protected LinkedList<AlgorithmListener> algorithmListeners;
    private MyAudioPlayer myAudioPlayer;
    private final boolean crossDirectionEnabled;

    public AbstractAlgorithm(Table table, boolean crossDirectionEnabled) {
        this.table = table;
        squares = table.squares;
        cameFrom = new HashMap<>(squares.size() * squares.size());
        algorithmListeners = new LinkedList<>();
        this.crossDirectionEnabled = crossDirectionEnabled;
        myAudioPlayer = new MyAudioPlayer(table);
    }

    abstract protected void nextIteration() throws AlgorithmFinishedException, PathNotFoundException, InterruptedException;

    @Override
    public void run() {
        while (true) {
            try {
                while (isSuspended) {
                    synchronized (this) {
                        wait();
                    }
                }
            } catch (InterruptedException e) {
                break;
            }

            try {
                nextIteration();
                if (!myAudioPlayer.isRunning() && !isSuspended) {
                    myAudioPlayer.resetAudio();
                    myAudioPlayer.playTraceSound();
                }
            } catch (InterruptedException | AlgorithmFinishedException e) {
                System.out.println(e.getMessage());
                notifyAlgorithmStateListeners();
                myAudioPlayer.stopTraceSound();
                break;
            } catch (PathNotFoundException e) {
                notifyAlgorithmStateListeners();
                myAudioPlayer.stopTraceSound();
                JOptionPane.showMessageDialog(table, e.getMessage());
                break;
            }
        }
    }

    public void stop() {
        thread.interrupt();
        myAudioPlayer.stopTraceSound();
    }

    public void start() {
        isSuspended = false;
        thread = new Thread(this);
        thread.start();
        myAudioPlayer.playTraceSound();
    }

    public void suspend() {
        isSuspended = true;
        myAudioPlayer.stopTraceSound();
    }

    public synchronized void resume() {
        isSuspended = false;
        notifyAll();
        myAudioPlayer.playTraceSound();
    }

    protected synchronized void notifyCounterListenersAndIncreaseCounter() {
        for (AlgorithmListener listener : algorithmListeners) {
            listener.valueChanged(new CounterEvent(this, ++steps));
        }
    }

    private synchronized void notifyAlgorithmStateListeners() {
        for (AlgorithmListener listener : algorithmListeners) {
            listener.algorithmFinished();
        }
    }

    protected LinkedList<Square> getNeighboursOf(Square current) {
        int i = current.x;
        int j = current.y;
        LinkedList<Square> neighbours = new LinkedList<>();
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
