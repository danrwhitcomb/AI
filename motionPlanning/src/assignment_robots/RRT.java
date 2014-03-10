package assignment_robots;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.Random;

import assignment_robots.SearchProblem.SearchNode;


public class RRT {
	
	CarRobot car;
	SteeredCar stCar;
	World world;
	HashSet<RRT_Node> tree;
	CarState startNode;
	CarState currentGoal;
	CarState goalNode;
	int window_height;
	int window_width;
	
	
	public RRT(World nWorld, int nWindowHeight, int nWindowWidth){
		world = nWorld;	
		window_height = nWindowHeight;
		window_width = nWindowWidth;
	}
	
	public LinkedList<CarState> runRRT(CarState nStart, CarState nGoal, CarRobot nCar){
		HashMap<CarState, CarState> reachedFrom = new HashMap<CarState, CarState>();
		car = nCar;
		stCar = new SteeredCar();
		startNode = nStart;
		goalNode = nGoal;
		CarState currentState = startNode;
		
		reachedFrom.put(startNode, null);
		int currentDepth = 0;
		
		while(!currentState.equals(goalNode)){
			ArrayList<double[]> sortedMoves = new ArrayList<double[]>();
			randomizeGoalNode();
			
			for(int move = 0; move < stCar.control.length; move++){
				
				if(!world.carCollisionPath(car, currentState, move, 1) ){
					CarState newState = stCar.move(currentState, move, 1);
					
					if(sortedMoves.size() == 0){
						sortedMoves.add(newState.s);
					} else {
						int index = 0;
						
						for(double[] state : sortedMoves){
							if(distance(newState.s, currentGoal.s) <= distance(state, currentGoal.s)){		
									sortedMoves.add(index, newState.s);
									break;
							}
							index++;
						}
					}
				}
			}
		
			double[] move = sortedMoves.get(0);
			CarState tmpState = new CarState();
			tmpState.set(move[0], move[1], move[2]);
			
			reachedFrom.put(tmpState, currentState);
			
			currentState = tmpState;
			System.out.println(currentState);
			System.out.println("Still finding path");
		}
		
		System.out.println(currentDepth);
		return backchain(currentState, reachedFrom);
	}
	
	protected LinkedList<CarState> backchain(CarState node,
			HashMap<CarState, CarState> visited) {

		LinkedList<CarState> solution = new LinkedList<CarState>();

		// chain through the visited hashmap to find each previous node,
		// add to the solution
		while (node != null) {
			solution.addFirst(node);
			System.out.print(node);
			node = visited.get(node);
			System.out.print("\n");
		}

		return solution;
	}
	
	public double distance(double[] state1, double[] state2){
		double total = 0;
		double tmp = 0;
		for(int i = 0; i < state1.length - 1; i++){
			tmp = state1[i] - state2[i];
			tmp = Math.pow(tmp, 2);
			total += tmp;
		}
		
		return Math.sqrt(total);
	}
	
	public void randomizeGoalNode(){
		Random r = new Random();
		double rand = r.nextDouble();
		
		if(rand <= .6){
			currentGoal = goalNode; 
		} else {
			currentGoal = generateRandomState(r);
		}
	}
	
	public CarState generateRandomState(Random r){
		double[] state = new double[3];
		state[0] = r.nextDouble() * window_width;
		state[1] = r.nextDouble() * window_height;
		state[2] = r.nextDouble() * Math.PI *2;
		
		CarState newState = new CarState();
		newState.set(state[0], state[1], state[2]);
		
		return newState;
	}
	
	public class RRT_Node implements SearchNode {
		CarState state;
		HashSet<RRT_Node> leaves;
		RRT_Node parent;
		
		public RRT_Node(CarState nState, RRT_Node nParent) {
			state = nState;
			parent = nParent;
		}
	
		@Override
		public boolean equals(Object o){
			return Arrays.equals(this.state.s, ((RRT_Node)o).state.s);
		}

		@Override
		public int compareTo(SearchNode o) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public ArrayList<SearchNode> getSuccessors() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean goalTest() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public double getCost() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public double heuristic() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public double priority() {
			// TODO Auto-generated method stub
			return 0;
		}
	}
}
