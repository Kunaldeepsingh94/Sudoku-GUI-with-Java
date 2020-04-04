//@Author - Kunal
package SudokuProject.SudokuGenerator;

import java.util.Random;

public class SudokuGenerator {

    int[][] board = new int[9][9];
    int row, col;
    String choice;

    private int[][] solvedBoard = new int[9][9];

    Random rand = new Random();

    // Generates a valid sudoku board
    private void generate() {
        int temp = 1;
        int temp1 = 1;
        for (row = 0; row < 9; row++) {
            temp = temp1;
            for (col = 0; col < 9; col++) {
                if (temp <= 9) {
                    board[row][col] = temp++;
                } else {
                    temp = 1;
                    board[row][col] = temp++;
                }
            }
            temp1 = temp + 3;
            if (temp == 10) {
                temp1 = 4;
            }
            if (temp1 > 9) {
                temp1 = (temp1 % 9) + 1;
            }
        }
    }

    // Randomizing time
    private void randomizing() {
        int interchangeIndex1;
        int interchangeIndex2;
        int max = 2;// We gonna randomize 2 rows
        int min = 0;// or columns

        // 3 sub groups so loop goes brr 3 times
        for (int i = 0; i < 3; i++) {
            interchangeIndex1 = rand.nextInt(max - min + 1) + min;// Index of first row to be interchanged is selected

            do {
                interchangeIndex2 = rand.nextInt(max - min + 1) + min;// Index of second row to be interchanged is
                                                                      // selected
            } while (interchangeIndex1 == interchangeIndex2);// so that both the indices dont turn out to be same

            max += 3;
            min += 3;
            rowExchange(interchangeIndex1, interchangeIndex2);
            columnExchange(interchangeIndex1, interchangeIndex2);
        }
    }

    // Swippity swap
    private void columnExchange(int interchangeIndex1, int interchangeIndex2) {
        int temp;
        for (row = 0; row < 9; row++) {
            temp = board[row][interchangeIndex1];
            board[row][interchangeIndex1] = board[row][interchangeIndex2];
            board[row][interchangeIndex2] = temp;
        }
    }

    // Swippity swap part2
    private void rowExchange(int interchangeIndex1, int interchangeIndex2) {
        int temp;
        for (col = 0; col < 9; col++) {
            temp = board[interchangeIndex1][col];
            board[interchangeIndex1][col] = board[interchangeIndex2][col];
            board[interchangeIndex2][col] = temp;
        }
    }

    // More row randomization
    private void rowExchangeOnceMore(int interchangeIndex1, int interchangeIndex2) {
        int temp;
        for (int i = 1; i <= 3; i++) {
            for (int j = 0; j < 9; j++) {
                temp = board[interchangeIndex1][j];
                board[interchangeIndex1][j] = board[interchangeIndex2][j];
                board[interchangeIndex2][j] = temp;
            }
            interchangeIndex1++;
            interchangeIndex2++;
        }
    }

    // More column randomization
    private void columnExchangeMore(int interchangeIndex1, int interchangeIndex2) {
        int temp;
        for (int i = 1; i <= 3; i++) {
            for (int j = 0; j < 9; j++) {
                temp = board[j][interchangeIndex1];
                board[j][interchangeIndex1] = board[j][interchangeIndex2];
                board[j][interchangeIndex2] = temp;
            }
            interchangeIndex1++;
            interchangeIndex2++;
        }
    }

    //Time to yeet some numbers
    private void numberRemover(String choice) {
        int randomCol = 0;
        int max = 8;
        int min = 0;
        switch (choice) {
            case "easy": {
                for (int counter = 0; counter < 8; counter++) {
                    for (row = 0; row < 9; row++) {
                        randomCol = rand.nextInt(max - min + 1) + min;
                        if (board[row][randomCol] != 0) {
                            board[row][randomCol] = 0;
                        } else {
                            continue;
                        }
                    }
                }
            }
                break;

            case "medium": {
                for (int counter = 0; counter < 9; counter++) {
                    for (row = 0; row < 9; row++) {
                        randomCol = rand.nextInt(max - min + 1) + min;
                        if (board[row][randomCol] != 0) {
                            board[row][randomCol] = 0;
                        } else {
                            continue;
                        }
                    }
                }
            }
                break;

            case "hard": {
                for (int counter = 0; counter < 10; counter++) {
                    for (row = 0; row < 9; row++) {
                        randomCol = rand.nextInt(max - min + 1) + min;
                        if (board[row][randomCol] != 0) {
                            board[row][randomCol] = 0;
                        } else {
                            continue;
                        }
                    }
                }
            }
                break;

            default:
                System.out.println("Invalid Option");
        }
    }

    public int[][] returnBoard(String difficulty) {
        int[] randArr = { 0, 3, 6 };
        int rand1 = 0;
        int rand2 = 0;

        generate();
        randomizing();

        // Randomly Randomly selects indices to be shuffled
        for (int i = 0; i < 2; i++) {
            rand1 = randArr[rand.nextInt(randArr.length)];
            do {
                rand2 = randArr[rand.nextInt(randArr.length)];
            } while (rand1 == rand2);

            if (i == 0) {
                rowExchangeOnceMore(rand1, rand2);
            } else {
                columnExchangeMore(rand1, rand2);
            }
        }

        for (row = 0; row < 9; row++) {
            for (col = 0; col < 9; col++) {
                solvedBoard[row][col] = board[row][col];
            }
        }

        numberRemover(difficulty);
        return board;
    }

    public int[][] returnSolvedBoard() {
        return solvedBoard;
    }
}
