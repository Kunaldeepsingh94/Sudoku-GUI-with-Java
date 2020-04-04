//@Author - Taranpreet Singh
package SudokuProject.SudokuFrame;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.plaf.metal.MetalButtonUI;

import java.awt.*;
import java.awt.event.*;
import java.util.Random;

import SudokuProject.SudokuGenerator.SudokuGenerator;

public class SudokuFrame extends JFrame implements ActionListener, KeyListener {

    //Lots of declarations, skip if you want
    JFrame frame = new JFrame("Sudoku");
    JPanel panel = new JPanel();
    JMenuBar menuBar = new JMenuBar();
    JToggleButton[] cells = new JToggleButton[81];
    JToggleButton selectedButton = new JToggleButton();
    ButtonGroup group = new ButtonGroup(); // All the 81 cells are grouped under this. This prevents more than one
                                           // button to be toggled at once
    SudokuGenerator generator = new SudokuGenerator();

    int[][] board = new int[9][9];
    int[][] originalBoard = new int[9][9]; // Used to reset the board
    int[][] solvedBoard = new int[9][9];
    int index;
    //Lots of declarations end

    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    int textSize = (int) screenSize.getWidth() / 80;
    boolean firstTime = true;
    int hintCount = 3;
    int hintsTaken = 0;
    JMenuItem hintMenuItem; //Had to increase this one's scope to the whole class, as everyone needed to disable/enable this

    public SudokuFrame() {

        String rules;

        // Pop up menu starts
        rules = "The only way the puzzle can be considered solved correctly is when \n1. All the white boxes are filled\n2. Each row, column and box must contain all of the numbers 1 through 9\n3. Two numbers in the same row, column or box cannot be the same\n\nStart the game by choosing a difficulty";

        String[] difficulty = { "Easy", "Medium", "Hard" };
        int choice = JOptionPane.showOptionDialog(null, rules, "Rules", JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE, null, difficulty, difficulty[0]);

        switch (choice) {
            case 0:
                board = generator.returnBoard("easy");
                break;
            case 1:
                board = generator.returnBoard("medium");
                break;
            case 2:
                board = generator.returnBoard("hard");
                break;

            default:
                board = generator.returnBoard("easy");
        }
        // Pop up menu ends

        solvedBoard = generator.returnSolvedBoard();

        // Loop to put buttons in the grid
        for (int x = 0; x < 81; x++) {
            cells[x] = new JToggleButton();
            cells[x].putClientProperty("index", x); // Attaches indices of the values in the matrix to the buttons i.e
                                                    // the first button is linked to (0,0) in the matrix, and last to
                                                    // (8,8)
            cells[x].setBackground(Color.lightGray);
            cells[x].setBorder(BorderFactory.createLineBorder(Color.BLACK, 0));
            cells[x].setFont(new Font("Arial", Font.PLAIN, textSize));
            group.add(cells[x]);
            cells[x].addActionListener(this);
            panel.add(cells[x]);
        }

        panel.setLayout(new GridLayout(9, 9, 2, 2));
        panel.setBackground(Color.black);

        // Stores the original board, used for resetting the board
        copyMatricesByValue(originalBoard, board);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Menu begins
        JMenu gameMenu = new JMenu("Game");
        JMenu helpMenu = new JMenu("Help");
        JMenu newGameMenu = new JMenu("New Game");

        JMenuItem easyDifficultyMenuItem = new JMenuItem(new AbstractAction("Easy") {
            public void actionPerformed(ActionEvent e) {
                startNewGame("easy");
            }
        });
        JMenuItem mediumDifficultyMenuItem = new JMenuItem(new AbstractAction("Medium") {
            public void actionPerformed(ActionEvent e) {
                startNewGame("medium");
            }
        });
        JMenuItem hardDifficultyMenuItem = new JMenuItem(new AbstractAction("Hard") {
            public void actionPerformed(ActionEvent e) {
                startNewGame("hard");
            }
        });

        newGameMenu.add(easyDifficultyMenuItem);
        newGameMenu.add(mediumDifficultyMenuItem);
        newGameMenu.add(hardDifficultyMenuItem);

        JMenuItem resetMenuItem = new JMenuItem(new AbstractAction("Reset") {
            public void actionPerformed(ActionEvent e) {
                hintCount = 3;
                hintsTaken = 0;
                setButtonsToDefault();
                printBoard(originalBoard);
                copyMatricesByValue(board, originalBoard);
                hintMenuItem.setEnabled(true);
            }
        });
        hintMenuItem = new JMenuItem(new AbstractAction("Hint") {
            public void actionPerformed(ActionEvent e) {

                if (!isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Board is full"); // If board is full, shows pop up menu
                } else {
                    if (hintsTaken >= hintCount) {
                        JOptionPane.showMessageDialog(null, "Cannot take more than 3 hints in one game"); // Checks if the user has already taken 3 hints or not                                                                                                          
                        hintMenuItem.setEnabled(false);
                    } else {
                        while (true) {

                            // Hits a random index
                            Random random = new Random();
                            int randomIndex = random.nextInt(81);
                            int row = randomIndex / 9;
                            int column = randomIndex % 9;

                            // Checks if random index selected is empty
                            if (board[row][column] == 0) {
                                board[row][column] = solvedBoard[row][column]; // Gets value of the empty cell
                                Integer number = (Integer) board[row][column]; // Some typecasting shit.
                                cells[randomIndex].setEnabled(false); // Makes the button uneditable by user
                                cells[randomIndex].setUI(new MetalButtonUI() {
                                    protected Color getDisabledTextColor() {
                                        return Color.BLACK; // Had to make this to change UI of disabled buttons because
                                                            // pre defined swing methods don't work idk why.
                                    }
                                });

                                cells[randomIndex].setText(Integer.toString(number)); // Puts the correct answer on the
                                                                                      // button
                                if (isValid(row, column, board[row][column]) && !isEmpty() && isBoardCorrect()) {
                                    puzzleSolved(); // Checks if after hint taken, the board is solved. If it is,
                                                    // displays a pop-up with some options
                                }
                                hintsTaken++;
                                break;
                            }
                        }
                    }
                }
            }
        });

        JMenuItem solveMenuItem = new JMenuItem(new AbstractAction("Solve") {
            public void actionPerformed(ActionEvent e) {
                // Resets the borders to black
                for (int i = 0; i < 81; i++) {
                    cells[i].setBorder(BorderFactory.createLineBorder(Color.BLACK, 0));
                }

                copyMatricesByValue(board, solvedBoard);
                printBoard(board);
                hintMenuItem.setEnabled(false); // Disables hint menu after board is solved
            }
        });

        // Adding JMenuItems and JMenu to JMenu
        gameMenu.add(newGameMenu);
        gameMenu.add(resetMenuItem);
        helpMenu.add(hintMenuItem);
        helpMenu.add(solveMenuItem);

        // Adding JMenu to JMenuBar
        menuBar.add(gameMenu);
        menuBar.add(helpMenu);
        // Menu ends

        // Prints the board matrix to GUI
        printBoard(board);

        // Adding the panel and menu to the frame
        frame.setJMenuBar(menuBar);
        frame.pack();
        frame.add(panel);
        frame.pack();
        frame.setSize(600, 600);

        // Centers the window
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
        frame.setLocation(x, y);

        frame.setVisible(true);
    }

    // Checks if any numbers in the board are not repeated
    private boolean isBoardCorrect() {
        boolean isGreen = false;

        // Checks the borders of filled cells. Returns false if even one has red border.
        // Returns true if all filled buttons have green borders. False if any other color
        for (int i = 0; i < 81; i++) {
            LineBorder border = (LineBorder) cells[i].getBorder();
            if (border.getLineColor() == Color.RED) {
                return false;
            } else if (border.getLineColor() == Color.GREEN) {
                isGreen = true;
            }
        }
        if (isGreen) {
            return true;
        } else {
            return false;
        }

    }

    // Generates new board and resets hint count
    private void startNewGame(String difficulty) {
        hintCount = 3;
        hintsTaken = 0;
        setButtonsToDefault();
        board = generator.returnBoard(difficulty);
        printBoard(board);
        copyMatricesByValue(originalBoard, board);
        hintMenuItem.setEnabled(true);
    }

    // Sets default values for background and borders for all cells
    private void setButtonsToDefault() {
        for (int i = 0; i < 81; i++) {
            cells[i].setBackground(Color.lightGray);
            cells[i].setBorder(BorderFactory.createLineBorder(Color.BLACK, 0));
        }
    }

    // Pretty self explanatory
    private void copyMatricesByValue(int a[][], int b[][]) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                a[i][j] = b[i][j];
            }
        }
    }

    // Listener for cells
    public void actionPerformed(ActionEvent e) {
        selectedButton = (JToggleButton) e.getSource();
        index = (int) selectedButton.getClientProperty("index");
        selectedButton.addKeyListener(this);
    }

    // Checks if a number put in the board is valid or not
    private boolean isValid(int row, int column, int number) {

        // Checks repetition in rows
        for (int c = 0; c < 9; c++) {
            if (board[row][c] == number && c != column) {
                return false;
            }
        }

        // Checks repetition in columns
        for (int r = 0; r < 9; r++) {
            if (board[r][column] == number && r != row) {
                return false;
            }
        }

        // Makes the index divisble by 3, hence knowing the box's starting indices
        int boxRowStart = row - row % 3;
        int boxColStart = column - column % 3;

        // Checks repetition in box
        for (int r = boxRowStart; r < boxRowStart + 3; r++) {
            for (int c = boxColStart; c < boxColStart + 3; c++) {
                if (board[r][c] == number && r != row && c != column) {
                    return false;
                }
            }
        }
        return true;
    }

    // Again, pretty self explanatory
    private boolean isEmpty() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (board[i][j] == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    // Listeners for keys start
    public void keyPressed(KeyEvent e) {
        Integer digit;
        Integer r, c;
        r = index / 9;
        c = index % 9;
        digit = Integer.valueOf(e.getKeyCode() - 48); // Converts keyCode to digits to put into the board

        if (digit > 0 && digit < 10) {
            board[r][c] = digit;
        }

        String s = digit.toString();

        // Checks if key entered by user is digit, sets the cell text and gives it a
        // red/green border depending on validity of the key.
        if (e.getKeyCode() >= 49 && e.getKeyCode() <= 57) {
            selectedButton.setText(s);
            if (!isValid(r, c, digit)) {
                selectedButton.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
            } else {
                selectedButton.setBorder(BorderFactory.createLineBorder(Color.GREEN, 2));
            } // Checks if key entered is backspace/delete/escape, and removes the digit entered in selected cell
        } else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE || e.getKeyCode() == KeyEvent.VK_DELETE 
                || e.getKeyCode() == KeyEvent.VK_ESCAPE) { 
            board[r][c] = 0;
            selectedButton.setText("");
            selectedButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 0)); // Changes the border of the cell
                                                                                      // back to black.
            if (!hintMenuItem.isEnabled()) {
                hintMenuItem.setEnabled(true);
            }
        }

        int i = (int) selectedButton.getClientProperty("index");
        int row = i / 9;
        int column = i % 9;

        // Checks if puzzle is solved. If it is, displays a pop up menu with some options
        if (isValid(row, column, board[row][column]) && !isEmpty() && isBoardCorrect()) {
            puzzleSolved();
        }

        // Checks if board is full(NOT SOLVED), disables the hintMenuItem
        if (!isEmpty()) {
            hintMenuItem.setEnabled(false);
        }
    }

    // Displays a pop up menu with options to replay in easy, medium or hard, or to
    // exit the game
    private void puzzleSolved() {
        String[] options = { "Easy", "Medium", "Hard", "Exit" };
        int option = JOptionPane.showOptionDialog(null, "Sudoku Completed",
                "Congratulations! You have solved the puzzle. Want to replay?", JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE, null, options, options[3]);

        switch (option) {
            case 0:
                startNewGame("easy");
                break;
            case 1:
                startNewGame("medium");
                break;
            case 2:
                startNewGame("hard");
                break;
            case 3:
                System.exit(0);

            default:
                System.exit(0);
        }
    }

    // Deselects button on key release
    public void keyReleased(KeyEvent e) {
        group.clearSelection();
        selectedButton.removeKeyListener(this);
    }

    // I don't understand what this does either. I had to add this because 
    // I implemented KeyListener, and Java won't leave me alone
    public void keyTyped(KeyEvent e) {
    }
    // Listeners for keys end.


    // Prints numbers onto the cells in GUI.
    public void printBoard(int[][] board) {
        for (int x = 0; x < 81; x++) {
            cells[x].setEnabled(true);
        }
        Integer digit;
        String s;

        int buttonIndex = 0;

        // Iterates through board matrix
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                digit = Integer.valueOf(board[i][j]);
                if (digit == 0) {
                    cells[buttonIndex].setText(""); // Sets empty string on cell if digit = 0, i.e empty cell
                    cells[buttonIndex].setBackground(Color.white); // Sets empty cells white
                    buttonIndex++;
                    continue;
                } else if (digit != 0) {
                    s = digit.toString();
                    cells[buttonIndex].setFont(new Font("Arial", Font.PLAIN, textSize)); // Sets font
                    cells[buttonIndex].setText(s);
                    cells[buttonIndex].setEnabled(false); // Disables button from user input
                    cells[buttonIndex].setUI(new MetalButtonUI() { // Again, UI of disabled buttons can't be changed normally
                                                                   // because again, swing shit
                        protected Color getDisabledTextColor() {
                            return Color.BLACK;
                        }
                    });
                    if (cells[buttonIndex].getBackground() == Color.lightGray) {
                        cells[buttonIndex].setFont(new Font("Arial", Font.BOLD, textSize)); // Bolds the prefilled numbers
                                                                                            
                    } else {
                        cells[buttonIndex].setFont(new Font("Arial", Font.PLAIN, textSize));                                     
                    }
                    buttonIndex++;
                }
            }
        }
    }
}
//Thanks for coming to this shit-show. 