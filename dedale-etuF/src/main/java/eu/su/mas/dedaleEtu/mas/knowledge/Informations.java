package eu.su.mas.dedaleEtu.mas.knowledge;

import java.io.Serializable;
import java.util.List;

import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation.MapAttribute;
import dataStructures.serializableGraph.*;
import dataStructures.tuple.Couple;
public class Informations implements  Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8872726727555631558L;
	
	private SerializableSimpleGraph<String, MapAttribute> sg;
	private List<Couple<String,Integer>> listDistance;
	
	public Informations(SerializableSimpleGraph<String, MapAttribute> sg,List<Couple<String,Integer>> listDistance) {
		this.sg=sg;
		this.listDistance=listDistance;
	}

	public SerializableSimpleGraph<String, MapAttribute> getGraph() {
		// TODO Auto-generated method stub
		return sg;
	}
	public List<Couple<String,Integer>> getNodes(){
		return listDistance;
	}

}
