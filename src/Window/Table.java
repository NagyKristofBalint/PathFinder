package Window;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Table extends JPanel {

    public ArrayList<ArrayList<Square>> squares;
    public static final Color START_COLOR = Color.GREEN;
    public static final Color FINISH_COLOR = Color.RED;
    public static final Color MARKER_COLOR = Color.YELLOW;
    public static final Color REMAINING_COLOR = Color.BLUE;
    public static final Color WALL_COLOR = Color.BLACK;
    public static final Color PATH_COLOR = Color.CYAN;
    private int startX = 0;
    private int startY = 0;
    private int finishX;
    private int finishY;

    Table(int size) {
        setFinish(size - 1, size - 1);

        Square.setTable(this);

        setLayout(new GridLayout(size, size));
        squares = new ArrayList<>();
        for (int i = 0; i < size; ++i) {
            squares.add(new ArrayList<>());
            for (int j = 0; j < size; ++j) {
                Square square = new Square(i, j);
                squares.get(i).add(square);
                add(square);
            }
        }

        squares.get(getStartX()).get(getStartY()).setBackground(START_COLOR);
        squares.get(getFinishX()).get(getFinishY()).setBackground(FINISH_COLOR);
    }

    public void setListenersEnabled(boolean x) {
        Square.setListenersEnabled(x);
    }


    public int getStartY() {
        return startY;
    }

    public int getStartX() {
        return startX;
    }

    public int getFinishX() {
        return finishX;
    }

    public int getFinishY() {
        return finishY;
    }

    public void setFinish(int x, int y) {
        finishX = x;
        finishY = y;
    }

    public void setStart(int x, int y) {
        startX = x;
        startY = y;
    }

    public Square getStart() {
        return squares.get(getStartX()).get(getStartY());
    }

    public Square getFinish() {
        return squares.get(getFinishX()).get(getFinishY());
    }
}