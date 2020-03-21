package seedu.address.ui;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import seedu.address.commons.core.LogsCenter;
import seedu.address.model.internship.InternshipApplication;
import seedu.address.model.statistics.Statistics;
import seedu.address.model.status.Status;

import java.text.DecimalFormat;
import java.util.logging.Logger;

/**
 * Controller for the statistics page
 */
public class StatisticsWindow extends UiPart<Stage> {

    private static final Logger logger = LogsCenter.getLogger(StatisticsWindow.class);
    private static final String FXML = "StatisticsWindow.fxml";
    private final DecimalFormat df = new DecimalFormat("###.##");

    @FXML
    private BarChart<String, Integer> internshipApplicationChart;
    @FXML
    private CategoryAxis status;
    @FXML
    private NumberAxis count;
    @FXML
    private PieChart internshipApplicationPie;

    /**
     * Creates a new StatisticsWindow.
     *
     * @param root Stage to use as the root of the StatisticsWindow.
     */
    public StatisticsWindow(Stage root, Statistics statistics,
                            ObservableList<InternshipApplication> internshipApplicationList) {
        super(FXML, root);
        bindStatistics(statistics, internshipApplicationList);
        updateStatisticsOnChange(statistics, internshipApplicationList);
    }

    /**
     * Creates a new StatisticsWindow.
     */
    public StatisticsWindow(Statistics statistics, ObservableList<InternshipApplication> internshipApplicationList) {
        this(new Stage(), statistics, internshipApplicationList);
    }

    /**
     * Adds an event listener to update the statistics upon any changes in the given list of internship application.
     * @param statistics
     * @param internshipApplicationList
     */
    public void updateStatisticsOnChange(Statistics statistics,
                                         ObservableList<InternshipApplication> internshipApplicationList) {
        internshipApplicationList.addListener((ListChangeListener<InternshipApplication>) c -> {
            while (c.next()) {
                if (c.wasAdded() || c.wasRemoved() || c.wasUpdated() || c.wasReplaced()) {
                    bindStatistics(statistics, internshipApplicationList);
                }
            }
        });
    }

    /**
     * Computes and binds the statistics to the user interface.
     * @param statistics
     * @param internshipApplicationList
     */
    public void bindStatistics(Statistics statistics, ObservableList<InternshipApplication> internshipApplicationList) {
        internshipApplicationChart.getData().clear();
        statistics.computeAndUpdateStatistics(internshipApplicationList);
//        status.setLabel("Status");
//        count.setLabel("Count");
        loadBarChart(statistics);
        loadPieChart(statistics);
    }

    public void loadBarChart(Statistics statistics) {
        // issue: color changes, and there is transition upon re-rendering
        // one solution can be to generate new Xaxis, Yaxis, and barchart each time, instead of reusing
        ObservableList<XYChart.Data> xyChartData = FXCollections.observableArrayList(
                new XYChart.Data(Status.WISHLIST.toString(), statistics.getWishlistCount()),
                new XYChart.Data(Status.APPLIED.toString(), statistics.getAppliedCount()),
                new XYChart.Data(Status.INTERVIEW.toString(), statistics.getInterviewCount()),
                new XYChart.Data(Status.OFFERED.toString(), statistics.getOfferedCount()),
                new XYChart.Data(Status.REJECTED.toString(), statistics.getRejectedCount())
        );
        ObservableList<XYChart.Series<String, Integer>> series = FXCollections.observableArrayList(
                new XYChart.Series(xyChartData)
        );
        internshipApplicationChart.setLegendVisible(false);
        internshipApplicationChart.setData(series);
    }

    public void loadPieChart(Statistics statistics) {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data(Status.WISHLIST.toString(), statistics.getWishlistPercentage()),
                new PieChart.Data(Status.APPLIED.toString(), statistics.getAppliedPercentage()),
                new PieChart.Data(Status.INTERVIEW.toString(), statistics.getInterviewPercentage()),
                new PieChart.Data(Status.OFFERED.toString(), statistics.getOfferedPercentage()),
                new PieChart.Data(Status.REJECTED.toString(), statistics.getRejectedPercentage())
        );
        pieChartData.forEach(data ->
                data.nameProperty().bind(
                        Bindings.concat(String.format("%s (%.2f%%)", data.getName(), data.getPieValue()))
                )
        );
        internshipApplicationPie.setData(pieChartData);
    }

    /**
     * Shows the statistics window.
     * @throws IllegalStateException
     * <ul>
     *     <li>
     *         if this method is called on a thread other than the JavaFX Application Thread.
     *     </li>
     *     <li>
     *         if this method is called during animation or layout processing.
     *     </li>
     *     <li>
     *         if this method is called on the primary stage.
     *     </li>
     *     <li>
     *         if {@code dialogStage} is already showing.
     *     </li>
     * </ul>
     */
    public void show() {
        logger.fine("Generating statistics about your internship applications.");
        getRoot().show();
        getRoot().centerOnScreen();
    }

    /**
     * Returns true if the statistics window is currently being shown.
     */
    public boolean isShowing() {
        return getRoot().isShowing();
    }

    /**
     * Hides the statistics window.
     */
    public void hide() {
        getRoot().hide();
    }

    /**
     * Focuses on the statistics window.
     */
    public void focus() {
        getRoot().requestFocus();
    }

}
