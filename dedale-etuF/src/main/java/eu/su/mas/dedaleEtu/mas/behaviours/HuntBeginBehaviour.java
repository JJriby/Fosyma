package eu.su.mas.dedaleEtu.mas.behaviours;

import java.util.ArrayList;
import java.util.List;

import dataStructures.serializableGraph.SerializableSimpleGraph;
import eu.su.mas.dedale.mas.agent.behaviours.platformManagment.startMyBehaviours;
import eu.su.mas.dedaleEtu.mas.agents.dummies.explo.HunterAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation.MapAttribute;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.FSMBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

public class HuntBeginBehaviour extends CyclicBehaviour {
    private boolean mapReceived = false;
    private HunterAgent myAgent;

    public HuntBeginBehaviour(HunterAgent a) {
        this.myAgent = a;
    }

    public void action() {
        ACLMessage msg = myAgent.receive(MessageTemplate.MatchProtocol("SHARE-MAP"));
        if (msg != null) {
            try {
                SerializableSimpleGraph<String, MapRepresentation.MapAttribute> receivedMap = 
                    (SerializableSimpleGraph<String, MapRepresentation.MapAttribute>) msg.getContentObject();
                
                
                System.out.println("____________Recieved Map for "+this.myAgent.getLocalName()+" ____________");
                System.out.println(receivedMap);
                System.out.println("________________________");

                MapRepresentation mapRepresentation = new MapRepresentation();
                mapRepresentation.mergeMap(receivedMap);
                myAgent.setMap(mapRepresentation);
                
                register_ListHunterAgentNames();

                System.out.println("Map received and integrated for " + myAgent.getLocalName());
                myAgent.startFSMBehaviours(); 
                myAgent.removeBehaviour(this); 
            } catch (UnreadableException | ClassCastException e) {
                e.printStackTrace();
            }
        } else {
            block();
        }
    }
    
    private void register_ListHunterAgentNames() {
    	
    	List<AID> hunterAids = new ArrayList<>();
        
		DFAgentDescription[] result = null;
		DFAgentDescription dfd = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType("HUNTING");
		dfd.addServices(sd);
		
		DFAgentDescription me = new DFAgentDescription();
		me.setName(this.myAgent.getAID());
		
		try {
			result = DFService.search(this.myAgent, dfd);
		} catch (FIPAException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < result.length; i++) {
			hunterAids.add(result[i].getName());
			System.out.println("__________Coop Hunter added for____________"+this.myAgent.getLocalName());
			System.out.println(result[i].getName());
			System.out.println("_____________________________________________");
		}
		hunterAids.remove(me.getName());
		
		this.myAgent.setList_HunteragentNames(hunterAids); 
		System.out.println();
    }
}
