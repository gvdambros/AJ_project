package model.PDBProcessor;

import javafx.geometry.Point3D;
import model.Protein;
import model.graph.InvalidEdge;
import model.graph.MyNode;
import model.sequence.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;

/**
 * Created by gvdambros on 1/28/17.
 */
public class PDBProcessor {

    private File file;

    public PDBProcessor(File file) {
        this.file = file;
    }

    public void run(Protein protein, HashMap<String, Point3D> atomsPosition) {
        try {

            List<String> data = Files.readAllLines(file.toPath());

            String lastOption = "";
            String lastID = "";

            Sheet sheet = new Sheet();
            int count = 0;
            int max;
            boolean terminate = false;

            for(String temp : data){

                String option = temp.substring(MyConstants.OPT_BEGIN_POSITION, MyConstants.OPT_END_POSITION);
                String id = ".";
                int begin, end;

                switch(option){
                    case "SEQRES":
                        max = Integer.parseInt(temp.substring(MyConstants.SEQRES_LENGTH_POSITION, MyConstants.SEQRES_LENGTH_POSITION + 4).replace(" ",""));
                        id = temp.substring(MyConstants.SEQRES_ID_POSITION, MyConstants.SEQRES_ID_POSITION + 3);
                        for(int i = MyConstants.SEQRES_BEGIN_POSITION; i < MyConstants.SEQRES_END_POSITION; i = i + MyConstants.SEQRES_OFFSET) {
                            protein.addAminoAcid( new AminoAcid(temp.substring(i, i+3)) );
                            if(++count >= max) break;
                        }
                        break;
                    case "HELIX ":
                        id = temp.substring(MyConstants.HELIX_ID_POSITION, MyConstants.HELIX_ID_POSITION + 3);
                        begin = Integer.parseInt(temp.substring(MyConstants.HELIX_BEGIN_POSITION, MyConstants.HELIX_BEGIN_POSITION + 4).replace(" ",""));
                        end = Integer.parseInt(temp.substring(MyConstants.HELIX_END_POSITION, MyConstants.HELIX_END_POSITION + 4).replace(" ",""));
                        Helix helix = new Helix( id, begin - 1, end );
                        protein.getSequence().addHelix( helix );
                        break;
                    case "SHEET ":
                        id = temp.substring(MyConstants.SHEET_ID_POSITION, MyConstants.SHEET_ID_POSITION + 3);
                        int actual = Integer.parseInt(temp.substring(MyConstants.STRAND_NR_POSITION, MyConstants.STRAND_NR_POSITION + 3).replace(" ",""));
                        max = Integer.parseInt(temp.substring(MyConstants.SHEET_LENGTH_POSITION, MyConstants.SHEET_LENGTH_POSITION + 2).replace(" ",""));
                        if(!lastID.equals(id) || !lastOption.equals(option)){
                            sheet = new Sheet();
                        }
                        begin = Integer.parseInt(temp.substring(MyConstants.STRAND_BEGIN_POSITION, MyConstants.STRAND_BEGIN_POSITION + 3).replace(" ",""));
                        end = Integer.parseInt(temp.substring(MyConstants.STRAND_END_POSITION, MyConstants.STRAND_END_POSITION + 3).replace(" ",""));
                        Strand strand = new Strand ( id, begin - 1, end );
                        sheet.addStrand( strand );
                        if(actual == max) protein.getSequence().addSheet(sheet);
                        break;
                    case "ATOM  ":
                        id = temp.substring(MyConstants.ATOM_ID_POSITION, MyConstants.ATOM_ID_POSITION + 4);
                        String atomType = temp.substring(MyConstants.ATOM_TYPE_POSITION, MyConstants.ATOM_TYPE_POSITION + 3).replace(" ","");
                        int residualNumber = Integer.parseInt(temp.substring(MyConstants.ATOM_RESIDUAL_POSITION, MyConstants.ATOM_RESIDUAL_POSITION + 3).replace(" ",""));
                        double X = Double.parseDouble(temp.substring(MyConstants.ATOM_X_POSITION, MyConstants.ATOM_X_POSITION + 7).replace(" ",""));
                        double Y = Double.parseDouble(temp.substring(MyConstants.ATOM_Y_POSITION, MyConstants.ATOM_Y_POSITION + 7).replace(" ",""));
                        double Z = Double.parseDouble(temp.substring(MyConstants.ATOM_Z_POSITION, MyConstants.ATOM_Z_POSITION + 7).replace(" ",""));
                        MyNode myNode;
                        try{
                            myNode = new MyNode ( id, atomType, residualNumber );
                        }
                        catch (InvalidAtomType e){
                            break;
                        }
                        atomsPosition.put(id, new Point3D(X, Y, Z));
                        protein.addNode( myNode );

                        break;
                    case "TER   ":
                        terminate = true;
                        break;
                }
                lastOption = option;
                lastID = id;
                if(terminate){
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        protein.createGraph();
    }
}
