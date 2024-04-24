package eu.su.mas.dedaleEtu.mas.agents.dummies.explo;

import java.util.ArrayList;
import java.util.List;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;

import eu.su.mas.dedale.mas.agent.behaviours.platformManagment.*;
import eu.su.mas.dedaleEtu.mas.behaviours.AnswerPingMapBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.BeginBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.ClearMapMailBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.EndExploBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.ExploBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.PingingBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.ReceivePriorityBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.SendMapBehviour;
import eu.su.mas.dedaleEtu.mas.behaviours.SendPartialMapBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.SendPriorityBehaviour;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.FSMBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

/**
 * <pre>
 * ExploreCoop agent. 
 * Basic example of how to "collaboratively" explore the map
 *  - It explore the map using a DFS algorithm and blindly tries to share the topology with the agents within reach.
 *  - The shortestPath computation is not optimized
 *  - Agents do not coordinate themselves on the node(s) to visit, thus progressively creating a single file. It's bad.
 *  - The agent sends all its map, periodically, forever. Its bad x3.
 *  
 * It stops when all nodes have been visited.
 * 
 * 
 * </pre>
 * 
 * @author hc
 *
 */

public class ExploreCoopAgent extends AbstractDedaleAgent {

	private static final long serialVersionUID = -7969469610241668140L;
	private MapRepresentation myMap;
	private boolean pinged = false;
	private List<AID> list_agentNames;

	private List<String> priority = new ArrayList<String>();
	private boolean explo = true;
	private ExploBehaviour exploration;
	private PingingBehaviour ping;
	private EndExploBehaviour endExplo;
	private SendMapBehviour sendMap;
	private SendPartialMapBehaviour sendPartialMap;
	private SendPriorityBehaviour sendPriority;
	private String lastKnownPosition;
	private BeginBehaviour begin;
	private AID partner;
	private ReceivePriorityBehaviour receivePriority;
	private ClearMapMailBehaviour clearMapMail;
	private FSMBehaviour explore;
	private AnswerPingMapBehaviour answer;

	/**
	 * This method is automatically called when "agent".start() is executed.
	 * Consider that Agent is launched for the first time. 1) set the agent
	 * attributes 2) add the behaviours
	 * 
	 */
	protected void setup() {

		super.setup();

		final Object[] args = getArguments();

		this.exploration = new ExploBehaviour(this);
		this.ping = new PingingBehaviour(this);
		this.sendMap = new SendMapBehviour(this);
		this.sendPartialMap = new SendPartialMapBehaviour(this);
		this.sendPriority = new SendPriorityBehaviour(this);
		this.endExplo = new EndExploBehaviour(this);
		this.begin = new BeginBehaviour(this);
		this.receivePriority = new ReceivePriorityBehaviour(this);
		this.clearMapMail = new ClearMapMailBehaviour(this);
		this.explore = new FSMBehaviour(this);
		// etats
		explore.registerFirstState(this.begin, "Begin");
		explore.registerState(this.ping, "Ping");
		explore.registerState(this.exploration, "Explo");
		explore.registerState(this.sendMap, "sendMap");
		explore.registerState(this.sendPartialMap, "sendPartialMap");
		explore.registerState(this.sendPriority, "sendPriority");
		explore.registerState(this.receivePriority, "receivePriority");
		explore.registerState(this.clearMapMail, "clearMapMail");
		explore.registerLastState(this.endExplo, "EndExplo");

		// transitions
		explore.registerTransition("Begin", "Explo", 0);
		explore.registerTransition("Explo", "Ping", 0);
		explore.registerTransition("Ping", "Explo", 0);
		explore.registerTransition("Ping", "sendMap", 2);
		explore.registerTransition("Ping", "sendPartialMap", 1);
		explore.registerTransition("Explo", "EndExplo", 3);
		explore.registerTransition("sendMap", "sendPriority", 0);
		explore.registerTransition("sendPriority", "clearMapMail", 0);
		explore.registerTransition("sendPartialMap", "receivePriority", 0);
		explore.registerTransition("sendPartialMap", "Explo", 1);
		explore.registerTransition("receivePriority", "clearMapMail", 0);
		explore.registerTransition("clearMapMail", "Explo", 0);

		list_agentNames = new ArrayList<AID>();
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());

		ServiceDescription sd = new ServiceDescription();
		sd.setType("EXPLORATION");

		sd.setName(getLocalName());

		dfd.addServices(sd);

		try {
			DFService.register(this, dfd);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}

		List<Behaviour> lb = new ArrayList<Behaviour>();

		/************************************************
		 * 
		 * ADD the behaviours of the Dummy Moving Agent
		 * 
		 ************************************************/

		lb.add(this.explore);

		/***
		 * MANDATORY TO ALLOW YOUR AGENT TO BE DEPLOYED CORRECTLY
		 */

		addBehaviour(new startMyBehaviours(this, lb));

		System.out.println("the  agent " + this.getLocalName() + " is started");

	}

	public boolean getPinged() {
		return pinged;
	}

	public void setPinged(boolean pinged) {
		this.pinged = pinged;
	}

	public List<AID> getAgentList() {
		return list_agentNames;
	}

	public void setPriority(List<String> priority) {
		this.priority = priority;

	}

	public List<String> getPriority() {
		return priority;
	}

	public void finishExplo() {
		this.explo = false;
	}

	public boolean getExplo() {
		return explo;
	}

	public MapRepresentation getMap() {

		return this.myMap;
	}

	public String getLastKnownPosition() {
		return lastKnownPosition;
	}

	public void setLastKnowPosition(String pos) {
		this.lastKnownPosition = pos;
	}

	public void setMap(MapRepresentation mapRepresentation) {
		this.myMap = mapRepresentation;

	}

	public List<AID> getList_agentNames() {
		return list_agentNames;
	}

	public void setList_agentNames(List<AID> list_agentNames) {
		this.list_agentNames = list_agentNames;
	}

	public void setPartner(AID sender) {
		this.partner = sender;

	}

	public AID getPartner() {
		return partner;
	}

	public void endExplo() {

		this.removeBehaviour(this.explore);
		FSMBehaviour passiveExplo = new FSMBehaviour();
		this.answer = new AnswerPingMapBehaviour(this);

		passiveExplo.registerFirstState(this.answer, "Answer");
		passiveExplo.registerState(new SendPartialMapBehaviour(this), "sendPM");

		passiveExplo.registerState(new ClearMapMailBehaviour(this), "clearMM");

		passiveExplo.registerTransition("Answer", "Answer", 0);
		passiveExplo.registerTransition("Answer", "sendPM", 1);

		passiveExplo.registerTransition("sendPM", "clearMM", 0);
		passiveExplo.registerTransition("sendPM", "Answer", 1);
		passiveExplo.registerTransition("clearMM", "Answer", 0);

		this.addBehaviour(passiveExplo);

	}
	
	public void takeDown() {
		super.takeDown();
	}
	
	
	
}
