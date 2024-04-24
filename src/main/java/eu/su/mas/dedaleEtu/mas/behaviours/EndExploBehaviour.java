package eu.su.mas.dedaleEtu.mas.behaviours;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import dataStructures.serializableGraph.SerializableSimpleGraph;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.dummies.explo.ExploreCoopAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation.MapAttribute;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

public class EndExploBehaviour extends OneShotBehaviour {

    private static final long serialVersionUID = -4205918489017546675L;
    private ExploreCoopAgent myAgent;

    public EndExploBehaviour(ExploreCoopAgent myAgent) {
        super(myAgent);
        this.myAgent = myAgent;
    }

    @Override
    public void action() {
        System.out.println("Exploration Ended. Transitioning to hunt...");
        
        // Deregister from the exploration service
        try {
            DFService.deregister(myAgent);
        } catch (FIPAException e) {
            e.printStackTrace();
        }
        
        // Register for the hunting service
        ServiceDescription sd = new ServiceDescription();
        sd.setType("HUNTING");
        sd.setName(myAgent.getLocalName());
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(myAgent.getAID());
        dfd.addServices(sd);
        try {
            DFService.register(myAgent, dfd);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
        
        // Retrieve the map and share it with hunters
        SerializableSimpleGraph<String, MapAttribute> map = myAgent.getMap().getSerializableGraph();
        
        shareMapWithHunters(map);
        myAgent.takeDown();
    }

    private void shareMapWithHunters(SerializableSimpleGraph<String, MapAttribute> map) {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setProtocol("SHARE-MAP");

        // Assuming you have a method to get the list of hunter agents
        for (AID hunterAid : getHunterAgents()) {
            msg.addReceiver(hunterAid);
            
            
            System.out.println("____________HUNTER AID____________");
            System.out.println(hunterAid);
            System.out.println("________________________");
        }

        try {
            msg.setContentObject(map);
        } catch (IOException e) {
            e.printStackTrace();
        }

        myAgent.send(msg);
        System.out.println("Map shared with Hunter Agents.");
        
    }

    // Dummy method to represent getting the list of hunter agents
    private List<AID> getHunterAgents() {
        // You would have the actual implementation here
        List<AID> hunterAids = new ArrayList<>();
        DFAgentDescription dfd = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType("HUNTING");
		dfd.addServices(sd);
		DFAgentDescription[] result = null;
		
		
		DFAgentDescription me = new DFAgentDescription();
		me.setName(this.myAgent.getAID());
		
		
		
		try {
			result = DFService.search(this.myAgent, dfd);
		} catch (FIPAException e) {
			e.printStackTrace();
		}
		for (int i = 1; i < result.length; i++) {
			hunterAids.add(result[i].getName());
		}
		hunterAids.remove(me.getName());
        return hunterAids;
    }
}
