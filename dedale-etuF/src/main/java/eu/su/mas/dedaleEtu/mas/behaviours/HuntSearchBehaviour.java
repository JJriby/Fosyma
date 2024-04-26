package eu.su.mas.dedaleEtu.mas.behaviours;

import java.util.List;
import java.util.Random;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.env.gs.gsLocation;
import eu.su.mas.dedale.env.Location;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.dummies.explo.HunterAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation.MapAttribute;
import jade.core.behaviours.SimpleBehaviour;

public class HuntSearchBehaviour extends SimpleBehaviour {

    private static final long serialVersionUID = 3052769121784171812L;
    private boolean finished = false;
    private MapRepresentation myMap;

    public HuntSearchBehaviour(final HunterAgent myagent, MapRepresentation myMap) {
        super(myagent);
        this.myMap = myMap;
    }

    public void action() {
        HunterAgent dedaleAgent = (HunterAgent) this.myAgent;
        Location myPosition = dedaleAgent.getCurrentPosition();

        if (myPosition != null) {
            this.myMap.addNode(myPosition.getLocationId(), MapAttribute.closed);
            List<Couple<Location, List<Couple<Observation, Integer>>>> lobs = dedaleAgent.observe();
            
            try {
				this.myAgent.doWait(500);  // Slight wait to simulate real-time decision making
			} catch (Exception e) {
				e.printStackTrace();
			}

            boolean stenchDetected = false;
            String potentialStenchSource = null;

            for (Couple<Location, List<Couple<Observation, Integer>>> loc : lobs) {
                Location observedLocation = loc.getLeft();
                this.myMap.addNewNode(observedLocation.getLocationId());
                this.myMap.addEdge(myPosition.getLocationId(), observedLocation.getLocationId());
                for (Couple<Observation, Integer> obs : loc.getRight()) {
                    if (obs.getLeft() == Observation.STENCH) {  // Stench indicates potential ogre nearby
                        stenchDetected = true;
                        potentialStenchSource = observedLocation.getLocationId();
                        System.out.println("Stench detected at " + potentialStenchSource);
                        break;
                    }
                }
                if (stenchDetected) break;
            }

            if (stenchDetected ) {
            	dedaleAgent.set_stenchDetectedPosition(potentialStenchSource);
//            	System.out.println("DONE !!!!!!!!!!!!!!!!");
            	done();
                finished = true;  // End this behavior
            } else {
                // Continue random walk or other logic when no stench is detected
                if (!lobs.isEmpty()) {
                    int index = new Random().nextInt(lobs.size());
                    dedaleAgent.moveTo(new gsLocation(lobs.get(index).getLeft().getLocationId()));
                } else {
                    System.out.println("No accessible new locations observed.");
                    finished = true;  // Consider ending exploration or other logic
                }
            }
        } else {
            System.err.println("Error: Could not retrieve current position for agent " + dedaleAgent.getLocalName());
            finished = true;
        }
    }

    @Override
    public boolean done() {
        return finished;
    }
}
