package graph;

/**
 * Created by gvdambros on 1/14/17.
 */
public class AminoAcid {

    String code;

    public AminoAcid(String code){
        this.code = code;
    }

    public String toString(){
        try {
            return utils.AminoAcidSynonym(code);
        } catch (Exception e) {
            // TODO: 1/17/17  
            return "X";
        }
    }

}
