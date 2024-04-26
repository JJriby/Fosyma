package eu.su.mas.dedaleEtu.mas.behaviours;

import java.io.IOException;
import java.util.List;

import dataStructures.serializableGraph.SerializableSimpleGraph;
import eu.su.mas.dedale.env.Location;
import eu.su.mas.dedaleEtu.mas.agents.dummies.explo.HunterAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class HandleHunterMessagesBehaviour extends CyclicBehaviour {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private HunterAgent myAgent;
	MapRepresentation myMap;

	public HandleHunterMessagesBehaviour(HunterAgent agent, MapRepresentation map) {
		super(agent);
		this.myAgent = agent;
		this.myMap = map;
	}

	@Override
	public void action() {
//		System.out.println("Checking for messages in HandleHunterMessagesBehaviour.");
	    MessageTemplate template = MessageTemplate.or(
	        MessageTemplate.MatchProtocol("RequestHelp"),
	        MessageTemplate.MatchProtocol("StenchDetected")
	    );

	    ACLMessage msg = myAgent.receive(template);
	    if (msg != null) {
	        System.out.println(myAgent.getLocalName() + " received a message: " + msg.getContent());
	        switch (msg.getProtocol()) {
	            case "RequestHelp":
	                handleRequestHelp(msg);
	    	        System.out.println(myAgent.getLocalName() + " did receive a help message.");
	                break;
	            case "StenchDetected":
	                handleStenchDetected(msg);
	                break;
	        }
	    } else {
//	        System.out.println(myAgent.getLocalName() + " did not receive any messages.");
//	        block();  // You can adjust the block time to see effects
	    }
	}


	private void handleRequestHelp(ACLMessage msg) {
		String locationNeedingHelp = msg.getContent();
		System.out.println(myAgent.getLocalName() + " received a help request near " + locationNeedingHelp);

		// Decide how to assist based on the agent's current capability and position
		if (canMoveTowards(locationNeedingHelp)) {
//			myAgent.addBehaviour(new MoveTowardsBehaviour(myAgent, locationNeedingHelp));
		} else {
			sendMapData(msg.getSender(), locationNeedingHelp);
		}
	}

	private void handleStenchDetected(ACLMessage msg) {
		String stenchLocation = msg.getContent();
		System.out.println(myAgent.getLocalName() + " received a stench update at " + stenchLocation);

		// Update the agent's knowledge or state with the new stench information
		myAgent.set_stenchDetectedPosition(stenchLocation);

		// Optionally, initiate or change the behavior to approach the new stench source
		if (!myAgent.getCurrentPosition().getLocationId().equals(stenchLocation)) {
			myAgent.addBehaviour(new HuntApproachBehaviour(myAgent, this.myMap));
		}
	}

	private boolean canMoveTowards(String location) {
	    // Retrieve the current position of the agent
	    Location myPosition = myAgent.getCurrentPosition();
	    if (myPosition == null) {
	        System.err.println("Current position is unknown, cannot move towards the requested location.");
	        return false;
	    }

	    // Check if the location is already the current position
	    if (myPosition.getLocationId().equals(location)) {
	        System.out.println("Already at the requested location.");
	        return false;
	    }

	    // Use the map to find a path to the requested location
	    List<String> path = this.myMap.getShortestPath(myPosition.getLocationId(), location);

	    // Check if a path exists
	    if (path == null || path.isEmpty()) {
	        System.out.println("No path found to the requested location: " + location);
	        return false;
	    }

	    // Optional: Check the length of the path if there's a limit on how far the agent should move
	    if (path.size() > 6) { 
	        System.out.println("The requested location is too far away (" + path.size() + " steps).");
	        return false;
	    }

	    // Optional: Check for known obstacles or hazardous conditions along the path
	    if (pathContainsObstacles(path)) {
	        System.out.println("Path to requested location contains obstacles or hazards.");
	        return false;
	    }

	    // If all checks are passed, moving towards the location is feasible
	    return true;
	}

	private boolean pathContainsObstacles(List<String> path) {
	    
	    for (String nodeId : path) {
//	        if (myMap.isObstacle(nodeId)) {  // Assume `isObstacle` method exists in MapRepresentation
//	            return true;
//	        }
	    }
	    return false;
	}

	private void sendMapData(AID requester, String location) {
		try {
			SerializableSimpleGraph<String, MapRepresentation.MapAttribute> sg = myMap.getSerializableGraph();
			ACLMessage mapMsg = new ACLMessage(ACLMessage.INFORM);
			mapMsg.addReceiver(requester);
			mapMsg.setProtocol("MapData");
			mapMsg.setContentObject(sg);
			myAgent.send(mapMsg);
			System.out.println("Sent map data to " + requester.getLocalName());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
