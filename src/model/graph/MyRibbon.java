package model.graph;

import javafx.beans.property.*;

/**
 * Created by Neuroteam on 31.01.2017.
 */
public class MyRibbon {

    private Property<MyNode> ca, cb, nca, ncb;

    private StringProperty name;
    private DoubleProperty weight;
    private Object userData;

    public MyRibbon(MyNode ca, MyNode cb, MyNode nca, MyNode ncb,  String name, double weight) {
        this.ca = new SimpleObjectProperty<>(ca);
        this.cb = new SimpleObjectProperty<>(cb);
        this.nca = new SimpleObjectProperty<>(nca);
        this.ncb = new SimpleObjectProperty<>(ncb);
        this.name = new SimpleStringProperty(name);
        this.weight = new SimpleDoubleProperty(weight);
    }

    public MyRibbon(MyNode ca, MyNode cb, MyNode nca, MyNode ncb, String name) {
        this.ca = new SimpleObjectProperty<>(ca);
        this.cb = new SimpleObjectProperty<>(cb);
        this.nca = new SimpleObjectProperty<>(nca);
        this.ncb = new SimpleObjectProperty<>(ncb);
        this.name = new SimpleStringProperty(name);
        this.weight = new SimpleDoubleProperty(-1);
    }

    public MyRibbon(MyNode ca, MyNode cb, MyNode nca, MyNode ncb) {
        this.ca = new SimpleObjectProperty<>(ca);
        this.cb = new SimpleObjectProperty<>(cb);
        this.nca = new SimpleObjectProperty<>(nca);
        this.ncb = new SimpleObjectProperty<>(ncb);
        this.name = new SimpleStringProperty("");
        this.weight = new SimpleDoubleProperty(-1);
    }

    public String getName() { return name.getValue(); }

    public MyNode getCa() {
        return ca.getValue();
    }

    public MyNode getCb() {
        return cb.getValue();
    }

    public MyNode getNca() {
        return nca.getValue();
    }

    public MyNode getNcb() {
        return ncb.getValue();
    }
}
