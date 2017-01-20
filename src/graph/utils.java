package graph;

/**
 * Created by gvdambros on 1/17/17.
 */
public class utils {

    public static String AminoAcidSynonym(String aminoAcid) throws Exception {
        switch (aminoAcid){
            case "ALA":
                return "A";
            case "ARG":
                return "R";
            case "ASN":
                return "N";
            case "ASP":
                return "D";
            case "ASX":
                return "B";
            case "CYS":
                return "C";
            case "GLN":
                return "Q";
            case "GLU":
                return "E";
            case "GLX":
                return "Z";
            case "GLY":
                return "G";
            case "HIS":
                return "H";
            case "ILE":
                return "I";
            case "LEU":
                return "L";
            case "LYS":
                return "K";
            case "MET":
                return "M";
            case "PHE":
                return "F";
            case "PRO":
                return "P";
            case "SER":
                return "S";
            case "THR":
                return "T";
            case "TRP":
                return "W";
            case "TYR":
                return "Y";
            case "VAL":
                return "V";
            default:
                throw new Exception("Unknown Amino Acid");
        }
    }
}
