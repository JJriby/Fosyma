package eu.su.mas.dedaleEtu.mas.knowledge;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Random;

import java.util.stream.Stream;

import org.graphstream.algorithm.Dijkstra;
import org.graphstream.graph.Edge;
import org.graphstream.graph.EdgeRejectedException;
import org.graphstream.graph.ElementNotFoundException;
import org.graphstream.graph.Graph;
import org.graphstream.graph.IdAlreadyInUseException;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.fx_viewer.FxViewer;
import org.graphstream.ui.view.Viewer;
import dataStructures.serializableGraph.*;
import dataStructures.tuple.Couple;
import javafx.application.Platform;

/**
 * This simple topology representation only deals with the graph, not its content.</br>
 * The knowledge representation is not well written (at all), it is just given as a minimal example.</br>
 * The viewer methods are not independent of the data structure, and the dijkstra is recomputed every-time.
 * 
 * @author hc
 */
public class MapRepresentation implements Serializable {

	/**
	 * A node is open, closed, or agent
	 * @author hc
	 *
	 */

	public enum MapAttribute {	
		agent,open,closed;

	}

	private static final long serialVersionUID = -1333959882640838272L;

	/*********************************
	 * Parameters for graph rendering
	 ********************************/

	private String defaultNodeStyle= "node {"+"fill-color: black;"+" size-mode:fit;text-alignment:under; text-size:14;text-color:white;text-background-mode:rounded-box;text-background-color:black;}";
	private String nodeStyle_open = "node.agent {"+"fill-color: forestgreen;"+"}";
	private String nodeStyle_agent = "node.open {"+"fill-color: blue;"+"}";
	private String nodeStyle=defaultNodeStyle+nodeStyle_agent+nodeStyle_open;

	private transient Graph g; //data structure non serializable
	private transient Viewer viewer; //ref to the display,  non serializable
	private Integer nbEdges;//used to generate the edges ids

	private SerializableSimpleGraph<String, MapAttribute> sg;//used as a temporary dataStructure during migration

	private HiddenNodesManager nodemanager;


	public MapRepresentation() {
		this.nodemanager = new HiddenNodesManager(this.g);
		//System.setProperty("org.graphstream.ui.renderer","org.graphstream.ui.j2dviewer.J2DGraphRenderer");
		this.nodemanager = new HiddenNodesManager(this.g);
		System.setProperty("org.graphstream.ui", "javafx");
		this.g= new SingleGraph("My world vision");
		this.g.setAttribute("ui.stylesheet",nodeStyle);

		Platform.runLater(() -> {
			openGui();
		});
		//this.viewer = this.g.display();

		this.nbEdges=0;
	}

	/**
	 * Add or replace a node and its attribute 
	 * @param id
	 * @param mapAttribute
	 */
	public synchronized void addNode(String id,MapAttribute mapAttribute){
		Node n;
		if (this.g.getNode(id)==null){
			n=this.g.addNode(id);
		}else{
			n=this.g.getNode(id);
		}
		n.clearAttributes();
		n.setAttribute("ui.class", mapAttribute.toString());
		n.setAttribute("ui.label",id);
	}

	/**
	 * Add a node to the graph. Do nothing if the node already exists.
	 * If new, it is labeled as open (non-visited)
	 * @param id id of the node
	 * @return true if added
	 */
	public synchronized boolean addNewNode(String id) {
		if (this.g.getNode(id)==null){
			addNode(id,MapAttribute.open);
			return true;
		}
		return false;
	}

	/**
	 * Add an undirect edge if not already existing.
	 * @param idNode1
	 * @param idNode2
	 */
	public synchronized void addEdge(String idNode1,String idNode2){
		this.nbEdges++;
		try {
			this.g.addEdge(this.nbEdges.toString(), idNode1, idNode2);
		}catch (IdAlreadyInUseException e1) {
			System.err.println("ID existing");
			System.exit(1);
		}catch (EdgeRejectedException e2) {
			this.nbEdges--;
		} catch(ElementNotFoundException e3){

		}
	}
	
	public synchronized void addEdge(String id,String idNode1,String idNode2){
		try {
			this.g.addEdge(id, idNode1, idNode2);
		}catch (IdAlreadyInUseException e1) {
			System.err.println("ID existing");
			System.exit(1);
		}catch (EdgeRejectedException e2) {
			this.nbEdges--;
		} catch(ElementNotFoundException e3){

		}
	}
	

	/**
	 * Compute the shortest Path from idFrom to IdTo. The computation is currently not very efficient
	 * 
	 * 
	 * @param idFrom id of the origin node
	 * @param idTo id of the destination node
	 * @return the list of nodes to follow, null if the targeted node is not currently reachable
	 */
	public synchronized List<String> getShortestPath(String idFrom,String idTo){
		List<String> shortestPath=new ArrayList<String>();

		Dijkstra dijkstra = new Dijkstra();//number of edge
		dijkstra.init(g);
		dijkstra.setSource(g.getNode(idFrom));
		dijkstra.compute();//compute the distance to all nodes from idFrom
		List<Node> path=dijkstra.getPath(g.getNode(idTo)).getNodePath(); //the shortest path from idFrom to idTo
		Iterator<Node> iter=path.iterator();
		while (iter.hasNext()){
			shortestPath.add(iter.next().getId());
		}
		dijkstra.clear();
		if (shortestPath.isEmpty()) {//The openNode is not currently reachable
			return null;
		}else {
			shortestPath.remove(0);//remove the current position
		}
		return shortestPath;
	}
	
	
	public void removeNodeTimer(String id) {
		this.nodemanager.removeNode(id);
	}
	
	public synchronized List<String> getShortestPathChase(String idFrom,String idTo){
		this.nodemanager.updateTimers();
		List<String> shortestPath=new ArrayList<String>();

		Dijkstra dijkstra = new Dijkstra();//number of edge
		dijkstra.init(g);
		dijkstra.setSource(g.getNode(idFrom));
		dijkstra.compute();//compute the distance to all nodes from idFrom
		List<Node> path=dijkstra.getPath(g.getNode(idTo)).getNodePath(); //the shortest path from idFrom to idTo
		Iterator<Node> iter=path.iterator();
		while (iter.hasNext()){
			shortestPath.add(iter.next().getId());
		}
		dijkstra.clear();
		if (shortestPath.isEmpty()) {//The openNode is not currently reachable
			return null;
		}else {
			shortestPath.remove(0);//remove the current position
		}
		return shortestPath;
	}

	public List<String> getShortestPathToClosestOpenNode(String myPosition) {
		//1) Get all openNodes
		List<String> opennodes=getOpenNodes();

		//2) select the closest one
		List<Couple<String,Integer>> lc=
				opennodes.stream()
				.map(on -> (getShortestPath(myPosition,on)!=null)? new Couple<String, Integer>(on,getShortestPath(myPosition,on).size()): new Couple<String, Integer>(on,Integer.MAX_VALUE))//some nodes my be unreachable if the agents do not share at least one common node.
				.collect(Collectors.toList());

		Optional<Couple<String,Integer>> closest=lc.stream().min(Comparator.comparing(Couple::getRight));
		//3) Compute shorterPath

		return getShortestPath(myPosition,closest.get().getLeft());
	}



	public List<String> getOpenNodes(){
		return this.g.nodes()
				.filter(x ->x .getAttribute("ui.class")==MapAttribute.open.toString()) 
				.map(Node::getId)
				.collect(Collectors.toList());
	}


	/**
	 * Before the migration we kill all non serializable components and store their data in a serializable form
	 */
	public void prepareMigration(){
		serializeGraphTopology();

		closeGui();

		this.g=null;
	}

	/**
	 * Before sending the agent knowledge of the map it should be serialized.
	 */
	private void serializeGraphTopology() {
		this.sg= new SerializableSimpleGraph<String,MapAttribute>();
		Iterator<Node> iter=this.g.iterator();
		while(iter.hasNext()){
			Node n=iter.next();
			sg.addNode(n.getId(),MapAttribute.valueOf((String)n.getAttribute("ui.class")));
		}
		Iterator<Edge> iterE=this.g.edges().iterator();
		while (iterE.hasNext()){
			Edge e=iterE.next();
			Node sn=e.getSourceNode();
			Node tn=e.getTargetNode();
			sg.addEdge(e.getId(), sn.getId(), tn.getId());
		}	
	}


	public synchronized SerializableSimpleGraph<String,MapAttribute> getSerializableGraph(){
		serializeGraphTopology();
		return this.sg;
	}

	/**
	 * After migration we load the serialized data and recreate the non serializable components (Gui,..)
	 */
	public synchronized void loadSavedData(){

		this.g= new SingleGraph("My world vision");
		this.g.setAttribute("ui.stylesheet",nodeStyle);

		openGui();

		Integer nbEd=0;
		for (SerializableNode<String, MapAttribute> n: this.sg.getAllNodes()){
			this.g.addNode(n.getNodeId()).setAttribute("ui.class", n.getNodeContent().toString());
			for(String s:this.sg.getEdges(n.getNodeId())){
				this.g.addEdge(nbEd.toString(),n.getNodeId(),s);
				nbEd++;
			}
		}
		System.out.println("Loading done");
	}

	/**
	 * Method called before migration to kill all non serializable graphStream components
	 */
	private synchronized void closeGui() {
		//once the graph is saved, clear non serializable components
		if (this.viewer!=null){
			//Platform.runLater(() -> {
			try{
				this.viewer.close();
			}catch(NullPointerException e){
				System.err.println("Bug graphstream viewer.close() work-around - https://github.com/graphstream/gs-core/issues/150");
			}
			//});
			this.viewer=null;
		}
	}

	/**
	 * Method called after a migration to reopen GUI components
	 */
	private synchronized void openGui() {
		this.viewer =new FxViewer(this.g, FxViewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);//GRAPH_IN_GUI_THREAD)
		viewer.enableAutoLayout();
		viewer.setCloseFramePolicy(FxViewer.CloseFramePolicy.CLOSE_VIEWER);
		viewer.addDefaultView(true);

		g.display();
	}

	public void mergeMap(SerializableSimpleGraph<String, MapAttribute> sgreceived) {
		//System.out.println("You should decide what you want to save and how");
		//System.out.println("We currently blindy add the topology");

		if(sgreceived != null) {
			for (SerializableNode<String, MapAttribute> n: sgreceived.getAllNodes()){
				//System.out.println(n);
				boolean alreadyIn =false;
				//1 Add the node
				Node newnode=null;
				try {
					newnode=this.g.addNode(n.getNodeId());
				}	catch(IdAlreadyInUseException e) {
					alreadyIn=true;
					System.out.println("Already in"+n.getNodeId());
				}
				if (!alreadyIn) {
					newnode.setAttribute("ui.label", newnode.getId());
					newnode.setAttribute("ui.class", n.getNodeContent().toString());
				}else{
					newnode=this.g.getNode(n.getNodeId());
					//3 check its attribute. If it is below the one received, update it.
					if (((String) newnode.getAttribute("ui.class"))==MapAttribute.closed.toString() || n.getNodeContent().toString()==MapAttribute.closed.toString()) {
						newnode.setAttribute("ui.class",MapAttribute.closed.toString());
					}
				}
			}
		
			//4 now that all nodes are added, we can add edges
			for (SerializableNode<String, MapAttribute> n: sgreceived.getAllNodes()){
				for(String s:sgreceived.getEdges(n.getNodeId())){
					addEdge(n.getNodeId(),s);
				}
			}

		}
		System.out.println("Merge done");
	}

	/**
	 * 
	 * @return true if there exist at least one openNode on the graph 
	 */
	public boolean hasOpenNode() {
		return (this.g.nodes()
				.filter(n -> n.getAttribute("ui.class")==MapAttribute.open.toString())
				.findAny()).isPresent();
	}

	public SerializableSimpleGraph<String, MapAttribute> getPartialGraph (SerializableSimpleGraph<String, MapAttribute> sg2) {
		SerializableSimpleGraph<String, MapAttribute> sg1 = this.getSerializableGraph();
		sg= new SerializableSimpleGraph<String,MapAttribute>();
		SingleGraph gn = new SingleGraph("Partial Graph");
		gn.setAttribute("ui.stylesheet",nodeStyle);
		Set<SerializableNode<String, MapAttribute>> nodes = sg2.getAllNodes();
		int i=0;
		for (SerializableNode<String, MapAttribute> n: sg1.getAllNodes()){
			if (!nodes.contains(n)) {
				Node n1;
				i+=1;
				n1=gn.addNode(n.getNodeId());
				n1.clearAttributes();
				n1.setAttribute("ui.class", n.getNodeContent().toString());
				n1.setAttribute("ui.label",n.getNodeId());
			}			
		}
		if (i==0) {
			return null;
		}
		SerializableSimpleGraph<String, MapAttribute> sgf = new SerializableSimpleGraph<String,MapAttribute>();
		Iterator<Node> iter=gn.iterator();
		while(iter.hasNext()){
			Node n=iter.next();
			sgf.addNode(n.getId(),MapAttribute.valueOf((String)n.getAttribute("ui.class")));
		}
		Iterator<Edge> iterE=gn.edges().iterator();
		while (iterE.hasNext()){
			Edge e=iterE.next();
			Node sn=e.getSourceNode();
			Node tn=e.getTargetNode();
			sgf.addEdge(e.getId(), sn.getId(), tn.getId());
		}
		return sgf;
	}

	public List<String> getAmbushPoint(int nbAgents, int nbGolem, int mode) {
		//choose randomly a possible AmbushPoint based on the number of available agents
		//the point will not be a leaf or a point before a leaf to increase the chances of choosing a passage point
		List<String> choice=new ArrayList();
		List<String> possiblePoints=new ArrayList<String>();
		Random rand = new Random();
		System.out.println("I have "+nbAgents+ " at my disposal");
		
		Iterator<Node> iter=this.g.iterator();
		while(iter.hasNext()){
			Node n=iter.next();
			if (n.getDegree()<=nbAgents){
				if (n.getDegree()!=1) {
					possiblePoints.add(n.getId());
				}
				
			}
		}
			if (mode==1) {
				System.out.println("size :"+ possiblePoints.size());
				choice.add(possiblePoints.get(rand.nextInt(possiblePoints.size())));
		}
		else {
			if (mode==2) {
				boolean done=false;
				while (!done) {
					List<String> choices=new ArrayList<String>();
					for (int q=0;q<nbGolem;q++) {
						String choice1 = possiblePoints.get(rand.nextInt(possiblePoints.size()));
						while (choices.contains(choice1)) {
							choice1 = possiblePoints.get(rand.nextInt(possiblePoints.size()));
						}
						choices.add(choice1);
					}
					int s=0;
					for (int q=0;q<nbGolem;q++) {
						s+=getSurroundingPoints(choices.get(q)).size();
					}
					if (s<=nbAgents) {
						done=true;
						choice=choices;
					}
				}
			}
			else {
				if (mode==3) {
					int nbrestant=nbAgents;
					while(possiblePoints.size()>1) {
						String choice1 = possiblePoints.get(rand.nextInt(possiblePoints.size()));
						while ((choice.contains(choice1))||(getSurroundingPoints(choice1).size()>nbrestant)) {
							possiblePoints.remove(choice1);
							if (possiblePoints.size()==0) {
								return choice;
							}
							choice1 = possiblePoints.get(rand.nextInt(possiblePoints.size()));
						}
						choice.add(choice1);
						nbrestant-=getSurroundingPoints(choice1).size();
					}
				}
			}
		}
				
		return choice;
	}

	public List<String> getSurroundingPoints(String Point) {
		List<String> points=new ArrayList<String>();
		Iterator<Node> iter=this.g.iterator();
		while(iter.hasNext()){
			Node n=iter.next();
			if (n.getId()==Point) {
				Stream<Node> surroundings = n.neighborNodes();
				List<Node> list=surroundings.collect(Collectors.toList());
				for (int i=0;i<list.size();i++) {
					points.add(list.get(i).getId());
				}
				return points;
			}
			
		}
		return points;
	}
	
	public void setGraphData(SerializableSimpleGraph<String, MapAttribute> sGraph) {
        // Ensure the graph is initialized
        if (this.g == null) {
            this.g = new SingleGraph("My world vision");
            this.g.setAttribute("ui.stylesheet", nodeStyle);
        }

        // Apply data from the serializable graph
        for (SerializableNode<String, MapAttribute> node : sGraph.getAllNodes()) {
            Node graphNode = this.g.getNode(node.getNodeId());
            if (graphNode == null) { // If node doesn't exist, add it
                graphNode = this.g.addNode(node.getNodeId());
            }
            graphNode.setAttribute("ui.class", node.getNodeContent().toString());
            graphNode.setAttribute("ui.label", node.getNodeId());
        }

        for (SerializableNode<String, MapAttribute> node : sGraph.getAllNodes()) {
            for (String edgeId : sGraph.getEdges(node.getNodeId())) {
                if (this.g.getEdge(edgeId) == null) {
                    this.g.addEdge(edgeId, node.getNodeId(), edgeId);
                }
            }
        }
    }
	


}