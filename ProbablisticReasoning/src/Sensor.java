import java.util.Random;


public class Sensor {
	
	public int[][] colorMap; 
	public int[] colors;
	
	public Sensor(int[][] map, int[] c){
		colorMap = map;
		colors = c;
	}
	
	public int getValue(int x, int y){
		int color = colorMap[y][x];
		int[] otherColors = getOtherColors(color);
		
		Random r = new Random();
		double rand = r.nextDouble();
		if(rand < .88){
			return color;
		} else if(rand >= .88 && rand < .92){
			return otherColors[0];
		} else if(rand >= .92 && rand < .96){
			return otherColors[1];
		} else {
			return otherColors[2];
		}
		
	}
	
	private int[] getOtherColors(int color){
		int[] otherColors = new int[colors.length - 1];
		int i = 0;
		for(int c : colors){
			if(c != color){
				otherColors[i] = c;
				i++;
			}
		}
		
		return otherColors;
	}
}
