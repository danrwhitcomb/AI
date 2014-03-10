package assignment_mazeworld;

import java.util.ArrayList;
import java.util.Arrays;
import java.math.*;

import assignment_mazeworld.Robot;
import assignment_mazeworld.SearchProblem.SearchNode;
import assignment_mazeworld.SimpleMazeProblem.SimpleMazeNode;

public class RobotMazeProblem extends InformedSearchProblem{
	
	private static int actions[][] = {Maze.NORTH, Maze.EAST, Maze.SOUTH, Maze.WEST}; 
	private Maze maze;
	private ArrayList<Robot> robots;

	
	public RobotMazeProblem(Maze m, int[][] sl, int[][] gl){
		maze = m;
		ArrayList<Robot> initialRobots = new ArrayList<Robot>();
		
		for(int i=0; i < sl.length; i++){
			initialRobots.add(new Robot(gl[i][0], gl[i][1]));
		}
		robots = initialRobots;
		startNode = new RobotMazeNode(sl, initialRobots.get(0), 0);
	}
	
	/*RobotMazeNode*/
	public class RobotMazeNode implements SearchNode{
		
		private double cost;  
		private int[][] locations;
		private Robot robotToMove;

			
		public RobotMazeNode(int[][] loc, Robot newRobotToMove, double newCost){
			robotToMove = newRobotToMove;
			cost = newCost;
			locations = new int[loc.length][2];
			
			for(int i=0; i < loc.length; i++){
				locations[i] = Arrays.copyOf(loc[i], loc[i].length);
			}
		}
		
		public ArrayList<SearchNode> getSuccessors(){
			//ArrayList<int[]> moves = robotToMove.getMoves(actions, locations);
			ArrayList<SearchNode> successors = new ArrayList<SearchNode>();
			int index = indexCurRobo();
			
			//Add actions
			for(int[] i : actions){
				if(robotToMove.isSafeMove(locations, robots, i) && 
						maze.isLegal(locations[index][0] + i[0], locations[index][1] + i[1])){
					
					locations[index][0] += i[0];
					locations[index][1] += i[1];
					int newIndex = index + 1 == robots.size() ? 0 : index + 1;
					
					successors.add(new RobotMazeNode(locations, robots.get(newIndex), getCost() + 1));
					
					locations[index][0] -= i[0];
					locations[index][1] -= i[1];
				}
			}
			
			//If no moves are possible, add a move to stay still
			if(successors.isEmpty()){
				index = index + 1 == robots.size() ? 0:index + 1;
				successors.add(new RobotMazeNode(locations, robots.get(index), getCost() + 1));
			}
			
			return successors;
		}
		
		public boolean goalTest(){
			int i = 0;
			for(Robot robot : robots){
				if(locations[i][0] == robot.goal[0] &&
						locations[i][1] == robot.goal[1]){
					i++;
					continue;
				}
				return false;
			}
			return true;
		}
		
		@Override
		public int compareTo(SearchNode o) {
			return (int) Math.signum(priority() - o.priority());
		}
		
		
		@Override
		public double getCost() {
			return cost;
		}
		
		@Override
		public double heuristic() {
			double dx = 0;
			double dy = 0; 
			int i = 0;
			
	
			for(Robot robot : robots){
				dx += (robot.goal[0] - locations[i][0]);
				dy += (robot.goal[1] - locations[i][1]);
				i++;
			}
			
			//dx = robotToMove.goal[0] - locations[indexCurRobo()][0];
			//dy = robotToMove.goal[1] - locations[indexCurRobo()][1];
			
			return Math.abs(dx) + Math.abs(dy);
		}
		
		@Override
		public double priority() {
			return heuristic() + getCost();
		}
		
		public int hashCode(){
			double total = 0;
			for(int i=0; i < robots.size(); i++){
				total += Math.pow((double)locations[i][0] + (double)locations[i][1], 2);
			}
			total /= indexCurRobo();
			return (int)total;
		}
		
		public String toString(){
			String str = "";
			System.out.print("Robot to Move: " + indexCurRobo());
			for(int i=0; i < robots.size(); i++){
				str += " [" + locations[i][0] + ", " + locations[i][1] + "] ";
			}
			return str;
		}
		
		public Robot getRobotToMove(){
			return robotToMove;
		}
		public int[][] getLocations(){
			return locations;
		}
		
		public boolean equals(Object other) {
			if(robotToMove == ((RobotMazeNode)other).getRobotToMove()){
				int[][] otherLocations = ((RobotMazeNode)other).getLocations();
				for(int i = 0; i < locations.length; i++){
					if(!(locations[i][0] == otherLocations[i][0] && locations[i][1] == otherLocations[i][1])){
						return false;
					}
				}
				return true;
			}
			return false;
		}
		
		public int indexCurRobo(){
			return robots.indexOf(robotToMove);
		}
		
		// FUNCTIONS FOR THE ANIMATION
		// THE PRVIOUS ROBOT INDEX NEEDS TO BE RETURNED
		// BECAUSE robotToMove IS THE ONE THAT WILL MOVE
		public int getX(){
			int index = indexCurRobo() == 0 ? locations.length - 1 : indexCurRobo() - 1;
			return locations[index][0];
		}
		
		public int getY(){
			int index = indexCurRobo() == 0 ? locations.length - 1 : indexCurRobo() - 1;
			return locations[index][1];
		}
		
		public int roboThatMoved(){
			return 	indexCurRobo() == 0 ? locations.length - 1 : indexCurRobo() - 1;

		}
	}

}
