package model;

import model.graph.InvalidEdge;
import model.graph.MyGraph;
import model.graph.MyNode;
import model.graph.MyRibbon;
import model.sequence.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by gvdambros on 1/28/17.
 */
public class Protein {

    private MyGraph myGraph;
    private Sequence sequence;
    private HashMap<AminoAcid, HashMap<AtomInfo.AtomsTypes, MyNode>> relation;

    public Protein() {
        myGraph = new MyGraph();
        sequence = new Sequence();
        relation = new HashMap<>();
    }

    public void addAminoAcid(AminoAcid aminoAcid){
        sequence.addAminoAcid(aminoAcid);
        relation.put(aminoAcid, new HashMap<>());
    }

    public void addNode(MyNode myNode){
        AminoAcid aminoAcid = sequence.getAminoAcid(myNode.getResidualNumber() - 1);
        HashMap<AtomInfo.AtomsTypes, MyNode>  myNodeMap = relation.remove(aminoAcid);
        myNodeMap.put(myNode.getAtomInfo().getType(), myNode);
        relation.put(aminoAcid, myNodeMap);
    }

    public MyGraph getMyGraph() {
        return myGraph;
    }

    public void setMyGraph(MyGraph myGraph) {
        this.myGraph = myGraph;
    }

    public Sequence getSequence() {
        return sequence;
    }

    public void setSequence(Sequence sequence) {
        this.sequence = sequence;
    }

    public void clear() {
        this.sequence.clear();
        this.myGraph.clear();
    }


    public void createGraph() {
        int i;
        for (i = 0; i < sequence.length(); i++) {
            connectAminoAcid(i, true);
            if( i <  sequence.length() - 1) connectAminoAcids(i, i + 1, true);
        }
    }

    public void createRibbon() {
        for (int i = 1; i < sequence.length(); i++) {
            addRibbon(i-1, i);
        }
    }

    public void createSimpleCartoon() throws InvalidEdge {

        for(Helix temp : sequence.getHelixes()){
            MyNode source = relation.get( sequence.getAminoAcid( temp.begin - 1 ) ).get(AtomInfo.AtomsTypes.N);
            MyNode target = relation.get( sequence.getAminoAcid( temp.end - 1 ) ).get(AtomInfo.AtomsTypes.C);
            myGraph.addHelix(source, target);
        }
        for(Sheet sheet : sequence.getSheets()){
            for(Strand strand: sheet.getStrands()) {
                for(int i = strand.begin; i < strand.end; i++){
                    addRibbon(i-1, i);
                }
            }
        }
        boolean[] mask = sequence.maskAminoAcids();
        int i;
        for( i = 0; i < mask.length - 1; i++){
            if(mask[i] || mask[i + 1]){
                connectAminoAcids(i, i + 1, false);
                connectAminoAcid(i, false);
            }
        }
        if(mask[i]) connectAminoAcid(i, false);

    }

    private void connectAminoAcids(int i, int j, boolean graph){
        HashMap<AtomInfo.AtomsTypes, MyNode> source = relation.get(sequence.getAminoAcid(i));
        HashMap<AtomInfo.AtomsTypes, MyNode> target = relation.get(sequence.getAminoAcid(j));
        try {
            if(!source.isEmpty() && !target.isEmpty()){
                myGraph.connectNodes(source.get(AtomInfo.AtomsTypes.C), target.get(AtomInfo.AtomsTypes.N));
            }
        } catch (InvalidEdge invalidEdge) {
            invalidEdge.printStackTrace();
        }
    }

    private void connectAminoAcid(int i, boolean graph) {
        HashMap<AtomInfo.AtomsTypes, MyNode> source = relation.get(sequence.getAminoAcid(i));
        try {
            if (!source.isEmpty()) {
                myGraph.connectNodes(source.get(AtomInfo.AtomsTypes.N), source.get(AtomInfo.AtomsTypes.CA));
                myGraph.connectNodes(source.get(AtomInfo.AtomsTypes.CA), source.get(AtomInfo.AtomsTypes.C));
                if (graph) {
                    if (source.get(AtomInfo.AtomsTypes.CB) != null) {
                        myGraph.connectNodes(source.get(AtomInfo.AtomsTypes.CA), source.get(AtomInfo.AtomsTypes.CB));
                    }
                    myGraph.connectNodes(source.get(AtomInfo.AtomsTypes.C), source.get(AtomInfo.AtomsTypes.O));
                }
            }
        } catch (InvalidEdge invalidEdge) {
            invalidEdge.printStackTrace();
        }
    }

    private void addRibbon(int i, int j){
        if(sequence.getAminoAcid(i).toString() != "G" && sequence.getAminoAcid(j).toString() != "G") {
            HashMap<AtomInfo.AtomsTypes, MyNode> source = relation.get(sequence.getAminoAcid(i));
            HashMap<AtomInfo.AtomsTypes, MyNode> target = relation.get(sequence.getAminoAcid(j));
            MyNode sourceCA = source.get(AtomInfo.AtomsTypes.CA);
            MyNode sourceCB = source.get(AtomInfo.AtomsTypes.CB);
            MyNode targetCA = target.get(AtomInfo.AtomsTypes.CA);
            MyNode targetCB = target.get(AtomInfo.AtomsTypes.CB);
                /*System.out.println(sequence.getAminoAcid(i));
                System.out.println(sequence.getAminoAcid(j));
                System.out.println(source);
                System.out.println(target);
                System.out.println(source.get(AtomInfo.AtomsTypes.CB));
                System.out.println(source.get(AtomInfo.AtomsTypes.CA));
                System.out.println(target.get(AtomInfo.AtomsTypes.CB));
                System.out.println(target.get(AtomInfo.AtomsTypes.CA));*/
            if (sourceCA != null && sourceCB != null && targetCA != null && targetCB != null) {
                myGraph.addRibbon(new MyRibbon(sourceCA, sourceCB, targetCA, targetCB));
            }
        }
    }

}
