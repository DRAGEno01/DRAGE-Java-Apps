import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.GridPane;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.paint.Color;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;
import org.json.JSONArray;

public class DJA extends Application {
    private static final String MARKETPLACE_URL = "https://raw.githubusercontent.com/DRAGEno01/DRAGE-Java-Apps/refs/heads/main/code/apps.json";
    private VBox installedAppsContainer;
    private VBox marketplaceContainer;

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f0f0f0;");

        // Header
        HBox header = createHeader();
        root.setTop(header);

        // Main content
        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(20));

        // Installed Apps Section
        Label installedAppsLabel = new Label("Installed Apps");
        installedAppsLabel.setFont(Font.font("System", FontWeight.BOLD, 20));
        
        installedAppsContainer = new VBox(10);
        ScrollPane installedAppsScroll = new ScrollPane(installedAppsContainer);
        installedAppsScroll.setFitToWidth(true);
        installedAppsScroll.setStyle("-fx-background: #f0f0f0; -fx-background-color: transparent;");

        // Marketplace Section
        Label marketplaceLabel = new Label("Marketplace");
        marketplaceLabel.setFont(Font.font("System", FontWeight.BOLD, 20));
        
        marketplaceContainer = new VBox(10);
        ScrollPane marketplaceScroll = new ScrollPane(marketplaceContainer);
        marketplaceScroll.setFitToWidth(true);
        marketplaceScroll.setStyle("-fx-background: #f0f0f0; -fx-background-color: transparent;");

        mainContent.getChildren().addAll(
            installedAppsLabel, 
            installedAppsScroll,
            marketplaceLabel,
            marketplaceScroll
        );

        root.setCenter(mainContent);

        // Load marketplace apps
        loadMarketplaceApps();

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("DRAGE Java Apps");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private HBox createHeader() {
        HBox header = new HBox();
        header.setStyle("-fx-background-color: #2196f3; -fx-padding: 15px;");
        
        Label title = new Label("DRAGE Java Apps");
        title.setFont(Font.font("System", FontWeight.BOLD, 24));
        title.setTextFill(Color.WHITE);
        
        Button refreshButton = new Button("Refresh Marketplace");
        refreshButton.setStyle("-fx-background-color: white; -fx-text-fill: #2196f3;");
        refreshButton.setOnAction(e -> loadMarketplaceApps());
        
        header.getChildren().addAll(title, refreshButton);
        header.setSpacing(20);
        
        return header;
    }

    private void loadMarketplaceApps() {
        marketplaceContainer.getChildren().clear();
        
        try {
            URL url = new URL(MARKETPLACE_URL);
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuilder jsonContent = new StringBuilder();
            String line;
            
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }
            
            JSONObject json = new JSONObject(jsonContent.toString());
            JSONArray apps = json.getJSONArray("apps");
            
            for (int i = 0; i < apps.length(); i++) {
                JSONObject app = apps.getJSONObject(i);
                addAppToMarketplace(app);
            }
            
        } catch (Exception e) {
            Label errorLabel = new Label("Error loading marketplace: " + e.getMessage());
            errorLabel.setTextFill(Color.RED);
            marketplaceContainer.getChildren().add(errorLabel);
        }
    }

    private void addAppToMarketplace(JSONObject app) {
        HBox appContainer = new HBox(15);
        appContainer.setStyle("-fx-background-color: white; -fx-padding: 10px; -fx-background-radius: 5px;");
        
        try {
            // App Icon
            ImageView icon = new ImageView(new Image(app.getString("icon")));
            icon.setFitHeight(50);
            icon.setFitWidth(50);
            
            // App Info
            VBox appInfo = new VBox(5);
            Label nameLabel = new Label(app.getString("name"));
            nameLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
            Label descLabel = new Label(app.getString("description"));
            Label versionLabel = new Label("Version: " + app.getString("version"));
            Label authorLabel = new Label("By: " + app.getString("author"));
            
            appInfo.getChildren().addAll(nameLabel, descLabel, versionLabel, authorLabel);
            
            // Install Button
            Button installButton = new Button("Install");
            installButton.setStyle("-fx-background-color: #2196f3; -fx-text-fill: white;");
            installButton.setOnAction(e -> installApp(app));
            
            appContainer.getChildren().addAll(icon, appInfo, installButton);
            marketplaceContainer.getChildren().add(appContainer);
            
        } catch (Exception e) {
            System.err.println("Error adding app to marketplace: " + e.getMessage());
        }
    }

    private void installApp(JSONObject app) {
        // TODO: Implement installation logic
        System.out.println("Installing app: " + app.getString("name"));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
