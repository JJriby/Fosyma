package eu.su.mas.dedaleEtu.mas.behaviours;

import java.util.Random;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.dummies.explo.ExploreCoopAgent;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class PingingBehaviour extends OneShotBehaviour {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9069423658414828637L;
	private int value=0;

	public PingingBehaviour(ExploreCoopAgent exploreCoopAgent) {
		this.myAgent=exploreCoopAgent;
	}

	@Override
	public void action() {
		Random rand = new Random(); int nombreAleatoire = rand.nextInt(50);
		MessageTemplate mt =MessageTemplate.MatchProtocol("PINGMAP");
		ACLMessage msg= this.myAgent.blockingReceive(mt,250+nombreAleatoire);
		if(msg==null) {
		
			ACLMessage msg1 = new ACLMessage(ACLMessage.INFORM);
			msg1.setProtocol("PINGMAP");
			msg1.setSender(this.myAgent.getAID());
			for (AID agentName : ((ExploreCoopAgent)this.myAgent).getList_agentNames()) {
				msg1.addReceiver(agentName);
			}
			((AbstractDedaleAgent)myAgent).sendMessage(msg1);
			value=0;
			MessageTemplate mt1 =MessageTemplate.MatchProtocol("ACK");
			ACLMessage msg11= this.myAgent.blockingReceive(mt1,250);
			if(msg11!=null) {
				((ExploreCoopAgent)this.myAgent).setPartner(msg11.getSender());
				value=2;
			}
		}else {
			((ExploreCoopAgent)this.myAgent).setPartner(msg.getSender());
			ACLMessage msg1 = new ACLMessage(ACLMessage.INFORM);
			msg1.setSender(this.myAgent.getAID());
			msg1.addReceiver(((ExploreCoopAgent)this.myAgent).getPartner());
			msg1.setProtocol("ACK");
			((AbstractDedaleAgent)myAgent).sendMessage(msg1);
			value=1;
			
		}

	}
	
	public int onEnd() {
		return value;
	}

}
