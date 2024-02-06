package animasitumbuhan;

import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;

import static java.lang.Math.random;

public class Leaf extends Ellipse {

    public final Color LEAF_COLOR = Color.rgb(34, 139, 34);

    public Leaf(Branch parentBranch) {
        super(0, parentBranch.length / 2., 2, parentBranch.length / 2.);
        setScaleX(0); 
        setScaleY(0);

        setFill(LEAF_COLOR);
    }
}
