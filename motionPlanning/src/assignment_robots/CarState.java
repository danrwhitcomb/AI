package assignment_robots;

import java.util.Arrays;

// this class declares the configuration of a car robot;
// standard set and get function;

public class CarState {
	protected double[] s;

	public CarState () {
		s = new double[3];
		s[0] = 0;
		s[1] = 0;
		s[2] = 0;
	}

	public CarState (double x, double y, double theta) {
		s = new double[3];
		s[0] = x;
		s[1] = y;
		s[2] = theta;
	}

	public void set(double x, double y, double theta) {
		s[0] = x;
		s[1] = y;
		s[2] = theta;
		
	}

	public double getX() {
		return s[0];
	}

	public double getY() {
		return s[1];
	}

	public double getTheta() {
		return s[2];
	}

	public double[] get() {
		return s;
	}
	
	@Override
	public boolean equals(Object o){
		double x = Math.abs(this.s[0] - ((CarState)o).s[0]);
		double y = Math.abs(this.s[1] - ((CarState)o).s[1]);
		double theta = Math.abs(this.s[2] - ((CarState)o).s[2])/6.28;
		
		if(x < 20 && y < 20 ){
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public String toString(){
		return Arrays.toString(this.s);
	}
}
