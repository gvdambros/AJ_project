package graph;

import com.sun.deploy.util.StringUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by gvdambros on 1/14/17.
 */
public class Sequence {


    public ObservableList<AminoAcid> aminoAcids;
    public ObservableList<Helix> helixes;
    public ObservableList<Sheet> sheets;

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
        String aux = "";
        for(AminoAcid temp: aminoAcids){
            aux = aux + temp.toString();
        }
        return aux + "\n\n" + aux + "\n" + mixString(helixesToString(), sheetsToString()) + "\n" + sheetsToString();
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
            for(Strand strand: sheet.strands){
                for(int i = strand.begin - 1; i < strand.end; i++){
                    aux[i] = 'E';
                }
            }
        }
        return new String(aux).replace('\0','-');
    }

    public String helixesToString(){
        char[] aux = new char[aminoAcids.size()];
        for(Helix helix: helixes){
                for(int i = helix.begin - 1; i < helix.end; i++){
                    aux[i] = 'H';
            }
        }
        return new String(aux).replace('\0','-');
    }


    public void clear() {
        aminoAcids.clear();
        helixes.clear();
        sheets.clear();
    }
}
