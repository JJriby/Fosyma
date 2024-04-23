package eu.su.mas.dedaleEtu.mas.behaviours;

import java.util.ArrayList;
import java.util.List;


import eu.su.mas.dedaleEtu.mas.agents.dummies.explo.HunterAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import jade.core.AID;

import jade.core.behaviours.OneShotBehaviour;

import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class HuntBeginBehaviour extends OneShotBehaviour  {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean done = false;
	private HunterAgent myAgent;

	public HuntBeginBehaviour(HunterAgent a) {
        this.myAgent = a;
    }
	
	@Override
    public void action() {
        System.out.println("Initializing and setting up for hunter "+ this.myAgent.getLocalName());
        this.myAgent.setMap(new MapRepresentation());
		DFAgentDescription dfd = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType("HUNTING");
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
        done = true;
    }
	
	
//	@Override
//    public boolean done() {
//        return done;
//    }
	
	public int onEnd() {
		return 0;
	}

}
