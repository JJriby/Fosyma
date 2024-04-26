package eu.su.mas.dedaleEtu.mas.behaviours;

import java.util.List;
import java.util.Random;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Location;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import jade.core.behaviours.TickerBehaviour;

public class RandomWalkBehaviour2 extends TickerBehaviour{
	private static final long serialVersionUID = 3088209402507795289L;

    public RandomWalkBehaviour2(final AbstractDedaleAgent myagent) {
        super(myagent, 1000);
    }

    @Override
    public void onTick() {
        Location myPosition = ((AbstractDedaleAgent) this.myAgent).getCurrentPosition();

        if (myPosition != null) {
            List<Couple<Location, List<Couple<Observation, Integer>>>> lobs = ((AbstractDedaleAgent) this.myAgent).observe();

            if (lobs != null && !lobs.isEmpty()) {
                // Move randomly
                Random random = new Random();
                int moveId = 1 + random.nextInt(lobs.size() - 1); 
                ((AbstractDedaleAgent) this.myAgent).moveTo(lobs.get(moveId).getLeft());
            } else {
                System.out.println(this.myAgent.getLocalName() + ": No movements possible.");
            }
        } else {
            System.out.println(this.myAgent.getLocalName() + ": I'm lost, I don't know where I am.");
        }
    }

}
