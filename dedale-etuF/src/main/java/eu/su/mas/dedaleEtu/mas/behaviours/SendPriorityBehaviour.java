package eu.su.mas.dedaleEtu.mas.behaviours;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.dummies.explo.ExploreCoopAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.Informations;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

public class SendPriorityBehaviour extends OneShotBehaviour {

	/**
	 * 
	 */
	private int value=0;
	private static final long serialVersionUID = -314754149388556935L;

	public SendPriorityBehaviour(ExploreCoopAgent exploreCoopAgent) {
		this.myAgent=exploreCoopAgent;	
	}

	@Override
	public void action() {
		
		MessageTemplate msgTemplate=MessageTemplate.and(MessageTemplate.and(
				MessageTemplate.MatchPerformative(ACLMessage.INFORM),MessageTemplate.MatchProtocol("SHARE-PARTIAL-MAP")), 
				MessageTemplate.MatchSender(((ExploreCoopAgent) this.myAgent).getPartner()));
		ACLMessage msgReceived=this.myAgent.blockingReceive(msgTemplate,1000);
		
		if(msgReceived!=null) {
			Informations info=null;
			try {
				info = (Informations) msgReceived.getContentObject();
			} catch (UnreadableException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			((ExploreCoopAgent) this.myAgent).getMap().mergeMap(info.getGraph());
			ArrayList<String> myNodes=new ArrayList<String>();
			ArrayList<String> partnerNodes=new ArrayList<String>();
			for(Couple<String,Integer> node : info.getNodes()) {
				int distPartner=node.getRight();
				System.out.println(((ExploreCoopAgent) this.myAgent).getMap().getShortestPath(((AbstractDedaleAgent)this.myAgent).getCurrentPosition().toString(),node.getLeft()));
				List<String> a = ((ExploreCoopAgent) this.myAgent).getMap().getShortestPath(((AbstractDedaleAgent)this.myAgent).getCurrentPosition().toString(),node.getLeft());
				int myDist=Integer.MAX_VALUE;
				if(a!=null) {
					myDist=a.size();
				}
				if(myDist<distPartner) {
					myNodes.add(node.getLeft());
				}else {
					partnerNodes.add(node.getLeft());
				}
			}
			((ExploreCoopAgent)this.myAgent).setPriority(myNodes);
			
			
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			msg.setProtocol("SHARE-PRIORITY");
			try {
				msg.setContentObject(partnerNodes);
			} catch (IOException e) {
				e.printStackTrace();
			}
			msg.setSender(this.myAgent.getAID());
			msg.addReceiver(((ExploreCoopAgent) this.myAgent).getPartner());
			((AbstractDedaleAgent)this.myAgent).sendMessage(msg);
			System.out.println(myNodes);
			System.out.println(partnerNodes);
		}

	}
	
	public int onEnd() {
		return 0;
	}

}
