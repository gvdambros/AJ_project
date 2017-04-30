package model.sequence;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by gvdambros on 1/14/17.
 */
public class Sequence {

    private ObservableList<AminoAcid> aminoAcids;
    private ObservableList<Helix> helixes;
    private ObservableList<Sheet> sheets;

    public Sequence(){
        helixes = FXCollections.observableList(new ArrayList<>());
        aminoAcids = FXCollections.observableList(new ArrayList<>());
        sheets = FXCollections.observableList(new ArrayList<>());
    }

    public void addAminoAcid(AminoAcid aminoAcid){
        aminoAcids.add(aminoAcid);
    }

    public void addHelix(Helix helix){
        helixes.add(helix);
    }

    public void addSheet(Sheet sheet){ sheets.add(sheet); }

    public String toString(){
        return getPrimaryToString() + "\n" + getSecondaryToString();
    }

    public String getPrimaryToString(){
        String aux = "";
        for(AminoAcid temp: aminoAcids){
            aux = aux + temp.toString();
        }
        return aux;
    }

    public String getSecondaryToString(){
        return mixString(helixesToString(), sheetsToString());
    }

    public String getPrimaryToString(int splitPosition){
        return selectInString( getPrimaryToString(), splitPosition);
    }

    public ObservableList<Helix> getHelixes() {
        return helixes;
    }

    public ObservableList<Sheet> getSheets() {
        return sheets;
    }

    private String selectInString(String string, int splitPosition){
        return string.substring(0,splitPosition ) + "(" + string.substring(splitPosition, splitPosition + 1) + ")" + string.substring(splitPosition + 1, string.length());
    }

    public String getSecondaryToString(int splitPosition){
        return selectInString(getSecondaryToString(), splitPosition);
    }

    private String mixString(String s1, String s2) {
        char[] aux = new char[aminoAcids.size()];
        for(int i = 0; i < aminoAcids.size(); i++){
            if(s1.charAt(i) != '-') aux[i] = s1.charAt(i);
            else if(s2.charAt(i) != '-') aux[i] = s2.charAt(i);
        }
        return new String(aux).replace('\0','-');
    }

    private String sheetsToString() {
        char[] aux = new char[aminoAcids.size()];
        for(Sheet sheet: sheets){
            for(Strand strand: sheet.getStrands()){
                for(int i = strand.begin; i < strand.end; i++){
                    aux[i] = 'E';
                }
            }
        }
        return new String(aux).replace('\0','-');
    }

    public String helixesToString(){
        char[] aux = new char[aminoAcids.size()];
        for(Helix helix: helixes){
                for(int i = helix.begin; i < helix.end; i++){
                    aux[i] = 'H';
            }
        }
        return new String(aux).replace('\0','-');
    }

    public void clear() {
        helixes.clear();
        sheets.clear();
        aminoAcids.clear();
    }

    public HashMap<String,Integer> getCountAminoAcids() {
        HashMap<String, Integer> countAminoAcids = new HashMap<>();
        for (AminoAcid temp: aminoAcids){
            if( countAminoAcids.containsKey(temp.code) ){
                int count = countAminoAcids.get(temp.code);
                countAminoAcids.put(temp.code, count + 1 );
            }
            else{
                countAminoAcids.put(temp.code, 1 );
            }
        }
        return countAminoAcids;
    }

    public HashMap<String,Integer> getCountAminoAcidsInHelixes() {
        HashMap<String, Integer> countAminoAcids = new HashMap<>();
        String secondary = helixesToString();
        for(int i = 0; i < secondary.length(); i++){
            if(secondary.charAt(i) == 'H') {
                if (countAminoAcids.containsKey(aminoAcids.get(i).code)) {
                    int count = countAminoAcids.get(aminoAcids.get(i).code);
                    countAminoAcids.put(aminoAcids.get(i).code, count + 1);
                } else {
                    countAminoAcids.put(aminoAcids.get(i).code, 1);
                }
            }
        }
        return countAminoAcids;
    }

    public HashMap<String,Integer> getCountAminoAcidsInSheets() {
        HashMap<String, Integer> countAminoAcids = new HashMap<>();
        String secondary = sheetsToString();
        for(int i = 0; i < secondary.length(); i++){
            if(secondary.charAt(i) == 'E') {
                if (countAminoAcids.containsKey(aminoAcids.get(i).code)) {
                    int count = countAminoAcids.get(aminoAcids.get(i).code);
                    countAminoAcids.put(aminoAcids.get(i).code, count + 1);
                } else {
                    countAminoAcids.put(aminoAcids.get(i).code, 1);
                }
            }
        }
        return countAminoAcids;
    }

    public int length() {
        return aminoAcids.size();
    }

    public AminoAcid getAminoAcid(int i){
        return aminoAcids.get(i);
    }

    public ObservableList<AminoAcid> getAminoAcids() {
        return aminoAcids;
    }

    public boolean[] maskAminoAcids(){
        String aux = getSecondaryToString();
        boolean[] mask = new boolean[aux.length()];
        for(int i = 0; i< aux.length(); i++){
            mask[i] = aux.charAt(i) == '-';
        }
        return mask;
    }

}
