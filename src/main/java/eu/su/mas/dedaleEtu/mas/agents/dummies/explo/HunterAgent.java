package eu.su.mas.dedaleEtu.mas.agents.dummies.explo;

import java.util.ArrayList;
import java.util.List;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedale.mas.agent.behaviours.platformManagment.startMyBehaviours;
import eu.su.mas.dedaleEtu.mas.behaviours.HuntBeginBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.HuntSearchBehaviour;
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
    private List<AID> list_agentNames;

    protected void setup() {
        super.setup();
        registerAgentService();

        addBehaviour(new HuntBeginBehaviour(this));
        
        System.out.println("The agent " + getLocalName() + " is initialized and waiting for map data.");
    }

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
    }

    public void startFSMBehaviours() {
        List<Behaviour> lb = new ArrayList<>();
        FSMBehaviour fsm = setupFSMBehaviour();
        lb.add(fsm);
        addBehaviour(new startMyBehaviours(this, lb));
    }

    private FSMBehaviour setupFSMBehaviour() {
        FSMBehaviour fsm = new FSMBehaviour(this) {
            @Override
            public int onEnd() {
                System.out.println("FSM behaviour completed.");
                myAgent.doDelete();
                return super.onEnd();
            }
        };

        fsm.registerFirstState(new HuntSearchBehaviour(this, getMap()), "SEARCH");
        fsm.registerState(new HuntApproachBehaviour(this, getMap()), "APPROACH");
        fsm.registerLastState(new HuntAttackBehaviour(this, getMap()), "ATTACK");

        fsm.registerDefaultTransition("SEARCH", "SEARCH");
        fsm.registerTransition("SEARCH", "APPROACH", 1);
        fsm.registerDefaultTransition("APPROACH", "SEARCH");
        fsm.registerTransition("APPROACH", "ATTACK", 1);
        fsm.registerTransition("ATTACK", "SEARCH", 0);
        fsm.registerDefaultTransition("ATTACK", "SEARCH");

        return fsm;
    }

    @Override
    protected void takeDown() {
        deregisterAgentService();
        System.out.println("Agent " + getLocalName() + ": Done and dusted!");
    }

    private void deregisterAgentService() {
        try {
            DFService.deregister(this);
        } catch (Exception e) {
            System.err.println("Error deregistering agent " + getLocalName());
        }
    }

    public MapRepresentation getMap() {
        return this.myMap;
    }

    public void setMap(MapRepresentation mapRepresentation) {
        this.myMap = mapRepresentation;
//        startFSMBehaviours();  // Start FSM behaviours after map is set
    }

    public List<AID> getList_agentNames() {
        return list_agentNames;
    }

    public void setList_agentNames(List<AID> list_agentNames) {
        this.list_agentNames = list_agentNames;
    }
}
