package graph;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gvdambros on 11/20/16.
 */
public class ConnectedComponent {

    private List<MyNode> nodes;

    public ConnectedComponent() {
        nodes = new ArrayList<MyNode>();
    }

    public ConnectedComponent(MyNode myNode){
        nodes = new ArrayList<MyNode>();
        nodes.add(myNode);
    }

    public List<MyNode> getNodes() {
        return nodes;
    }

    public boolean hasNode(MyNode myNode){
        return nodes.contains(myNode);
    }

    public void join(ConnectedComponent connectedComponent){
        for(MyNode temp: connectedComponent.getNodes()){
            if( !this.hasNode(temp) ){
                nodes.add(temp);
            }
        }
    }
}
