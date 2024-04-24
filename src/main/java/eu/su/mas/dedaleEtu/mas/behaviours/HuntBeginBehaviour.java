package eu.su.mas.dedaleEtu.mas.behaviours;

import java.util.ArrayList;
import java.util.List;

import dataStructures.serializableGraph.SerializableSimpleGraph;
import eu.su.mas.dedale.mas.agent.behaviours.platformManagment.startMyBehaviours;
import eu.su.mas.dedaleEtu.mas.agents.dummies.explo.HunterAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation.MapAttribute;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.FSMBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

public class HuntBeginBehaviour extends CyclicBehaviour {
    private static final long serialVersionUID = 1L;
    private HunterAgent myAgent;
    private boolean mapReceived = false;

    public HuntBeginBehaviour(HunterAgent a) {
        super(a);
        this.myAgent = a;
    }

    @Override
    public void action() {
        ACLMessage msg = myAgent.receive(MessageTemplate.MatchProtocol("SHARE-MAP"));
        if (msg != null) {
            try {
                SerializableSimpleGraph<String, MapAttribute> receivedMap =  
                		(SerializableSimpleGraph<String, MapAttribute>) msg.getContentObject();
                MapRepresentation mapRepresentation = new MapRepresentation();
                mapRepresentation.mergeMap(receivedMap);
//                mapRepresentation.prepareMigration();
                myAgent.setMap(new MapRepresentation());
                System.out.println("Map received and set for " + myAgent.getLocalName());
//                System.out.println("Map received " + myAgent.getMap().getSerializableGraph());
                mapReceived = true;
            } catch (UnreadableException | ClassCastException e) {
                e.printStackTrace();
            }
        } else {
            block();
        }
        if (mapReceived) {
        	List<Behaviour> lb=new ArrayList<Behaviour>();
        	
        	lb.add(new RandomWalkBehaviour(this.myAgent));
        	myAgent.addBehaviour(new startMyBehaviours(this.myAgent,lb));
        	myAgent.removeBehaviour(this);
        	
            
        }
    }
}
