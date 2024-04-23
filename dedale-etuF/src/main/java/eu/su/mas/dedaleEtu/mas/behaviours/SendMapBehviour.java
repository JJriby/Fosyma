package eu.su.mas.dedaleEtu.mas.behaviours;

import java.io.IOException;

import dataStructures.serializableGraph.SerializableSimpleGraph;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.dummies.explo.ExploreCoopAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation.MapAttribute;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public class SendMapBehviour extends OneShotBehaviour {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2673508417406482861L;

	public SendMapBehviour(ExploreCoopAgent exploreCoopAgent) {
		this.myAgent=exploreCoopAgent;
	}

	@Override
	public void action() {
		System.out.println("SMap"+this.myAgent.getLocalName());
		ACLMessage msg=new ACLMessage(ACLMessage.INFORM);
		msg.setProtocol("SHARE-MAP");
		msg.setSender(this.myAgent.getAID());
		msg.addReceiver(((ExploreCoopAgent) this.myAgent).getPartner());
		SerializableSimpleGraph<String, MapAttribute> sg = ((ExploreCoopAgent) this.myAgent).getMap().getSerializableGraph();
		try {
			msg.setContentObject(sg);
		} catch (IOException e) {
			e.printStackTrace();
		}
		((AbstractDedaleAgent)this.myAgent).sendMessage(msg);
		

	}
	
	public int onEnd() {
		return 0;
	}

}
