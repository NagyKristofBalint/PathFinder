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
    private int startX = 0;
    private int startY = 0;
    private int finishX;
    private int finishY;

    Table(int size) {
        setFinishX(size - 1);
        setFinishY(size - 1);

        Square.setTableSize(size);
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


        //if()

        squares.get(getStartX()).get(getStartY()).setBackground(START_COLOR);
        squares.get(getFinishX()).get(getFinishY()).setBackground(FINISH_COLOR);
    }

    public void setListenersEnabled(boolean x) {
        Square.setListenersEnabled(x);
    }

    public int getStartY() {
        return startY;
    }

    public void setStartY(int startY) {
        this.startY = startY;
    }

    public int getStartX() {
        return startX;
    }

    public void setStartX(int startX) {
        this.startX = startX;
    }

    public int getFinishX() {
        return finishX;
    }

    public void setFinishX(int finishX) {
        this.finishX = finishX;
    }

    public int getFinishY() {
        return finishY;
    }

    public void setFinishY(int finishY) {
        this.finishY = finishY;
    }
}
