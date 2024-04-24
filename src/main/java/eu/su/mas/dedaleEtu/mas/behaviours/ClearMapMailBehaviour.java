package eu.su.mas.dedaleEtu.mas.behaviours;

import eu.su.mas.dedaleEtu.mas.agents.dummies.explo.ExploreCoopAgent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class ClearMapMailBehaviour extends OneShotBehaviour {


	public ClearMapMailBehaviour(ExploreCoopAgent exploreCoopAgent) {
		this.myAgent=exploreCoopAgent;
	}

	@Override
	public void action() {
		MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM), 
				MessageTemplate.or(
						MessageTemplate.or(MessageTemplate.MatchProtocol("PINGMAP"),MessageTemplate.MatchProtocol("SHARE-MAP")),
						MessageTemplate.or(MessageTemplate.MatchProtocol("SHARE-PARTIAL-MAP"),MessageTemplate.MatchProtocol("SHARE-PRIORITY"))));
		ACLMessage msg= this.myAgent.receive(mt);
		while(msg!=null) {
			msg=this.myAgent.receive(mt);
		}

	}
	
	public int onEnd() {
		return 0;
	}

}
