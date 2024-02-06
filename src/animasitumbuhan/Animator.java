package animasitumbuhan;

import javafx.animation.*;
import javafx.beans.binding.DoubleBinding;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

import java.util.List;

import static java.lang.Math.random;
import static java.lang.Math.sin;
import static animasitumbuhan.Util.addChildToParent;


public class Animator implements Runnable {

    public static final Duration BRANCH_GROWING_DURATION = Duration.seconds(2);
    public static final Duration GRASS_BECOME_GREEN_DURATION = Duration.seconds(5);
    public static final Duration GRASS_BECOME_YELLOW_DURATION = Duration.seconds(5);
    public static final Duration LEAF_BECOME_YELLOW_DURATION = Duration.seconds(5);
    public static final Duration WIND_CYCLE_DURATION = Duration.seconds(5);
    public static final Duration LEAF_APPEARING_DURATION = Duration.seconds(2);
    public static final Duration FLOWER_APPEARING_DURATION = Duration.seconds(1);
    private final TreeGenerator treeGenerator;
    private final GrassGenerator grassGenerator;

    Animator(TreeGenerator treeGenerator, GrassGenerator grassGenerator) {
        this.treeGenerator = treeGenerator;
        this.grassGenerator = grassGenerator;
    }

    @Override
    public void run() {

        Tree tree = treeGenerator.generateTree();
        List<Blade> grass = grassGenerator.generateGrass();

        SequentialTransition branchGrowingAnimation = new SequentialTransition();
        ParallelTransition treeWindAnimation = new ParallelTransition();

        for (int i = 0; i < tree.generations.size(); i++) {
            List<Branch> branchGeneration = tree.generations.get(i);
            branchGrowingAnimation.getChildren().add(animateBranchGrowing(branchGeneration)); //create animation for current crown
            treeWindAnimation.getChildren().add(animateTreeWind(branchGeneration, i));
        }

        final Transition all = new ParallelTransition(new GrassWindAnimation(grass), treeWindAnimation, new SequentialTransition(branchGrowingAnimation, seasonsAnimation(tree, grass)));
        all.play();
    }

    private Animation animateBranchGrowing(List<Branch> branchGeneration) {

        ParallelTransition sameDepthBranchAnimation = new ParallelTransition();
        for (final Branch branch : branchGeneration) {
            Timeline branchGrowingAnimation = new Timeline(new KeyFrame(BRANCH_GROWING_DURATION, new KeyValue(branch.base.endYProperty(), branch.length)));//line is growing by changing endY from 0 to brunch.length
            PauseTransition pauseTransition = new PauseTransition();
            pauseTransition.setOnFinished(t -> branch.base.setStrokeWidth(branch.length / 25));
            sameDepthBranchAnimation.getChildren().add(
                    new SequentialTransition(
                            pauseTransition,
                            branchGrowingAnimation));

        }
        return sameDepthBranchAnimation;

    }

    private Animation animateTreeWind(List<Branch> branchGeneration, int depth) {
        ParallelTransition wind = new ParallelTransition();
        for (final Branch brunch : branchGeneration) {
            final Rotate rotation = new Rotate(0);
            brunch.getTransforms().add(rotation);

            Timeline windTimeline = new Timeline(new KeyFrame(WIND_CYCLE_DURATION, new KeyValue(rotation.angleProperty(), depth * 2)));
            windTimeline.setAutoReverse(true);
            windTimeline.setCycleCount(Animation.INDEFINITE);
            wind.getChildren().add(windTimeline);
        }
        return wind;
    }

    private Transition seasonsAnimation(final Tree tree, final List<Blade> grass) {

        Transition spring = animateSpring(tree.leafage, grass);
        Transition flowers = animateFlowers(tree.flowers);
        Transition autumn = animateAutumn(tree.leafage, grass);

        SequentialTransition sequentialTransition = new SequentialTransition(spring, flowers, autumn);
        return sequentialTransition;
    }

    private Transition animateSpring(List<Leaf> leafage, List<Blade> grass) {
        ParallelTransition springAnimation = new ParallelTransition();
        for (Leaf leaf : leafage) {
            ScaleTransition leafageAppear = new ScaleTransition(LEAF_APPEARING_DURATION, leaf);
            leafageAppear.setToX(1);
            leafageAppear.setToY(1);
            springAnimation.getChildren().add(leafageAppear);
        }
        return springAnimation;
    }

    private Transition animateFlowers(List<Flower> flowers) {

        ParallelTransition flowersAppearAndFallDown = new ParallelTransition();

        for (int i = 0; i < flowers.size(); i++) {
            final Flower flower = flowers.get(i);
            for (Ellipse petal : flower.getPetals()) {

                FadeTransition flowerAppear = new FadeTransition(FLOWER_APPEARING_DURATION, petal);
                flowerAppear.setToValue(1);
                flowerAppear.setDelay(FLOWER_APPEARING_DURATION.divide(3).multiply(i + 1));
                                                                                                                 //Additional sequential transition as workaround.
                flowersAppearAndFallDown.getChildren().add(new SequentialTransition(new SequentialTransition(        //TODO here is issue https://javafx-jira.kenai.com/browse/RT-35126
                        flowerAppear,
                        fakeFallDownAnimation(petal))));
            }
        }
        return flowersAppearAndFallDown;
    }

    private Transition animateAutumn(List<Leaf> leafage, List<Blade> grass) {
        ParallelTransition autumn = new ParallelTransition();
        return autumn;
    }

    private Animation fakeFallDownAnimation(final Ellipse petalOld) {
        return fakeFallDownEllipseAnimation(petalOld, null, node -> node.setOpacity(0));
    }

    private Animation fakeFallDownEllipseAnimation(final Ellipse sourceEllipse, Color fakeColor, final HideMethod hideMethod) {

        final Ellipse fake = copyEllipse(sourceEllipse, fakeColor);
        addChildToParent(treeGenerator.content, fake);

        PauseTransition replaceFakeWithSource = new PauseTransition(Duration.ONE);
        replaceFakeWithSource.setDelay(Duration.minutes(0.9 * random() + 0.1));
        replaceFakeWithSource.setOnFinished(e -> {

            final Point2D position = treeGenerator.content.sceneToLocal(sourceEllipse.localToScene(0, 0));

            DoubleBinding sinPath = new DoubleBinding() {

                {
                    bind(fake.translateYProperty());
                }

                @Override
                protected double computeValue() {
                    return 50 * sin((fake.translateYProperty().doubleValue() - position.getY()) / 20);
                }
            };
            fake.setTranslateY(position.getY());
            fake.setCenterX(0);
            fake.setCenterY(0);
            fake.translateXProperty().bind(sinPath.add(position.getX()));
            fake.rotateProperty().bind(fake.translateYProperty().multiply(2).add(random() * 180));
            fake.setOpacity(1);
            hideMethod.hide(sourceEllipse);
        });
        TranslateTransition fallDown = new TranslateTransition(Duration.seconds(30), fake);
        fallDown.setToY(random() * 30 + 1);
        FadeTransition disappear = new FadeTransition(Duration.seconds(2), fake);
        disappear.setDelay(Duration.seconds(5));
        disappear.setToValue(0);


        return new SequentialTransition(replaceFakeWithSource,
                fallDown,
                disappear);
    }

    private Ellipse copyEllipse(Ellipse petalOld, Color color) {
        Ellipse ellipse = new Ellipse();
        ellipse.setRadiusX(petalOld.getRadiusX());
        ellipse.setRadiusY(petalOld.getRadiusY());
        if (color == null) {
            ellipse.setFill(petalOld.getFill());
        } else {
            ellipse.setFill(color);
        }
        ellipse.setRotate(petalOld.getRotate());
        ellipse.setOpacity(0);
        return ellipse;
    }

    @FunctionalInterface
    private static interface HideMethod {

        void hide(Node node);
    }
}