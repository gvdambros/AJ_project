package model.sequence;

import javafx.scene.paint.Color;

/**
 * Created by gvdambros on 1/19/17.
 */
public class AtomInfo {


    public enum AtomsTypes {
        C, CA, CB, N, O
    }

    private AtomsTypes type;

    public AtomInfo(String type) throws InvalidAtomType {
        switch (type){
            case "O":
                this.type = AtomsTypes.O;
                break;
            case "C":
                this.type = AtomsTypes.C;
                break;
            case "CA":
                this.type = AtomsTypes.CA;
                break;
            case "CB":
                this.type = AtomsTypes.CB;
                break;
            case "N":
                this.type = AtomsTypes.N;
                break;
            default:
                throw new InvalidAtomType("Unknown type");
        }
    }

    public AtomsTypes connectTo(){
        switch (this.type){
            case O:
                return AtomsTypes.C;
            case C:
                return AtomsTypes.CA;
            case CA:
                return AtomsTypes.N;
            case CB:
                return AtomsTypes.CA;
            case N:
                return AtomsTypes.C;
        }
        return null;
    }

    public AtomsTypes getType() {
        return type;
    }


    public Color getColor() {
        switch (this.type){
            case O:
                return Color.RED;
            case C:
            case CA:
            case CB:
                return Color.GRAY;
            case N:
                return Color.BLUE;
        }
        return null;
    }


    public double getSize() {
        switch (this.type){
            case O:
                return .60;
            case C:
            case CA:
            case CB:
                return .70;
            case N:
                return .65;
        }
        return 0;
    }

}
