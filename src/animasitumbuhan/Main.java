package animasitumbuhan;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;


import java.util.Timer;
import java.util.TimerTask;
import javafx.scene.paint.RadialGradient;
import javafx.scene.shape.Circle;

public class Main extends Application {

    private static final int SCENE_WIDTH = 1000;
    private static final int SCENE_HEIGHT = 800;
    private static final int NUMBER_OF_BRANCH_GENERATIONS = 8;
    private static final int NUM_BLADES = 500;
    private Group rootContent;
    private Group grassContent;
    private Group treeContent;

    @Override
    public void start(final Stage stage) {
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.sizeToScene();
        stage.setScene(new AppScene());

        final Text close = new Text("Tutup");
        close.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        close.setStyle("-fx-background-color:WHITE;-fx-text-fill:gray;");
        close.setOpacity(0);
        close.setOnMouseClicked(e -> {
            Platform.exit();
            System.exit(0);
        });
        StackPane stackPane = new StackPane(close);
        stackPane.setTranslateY(stage.getScene().getHeight() - 80);
        stackPane.getTransforms().add(new Rotate(180));
        stackPane.translateXProperty().bind(stackPane.widthProperty().divide(2));
        rootContent.getChildren().add(stackPane);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(500),close);
        fadeIn.setToValue(0.5);
        stage.getScene().setOnMouseEntered(e -> fadeIn.playFromStart());
        FadeTransition fadeOut = new FadeTransition(Duration.millis(500),close);
        fadeOut.setToValue(0);
        stage.getScene().setOnMouseExited(e -> fadeOut.play());
        stage.show();

        stage.setOnCloseRequest(t -> {
            Platform.exit();
            System.exit(0);
        });

        new Animator(new TreeGenerator(treeContent, NUMBER_OF_BRANCH_GENERATIONS), new GrassGenerator(grassContent, NUM_BLADES)).run();

        new Timer().schedule(new TimerTask() {

            @Override
            public void run() {
                System.out.println("FPS " + com.sun.javafx.perf.PerformanceTracker.getSceneTracker(stage.getScene()).getInstantFPS());
            }
        }, 0, 1000);
    }

    public static void main(String[] args) {
        launch(args);
    }

    private class AppScene extends Scene {

        public AppScene() {
            super(rootContent = new Group(), SCENE_WIDTH, SCENE_HEIGHT, Color.TRANSPARENT);
            rootContent.setClip(null);

            Rectangle background = new Rectangle(-SCENE_WIDTH / 2, 0, SCENE_WIDTH, SCENE_HEIGHT);
            background.setFill(new LinearGradient(0, 0, 0, SCENE_HEIGHT, false, CycleMethod.NO_CYCLE, new Stop(0.3, Color.WHITE),
                    new Stop(1., Color.rgb(173, 216, 230)))); 
            rootContent.getChildren().add(background);
            rootContent.getChildren().add(treeContent = new Group()); 
            rootContent.getChildren().add(grassContent = new Group()); 
            rootContent.getTransforms().addAll(new Translate(SCENE_WIDTH / 2, SCENE_HEIGHT), new Rotate(180));
            background.setStroke(Color.BLACK); 
            background.setStrokeWidth(10); 

            drawGrass();
            addSun();
        }

        private void drawGrass() {
            Rectangle grass = new Rectangle(-SCENE_WIDTH / 2, 0, SCENE_WIDTH, 50);
            grass.setFill(Color.rgb(0, 128, 0));
            grassContent.getChildren().add(grass);
        }

        private void addSun() {
            Stop[] stops = new Stop[]{
                    new Stop(0, Color.YELLOW),
                    new Stop(1, Color.ORANGE)
            };

            RadialGradient gradient = new RadialGradient(0, 0, 0.5, 0.5, 0.8, true, null, stops);

            Circle sun = new Circle(50, gradient); 
            sun.setTranslateX(-SCENE_WIDTH / 4); 
            sun.setTranslateY(600); 
            rootContent.getChildren().add(sun);

            addSunRays(sun);
        }

        private void addSunRays(Circle sun) {
            double rayLength = 50; 
            double gap = 10; 
            int numRays = 12; 

            for (int i = 0; i < numRays; i++) {
                double angle = 360.0 / numRays * i;

                double startX = sun.getTranslateX() + (sun.getRadius() + gap) * Math.cos(Math.toRadians(angle));
                double startY = sun.getTranslateY() + (sun.getRadius() + gap) * Math.sin(Math.toRadians(angle));

                double endX = startX + rayLength * Math.cos(Math.toRadians(angle));
                double endY = startY + rayLength * Math.sin(Math.toRadians(angle));

                javafx.scene.shape.Line ray = new javafx.scene.shape.Line(startX, startY, endX, endY);
                ray.setStrokeWidth(2); 
                ray.setStroke(Color.YELLOW);
                rootContent.getChildren().add(ray);
            }
        }

    }
}
