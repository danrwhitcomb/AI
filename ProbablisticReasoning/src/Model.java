import java.util.Arrays;
import java.util.LinkedList;

public class Model {

	public int MAZE_HEIGHT;
	public int MAZE_WIDTH;

	public int[][] colorMap;
	public double[][] probMap;

	public Model(int width, int height, int[][] c_map, double[][] p_map){
		MAZE_HEIGHT = height;
		MAZE_WIDTH = width;

		colorMap = c_map;
		probMap = new double[height][width];
	}

	/*
	 * loc1 is previous position
	 * loc2 is new position
	 * 
	 * uses first-order markov assumption
	 */
	public double computeTransitionProbDistro(int prevColor, int x, int y){

		double prob = 0;
		double stayingStillProb = 0;

		if(isInMaze(x, y+1)){
			prob += (0.25 * probMap[y+1][x]);
		} else {
			stayingStillProb += 0.25;
		}


		if(isInMaze(x, y-1)){
			prob += (0.25 * probMap[y-1][x]);
		} else {
			stayingStillProb += 0.25;
		}

		if(isInMaze(x+1, y)){
			prob += (0.25 * probMap[y][x+1]);
		} else {
			stayingStillProb += 0.25;
		}

		if(isInMaze(x-1, y)){
			prob += (0.25 * probMap[y][x-1]);
		} else {
			stayingStillProb += 0.25;
		}


		return prob + (stayingStillProb * probMap[y][x]);
	}

	public double computeSensorProbDistro(int color, int x, int y){
		int locationColor = colorMap[y][x];
		return locationColor == color ? 0.88 : 0.04;
	}

	public void setProbMap(double[][] pMap){
		for(int i=0; i < pMap.length; i++){
			probMap[i] = Arrays.copyOf(pMap[i], pMap[i].length);
		}
	}

	private boolean isInMaze(int x, int y){
		return x >= 0 && x < MAZE_WIDTH && y >= 0 && y < MAZE_HEIGHT;
	}

	public double filterLocation(int x, int y, int[] history){
		if(history[1] == -1){
			return computeSensorProbDistro(history[0], x, y);
		}

		return computeTransitionProbDistro(history[1], x, y) * computeSensorProbDistro(history[0], x, y);
	}
}
