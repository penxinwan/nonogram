package eu.lestard.nonogram.puzzle;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import eu.lestard.grid.GridView;
import eu.lestard.nonogram.core.Numbers;
import eu.lestard.nonogram.core.State;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.value.ObservableDoubleValue;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class PuzzleView implements FxmlView<PuzzleViewModel> {

    @FXML
    private AnchorPane centerPane;

    @FXML
    private AnchorPane overviewPane;

    @FXML
    private AnchorPane topNumberPane;

    @FXML
    private AnchorPane leftNumberPane;

    @FXML
    private AnchorPane rootPane;

    @InjectViewModel
    private PuzzleViewModel viewModel;


    public void initialize(){
        initLayout();

        initCenterGrid();

        initTopNumberGrid();

        initLeftNumberGrid();

        initOverviewGrid();
    }

    private void initOverviewGrid() {
        GridView<State> overviewGridView = new GridView<>();
        overviewGridView.setGridModel(viewModel.getOverviewGridModel());

        overviewPane.getChildren().add(overviewGridView);

        overviewGridView.addColorMapping(State.FILLED, Color.BLACK);

        initAnchor(overviewGridView);
    }


    private void initLeftNumberGrid() {
        GridView<Numbers> leftNumberGridView = new GridView<>();
        leftNumberGridView.setGridModel(viewModel.getLeftNumberGridModel());
        leftNumberPane.getChildren().add(leftNumberGridView);

        initNumberGridMapping(leftNumberGridView);

        initAnchor(leftNumberGridView);
    }

    private void initNumberGridMapping(GridView<Numbers> gridView){

        for (Numbers numbers : Numbers.values()) {
            gridView.addNodeMapping(numbers, cell -> {
                if(!Numbers.EMPTY.equals(cell.getState())){
                    return new Label(Integer.toString(cell.getState().getNumber()));
                }else{
                    return new Label();
                }
            });
        }
    }

    private void initTopNumberGrid() {
        GridView<Numbers> topNumberGridView = new GridView<>();
        topNumberGridView.setGridModel(viewModel.getTopNumberGridModel());
        topNumberPane.getChildren().add(topNumberGridView);

        initNumberGridMapping(topNumberGridView);
        initAnchor(topNumberGridView);
    }

    private void initCenterGrid() {
        GridView<State> centerGridView = new GridView<>();
        centerGridView.setGridModel(viewModel.getCenterGridModel());

        centerGridView.addColorMapping(State.EMPTY, Color.WHITE);

        centerGridView.addColorMapping(State.FILLED, Color.BLACK);

        centerGridView.addNodeMapping(State.ERROR, cell -> new Cross(Color.RED));

        centerGridView.addNodeMapping(State.MARKED, cell -> new Cross());

        centerPane.getChildren().add(centerGridView);
        initAnchor(centerGridView);
    }

    private void initAnchor(Node node){
        AnchorPane.setBottomAnchor(node, 0.0);
        AnchorPane.setTopAnchor(node, 0.0);
        AnchorPane.setLeftAnchor(node, 0.0);
        AnchorPane.setRightAnchor(node, 0.0);
    }



    private void initLayout() {
        rootPane.setStyle("-fx-background-color:black");

        double anchorMargin = 1;

        final DoubleBinding oneThirdsWidth = rootPane.widthProperty().subtract(anchorMargin*4).divide(3);
        final DoubleBinding oneThirdsHeight = rootPane.heightProperty().subtract(anchorMargin * 4).divide(3);

        final DoubleBinding twoThirdsWidth = oneThirdsWidth.multiply(2);
        final DoubleBinding twoThirdsHeight = oneThirdsHeight.multiply(2);

        bindWidth(overviewPane, oneThirdsWidth);
        bindHeight(overviewPane, oneThirdsHeight);

        bindWidth(topNumberPane, twoThirdsWidth);
        bindHeight(topNumberPane, oneThirdsHeight);

        bindWidth(leftNumberPane, oneThirdsWidth);
        bindHeight(leftNumberPane, twoThirdsHeight);

        bindWidth(centerPane, twoThirdsWidth);
        bindHeight(centerPane, twoThirdsHeight);



        AnchorPane.setTopAnchor(overviewPane, anchorMargin);
        AnchorPane.setLeftAnchor(overviewPane, anchorMargin);

        AnchorPane.setTopAnchor(topNumberPane, anchorMargin);
        AnchorPane.setRightAnchor(topNumberPane, anchorMargin);

        AnchorPane.setBottomAnchor(leftNumberPane, anchorMargin);
        AnchorPane.setLeftAnchor(leftNumberPane, anchorMargin);

        AnchorPane.setBottomAnchor(centerPane,anchorMargin);
        AnchorPane.setRightAnchor(centerPane,anchorMargin);
    }

    private void bindWidth(Pane pane, ObservableDoubleValue width){
        pane.minWidthProperty().bind(width);
        pane.maxWidthProperty().bind(width);
    }

    private void bindHeight(Pane pane, ObservableDoubleValue height){
        pane.minHeightProperty().bind(height);
        pane.maxHeightProperty().bind(height);
    }

}
