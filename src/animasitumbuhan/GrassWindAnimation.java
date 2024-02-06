package animasitumbuhan;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.util.Duration;

import java.util.List;

import static java.lang.Math.PI;


class GrassWindAnimation extends Transition {

    final private DoubleProperty phase = new SimpleDoubleProperty(0);

    public GrassWindAnimation(List<Blade> blades) {

        setCycleCount(Animation.INDEFINITE);
        setInterpolator(Interpolator.LINEAR);
        setCycleDuration(Duration.seconds(3));
        for (Blade blade : blades) {
            blade.phase.bind(phase);
        }
    }

    @Override
    protected void interpolate(double fraction) {
        phase.set(fraction * 2 * PI);
    }
}
