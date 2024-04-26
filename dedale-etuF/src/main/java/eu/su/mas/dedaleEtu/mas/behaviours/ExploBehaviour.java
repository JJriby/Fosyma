package eu.su.mas.dedaleEtu.mas.behaviours;

import java.util.Iterator;
import java.util.List;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Location;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.env.gs.gsLocation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.dummies.explo.ExploreCoopAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation.MapAttribute;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class ExploBehaviour extends OneShotBehaviour {
	private ExploreCoopAgent myAgent;
	private boolean finished;
	private int value=0;
	public ExploBehaviour(ExploreCoopAgent exploreCoopAgent) {
		this.myAgent=exploreCoopAgent;
	}

	@Override
	public void action() {
		String myPosition=((AbstractDedaleAgent)this.myAgent).getCurrentPosition().toString();
		if(((ExploreCoopAgent)this.myAgent).getPriority().contains(myPosition)) {
			List<String> p = ((ExploreCoopAgent)this.myAgent).getPriority();
			p.remove(myPosition);
			((ExploreCoopAgent)this.myAgent).setPriority(p);
		}
		
		if (myPosition!=null){
			List<Couple<Location,List<Couple<Observation,Integer>>>> lobs=((AbstractDedaleAgent)this.myAgent).observe();
			List<Couple<Location, List<Couple<Observation, Integer>>>> scent = ((AbstractDedaleAgent) this.myAgent).observe();
	        for (int i=0; i<scent.size();i++) {
	            Couple<Location,List<Couple<Observation,Integer>>> data = scent.get(i);
	            String pos = data.getLeft().toString();
	            List<Couple<Observation, Integer>> l = data.getRight();
	            for (int j=0;j<l.size();j++) {
	                Couple<Observation, Integer> da = l.get(j);
	                Observation obs = da.getLeft();
	                if (obs.getName().compareTo("Stench")==0){
	                    this.myAgent.setLastKnowPosition(pos);
	                }
	            }
	        }


			//when visiting, make it 'closed' and removing it from open
			this.myAgent.getMap().addNode(myPosition, MapAttribute.closed);

			// get the surrounding nodes and, 
			// if not in closedNodes, add them to open nodes.
			String nextNode=null;
			if(((ExploreCoopAgent)this.myAgent).getPriority().size()>0) {
				System.out.println("test");
				if(this.myAgent.getMap().getShortestPath(myPosition,((ExploreCoopAgent)this.myAgent).getPriority().get(0))!=null) {
					nextNode=this.myAgent.getMap().getShortestPath(myPosition,((ExploreCoopAgent)this.myAgent).getPriority().get(0)).get(0);
					System.out.println(this.myAgent.getLocalName()+"-- Priority list= "+((ExploreCoopAgent)this.myAgent).getPriority()+"| nextNode: "+nextNode);
			
				}
			}
			Iterator<Couple<Location, List<Couple<Observation, Integer>>>> iter=lobs.iterator();
			while(iter.hasNext()){
				//checking if the node and edge exist ( their existance not necessarily together )
				String nodeId=iter.next().getLeft().toString();
				boolean isNewNode=this.myAgent.getMap().addNewNode(nodeId);
				if (myPosition!=nodeId) {
					this.myAgent.getMap().addEdge(myPosition, nodeId);
					if (nextNode==null && isNewNode) nextNode=nodeId;
				}
			}

			// while openNodes is not empty, continues.
			if (!this.myAgent.getMap().hasOpenNode()){
				//Exploration finished
				value=3;
				finished=true;
				

			}else{

				// select next move.
				// If there exist one open node directly reachable, go for it,
				//	 otherwise choose one from the openNode list, compute the shortestPath and go for it
				if (nextNode==null){
					//no directly accessible openNode and no node in the priority list
					//chose one, compute the path and take the first step.
					System.out.println("goal:"+this.myAgent.getMap().getShortestPathToClosestOpenNode(myPosition).get( this.myAgent.getMap().getShortestPathToClosestOpenNode(myPosition).size()-1));
					nextNode=this.myAgent.getMap().getShortestPathToClosestOpenNode(myPosition).get(0);//getShortestPath(myPosition,this.openNodes.get(0)).get(0);
					System.out.println(this.myAgent.getLocalName()+"-- list= "+this.myAgent.getMap().getOpenNodes()+"| nextNode: "+nextNode);
				}else {
					System.out.println("nextNode notNUll - "+this.myAgent.getLocalName()+"-- list= "+this.myAgent.getMap().getOpenNodes()+"\n -- nextNode: "+nextNode);
				}
				((AbstractDedaleAgent)this.myAgent).moveTo(new gsLocation(nextNode));


			}
		}

		
		
	}
	public int onEnd() {
		return value;	
	}

}
