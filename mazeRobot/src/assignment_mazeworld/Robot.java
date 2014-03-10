package assignment_mazeworld;

import java.util.ArrayList;

public class Robot{
	
	public int goal[];

	public Robot(int gl_x, int gl_y){
		
		goal = new int[2];
		this.goal[0] = gl_x;
		this.goal[1] = gl_y;	
	}
	
	public boolean isSafeMove(int[][] loc, ArrayList<Robot> allRobots, int move[]){
		int index = allRobots.indexOf(this);
		
		for(int i=0; i < allRobots.size(); i++){
			if(i == index) continue;
			if(loc[index][0] + move[0] == loc[i][0] && loc[index][1] + move[1] == loc[i][1]) return false;
		}
		
		return true;
	}
}
