//@Author - Taranpreet Singh
import SudokuProject.SudokuFrame.*;

public class Sudoku {

    // Entry point
    public static void main(String[] args) {
        try {
            new SudokuFrame();
        } catch (Exception e) {
            System.out.println("Exception Occurred");
        }
    }
}