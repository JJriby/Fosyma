package eu.su.mas.dedaleEtu.mas.behaviours;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.SimpleBehaviour;

public class HuntSearchBehaviour extends SimpleBehaviour {
	
	private static final long serialVersionUID = 8567689731496787661L;
    private boolean finished = false;
    private MapRepresentation myMap;

    public HuntSearchBehaviour(final AbstractDedaleAgent myagent, MapRepresentation myMap) {
        super(myagent);
        this.myMap = myMap;
    }

	@Override
	public void action() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean done() {
		// TODO Auto-generated method stub
		return false;
	}

}
