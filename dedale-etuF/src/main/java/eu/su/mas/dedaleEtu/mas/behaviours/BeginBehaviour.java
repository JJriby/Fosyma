package eu.su.mas.dedaleEtu.mas.behaviours;

import java.util.ArrayList;
import java.util.List;

import eu.su.mas.dedaleEtu.mas.agents.dummies.explo.ExploreCoopAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class BeginBehaviour extends OneShotBehaviour {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7791312216997534115L;

	private ExploreCoopAgent myAgent;

	public BeginBehaviour(ExploreCoopAgent exploreCoopAgent) {
		this.myAgent = exploreCoopAgent;
	}

	@Override
	public void action() {
		System.out.println("Begin");
		this.myAgent.setMap(new MapRepresentation());
		DFAgentDescription dfd = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType("EXPLORATION");
		dfd.addServices(sd);
		DFAgentDescription[] result = null;
		try {
			result = DFService.search(this.myAgent, dfd);
		} catch (FIPAException e) {
			e.printStackTrace();
		}
		DFAgentDescription me = new DFAgentDescription();
		me.setName(this.myAgent.getAID());

		List<AID> list_agentNames = new ArrayList<AID>();
		for (int i = 0; i < result.length; i++) {
			list_agentNames.add(result[i].getName());
		}
		
		list_agentNames.remove(me.getName());
		System.out.println("_____________________LIST AGENTS______________________ FOR "+ this.myAgent.getLocalName());
		System.out.println(list_agentNames);
		System.out.println("_____________________END LIST AGENTS______________________");
		this.myAgent.setList_agentNames(list_agentNames);

	}

	public int onEnd() {
		return 0;
	}

}
