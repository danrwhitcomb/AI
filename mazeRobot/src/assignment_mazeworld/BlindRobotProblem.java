package assignment_mazeworld;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import assignment_mazeworld.SearchProblem.SearchNode;
import assignment_mazeworld.SimpleMazeProblem.SimpleMazeNode;

public class BlindRobotProblem extends InformedSearchProblem{

	private static int actions[][] = {Maze.NORTH, Maze.EAST, Maze.SOUTH, Maze.WEST}; 
	private Maze maze;

	public BlindRobotProblem(int s_x, int s_y, int g_x, int g_y, Maze m){
		maze = m;

		boolean[][] grid = new boolean[maze.height][maze.width];
		setupGrid(grid);
		int[] state = {s_x, s_y};
		startNode = new BlindRobotNode(grid, 0, state);
	}

	public void setupGrid(boolean[][] grid){
		char character;

		for(int i=0; i < maze.height; i++){
			for(int v=0; v < maze.width; v++){
				character = maze.getChar(v, i);
				if(character == '#'){
					grid[i][v] = false;
				} else {
					grid[i][v] = true;
				}
			}
		}
	}

	public boolean[][] copyGrid(boolean[][] grid){
		boolean[][] newGrid = new boolean[maze.height][maze.width];
		for(int i=0; i < maze.height; i++){
			for(int v=0; v < maze.width; v++){
				newGrid[v][i] = grid[v][i];
			}
		}

		return newGrid;
	}

	public class BlindRobotNode implements SearchNode{
		public boolean[][] grid;
		private int cost;
		private int[] state;

		public BlindRobotNode(boolean[][] newGrid, int nCost, int[] nState){
			grid = new boolean[maze.height][maze.width];
			for(int i=0; i < newGrid.length; i++){
				grid[i] = Arrays.copyOf(newGrid[i], newGrid[i].length);
			}
			cost = nCost;
			state = Arrays.copyOf(nState, nState.length);
		}

		@Override
		public int compareTo(SearchNode o) {
			return (int) Math.signum(priority() - o.priority());
		}
		@Override
		public ArrayList<SearchNode> getSuccessors() {
			ArrayList<SearchNode> successors = new ArrayList<SearchNode>();

			for (int[] action: actions) {
				boolean[][] newGrid = new boolean[maze.height][maze.width];

				for(int i=0; i < maze.height; i++){
					for(int v=0; v < maze.width; v++){
						
						int belowX = v - action[0];
						int belowY = i - action[1];
						int aboveX = v + action[0];
						int aboveY = i + action[1];
						
						if(grid[i][v]){
							
							if((!maze.isLegal(belowX, belowY) || !grid[belowY][belowX]) && maze.isLegal(aboveX, aboveY)){
								newGrid[i][v] = false;
							} else {
								newGrid[i][v] = true;
							}
						} else {
							if(maze.isLegal(belowX, belowY) && grid[belowY][belowX]){
								newGrid[i][v] = true;
							} else {
								newGrid[i][v] = false;
							}
						}
					}
				}

				if(maze.isLegal(state[0] + action[0], state[1] + action[1])){
					state[0] += action[0];
					state[1] += action[1];
					SearchNode succ = new BlindRobotNode(newGrid, cost + 1, state);
					successors.add(succ);
					state[0] -= action[0];
					state[1]-= action[1];
				} else {
					SearchNode succ = new BlindRobotNode(newGrid, cost + 1, state);
					successors.add(succ);
				}

			}
			return successors;
		}

		public boolean equals(Object other) {
			for(int i = 0; i < maze.height; i++){
				if(!Arrays.equals(grid[i], ((BlindRobotNode)other).grid[i])){
					return false;
				}
			}
			return true;
		}

		@Override
		public boolean goalTest() {
			int count = 0;
			for(boolean[] i : grid){
				for(boolean v : i){
					if(v){count++;}
				}
			}

			if(count == 1){return true;} else {return false;}
		}
		@Override
		public double getCost() {
			return cost;
		}
		@Override
		public double heuristic() {
			int total = 0;
			for(boolean[] i : grid){
				for(boolean v : i){
					if(v){total++;}
				}
			}

			return (double) total;
		}
		@Override
		public double priority() {
			return heuristic() + getCost();
		}

		public int hashCode(){
			int total = 0;
			for(int i = 0; i < maze.height; i++){
				for(int v = 0; v < maze.width; v++){
					if(grid[i][v]){
						total += (i ^ (v >> 3));
					}
				}
			}

			return total;
		}
		
		public String toString(){
			String str = "";
			
			for(int i = 0; i < maze.height; i++){
				for(int v = 0; v < maze.width; v++){
					int newX = i + maze.height - 1;
					int newY = v + maze.width - 1;
					if(grid[i][v]){
						str += ".";
					} else {
						str += "#";
					}
				}
				str+="\n";
			}
			return str;
		}
	}
}
