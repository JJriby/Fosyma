package eu.su.mas.dedaleEtu.mas.behaviours;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Location;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.env.gs.gsLocation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.domain.DFService;

public class FullHuntingBehaviour extends SimpleBehaviour{
	private static final long serialVersionUID = 8567689731496787661L;
	private boolean finished = false;
	private MapRepresentation myMap;
	private boolean hasLeadership;
	private List<String> ambushPoints;
	private String lastKnownGolemPosition;
	private int tickCount;
	private int maxIterations = 200;
	private boolean shouldMove;
	private List<AID> huntingAgents;
	private List<AID> explorationAgents;
	private int totalAgents;
	private int numberOfGolems;
	private String previousPosition;
	private List<AID> blockingAgents;
	private String blockPosition;
	private int sentAmbushers;
	private int messageTimer;

	public FullHuntingBehaviour(final AbstractDedaleAgent myagent, MapRepresentation map, String lastKnownPosition, int golemCount) {
        super(myagent);
        this.myMap = map;
        this.lastKnownGolemPosition = lastKnownPosition;
        this.shouldMove = true;
        this.huntingAgents = new ArrayList<>();
        this.explorationAgents = new ArrayList<>();
        this.numberOfGolems = golemCount;
        this.previousPosition = myagent.getCurrentPosition().toString();
        this.blockingAgents = new ArrayList<>();
        this.hasLeadership = true;
        this.sentAmbushers = 0;
        this.messageTimer = 0;

        initializeAgentLists();
    }

	private void initializeAgentLists() {
		try {
			// Hunting agents
			DFAgentDescription huntTemplate = new DFAgentDescription();
			ServiceDescription huntSd = new ServiceDescription();
			huntSd.setType("CHASSE");
			huntTemplate.addServices(huntSd);
			DFAgentDescription[] huntResults = DFService.search(myAgent, huntTemplate);
			for (DFAgentDescription dfd : huntResults) {
				if (!dfd.getName().equals(myAgent.getAID())) {
					huntingAgents.add(dfd.getName());
				}
			}

			// Exploration agents
			DFAgentDescription exploreTemplate = new DFAgentDescription();
			ServiceDescription exploreSd = new ServiceDescription();
			exploreSd.setType("EXPLORATION");
			exploreTemplate.addServices(exploreSd);
			DFAgentDescription[] exploreResults = DFService.search(myAgent, exploreTemplate);
			for (DFAgentDescription dfd : exploreResults) {
				explorationAgents.add(dfd.getName());
			}

			totalAgents = huntingAgents.size() + explorationAgents.size() + 1; // Including self
			ambushPoints = myMap.getAmbushPoint(totalAgents, numberOfGolems, 3);
		} catch (FIPAException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void action() {
		detectGolem();
		handleMessages();
		decideNextMove();
		manageMovement();
	}

	private void detectGolem() {
		List<Couple<Location, List<Couple<Observation, Integer>>>> observations = ((AbstractDedaleAgent) this.myAgent)
				.observe();
		for (Couple<Location, List<Couple<Observation, Integer>>> obs : observations) {
			Location loc = obs.getLeft();
			for (Couple<Observation, Integer> detail : obs.getRight()) {
				if (detail.getLeft() == Observation.STENCH) {
					lastKnownGolemPosition = loc.toString();
					System.out.println(myAgent.getLocalName() + " detected golem at " + lastKnownGolemPosition);
					break;
				}
			}
		}
	}

	private void handleMessages() {
		ACLMessage msg = myAgent.receive(MessageTemplate.MatchProtocol("Chase"));
		if (msg != null) {
			String content = msg.getContent();
			switch (content) {
			case "need_ambushers":
				if (hasLeadership) {
					sendAmbusherPosition(msg.getSender());
				} else {
					informReadyToHelp(msg.getSender());
				}
				break;
			case "is_golem_blocked":
				confirmBlockage(msg.getSender());
				break;
			}
		}
	}

	private void sendAmbusherPosition(AID receiver) {
		if (!ambushPoints.isEmpty()) {
			String position = ambushPoints.remove(0);
			ACLMessage reply = new ACLMessage(ACLMessage.INFORM);
			reply.addReceiver(receiver);
			reply.setContent(position);
			reply.setProtocol("Chase");
			myAgent.send(reply);
		}
	}

	private void informReadyToHelp(AID leader) {
		ACLMessage reply = new ACLMessage(ACLMessage.INFORM);
		reply.addReceiver(leader);
		reply.setContent("ready_to_help");
		reply.setProtocol("Chase");
		myAgent.send(reply);
	}

	private void confirmBlockage(AID requester) {
		ACLMessage reply = new ACLMessage(ACLMessage.INFORM);
		reply.addReceiver(requester);
		reply.setContent("golem_blocked");
		reply.setProtocol("Chase");
		myAgent.send(reply);
	}

	private void decideNextMove() {
		if (tickCount++ > maxIterations) {
			System.out.println(myAgent.getLocalName() + " reassessing strategy...");
			ambushPoints = myMap.getAmbushPoint(totalAgents, numberOfGolems, 3);
			tickCount = 0;
		}
	}

	private void manageMovement() {
		if (shouldMove) {
			if (lastKnownGolemPosition != null && !lastKnownGolemPosition.isEmpty()) {
				moveTowards(lastKnownGolemPosition);
			} else {
				moveRandomly();
			}
		}
	}

	private void moveTowards(String destination) {
		List<String> path = myMap.getShortestPath(myAgent.getLocalName(), destination);
		if (!path.isEmpty()) {
			String nextStep = path.get(0);
			((AbstractDedaleAgent) myAgent).moveTo(new gsLocation(nextStep));
		} else {
			lastKnownGolemPosition = null; // Reset if at destination
		}
	}

	private void moveRandomly() {
		List<String> possibleMoves = myMap.getSurroundingPoints(myAgent.getLocalName());
		String nextMove = possibleMoves.get(new Random().nextInt(possibleMoves.size()));
		((AbstractDedaleAgent) myAgent).moveTo(new gsLocation(nextMove.toString()));
	}

	@Override
	public boolean done() {
		return finished;
	}

}
