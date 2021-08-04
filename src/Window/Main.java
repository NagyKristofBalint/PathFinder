package Window;

import Algorithms.*;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main extends JFrame {

    private static final String TITLE = "Path Finder 1.0";
    private static final int WINDOW_WIDTH = 500;
    private static final int WINDOW_HEIGHT = 780;
    private static final int WINDOW_OFFSET_X = 500;
    private static final int WINDOW_OFFSET_Y = 20;
    private final Container mainPanel;
    private JPanel top;
    private JPanel bottom;
    private JComboBox algorithmChooser;
    private JSpinner sizeChooser;
    private JLabel timeLabel;
    private JButton startButton;
    private JButton pauseButton;
    private JButton stopButton;
    private JButton clearButton;
    private JSlider speedSlider;
    private Table table;
    private int tableSize = 10;
    private boolean algorithmChooserEnabled = true;
    private boolean sizeChooserEnabled = true;
    private boolean startButtonEnabled = true;
    private boolean pauseOrResume = true;
    private boolean stopButtonEnabled = false;
    private boolean pauseButtonEnabled = false;
    private AbstractAlgorithm algotihm;
    private final int MAX_SPEED = 50;
    private final int MIN_SPEED = 1;
    private final int MIN_TABLE_SIZE = 5;
    private final int MAX_TABLE_SIZE = 50;

    private void createTopSide() {
        timeLabel = new JLabel("00:00:00");
        String[] algorithmStrings = {"Moore", "A*"};
        algorithmChooser = new JComboBox(algorithmStrings);
        GridBagConstraints constraints = new GridBagConstraints();
        sizeChooser = new JSpinner(new SpinnerNumberModel(tableSize, MIN_TABLE_SIZE, MAX_TABLE_SIZE, 1));
        constraints.weightx = 1;
        constraints.gridy = 0;
        constraints.gridx = 0;
        constraints.ipady = 10;
        top.add(new JLabel("Elapsed Time"), constraints);

        constraints.gridx = 1;
        constraints.ipady = 0;
        top.add(new JLabel("Algorithm"), constraints);

        constraints.gridx = 2;
        top.add(new JLabel("Table Size"), constraints);

        constraints.gridy = 1;
        constraints.gridx = 0;
        top.add(timeLabel, constraints);

        constraints.gridx = 1;
        top.add(algorithmChooser, constraints);

        constraints.gridx = 2;
        top.add(sizeChooser, constraints);
    }

    private void createBottomSide() {
        startButton = new JButton("Start");
        pauseButton = new JButton("Pause");
        stopButton = new JButton("Stop");
        speedSlider = new JSlider(MIN_SPEED - 1, MAX_SPEED - 1);
        clearButton = new JButton("Clear");
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

        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.ipady = 0;
        bottom.add(clearButton, constraints);
    }

    private void createWindow() {
        top = new JPanel();
        bottom = new JPanel();
        table = new Table(tableSize);

        mainPanel.setLayout(new GridBagLayout());
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
        mainPanel.add(top, c);

        c.gridx = 0;
        c.gridy = 1;
        c.weighty = 6;
        mainPanel.add(table, c);

        c.gridx = 0;
        c.gridy = 2;
        c.weighty = 1;
        mainPanel.add(bottom, c);
        System.out.println("uj ablak");
    }

    private void addListeners() {
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                System.out.println("start");
                algorithmChooserEnabled = false;
                sizeChooserEnabled = false;
                startButtonEnabled = false;
                pauseOrResume = true;
                pauseButtonEnabled = true;
                stopButtonEnabled = true;
                validateControls();
                table.setListenersEnabled(false);
                AbstractAlgorithm.setDelay(MAX_SPEED - speedSlider.getValue());

                String choice = (String) algorithmChooser.getSelectedItem();
                if (choice.equals("Moore")) {
                    algotihm = new Moore(table);
                } else {
                    algotihm = new AStar(table);
                }

                algotihm.start();
            }
        });

        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("stop");
                algorithmChooserEnabled = true;
                sizeChooserEnabled = true;
                startButtonEnabled = false;
                pauseOrResume = true;
                stopButtonEnabled = false;
                pauseButtonEnabled = false;
                validateControls();
                table.setListenersEnabled(true);

                algotihm.stop();
            }
        });

        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (pauseOrResume) {
                    pauseOrResume = false;
                    algotihm.suspend();
                } else {
                    pauseOrResume = true;
                    algotihm.resume();
                }
                validateControls();
            }
        });

        speedSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                AbstractAlgorithm.setDelay(MAX_SPEED - speedSlider.getValue());
            }
        });

        sizeChooser.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (startButtonEnabled) {
                    tableSize = (int) sizeChooser.getValue();
                    mainPanel.removeAll();
                    createWindow();
                    addListeners();
                    revalidate();
                    System.out.println("spinner");
                }
            }
        });
    }

    Main() {
        mainPanel = getContentPane();
        createWindow();
        addListeners();
        this.setContentPane(mainPanel);
    }

    private void validateControls() {
        algorithmChooser.setEnabled(algorithmChooserEnabled);
        sizeChooser.setEnabled(sizeChooserEnabled);
        startButton.setEnabled(startButtonEnabled);
        System.out.println("csere");
        pauseButton.setEnabled(pauseButtonEnabled);
        if (pauseOrResume) {
            pauseButton.setText("Pause");
        } else {
            pauseButton.setText("Resume");
        }
        stopButton.setEnabled(stopButtonEnabled);
    }

    public static void main(String[] args) {
        Main MainWindow = new Main();
        MainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        MainWindow.setBounds(WINDOW_OFFSET_X, WINDOW_OFFSET_Y, WINDOW_WIDTH, WINDOW_HEIGHT);
        MainWindow.setTitle(TITLE);
        MainWindow.setVisible(true);
    }
}
