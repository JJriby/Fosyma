package eu.su.mas.dedaleEtu.mas.knowledge;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;


public class HiddenNodesManager {
	private Graph g;
	private ArrayList<String> removedNodes;
	private ArrayList<ArrayList<ArrayList<String>>> removedEdges;
	private ArrayList<Integer> timers;

	public HiddenNodesManager(Graph g) {
		this.g=g;
		this.removedNodes=new ArrayList<String>();
		this.removedEdges=new ArrayList<ArrayList<ArrayList<String>>>();
		this.timers=new ArrayList<Integer>();
	}
	
	public void removeNode(String id) {
		timers.add(10);
		removedNodes.add(id);
		Iterator<Edge> iterE=this.g.edges().iterator();
		ArrayList<ArrayList<String>> edges = new ArrayList<ArrayList<String>>();
		while (iterE.hasNext()){
			Edge e=iterE.next();
			Node sn=e.getSourceNode();
			Node tn=e.getTargetNode();
			if ((sn.getId().compareTo(id)==0) || (sn.getId().compareTo(id)==0)){
				ArrayList<String> myEdge =new ArrayList<String>();
				myEdge.add(e.getId());
				myEdge.add(sn.getId());
				myEdge.add(tn.getId());
				edges.add(myEdge);
			}
		}
		removedEdges.add(edges);
	}
	public void updateTimers() {
		for (int i=0;i<timers.size();i++) {
			int t=timers.get(i);
			if (t>1) {
				timers.set(i,t-1);
			}
			else {
				String node = removedNodes.get(i);
				ArrayList<ArrayList<String>> edges = removedEdges.get(i);
				this.g.addNode(node);
				for (int j=0;j<edges.size();j++) {
					List<String> edge = edges.get(j);
					this.g.addEdge(edge.get(0),edge.get(1),edge.get(2));
					}
				removedNodes.remove(i);
				removedEdges.remove(i);
				timers.remove(i);
				i--;
				}
			}
			
		}
	}
	
	
	


