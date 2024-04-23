package eu.su.mas.dedaleEtu.mas.behaviours;

import java.util.ArrayList;
import java.util.List;

import dataStructures.serializableGraph.SerializableSimpleGraph;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.dummies.explo.ExploreCoopAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation.MapAttribute;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.core.behaviours.Behaviour;
//import eu.su.mas.dedale.mas.agent.behaviours.startMyBehaviours;

public class EndExploBehaviour extends OneShotBehaviour {
	// cette classe est utilisée à la fin de l'exploration de carte, on inscrit
	// l'agent au service chasse et on commence la behaviour de chasse
	/**
	 * 
	 */
	private static final long serialVersionUID = -4205918489017546675L;
	private Agent myAgent;

	public EndExploBehaviour(Agent myAgent) {
		this.myAgent = myAgent;
	}

	@Override
	public void action() {
		System.out.println("End");
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(myAgent.getAID());
		try {
			DFService.deregister(this.myAgent);
		} catch (FIPAException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ServiceDescription sd = new ServiceDescription();
		sd.setType("CHASSE");

		sd.setName(myAgent.getLocalName());

		dfd.addServices(sd);

		try {
			DFService.register(this.myAgent, dfd);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
		((ExploreCoopAgent) this.myAgent).endExplo();
		System.out.println("explo_ended,start chase");
		SerializableSimpleGraph<String, MapAttribute> map = ((ExploreCoopAgent) this.myAgent).getMap().getSerializableGraph();
		System.out.println("______________MAP SHARE_________________");
		System.out.println(map);
		System.out.println("______________END MAP SHARE_________________");
//		List<Behaviour> lb = new ArrayList<Behaviour>();
//		ChaserBehaviour b = new ChaserBehaviour((AbstractDedaleAgent) this.myAgent,
//				((ExploreCoopAgent) this.myAgent).getMap(), ((ExploreCoopAgent) this.myAgent).getLastKnownPosition(),
//				-1);
//		this.myAgent.addBehaviour(b);
//		lb.add(b);
//		this.myAgent.addBehaviour(new startMyBehaviours((AbstractDedaleAgent)this.myAgent,lb));
		System.out.println("chase behavior created");

	}

}
