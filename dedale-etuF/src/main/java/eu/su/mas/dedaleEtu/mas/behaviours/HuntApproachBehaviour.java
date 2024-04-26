package eu.su.mas.dedaleEtu.mas.behaviours;

import java.util.List;

import eu.su.mas.dedale.env.Location;
import eu.su.mas.dedale.env.gs.gsLocation;
import eu.su.mas.dedaleEtu.mas.agents.dummies.explo.HunterAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;

public class HuntApproachBehaviour extends SimpleBehaviour {
    private static final long serialVersionUID = -1837475783203937853L;
    private MapRepresentation myMap;
    private boolean finished = false;
    private HunterAgent myAgent;

    public HuntApproachBehaviour(HunterAgent agent, MapRepresentation map) {
        super(agent);
        this.myAgent = agent;
        this.myMap = map;
    }

    @Override
    public void action() {
        Location myPosition = myAgent.getCurrentPosition();
        String targetLocation = myAgent.get_stenchDetectedPosition();

        if (myPosition != null && targetLocation != null) {
            List<String> pathToStench = myMap.getShortestPath(myPosition.getLocationId(), targetLocation);
            if (!pathToStench.isEmpty()) {
                String nextMove = pathToStench.get(0);
                if (!myAgent.moveTo(new gsLocation(nextMove))) {
                    // Broadcast to other agents that the path is blocked
                    broadcastStenchDetection(nextMove);
                    requestHelp(myPosition.getLocationId());
                } else {
                    // Successfully moved towards the target
                    finished = true;
                }
            } else {
                requestHelp(myPosition.getLocationId());
                System.out.println("Path to target is blocked or non-existent.");
                finished = true;
            }
        } else {
            System.err.println("Error: Could not retrieve current position or target position is unknown.");
            finished = true;
        }
    }

    private void broadcastStenchDetection(String location) {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setProtocol("StenchDetected");
        for (AID agentName : myAgent.getList_HunteragentNames()) {
            msg.addReceiver(agentName);
        }
        msg.setContent(location);
        System.out.println(myAgent.getLocalName()+": Stench Detected Message To "+ msg.getAllReceiver());
        myAgent.send(msg);
    }

    private void requestHelp(String currentLocation) {
        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        msg.setProtocol("RequestHelp");
        msg.setContent(currentLocation);
        for (AID agent : myAgent.getList_HunteragentNames()) {
            if (!agent.equals(myAgent.getAID())) {
                msg.addReceiver(agent);
                System.out.println(agent);
            }
        }
        System.out.println(myAgent.getLocalName()+": Help Message To "+ msg.getAllReceiver().toString());
        myAgent.send(msg);
    }

    @Override
    public boolean done() {
        return finished;
    }
}

