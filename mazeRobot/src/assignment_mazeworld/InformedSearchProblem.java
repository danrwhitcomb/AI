package assignment_mazeworld;

import java.util.ArrayList;
import java.lang.Thread.*;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import java.util.LinkedList;

public class InformedSearchProblem extends SearchProblem {

	public LinkedList<SearchNode> astarSearch() {

		resetStats();
		PriorityQueue<SearchNode> frontier = new PriorityQueue<SearchNode>();
		HashMap<SearchNode, SearchNode> backchain = new HashMap<SearchNode, SearchNode>();

		frontier.add(startNode);
		backchain.put(startNode, null);
		SearchNode currentNode;

		while(!frontier.isEmpty()){
			currentNode = frontier.poll();
			//frontier.clear();
			incrementNodeCount();
			updateMemory(frontier.size() + backchain.size());

			for(SearchNode node : currentNode.getSuccessors()){
				if(node.goalTest()){
					System.out.println("Goal node has been found");
					backchain.put(node, currentNode);
					return backchain(node, backchain);
				}

				if(backchain.containsKey(node)){
					continue;
				}
				//Add to backchain and visited
				backchain.put(node, currentNode);
				frontier.add(node);
			}
		}

		return null;
	}


	public LinkedList<SearchNode> uniform_cost_search() {
		resetStats();
		PriorityQueue<SearchNode> frontier = new PriorityQueue<SearchNode>();
		HashMap<SearchNode, SearchNode> backchain = new HashMap<SearchNode, SearchNode>();

		frontier.add(startNode);
		backchain.put(startNode, null);
		SearchNode currentNode;

		while(!frontier.isEmpty()){
			currentNode = frontier.poll();
			//frontier.clear();
			incrementNodeCount();

			for(SearchNode node : currentNode.getSuccessors()){
				if(node.goalTest()){
					System.out.println("Goal node has been found");
					backchain.put(node, currentNode);
					return backchain(node, backchain);
				}

				SearchNode valNode;
				if((valNode = backchain.get(node)) != null){
					if(!(valNode.getCost() > node.getCost()))
						continue;
				}
				//Add to backchain and visited
				backchain.put(node, currentNode);
				frontier.add(node);
			}
		}

		return null;
	}

}
