package eu.su.mas.dedaleEtu.mas.behaviours;

import java.util.Random;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.dummies.explo.ExploreCoopAgent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class AnswerPingMapBehaviour extends OneShotBehaviour{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4382160100119401351L;
	private int value=0;

	public AnswerPingMapBehaviour(ExploreCoopAgent exploreCoopAgent) {
		this.myAgent=exploreCoopAgent;
	}

	@Override
	public void action() {
		Random rand = new Random(); int nombreAleatoire = rand.nextInt(50);
		MessageTemplate mt =MessageTemplate.MatchProtocol("PINGMAP");
		ACLMessage msg= this.myAgent.blockingReceive(mt,250+nombreAleatoire);
		if(msg!=null) {
			((ExploreCoopAgent)this.myAgent).setPartner(msg.getSender());
			ACLMessage msg1 = new ACLMessage(ACLMessage.INFORM);
			msg1.setSender(this.myAgent.getAID());
			msg1.addReceiver(((ExploreCoopAgent)this.myAgent).getPartner());
			msg1.setProtocol("ACK");
			((AbstractDedaleAgent)myAgent).sendMessage(msg1);
			System.out.println(msg.getSender()+this.myAgent.getLocalName());
			this.value=1;
		}else {
			this.value=0;
		}
		
	}
	
	public int onEnd() {
		return value;
	}

}
