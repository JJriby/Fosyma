package eu.su.mas.dedaleEtu.mas.behaviours;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.SimpleBehaviour;

public class HuntAttackBehaviour extends SimpleBehaviour {
	
	private static final long serialVersionUID = 1L;
	private MapRepresentation myMap;

    public HuntAttackBehaviour(AbstractDedaleAgent agent, MapRepresentation map) {
        super(agent);
        this.myMap = map;
    }

	@Override
	public void action() {
//		System.out.println("ATTAAAAAAAAAAAAAAAAAAACK!!");

	}

	@Override
	public boolean done() {
		// TODO Auto-generated method stub
		return false;
	}

}
