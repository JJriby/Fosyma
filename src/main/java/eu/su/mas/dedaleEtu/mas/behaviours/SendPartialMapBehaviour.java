package eu.su.mas.dedaleEtu.mas.behaviours;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import dataStructures.serializableGraph.SerializableSimpleGraph;
import dataStructures.tuple.Couple;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.dummies.explo.ExploreCoopAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.Informations;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation.MapAttribute;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

public class SendPartialMapBehaviour extends OneShotBehaviour {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5186602375049085080L;
	private int value=0;

	public SendPartialMapBehaviour(ExploreCoopAgent exploreCoopAgent) {
		this.myAgent=exploreCoopAgent;	
	}

	@SuppressWarnings("unchecked")
	@Override
	public void action() {
		System.out.println("SPMap"+this.myAgent.getLocalName());
		MessageTemplate mt= MessageTemplate.and(MessageTemplate.and(
				MessageTemplate.MatchSender(((ExploreCoopAgent) this.myAgent).getPartner()),MessageTemplate.MatchPerformative(ACLMessage.INFORM)), 
				MessageTemplate.MatchProtocol("SHARE-MAP"));
		ACLMessage msg=this.myAgent.blockingReceive(mt,1000);
		if(msg!=null) {
			SerializableSimpleGraph<String, MapAttribute> sg = null;
			try {
				sg=(SerializableSimpleGraph<String, MapAttribute>) msg.getContentObject();
			} catch (UnreadableException e) {
				e.printStackTrace();
			}
			if(sg==null) {
				this.value=1;
				return;
			}
			((ExploreCoopAgent) this.myAgent).getMap().mergeMap(sg);
			List<String> openNodes = ((ExploreCoopAgent) this.myAgent).getMap().getOpenNodes();
			ArrayList<Couple<String,Integer>> ld=new ArrayList<Couple<String,Integer>>();
			for(String node : openNodes) {
				int size=Integer.MAX_VALUE;
				System.out.println(((ExploreCoopAgent) this.myAgent).getMap().getShortestPath(((AbstractDedaleAgent)this.myAgent).getCurrentPosition().toString(),node));
				List<String> way = ((ExploreCoopAgent) this.myAgent).getMap().getShortestPath(((AbstractDedaleAgent)this.myAgent).getCurrentPosition().toString(),node);
				if(way!=null) {
					size=way.size();
				}
				ld.add(new Couple<String, Integer>(node,size));
				
			}
			
			SerializableSimpleGraph<String, MapAttribute> sg1 = ((ExploreCoopAgent) this.myAgent).getMap().getSerializableGraph();
			
			ACLMessage msg1 = new ACLMessage(ACLMessage.INFORM);
			msg1.setProtocol("SHARE-PARTIAL-MAP");
			try {
				msg1.setContentObject(new Informations(sg1,ld));
			} catch (IOException e) {
				e.printStackTrace();
			}
			msg1.setSender(myAgent.getAID());
			msg1.addReceiver(((ExploreCoopAgent) this.myAgent).getPartner());
			((AbstractDedaleAgent)this.myAgent).sendMessage(msg1);
		}else {
			this.value=1;
		}

	}
	
	public int onEnd() {
		return value;
	}

}
