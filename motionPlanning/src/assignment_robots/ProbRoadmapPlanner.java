package assignment_robots;

import java.util.Arrays;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Random;
import java.util.PriorityQueue;

public class ProbRoadmapPlanner extends SearchProblem{

	HashMap<Configuration, HashSet<Configuration>> edges;
	World world;
	ArmLocalPlanner armLocalPlanner;
	Configuration goalNode;
	Configuration curConfig;
	ArmDriver driver;
	ArmRobot r;
	
	int k = 15;
	int n = 250;
	int radius = 100;
	int window_width;
	int window_height;

	public ProbRoadmapPlanner(int n_window_width, int n_window_height, World n_world, ArmDriver nDriver){
		edges = new HashMap<Configuration, HashSet<Configuration>>();		
		world = n_world;
		armLocalPlanner = new ArmLocalPlanner();
		driver = nDriver;

		window_height = n_window_height;
		window_width = n_window_width;
	}

	public LinkedList<SearchNode> prm(ArmRobot robot, double[] start, double[] goal){

		//Setup
		r = robot;
		Configuration startN = new Configuration(start);
		Configuration goalN = new Configuration(goal);

		edges.put(startN, new HashSet<Configuration>());
		edges.put(goalN, new HashSet<Configuration>());
		
		startNode = startN;
		goalNode = goalN;
		
		/*
		if(!world.armCollisionPath(robot, start, goal)){
			Configuration[] array = {new Configuration(start), new Configuration(goal)};
			return new LinkedList<SearchNode>(Arrays.asList(array));
		}
		*/
		
		
		
		double[] currentConfig = new double[robot.config.length];
		populateInitialRoadmap(currentConfig, robot);
		connectRoadmap();
		
		plotAndShowSuccessors(edges.get(startNode));
		/*
		HashSet<Configuration> randSet = edges.get(startNode);
		Iterator<Configuration> it = randSet.iterator();
		it.next();
		it.next();
		randSet = edges.get(it.next());
		
		plotAndShowSuccessors(randSet);
		*/
		
		HashSet<Configuration> goalSet = edges.get(goalN);
		HashSet<Configuration> startSet = edges.get(startN);
		for(Configuration con : startSet){
			if(goalSet.contains(con)){
				System.out.println("Fuck-a");
			}
		}
		
		
		return getPath(startN, goalN);
	}

	public void populateInitialRoadmap(double[] currentConfig, ArmRobot robot){
		int count = 0;

		while(count < n){
			currentConfig = generateRandomConfig(robot, currentConfig);
			if(!isColliding(robot, currentConfig) && !edges.containsKey(new Configuration(currentConfig))){
				edges.put(new Configuration(currentConfig), new HashSet<Configuration>());
				count++;
			}
		} 
	}
	
	public void connectRoadmap(){
		
		for(Configuration i : edges.keySet()){
		
			PriorityQueue<Configuration> queue = new PriorityQueue<Configuration>();
			curConfig = i;
			
			for(Configuration v : edges.keySet()){
				if(!v.equals(i)){
					queue.add(v);
				}	
			}
			
			Configuration j = queue.poll();
			int count = 0;
			while(j != null && count < k){
				
				if(!world.armCollisionPath(new ArmRobot(r.links), i.config, j.config)){
					if(j.goalTest()){
						System.out.println("Goal node added to neighbors");
					}
					edges.get(i).add(j); 
					count++;
					edges.get(j).add(i);
				}
				
				j = queue.poll();
			}
		}
	}

	public void plotAndShow(){
		ArmRobot robot = new ArmRobot(2);
		
		for(Configuration i : edges.keySet()){
			robot.set(i.config);
			driver.plotArmRobot(driver.group, robot, i.config);
		}
		 driver.sc.setRoot(driver.group);
		 driver.stage.show();
		
	}
	
	public void plotAndShowSuccessors(HashSet<Configuration> set){
		ArmRobot robot = new ArmRobot(2);
		driver.plotArmRobot(driver.group, robot, ((Configuration)startNode).config);
		for(Configuration i : set){
			robot.set(i.config);
			driver.plotArmRobot(driver.group, robot, i.config);
		}
		
		driver.sc.setRoot(driver.group);
		driver.stage.show();
	}

	private LinkedList<SearchNode> getPath(Configuration start, Configuration goal){
		
		return this.breadthFirstSearch();
	}


	private double[] generateRandomConfig(ArmRobot robot, double[] array){
		Random r = new Random();
		for(int i=0; i < array.length; i++){
			switch(i){
			case 0:
				array[i] = r.nextDouble() * window_width;
				break;

			case 1: 
				array[i] = r.nextDouble() * window_height;
				break;

			default:
				if(i % 2 == 0){
					array[i] = robot.config[i];
				} else {
					array[i] = r.nextDouble() * 2*Math.PI;
				}
				break;
			}
		}
		return array;
	}

	private boolean isColliding(ArmRobot robot, double[] config){
		ArmRobot currentRobo = new ArmRobot(robot.links);
		currentRobo.set(config);
		return world.armCollision(currentRobo);
	}

	public class Configuration implements SearchNode{

		public double[] config;

		public Configuration(double[] n_config){
			this.config = Arrays.copyOf(n_config, n_config.length);
		}

		@Override
		public int hashCode(){
			return Arrays.hashCode(this.config);
		}

		public String toString(){
			return Arrays.toString(this.config);
		}
		@Override
		public int compareTo(SearchNode o) {
	
			double time1 = armLocalPlanner.moveInParallel(curConfig.config, ((Configuration)o).config);
			double time2 = armLocalPlanner.moveInParallel(curConfig.config, this.config);
			if(time1 < time2)
				return 1;
			else
				return -1;		
		}
		
		public double distanceTo(Configuration config){
			return armLocalPlanner.moveInParallel(this.config, config.config);
		}

		@Override
		public ArrayList<SearchNode> getSuccessors() {
			/*
			PriorityQueue<Configuration> queue = new PriorityQueue<Configuration>();
			curConfig = this;
			
			for(Configuration i : edges){
					queue.add(i);
			}
			
			ArrayList<SearchNode> neighborArray = new ArrayList<SearchNode>();
			Configuration v = null;
			
			for(int i=0; i < k; i++){
				v = queue.poll();
				if(!world.armCollisionPath(new ArmRobot(r.links), this.config, v.config)){
					if(v.equals(goalNode)){
						System.out.println("Found goal node");
					}
					neighborArray.add(v);
				}	
			}
			*/
			ArrayList<SearchNode> neighborArray = new ArrayList<SearchNode>();
			
			HashSet<Configuration> neighbors = edges.get(this);
			System.out.println(neighbors.contains(goalNode));
			
			for(Configuration neighbor : neighbors){
				neighborArray.add(neighbor);
			}
			
			
			
			return neighborArray;
		}

		
		@Override
		public boolean equals(Object o) {
			return Arrays.equals(this.config, ((Configuration) o).config);
		}
		
		
		@Override
		public boolean goalTest() {
			return Arrays.equals(this.config, goalNode.config);
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
			return 0;
		}
	}
}
