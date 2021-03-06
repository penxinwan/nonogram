package eu.lestard.nonogram.core;

import eu.lestard.grid.Cell;
import eu.lestard.grid.GridModel;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Represents the current "state" of the game. It can answer questions like
 * 'Which fields are already revealed?' , "Which are marked?" or "How many errors does the user made?".
 */
public class GameInstance {

    private static final int MAX_ERRORS = 5;

    private ReadOnlyIntegerWrapper maxErrors = new ReadOnlyIntegerWrapper(MAX_ERRORS);
    private ReadOnlyIntegerWrapper currentErrors = new ReadOnlyIntegerWrapper(0);


    private Puzzle puzzle;

    private GridModel<State> gridModel = new GridModel<>();

    private ReadOnlyIntegerWrapper errors = new ReadOnlyIntegerWrapper(0);

    private ReadOnlyBooleanWrapper gameOver = new ReadOnlyBooleanWrapper();

    private ReadOnlyBooleanWrapper win = new ReadOnlyBooleanWrapper();

    /**
     * The list of column/row indexes that are already finished correctly.
     */
    private ObservableList<Integer> finishedColumns = FXCollections.observableArrayList();
    private ObservableList<Integer> finishedRows = FXCollections.observableArrayList();
    private boolean isPointInPuzzle;


    public GameInstance(Puzzle puzzle) {
        this.puzzle = puzzle;

        gridModel.setDefaultState(State.EMPTY);

        gridModel.setNumberOfColumns(puzzle.getSize());
        gridModel.setNumberOfRows(puzzle.getSize());
        gameOver.bind(errors.greaterThanOrEqualTo(maxErrors));
    }

    public Puzzle getPuzzle(){
        return puzzle;
    }

    /**
     * Mark the given cell with a single click.
     */
    public void markWithSingleClick(Cell<State> cell) {
        if (cell.getState() == State.EMPTY) {
            cell.changeState(State.MARKED);
        } else if (cell.getState() == State.MARKED) {
            cell.changeState(State.EMPTY);
        }
    }

    /**
     * Mark the cell with mouse over.
     */
    public void markWithMouseOver(Cell<State> cell){
        if(cell.getState() == State.EMPTY){
            cell.changeState(State.MARKED);
        }
    }

    /**
     * Reveal the cell with the given coordinates.
     *
     * This method is used when a single cell was clicked.
     * When the user moves the mouse over multiple cells with
     * a pressed mouse button, other rule apply on what happens
     * with the underlying cell state.
     * <br>
     * This is done by {@link #revealWithMouseOver(eu.lestard.grid.Cell)}.
     */
    public void revealWithSingleClick(Cell<State> cell) {
        if (cell.getState() == State.FILLED) {
            return;
        }

        if(cell.getState() == State.ERROR){
            return;
        }

        if (cell.getState() == State.MARKED) {
            cell.changeState(State.EMPTY);
            return;
        }

        if (puzzle.isPoint(cell.getColumn(), cell.getRow())) {
            cell.changeState(State.FILLED);

            checkNumberBlocks(cell.getColumn(), cell.getRow());
        } else {
            errors.set(errors.get() + 1);
            cell.changeState(State.ERROR);
        }
    }

    /**
     * Reveal the cell with the given coordinates.
     *
     * This method is used when the user moves with pressed mousebutton
     * over the cells.
     * <br>
     * When clicks on a single cell then
     * {@link #revealWithSingleClick(eu.lestard.grid.Cell)} is used.
     */
    public void revealWithMouseOver(Cell<State> cell){
        if(cell.getState() == State.EMPTY){
            if(puzzle.isPoint(cell.getColumn(), cell.getRow())){
                cell.changeState(State.FILLED);

                checkNumberBlocks(cell.getColumn(),cell.getRow());
            }else{
                errors.set(errors.get() + 1);
                cell.changeState(State.ERROR);
            }
        }
    }

    private void checkNumberBlocks(int column, int row) {
        checkColumnNumberBlocks(column);
        checkRowNumberBlocks(row);

        if(finishedColumns.size() == puzzle.getSize() && finishedRows.size() == puzzle.getSize()){
            win.set(true);
        }
    }

    private void checkRowNumberBlocks(int row){
        for (int i = 0; i < puzzle.getSize(); i++) {
            final boolean isRevealed = gridModel.getCell(i, row).getState() == State.FILLED;
            isPointInPuzzle = puzzle.isPoint(i, row);

            if (isRevealed != isPointInPuzzle) {
                return;
            }
        }

        finishedRows.add(row);
    }

    private void checkColumnNumberBlocks(int column){
        for (int i = 0; i < puzzle.getSize(); i++) {
            final boolean isRevealed = gridModel.getCell(column, i).getState() == State.FILLED;
            isPointInPuzzle = puzzle.isPoint(column, i);

            if (isRevealed != isPointInPuzzle) {
                return;
            }
        }

        finishedColumns.add(column);
    }

    public GridModel<State> getGridModel() {
        return gridModel;
    }

    public ObservableList<Integer> finishedColumnsList() {
        return finishedColumns;
    }

    public ObservableList<Integer> finishedRowsList() {
        return finishedRows;
    }

    public ReadOnlyIntegerProperty errors() {
        return errors.getReadOnlyProperty();
    }

    public ReadOnlyIntegerProperty maxErrors() {
        return maxErrors.getReadOnlyProperty();
    }

    public ReadOnlyBooleanProperty gameOver() {
        return gameOver.getReadOnlyProperty();
    }

    public ReadOnlyBooleanProperty win() {
        return win.getReadOnlyProperty();
    }
}
