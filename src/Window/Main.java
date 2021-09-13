package Window;

import Algorithms.*;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class Main extends JFrame implements AlgorithmListener {

    private static final String TITLE = "Path Finder 1.0";
    private static final int WINDOW_WIDTH = 525;
    private static final int WINDOW_HEIGHT = 780;
    private static final int WINDOW_OFFSET_X = 500;
    private static final int WINDOW_OFFSET_Y = 20;
    private final Container MAIN_PANEL;
    private JPanel top;
    private JPanel bottom;
    private JComboBox algorithmChooser;
    private JSpinner sizeChooser;
    private JLabel stepCountLabel;
    private JButton startButton;
    private JButton pauseButton;
    private JButton stopButton;
    private JButton clearPathButton;
    private JButton clearWallsButton;
    private JSlider speedSlider;
    private JCheckBox enableDiagonalDirectionCheckBox;
    private Table table;
    private int stepCount;
    private int tableSize = 25;
    private boolean algorithmChooserEnabled = true;
    private boolean sizeChooserEnabled = true;
    private boolean startButtonEnabled = true;
    private boolean pauseOrResume = true;
    private boolean stopButtonEnabled = false;
    private boolean pauseButtonEnabled = false;
    private boolean clearWallsButtonEnabled = true;
    private boolean clearPathButtonEnabled = false;
    private boolean diagonalDirectionEnabled = true;
    private AbstractAlgorithm algorithm;
    private final int MAX_SPEED = 150;
    private final int MIN_SPEED = 10;
    private final int MIN_TABLE_SIZE = 5;
    private final int MAX_TABLE_SIZE = 50;
    private int speed = (MIN_SPEED + MAX_SPEED) / 2;

    Main() {
        MAIN_PANEL = new JPanel();
        createWindow();
        addListeners();
        setContentPane(MAIN_PANEL);
    }

    private void createTopSide() {
        stepCountLabel = new JLabel("0");
        String[] algorithmStrings = {"Dijkstra", "A*"};
        algorithmChooser = new JComboBox(algorithmStrings);
        sizeChooser = new JSpinner(new SpinnerNumberModel(tableSize, MIN_TABLE_SIZE, MAX_TABLE_SIZE, 1));
        GridBagConstraints constraints = new GridBagConstraints();
        enableDiagonalDirectionCheckBox = new JCheckBox("Diagonal direction");

        constraints.weightx = 1;
        constraints.gridy = 0;
        constraints.gridx = 0;
        constraints.ipady = 10;
        top.add(new JLabel("Step Count"), constraints);

        constraints.gridx = 1;
        constraints.ipady = 0;
        top.add(new JLabel("Algorithm"), constraints);

        constraints.gridx = 2;
        top.add(new JLabel("Table size"), constraints);

        constraints.gridy = 1;
        constraints.gridx = 0;
        top.add(stepCountLabel, constraints);

        constraints.gridx = 1;
        top.add(algorithmChooser, constraints);

        constraints.gridx = 2;
        top.add(sizeChooser, constraints);

        constraints.gridx = 3;
        constraints.gridy = 0;
        constraints.gridheight = 2;
        top.add(enableDiagonalDirectionCheckBox, constraints);
    }

    private void createBottomSide() {
        startButton = new JButton("Start");
        pauseButton = new JButton("Pause");
        stopButton = new JButton("Stop");
        speedSlider = new JSlider(MIN_SPEED - 1, MAX_SPEED - 1, speed);
        clearPathButton = new JButton("Clear Path");
        clearWallsButton = new JButton("Clear Walls");
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1;
        constraints.insets = new Insets(0, 10, 0, 10);
        bottom.add(startButton, constraints);

        constraints.gridx = 1;
        constraints.gridy = 0;
        bottom.add(pauseButton, constraints);

        constraints.gridx = 2;
        constraints.gridy = 0;
        bottom.add(stopButton, constraints);

        JPanel speedPanel = new JPanel();
        speedPanel.setLayout(new BoxLayout(speedPanel, BoxLayout.X_AXIS));
        speedPanel.add(new JLabel("Speed"));
        speedPanel.add(speedSlider);

        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 3;
        constraints.ipady = 20;
        bottom.add(speedPanel, constraints);

        JPanel clearPanel = new JPanel();
        clearPanel.setLayout(new GridLayout(1, 2, 20, 0));
        clearPanel.add(clearPathButton);
        clearPanel.add(clearWallsButton);
        constraints.gridwidth = 3;
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.ipady = 0;
        bottom.add(clearPanel, constraints);
    }

    private void createWindow() {
        top = new JPanel();
        table = new Table(tableSize);
        bottom = new JPanel();

        MAIN_PANEL.setLayout(new GridBagLayout());
        top.setLayout(new GridBagLayout());
        bottom.setLayout(new GridBagLayout());

        createTopSide();
        createBottomSide();
        validateControls();

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 1;
        MAIN_PANEL.add(top, c);

        c.gridx = 0;
        c.gridy = 1;
        c.weighty = 6;
        MAIN_PANEL.add(table, c);

        c.gridx = 0;
        c.gridy = 2;
        c.weighty = 1;
        MAIN_PANEL.add(bottom, c);
    }

    private void addListeners() {

        sizeChooser.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (sizeChooserEnabled) {
                    tableSize = (int) sizeChooser.getValue();
                    MAIN_PANEL.removeAll();
                    createWindow();
                    addListeners();
                    revalidate();
                    table.setListenersEnabled(true);
                }
            }
        });

        Main thisWindow = this;
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                clearPath();
                algorithmChooserEnabled = false;
                sizeChooserEnabled = false;
                startButtonEnabled = false;
                pauseOrResume = true;
                pauseButtonEnabled = true;
                stopButtonEnabled = true;
                clearWallsButtonEnabled = false;
                clearPathButtonEnabled = false;
                diagonalDirectionEnabled = false;
                validateControls();
                table.setListenersEnabled(false);
                AbstractAlgorithm.setDelay(MAX_SPEED - speedSlider.getValue());

                String choice = (String) algorithmChooser.getSelectedItem();

                diagonalDirectionEnabled = enableDiagonalDirectionCheckBox.isSelected();
                if (choice.equals("Dijkstra")) {
                    algorithm = new Dijkstra(table, diagonalDirectionEnabled);
                } else {
                    algorithm = new AStar(table, diagonalDirectionEnabled);
                }
                algorithm.addAlgorithmListener(thisWindow);

                algorithm.start();
            }
        });

        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (pauseOrResume) {
                    pauseOrResume = false;
                    algorithm.suspend();
                } else {
                    pauseOrResume = true;
                    algorithm.resume();
                }
                validateControls();
            }
        });

        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AlgorithmFinished();
                algorithm.stop();
            }
        });

        speedSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                speed = speedSlider.getValue();
                AbstractAlgorithm.setDelay(MAX_SPEED - speedSlider.getValue());
            }
        });

        clearPathButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearPath();
                table.setListenersEnabled(true);
            }
        });

        clearWallsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (ArrayList<Square> row : table.squares) {
                    for (Square s : row) {
                        if (s.isWall()) {
                            s.makeWall();
                        }
                    }
                }
            }
        });
    }

    private void clearPath() {
        JPanel helper = new JPanel();
        for (ArrayList<Square> row : table.squares) {
            for (Square s : row) {
                if (!s.isWall())
                    s.setBackground(helper.getBackground());
            }
        }
        table.getStart().setBackground(Table.START_COLOR);
        table.getFinish().setBackground(Table.FINISH_COLOR);
    }

    private void validateControls() {
        algorithmChooser.setEnabled(algorithmChooserEnabled);
        sizeChooser.setEnabled(sizeChooserEnabled);
        startButton.setEnabled(startButtonEnabled);
        pauseButton.setEnabled(pauseButtonEnabled);
        if (pauseOrResume) {
            pauseButton.setText("Pause");
        } else {
            pauseButton.setText("Resume");
        }
        stopButton.setEnabled(stopButtonEnabled);
        clearPathButton.setEnabled(clearPathButtonEnabled);
        clearWallsButton.setEnabled(clearWallsButtonEnabled);
        enableDiagonalDirectionCheckBox.setEnabled(diagonalDirectionEnabled);
    }

    @Override
    public void AlgorithmFinished() {
        algorithmChooserEnabled = true;
        sizeChooserEnabled = true;
        startButtonEnabled = true;
        pauseOrResume = true;
        stopButtonEnabled = false;
        pauseButtonEnabled = false;
        clearPathButtonEnabled = true;
        clearWallsButtonEnabled = true;
        diagonalDirectionEnabled = true;
        validateControls();
        table.setListenersEnabled(false);
    }

    @Override
    public void valueChanged(CounterEvent e) {
        stepCountLabel.setText("" + e.getValue());
        stepCount = e.getValue();
    }

    public static void main(String[] args) {
        Main MainWindow = new Main();
        MainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        MainWindow.setBounds(WINDOW_OFFSET_X, WINDOW_OFFSET_Y, WINDOW_WIDTH, WINDOW_HEIGHT);
        MainWindow.setTitle(TITLE);
        MainWindow.setVisible(true);
    }
}
