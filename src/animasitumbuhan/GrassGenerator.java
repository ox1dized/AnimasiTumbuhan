package animasitumbuhan;

import javafx.scene.Group;

import java.util.ArrayList;
import java.util.List;

import static animasitumbuhan.Util.addChildToParent;


class GrassGenerator {

    private final Group content;
    private final int numBlades;

    public GrassGenerator(Group content, int numBlades) {
        this.content = content;
        this.numBlades = numBlades;
    }

    public List<Blade> generateGrass() {
        List<Blade> grass = new ArrayList<>(numBlades);
        for (int i = 0; i < numBlades; i++) {

            final Blade blade = new Blade();
            grass.add(blade);

            addChildToParent(content, blade);
        }
        return grass;
    }
}
