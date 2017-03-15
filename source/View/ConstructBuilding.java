package View;

import Controller.FightController;
import Controller.GameController;
import Model.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.stage.PopupWindow;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by cheny2 on 3/3/17.
 *
 * This class builds the view for the construct building phase.
 */
public class ConstructBuilding extends Application {

    private static VBox leftPanels;
    private static int[] currentGridCoords = {0, 0};
    private static ScrollPane availableBuildings;
    private static GridPane baseBuilding;
    private static Text playerInfo;
    private Integer STARTTIME = 59;
    public GameController controller;

    static Stage constructStage = new Stage();

    public ConstructBuilding(GameController controller) {
        this.controller = controller;
    }
    @Override
    public void start(Stage primaryStage) throws Exception {
        System.out.println("TESTING START!");

        GameController control = this.controller;

        List<PlayerImpl> players = control.getPlayers();

        constructStage = primaryStage;

        int playerIndex = control.getNumPlayers() - control.getNumRemainingPlayers() - 1;

        Font.loadFont(ConstructBuilding.class.getResource("resources/fonts/digital-7.ttf").toExternalForm(), 30);
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f2f2f2");

        leftPanels = new VBox();
        leftPanels.setSpacing(20);

        PlayerImpl currentPlayer = players.get(playerIndex);

        VBox currentBasePanel = addCurrectBasePanel();

        availableBuildings = addBuildingsToConstructPanel(currentPlayer);

        leftPanels.getChildren().addAll(currentBasePanel);

        Label timerLabel = new Label("00:" + STARTTIME.toString());
        timerLabel.setId("timer");

        VBox rightPanels = new VBox();
        rightPanels.setSpacing(200);
        VBox playerInfoPanel = addPlayerInfoPanel(currentPlayer);
        VBox functionPanels = addFunctionPanel(primaryStage, timerLabel, control);
        rightPanels.getChildren().addAll(playerInfoPanel, functionPanels);

        root.setLeft(leftPanels);
        root.setRight(rightPanels);

        root.setPadding(new Insets(30, 50, 30, 50));

        Scene scene = new Scene(root, 1400, 1000);
        scene.getStylesheets().add(Fight.class.getResource("static/ConstructBuilding.css").toExternalForm());

        final Integer[] timeSeconds = {STARTTIME};
        Timeline timeline;

        timerLabel.setText("00:" + timeSeconds[0].toString());
        timeline = new Timeline();
        timeline.setCycleCount(59);
        Timeline finalTimeline = timeline;
        timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {
             @Override
             public void handle(ActionEvent event) {
                 timeSeconds[0]--;
                 if (timeSeconds[0] < 10) {
                     timerLabel.setText("00:0" + timeSeconds[0].toString());
                 } else {
                     timerLabel.setText("00:" + timeSeconds[0].toString());
                 }
                 if (timeSeconds[0] <= 0) {
                     finalTimeline.stop();

                     if (control.getNumRemainingPlayers() > 0) {
                         control.setNumRemainingPlayers(control.getNumRemainingPlayers() - 1);
                         ConstructBuilding constructBuilding = new ConstructBuilding(control);
                         try {
                             constructBuilding.start(ConstructBuilding.constructStage);
                         } catch (Exception e) {
                             e.printStackTrace();
                         }
                     } else {
                         Fight fight = new Fight(control);
                         try {
                             fight.start(Fight.fightStage);
                         } catch (Exception e) {
                             e.printStackTrace();
                         }
                         constructStage.close();
                         control.setNumRemainingPlayers(control.getNumPlayers() - 1);
                     }
                 }
             }
         }));

        constructStage.setResizable(false);
        constructStage.setScene(scene);
        Timeline finalTimeline1 = timeline;

        constructStage.setOnShown(windowEvent -> {
            finalTimeline1.play();
        });
        constructStage.show();
    }

    //Makes the confirm button that purchases a building
    private static Button addConfirmButton(PlayerImpl player, Stage stagePopup, BuildingImpl building) {
        Button confirmButton = new Button("Confirm");
        confirmButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                stagePopup.close();
                player.setGold(player.getGold() - building.getCost());
                String teamName;
                if (player.getTeam() == 1) {
                    teamName = "Light";
                } else {
                    teamName = "Dark";
                }
                playerInfo.setText("Player Name: " + player.getPlayerName() +
                        "\nPlayer Gold: " + player.getGold().toString() +
                        "\nPlayer Score: " + Integer.toString(player.getScore()) +
                        "\nPlayer Team: " + teamName);

                if (building.getName().equals("Warrior Camp")) {
                    baseBuilding.add(new Button(), currentGridCoords[0], currentGridCoords[1]);
                } else if (building.getName().equals("Knight Academy")) {
                    baseBuilding.add(new Button(), currentGridCoords[0], currentGridCoords[1]);
                }
            }
        });
        return confirmButton;
    }

    /* Construct the function panel */
    private static VBox addFunctionPanel(Stage fightStage, Label timerLabel, GameController control) {
        VBox functionPanel = new VBox();
        functionPanel.setPadding(new Insets(10, 10, 10, 10));
        functionPanel.setSpacing(5);
        functionPanel.setAlignment(Pos.CENTER);
        functionPanel.setId("functionPanel");

        HBox timerContainer = new HBox();

        timerContainer.getChildren().addAll(timerLabel);
        timerContainer.setAlignment(Pos.CENTER);

        HBox nextPlayerButtonContainer = new HBox();
        nextPlayerButtonContainer.setAlignment(Pos.CENTER);
        Button nextPlayerButton = new Button("FINISH");
        nextPlayerButton.setId("functionButton");

        nextPlayerButton.setOnAction(event -> {
            if (control.getNumRemainingPlayers() > 0) {
                control.setNumRemainingPlayers(control.getNumRemainingPlayers() - 1);
                ConstructBuilding constructBuilding = new ConstructBuilding(control);
                try {
                    constructBuilding.start(ConstructBuilding.constructStage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Fight fight = new Fight(control);
                try {
                    fight.start(Fight.fightStage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                constructStage.close();
                control.setNumRemainingPlayers(control.getNumPlayers() - 1);
            }
        });

        nextPlayerButtonContainer.getChildren().addAll(nextPlayerButton);

        functionPanel.getChildren().addAll(timerContainer, nextPlayerButtonContainer);
        return functionPanel;
    }

    /* Construct the buldingsToConstruct panel */
    private static ScrollPane addBuildingsToConstructPanel(PlayerImpl player) {
        ScrollPane constructBuildingPanel = new ScrollPane();
        constructBuildingPanel.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        constructBuildingPanel.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        GridPane buildingContainer = addBuildingContainer(player);

        constructBuildingPanel.setContent(buildingContainer);

        return constructBuildingPanel;
    }

    /* Construct the building container */
    private static GridPane addBuildingContainer(PlayerImpl player) {
        GridPane buildingContainer = new GridPane();
        buildingContainer.setHgap(40);
        buildingContainer.setVgap(60);

        BuildingImpl shield = new ShieldKnightBuilding();
        VBox shieldKnight = addSingleBuilding(player, shield);
        buildingContainer.add(shieldKnight, 0, 0);

        VBox cupcake = addSingleBuilding(player, new CupCakeWarriorBuilding());
        buildingContainer.add(cupcake, 1, 0);

        return buildingContainer;
    }
    /*
    private static VBox addSingleBuilding(BuildingImpl building) {
        VBox singleBuildingContainer = new VBox();

        ImageView buildingImage = new ImageView(building.getImagePath());
        return singleBuildingContainer;
    }

    */

    /* Construct a single building in the existing building panel */
    private static VBox addSingleBuilding(PlayerImpl player, BuildingImpl building) {
        VBox singleBuildingContainer = new VBox();

        Image temp = new Image("file:assets/swordmanT1/t1buildevil.gif");
        ImageView buildingImg = new ImageView(temp);
        buildingImg.setStyle("-fx-background-color: transparent");
        buildingImg.setFitWidth(180);
        buildingImg.setPreserveRatio(true);

        Text buildingInfoBlock = new Text("Minions Spawned: " + building.getMinion().toString() +
                "\nMinion Attack:" + Double.toString(building.getMinion().getAtk()) +
                "\nMinion Move Speed:" + Double.toString(building.getMinion().getMoveSpeed()) +
                "\nMinion Health :" + Double.toString(building.getMinion().getHP()));
        buildingInfoBlock.prefWidth(180);
        buildingInfoBlock.maxWidth(180);
        buildingInfoBlock.setFont(Font.font("Herculanum", 15));
        buildingInfoBlock.setWrappingWidth(180);
        buildingInfoBlock.prefHeight(180);
        buildingInfoBlock.minHeight(180);

        Rectangle placeHolder = new Rectangle(180, 190);
        placeHolder.setFill(Color.TRANSPARENT);
        placeHolder.setStroke(Color.TRANSPARENT);


        StackPane imageViewContainer = new StackPane(buildingImg);

        StackPane infoBlockContainer = new StackPane(placeHolder, buildingInfoBlock);
        infoBlockContainer.prefWidth(180);
        infoBlockContainer.maxWidth(180);
        infoBlockContainer.prefHeight(180);
        infoBlockContainer.minHeight(180);
        infoBlockContainer.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        Button buildingImgContainer = new Button("");
        buildingImgContainer.setId("buildingImgContainer");

        buildingImgContainer.setOnAction(event -> {
            Stage stagePopup = new Stage();
            VBox comp = new VBox();
            comp.setSpacing(20);
            comp.setAlignment(Pos.CENTER);
            Text confirmAction = new Text("Confirm Purchase?");
            Button confirmButton = addConfirmButton(player, stagePopup, building);
            Button declineButton = new Button("Decline");
            HBox buttonHolder = new HBox();
            buttonHolder.setAlignment(Pos.CENTER);
            buttonHolder.setSpacing(40);
            buttonHolder.getChildren().addAll(confirmButton, declineButton);

            comp.getChildren().addAll(confirmAction, buttonHolder);

            Scene popupScene = new Scene(comp, 300, 200);
            stagePopup.setScene(popupScene);
            stagePopup.show();


            declineButton.setOnAction(event1 -> {
                stagePopup.close();
            });
        });

        buildingImgContainer.graphicProperty().bind(
                Bindings.when(
                        buildingImgContainer.hoverProperty()
                ).then(infoBlockContainer).otherwise(imageViewContainer));

        VBox buildingInfoGen = addBuildingInfo();

        singleBuildingContainer.getChildren().addAll(buildingImgContainer, buildingInfoGen);

        singleBuildingContainer.setPadding(new Insets(5, 5, 5, 5));

        return singleBuildingContainer;
    }

    /* Construct the building info block */
    private static VBox addBuildingInfo() {
        VBox buildingInfo = new VBox();
        buildingInfo.setAlignment(Pos.CENTER);

        Text buildingName = new Text("Cupcake House");
        Text buildingPrice = new Text("$200");

        buildingName.setFont(Font.font("Herculanum", 20));
        buildingPrice.setFont(Font.font("Herculanum", 20));

        buildingInfo.getChildren().addAll(buildingName, buildingPrice);

        return buildingInfo;
    }

    /* Construct the playerInfo panel */
    private static VBox addPlayerInfoPanel(PlayerImpl player) {
        VBox playerInfoPanel = new VBox();
        playerInfoPanel.setSpacing(20);
        playerInfoPanel.setPadding(new Insets(10, 10, 10, 10));
        playerInfoPanel.setId("playerInfoPanel");
        playerInfoPanel.setPrefSize(500, 800);

        Text titleText = new Text("Player Info: ");
        titleText.setFont(Font.font(null, FontWeight.EXTRA_BOLD, 30));

        String teamName = "";
        if (player.getTeam() == 1) {
            teamName = "Light";
        } else if (player.getTeam() == 2) {
            teamName = "Dark";
        }

        playerInfo = new Text("Player Name: " + player.getPlayerName() +
                "\nPlayer Gold: " + player.getGold().toString() +
                "\nPlayer Score: " + Integer.toString(player.getScore()) +
                "\nPlayer Team: " + teamName);

        playerInfo.setLineSpacing(10);
        playerInfo.setFont(Font.font(null, FontWeight.EXTRA_BOLD, 20));

        HBox buttonContainer = new HBox();
        buttonContainer.setAlignment(Pos.BASELINE_RIGHT);
        Button showMapButton = new Button("Show Map");
        showMapButton.setId("functionButton");
        buttonContainer.getChildren().addAll(showMapButton);

        playerInfoPanel.getChildren().addAll(titleText, playerInfo, buttonContainer);

        return playerInfoPanel;
    }

    private static VBox addCurrectBasePanel() {
        VBox currentBasePanel = new VBox();
        currentBasePanel.setPadding(new Insets(0, 10, 10, 10));
        currentBasePanel.setStyle("-fx-border-radius: 10 10 0 0;\n" +
                " -fx-background-radius: 10 10 0 0;");
        currentBasePanel.setId("currentBaseContainer");

        Text title = new Text("Existing Buildings");
        title.setFont(Font.font(null, FontWeight.EXTRA_BOLD, 30));
        StackPane currentBase = addCurrentBase();

        currentBasePanel.getChildren().addAll(title, currentBase);

        return currentBasePanel;
    }

    private static StackPane addCurrentBase() {
        StackPane currentBase = new StackPane();

//        ImageView baseBg = new ImageView(ConstructBuilding.class.getResource("static/landscape.png").toExternalForm());
//        baseBg.setFitWidth(400);
//        baseBg.setFitHeight(400);

//        GridPane baseGrid = addBaseGrid();

        baseBuilding = addBaseBuildings();

//        baseGrid.setPadding(new Insets(20, 0, 0, 50));
        baseBuilding.setPadding(new Insets(20, 0, 0, 50));

        currentBase.getChildren().addAll(baseBuilding);

        return currentBase;
    }

    private static GridPane addBaseBuildings() {
        GridPane baseBuildings = new GridPane();

//        baseBuildings.getRowConstraints().add(new RowConstraints(100));
//        baseBuildings.getColumnConstraints().add(new ColumnConstraints(100));

        List<Button> buttonList = new ArrayList<>();

        for (int row = 0; row < 4; row ++) {
            for (int col = 0; col < 4; col ++) {
                Image temp = new Image("file:assets/swordmanT1/buildbase.gif");
                ImageView base = new ImageView(temp);

                base.setFitHeight(125);
                base.setFitWidth(125);

                int[] gridCoord = {col, row};

                Button clickable = addClickable(gridCoord, base, buttonList);

                buttonList.add(clickable);

                baseBuildings.add(clickable, col, row);

            }
        }

        return baseBuildings;
    }

    private static Button addClickable(int[] gridCoord, ImageView base, List<Button> buttonList) {
        Button clickable = new Button("", base);
        clickable.setStyle("-fx-background-color: transparent");

        clickable.setOnAction(event ->  {
            for (Button button : buttonList) {
                button.setStyle("-fx-background-color: transparent");
            }
            clickable.setStyle("-fx-background-color: #ffcc99");
            if (!leftPanels.getChildren().contains(availableBuildings)) {
                leftPanels.getChildren().add(availableBuildings);
            }
            currentGridCoords[0] = gridCoord[0];
            currentGridCoords[1] = gridCoord[1];
        });
        return clickable;
    }




    private static class baseClickable extends Button {
        private baseClickable() {

        }

        private baseClickable(int row, int col) {
            this.colNum = col;
            this.rowNum = row;
        }

        public int getRowNum() {
            return rowNum;
        }

        private int rowNum;

        public int getColNum() {
            return colNum;
        }

        private int colNum;
    }
    public static void main(String[] args) {
        launch(args);
    }
}
