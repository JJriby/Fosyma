package eu.su.mas.dedaleEtu.mas.behaviours;

import java.util.List;

import eu.su.mas.dedaleEtu.mas.agents.dummies.explo.ExploreCoopAgent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

public class ReceivePriorityBehaviour extends OneShotBehaviour {

	public ReceivePriorityBehaviour(ExploreCoopAgent exploreCoopAgent) {
		this.myAgent=exploreCoopAgent;
	}

	@Override
	public void action() {
		System.out.println("rp");
		MessageTemplate msgTemplate=MessageTemplate.and(MessageTemplate.and(
				MessageTemplate.MatchPerformative(ACLMessage.INFORM),MessageTemplate.MatchProtocol("SHARE-PRIORITY")), 
				MessageTemplate.MatchSender(((ExploreCoopAgent) this.myAgent).getPartner()));
		ACLMessage msgReceived=this.myAgent.blockingReceive(msgTemplate,750);
		List<String> prio = null;
		if(msgReceived!=null) {
			try {
				prio=(List<String>) msgReceived.getContentObject();
			} catch (UnreadableException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(prio!=null) {
				((ExploreCoopAgent)this.myAgent).setPriority(prio);
			}
			
		}
		
		

	}
	
	public int onEnd() {
		return 0;
	}

}
