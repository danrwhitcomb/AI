import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Random;
import java.util.LinkedList;

public class BadSensorProblem {
	public int MAZE_WIDTH = 4;
	public int MAZE_HEIGHT = 4;
	public int iterations = 100;
	
	public int[] NORTH = {0, 1};
	public int[] EAST = {1, 0};
	public int[] SOUTH = {0, -1};
	public int[] WEST = {-1, 0};
	
	public int[][] moves = {NORTH, EAST, SOUTH, WEST};
	
	public int goalX;
	public int goalY;
	
	public int YELLOW = 0;
	public int RED = 1;
	public int BLUE = 2;
	public int GREEN = 3;
	
	public String[] colorStrings = {"Yellow", "Red", "Blue", "Green"}; 
	
	public int[] colors = {YELLOW, RED, BLUE, GREEN};
	
	public SensorRobot robot;
	
	int[][] colorMap = { //Remember this is flipped from the actual coordinates of the map
	{RED,    YELLOW, YELLOW, GREEN},
	{YELLOW, YELLOW, RED,    YELLOW},
	{YELLOW, YELLOW, YELLOW, YELLOW},
	{GREEN,  YELLOW, BLUE,   RED}
	};
	
	Model model;
	double[][] probMap; //Probability map
	
	public BadSensorProblem(int start_x, int start_y){
		robot = new SensorRobot(start_x, start_y, colorMap, colors);
		model = new Model(MAZE_WIDTH, MAZE_HEIGHT, colorMap, probMap);
		
		probMap = new double[MAZE_HEIGHT][MAZE_WIDTH];
		
		for(int i=0; i < MAZE_HEIGHT; i++){
			Arrays.fill(probMap[i], 1.0 / (MAZE_HEIGHT * MAZE_WIDTH));
		}
	}
	
	public void runMain(){
		Random r = new Random();
		
		int counter = 0;
		int countHighest = 0;
		
		int color = robot.useSensor();
		robot.updateHistory(color);
		runFilter(robot.getHistory());
		System.out.println("Initial distribution");
		System.out.println("Robot location: (" + robot.x + ", " + robot.y + ")");
		System.out.println("Color sensed: " + colorStrings[color]);
		System.out.println("Actual color: " + colorStrings[colorMap[robot.y][robot.x]]);
		printMaze();
		
		
		while(counter < iterations){
			int randInt = r.nextInt(4);
			int[] move = moves[randInt];
			robot.makeMove(move);
			
			System.out.println("Robot location: (" + robot.x + ", " + robot.y + ")");
			
			color = robot.useSensor();
			System.out.println("Color sensed: " + colorStrings[color]);
			System.out.println("Actual color: " + colorStrings[colorMap[robot.y][robot.x]]);
			robot.updateHistory(color);
			runFilter(robot.getHistory());	
			printMaze();
			counter++;
			
			if(isHighest(robot.x, robot.y)){
				countHighest++;
			}
			
			count++;
			int i = 1;
			robot.makeMove(move);
		}
		
		System.out.println("Prediction Accuracy: " + ((countHighest * 1.0 / iterations) * 100) + "%");
	}
	
	public boolean isHighest(int x, int y){
		int high_x = 0;
		int high_y = 0;
		double highestVal = 0;
		
		for(int i=0; i < MAZE_HEIGHT; i++){
			for(int v=0; v <MAZE_WIDTH; v++){
				if(probMap[i][v] > highestVal){
					high_x = v;
					high_y = i;
					highestVal = probMap[i][v];
				}
			}
		}
		
		if(x == high_x && y == high_y){return true;}
		return false;
	}
	
	public void runFilter(LinkedList<Integer> history){
		for(int y=0; y < MAZE_HEIGHT; y++){
			for(int x=0; x < MAZE_WIDTH; x++){
				probMap[y][x] = model.filterLocation(x, y, robot.getMarkovAssumption());
			}
		}
		model.setProbMap(probMap);
		normalize();
	}
	
	public void normalize(){
		double total = 0;
		for(int y=0; y < MAZE_HEIGHT; y++){
			for(int x=0; x < MAZE_WIDTH; x++){
				total+=probMap[y][x];
			}
		}
		double normalizer = 1.0 / total;
		
		for(int y=0; y < MAZE_HEIGHT; y++){
			for(int x=0; x < MAZE_WIDTH; x++){
				probMap[y][x] *= normalizer;
			}
		}
		
	}
	
	public void printMaze(){
		DecimalFormat df = new DecimalFormat("0.000");
		
		System.out.println("+-------------------------------+");
		for(int y=MAZE_HEIGHT - 1; y >= 0; y--){
			System.out.print("| ");
			for(int x=0; x < MAZE_WIDTH; x++){
				System.out.print(df.format(probMap[y][x]) + " | ");
			}
			System.out.print("\n");
		}
		
		System.out.println("+-------------------------------+\n\n");
	}
	
	public static void main(String args[]){
		BadSensorProblem prob = new BadSensorProblem(1, 1);
		prob.runMain();
	}
	
}
