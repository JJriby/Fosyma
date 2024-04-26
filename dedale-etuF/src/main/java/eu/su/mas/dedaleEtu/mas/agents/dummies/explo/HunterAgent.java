package eu.su.mas.dedaleEtu.mas.agents.dummies.explo;

import java.util.ArrayList;
import java.util.List;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedale.mas.agent.behaviours.platformManagment.startMyBehaviours;
import eu.su.mas.dedaleEtu.mas.behaviours.HuntBeginBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.HuntSearchBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.RandomWalkBehaviour2;
import eu.su.mas.dedaleEtu.mas.behaviours.HandleHunterMessagesBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.HuntApproachBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.HuntAttackBehaviour;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.FSMBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;

public class HunterAgent extends AbstractDedaleAgent {
    private static final long serialVersionUID = -7979469610241668140L;
    private MapRepresentation myMap;
    private List<AID> list_HunteragentNames;
    
    private RandomWalkBehaviour2 randomWalkBehaviour;
    
    private String stenchDetectedPosition = null;

    protected void setup() {
    	super.setup();
        registerAgentService();

        
        
        randomWalkBehaviour = new RandomWalkBehaviour2(this);

        // Other behaviors for initial setup
        List<Behaviour> lb = new ArrayList<>();
        lb.add(randomWalkBehaviour);
        lb.add(new HuntBeginBehaviour(this));

        // Start initial behaviors
        addBehaviour(new startMyBehaviours(this, lb));
        
        // Handle messages continuously throughout the agent's life
        
        

        System.out.println("The agent " + getLocalName() + " is initialized.");
    }
    
//    private void register_ListHunterAgentNames(DFAgentDescription dfd) {
//    	
//    	List<AID> hunterAids = new ArrayList<>();
//        
//		DFAgentDescription[] result = null;
//		
//		
//		DFAgentDescription me = new DFAgentDescription();
//		me.setName(this.getAID());
//		
//		try {
//			result = DFService.search(this, dfd);
//		} catch (FIPAException e) {
//			e.printStackTrace();
//		}
//		for (int i = 0; i < result.length; i++) {
//			hunterAids.add(result[i].getName());
//			System.out.println("__________Coop Hunter added for____________"+this.getLocalName());
//			System.out.println(result[i].getName());
//			System.out.println("_____________________________________________");
//		}
//		hunterAids.remove(me.getName());
//		
//		this.list_HunteragentNames = hunterAids;
//		System.out.println();
//    }

    private void registerAgentService() {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("HUNTING");
        sd.setName(getLocalName());
        dfd.addServices(sd);

        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
//        register_ListHunterAgentNames(dfd);
    }

    public void startFSMBehaviours() {
    	removeBehaviour(randomWalkBehaviour);
    	addBehaviour(new HandleHunterMessagesBehaviour(this, this.myMap));
        
        FSMBehaviour fsm = new FSMBehaviour(this) {
            @Override
            public int onEnd() {
                System.out.println("FSM behaviour completed.");
                myAgent.doDelete();
                return super.onEnd();
            }
        };

        // Register states with the FSM
        fsm.registerFirstState(new HuntSearchBehaviour(this, myMap), "SEARCH");
        fsm.registerState(new HuntApproachBehaviour(this, myMap), "APPROACH");
        fsm.registerLastState(new HuntAttackBehaviour(this, myMap), "ATTACK");

        // Configure transitions
        fsm.registerDefaultTransition("SEARCH", "APPROACH", new String[] {"SEARCH", "APPROACH"});
        fsm.registerTransition("APPROACH", "ATTACK", 1);
        fsm.registerTransition("APPROACH", "SEARCH", 0);

        addBehaviour(fsm);
    }

    public MapRepresentation getMap() {
        return this.myMap;
    }

    public void setMap(MapRepresentation mapRepresentation) {
        this.myMap = mapRepresentation;
    }

    public List<AID> getList_HunteragentNames() {
        return list_HunteragentNames;
    }

    public void setList_HunteragentNames(List<AID> list_agentNames) {
        this.list_HunteragentNames = list_agentNames;
    }
    
    public void set_stenchDetectedPosition(String nodeID) {
    	this.stenchDetectedPosition = nodeID;
    }
    public String get_stenchDetectedPosition() {
    	return this.stenchDetectedPosition;
    }
}
