package Window;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Square extends JPanel {
    private boolean isWall = false;
    public final int x;
    public final int y;
    private static boolean mousePressed = false;
    private static volatile boolean startPressed = false;
    private static volatile boolean finishPressed = false;
    private static boolean listenersEnabled = true;
    private static Color defaultBackgroundColor;
    private static Table table;
    private static int previousX;
    private static int previousY;

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Square)) {
            return false;
        }
        Square s = (Square) obj;
        return (s.x == x && s.y == y);
    }

    Square(int x, int y) {
        this.x = x;
        this.y = y;
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                if (listenersEnabled) {
                    mousePressed = true;
                    if (isStart()) {
                        startPressed = true;
                    } else if (isFinish()) {
                        finishPressed = true;
                    } else {
                        makeWall();
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                if (listenersEnabled) {
                    mousePressed = false;
                    startPressed = false;
                    finishPressed = false;
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                if (listenersEnabled) {
                    if (startPressed) {
                        if (!isFinish() && !isWall) {
                            setBackground(Table.START_COLOR);
                            table.setStartX(x);
                            table.setStartY(y);
                            table.squares.get(previousX).get(previousY).setBackground(defaultBackgroundColor);
                        } else {
                            mousePressed = false;
                            startPressed = false;
                            finishPressed = false;
                        }
                    } else {
                        if (finishPressed) {
                            if (!isStart() && !isWall) {
                                setBackground(Table.FINISH_COLOR);
                                table.setFinishX(x);
                                table.setFinishY(y);
                                table.squares.get(previousX).get(previousY).setBackground(defaultBackgroundColor);
                            } else {
                                mousePressed = false;
                                startPressed = false;
                                finishPressed = false;
                            }
                        } else {
                            if (mousePressed) {
                                makeWall();
                            }
                        }
                    }
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                if (listenersEnabled){
                    previousX = x;
                    previousY = y;
                }
            }
        });
    }

    public void makeWall() {
        if (!isStart() && !isFinish()) {
            if (!isWall) {
                isWall = true;
                setBackground(Table.WALL_COLOR);
            } else {
                isWall = false;
                setBackground(defaultBackgroundColor);
            }
        }
    }

    private boolean isStart() {
        return (x == table.getStartX() && y == table.getStartY());
    }

    private boolean isFinish() {
        return (x == table.getFinishX() && y == table.getFinishY());
    }

    public boolean isWall() {
        return isWall;
    }

    public static void setListenersEnabled(boolean isEnabled) {
        listenersEnabled = isEnabled;
    }

    public static void setTable(Table table) {
        Square.table = table;
    }
}
