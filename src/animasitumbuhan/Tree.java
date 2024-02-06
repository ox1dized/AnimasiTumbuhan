package animasitumbuhan;

import javafx.scene.Group;

import java.util.ArrayList;
import java.util.List;


public class Tree extends Group {

    final List<List<Branch>> generations = new ArrayList<>();
    final List<Branch> crown = new ArrayList<>();
    final List<Flower> flowers = new ArrayList<>();
    final List<Leaf> leafage = new ArrayList<>();

    public Tree(int depth) {
        for (int i = 0; i < depth; i++) {
            generations.add(new ArrayList<>());
        }
    }
}