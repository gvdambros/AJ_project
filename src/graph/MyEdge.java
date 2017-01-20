package graph;

import javafx.beans.property.*;
import javafx.collections.ObservableList;

import java.util.Properties;

/**
 * Created by gvdambros on 11/19/16.
 */
public class MyEdge {

    private Property<MyNode> source;
    private Property<MyNode> target;

    private StringProperty name;
    private DoubleProperty weight;
    private Object userData;

    public MyEdge(MyNode source, MyNode target, String name, double weight) {
        this.source = new SimpleObjectProperty<>(source);
        this.target = new SimpleObjectProperty<>(target);
        this.name = new SimpleStringProperty(name);
        this.weight = new SimpleDoubleProperty(weight);
    }

    public MyEdge(MyNode source, MyNode target, String name) {
        this.source = new SimpleObjectProperty<>(source);
        this.target = new SimpleObjectProperty<>(target);
        this.name = new SimpleStringProperty(name);
        this.weight = new SimpleDoubleProperty(-1);
    }

    public MyEdge(MyNode source, MyNode target) {
        this.source = new SimpleObjectProperty<>(source);
        this.target = new SimpleObjectProperty<>(target);
        this.name = new SimpleStringProperty("");
        this.weight = new SimpleDoubleProperty(-1);
    }

    public boolean isEqual(MyNode source, MyNode target){
        return source == this.source.getValue() && target == this.target.getValue();
    }

    public MyNode getSource() {
        return source.getValue();
    }

    public MyNode getTarget() {
        return target.getValue();
    }

    public String getName() { return name.getValue(); }


}
