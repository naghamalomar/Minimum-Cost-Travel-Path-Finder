package application;

import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class HelloController {
	private BorderPane root = new BorderPane();
	private TextField filenameField = new TextField();
	private Button browseButton = new Button("📂 Browse File");
	private Button startButton = new Button("🚀 Start Trip");
	private Label optimalPathLabel = new Label("Best Path: ");
	private Label costLabel = new Label("Total Cost: ");
	private Label altLabel = new Label("Alternatives: ");

	private TableView<TableRow> dpTableView = new TableView<>();
	private TableView<TableRow> pathTableView = new TableView<>();
	private File selectedFile;

	public Scene createScene(Stage stage) {
		stage.getIcons().add(new Image(getClass().getResourceAsStream("/icons8-distance-64.png")));

		Label title = new Label("🔍 Minimum Cost Travel Path Finder");
		title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
		title.setPadding(new Insets(20));
		title.setAlignment(Pos.CENTER);

		filenameField.setPromptText("No file selected...");
		filenameField.setEditable(false);
		filenameField.setMaxWidth(300);

		browseButton.setOnAction(e -> {
			FileChooser fileChooser = new FileChooser();
			fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
			selectedFile = fileChooser.showOpenDialog(stage);
			if (selectedFile != null) {
				filenameField.setText(selectedFile.getAbsolutePath());
			}
		});

		startButton.setOnAction(e -> {
			if (selectedFile != null) {
				try {
					PathFinder finder = new PathFinder();
					finder.loadFromFile(selectedFile);
					finder.calculateDP();

					setOptimalPath(finder.getBestPath());
					setCost(finder.getCost());
					setAlternatives(finder.getAllAlternativePaths());
					setDPTable(finder.getDPTable());
					setPathTable(finder.getPathTable());
				} catch (Exception ex) {
					Alert alert = new Alert(Alert.AlertType.ERROR);
					alert.setTitle("Error");
					alert.setHeaderText("Processing Error");
					alert.setContentText("Something went wrong:\n" + ex.getMessage());
					alert.showAndWait();
				}
			}
		});

		HBox fileControls = new HBox(10, browseButton, filenameField, startButton);
		fileControls.setAlignment(Pos.CENTER);
		fileControls.setPadding(new Insets(10));

		TabPane tabPane = new TabPane();
		tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

		VBox pathBox = new VBox(10, optimalPathLabel, costLabel);
		pathBox.setPadding(new Insets(10));
		Tab tab1 = new Tab("🛣️ Optimal Path", pathBox);

		Tab tab2 = new Tab("🔁 Alternatives", altLabel);

		Tab tab3 = new Tab("🧮 DP Table", new ScrollPane(dpTableView));
		Tab tab4 = new Tab("📊 Path Table", new ScrollPane(pathTableView));

		tabPane.getTabs().addAll(tab1, tab2, tab3, tab4);

		root.setTop(title);
		root.setCenter(tabPane);
		root.setBottom(fileControls);

		Scene scene = new Scene(root, 900, 600);
		scene.getStylesheets().add(getClass().getResource("/Styles.css").toExternalForm());

		stage.setScene(scene);
		stage.setMaximized(true);
		stage.show();

		return scene;
	}

	public void setOptimalPath(String path) {
		this.optimalPathLabel.setText("Best Path: " + path);
	}

	public void setCost(int cost) {
		this.costLabel.setText("Total Cost: " + cost);
	}

	public void setAlternatives(String text) {
		altLabel.setText("Alternatives: " + text); // Place the text in Label
	}

	public void setDPTable(String table) {
		dpTableView.getColumns().clear();
		dpTableView.getItems().clear();

		String[] rows = table.split("\n");
		for (String row : rows) {
			String[] cells = row.split("\t");
			dpTableView.getItems().add(new TableRow(cells));
		}

		for (int i = 0; i < rows[0].split("\t").length; i++) {
			TableColumn<TableRow, String> column = new TableColumn<>("Col " + (i + 1));
			final int columnIndex = i;
			column.setCellValueFactory(cellData -> cellData.getValue().get(columnIndex));
			column.setPrefWidth(90);
			dpTableView.getColumns().add(column);
		}

		dpTableView.setStyle("-fx-font-size: 13px; -fx-cell-size: 35px;");
		dpTableView.setFixedCellSize(35);
		dpTableView.setPrefHeight(500);
		dpTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
	}

	public void setPathTable(String table) {
		pathTableView.getColumns().clear();
		pathTableView.getItems().clear();

		String[] rows = table.split("\n");
		for (String row : rows) {
			String[] cells = row.split("\t");
			pathTableView.getItems().add(new TableRow(cells));
		}

		for (int i = 0; i < rows[0].split("\t").length; i++) {
			TableColumn<TableRow, String> column = new TableColumn<>("Col " + (i + 1));
			final int columnIndex = i;
			column.setCellValueFactory(cellData -> cellData.getValue().get(columnIndex));
			column.setPrefWidth(90);
			pathTableView.getColumns().add(column);
		}

		pathTableView.setStyle("-fx-font-size: 13px; -fx-cell-size: 35px;");
		pathTableView.setFixedCellSize(35);
		pathTableView.setPrefHeight(500);
		pathTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
	}
}

class TableRow {
	private final SimpleStringProperty[] cells;

	public TableRow(String[] cells) {
		this.cells = new SimpleStringProperty[cells.length];
		for (int i = 0; i < cells.length; i++) {
			this.cells[i] = new SimpleStringProperty(cells[i]);
		}
	}

	public SimpleStringProperty get(int index) {
		return cells[index];
	}
}
