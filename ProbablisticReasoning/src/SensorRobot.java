import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;
import java.util.ArrayList;

public class SensorRobot {
	public int x;
	public int y;
	
	private Sensor sensor;
	
	public LinkedList<Integer> sensorHistory;
	public int[][] colorMap;
	public int[] colors;
	
	public int MAZE_WIDTH = 4;
	public int MAZE_HEIGHT = 4;
	
	
	
	public SensorRobot(int n_x, int n_y, int[][] nColorMap, int[] n_colors){
		x = n_x;
		y = n_y;
		
		colorMap = nColorMap;
		colors = n_colors;
		
		sensor = new Sensor(colorMap, colors);
		sensorHistory = new LinkedList<Integer>();
	}
	
	public void makeMove(int[] move){
		//n_x and n_y should be -1, 0, or 1
		//representing a move in a cardinal direction
		if(x + move[0] < 0 || x+ move[0] >= MAZE_WIDTH || y + move[1] < 0 || y + move[1] >= MAZE_HEIGHT){
			return;
		}
		
		x += move[0];
		y += move[1];
	}
	
	public int useSensor(){
		return sensor.getValue(x, y);
	}
	
	public void updateHistory(int color){
		sensorHistory.addFirst(color);
	}
	
	public LinkedList<Integer> getHistory(){
		return sensorHistory;
	}
	
	private int[] getOtherColors(int color){
		int[] otherColors = new int[3];
		int i = 0;
		for(int c : colors){
			if(c != color){
				otherColors[i] = c;
				i++;
			}
		}
		
		return otherColors;
	}
	
	public int[] getMarkovAssumption(){
		
		int[] hist = new int[2];
		
		if(sensorHistory.size() == 1){
			hist[0] = sensorHistory.get(0);
			hist[1] = -1;
		} else {
			hist[0] = sensorHistory.get(0);
			hist[1] = sensorHistory.get(1);
		}
		
		return hist;
	}
}
