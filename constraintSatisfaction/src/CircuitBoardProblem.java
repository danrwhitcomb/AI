import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class CircuitBoardProblem extends ConstraintSatisfactionProblem{

	
	HashMap<Variable, HashMap<String, List<Object>>> world;
	public int BOARD_HEIGHT = 3;
	public int BOARD_WIDTH = 10;
	
	public CircuitBoardProblem(HashMap<Variable, HashMap<String, List<Object>>> n_world){
		world = n_world;
		returnedVars = new HashSet<Variable>();
	}
	
	@Override
	public Variable getNext(Variable currentVar){
		return null;
	}
	
	private class CircuitBoardVariable extends Variable{
		
		public int width;
		public int height;
		
		public CircuitBoardVariable(String n_designation, Value n_val, int n_width, int n_height){
			designation  = n_designation;
			val = n_val;
			
			width = n_width;
			height = n_height;
		}
		
		public Variable copy(){
			return new CircuitBoardVariable(designation, new CircuitBoardValue(((CircuitBoardValue)val).x_val, ((CircuitBoardValue)val).y_val), width, height);
		}
	}
	
	private class CircuitBoardValue extends Value{
		
		//Value represents the x and y coordinates
		//of the bottom left 
		public int x_val;
		public int y_val;
		
		public CircuitBoardValue(int n_x_val, int n_y_val){
			x_val = n_x_val;
			y_val = n_y_val;
		}

		public Value copy() {
			return new CircuitBoardValue(x_val, y_val);
		}
		
		public String toString(){
			return "(" + x_val + ", " + y_val + ")";
		}
		
	}
	
	private class CircuitBoardConstraint implements Constraint{

		public boolean isSatisfied(Variable variable) {
			CircuitBoardVariable var = (CircuitBoardVariable)variable;
			CircuitBoardValue val = (CircuitBoardValue)var.val;
			
			//Check if base point is off board
			if(val.x_val < 0 || val.y_val < 0 || val.x_val >= BOARD_WIDTH || val.y_val >= BOARD_WIDTH) {
				return false;
			}
			
			//Check if the rest of the chip is off board
			if(val.x_val + var.width > BOARD_WIDTH || val.y_val + var.height > BOARD_HEIGHT){
				return false;
			}
			
			//Check if there is overlap with any other chip
			for(Variable otherVar : world.keySet()){
				if(isOverlap(var, ((CircuitBoardVariable)otherVar))){
					return false;
				}
			}
			
			return true;
		}
		
		private boolean isOverlap(CircuitBoardVariable var1, CircuitBoardVariable var2){
			if(var1 == var2){
				return false;
			}
			
			CircuitBoardValue val1 = (CircuitBoardValue)var1.val;
			CircuitBoardValue val2 = (CircuitBoardValue)var2.val;
			
			if(val1 == null | val2 == null){
				return false;
			}
			
			if(val1.x_val == val2.x_val && val1.y_val == val2.y_val){ return true; }
			
			for(int i=val1.x_val; i < var1.width + val1.x_val; i++){
				for(int v=val1.y_val; v < var1.height + val1.y_val; v++){
					if(i >= val2.x_val && i < val2.x_val + var2.width && v >= val2.y_val && v < val2.y_val + var2.height){
						return true;
					}
				}
			}
			
			return false;
		}

		public Constraint copy() {
			return new CircuitBoardConstraint();
		}
	}
	
	public void printSolvedProblem(LinkedList<Variable> variables){
		System.out.println("=============");
		String[][] array = new String[BOARD_WIDTH][BOARD_HEIGHT];
		
		for(int i = 0; i < BOARD_WIDTH; i++){
			for(int v=0; v < BOARD_HEIGHT; v++){
				for(Variable var1 : world.keySet()){
					CircuitBoardVariable var = (CircuitBoardVariable)var1;
					CircuitBoardValue val = (CircuitBoardValue)var.val;
					
					if(val.x_val == i && val.y_val == v){
						for(int j=0; j<var.width;j++){
							for(int b=0; b<var.height;b++){
								array[i+j][v+b] = var.designation;
							}
						}
					}
				}
			}
		}
		
		for(int i=0; i < BOARD_HEIGHT; i++){
			for(int v=0; v < BOARD_WIDTH; v++){
				if(array[v][i] == null){
					System.out.format(".");
				} else {
					System.out.format(array[v][i]);
				}
			}
			System.out.format("\n");
		}
		
		System.out.println("=============");
		System.out.println("");
	}



	public static void main(String[] args){
		
		//World
		HashMap<Variable, HashMap<String, List<Object>>> world =
				new HashMap<Variable, HashMap<String, List<Object>>>();
		
		//Setup of variables
		CircuitBoardProblem cbProblem = new CircuitBoardProblem(world);
		CircuitBoardVariable a = cbProblem.new CircuitBoardVariable("a", null, 3, 2);
		CircuitBoardVariable b = cbProblem.new CircuitBoardVariable("b", null, 5, 2);
		CircuitBoardVariable c = cbProblem.new CircuitBoardVariable("c", null, 2, 3);
		CircuitBoardVariable e = cbProblem.new CircuitBoardVariable("e", null, 7, 1);
		
		
		//Setup constraint and domain
		//They all have the same constraint and domain so there only needs
		//to be one object for the whole group
		
		//Constraint checks
		CircuitBoardConstraint cbConstraint = cbProblem.new CircuitBoardConstraint();
		List<Object> constraintList = new ArrayList<Object>(Arrays.asList(cbConstraint));
		
		//Three color problem domain values
		//colors are r, g, b
		
		List<Object> domain = new ArrayList<Object>();
		
		for(int i=0; i < cbProblem.BOARD_WIDTH; i++){
			for(int v=0; v < cbProblem.BOARD_HEIGHT; v++){
				domain.add(cbProblem.new CircuitBoardValue(i, v));
			}
		}
		
		//Put variables in the world
		cbProblem.putVariable(a, constraintList, domain, null, world);
		cbProblem.putVariable(b, constraintList, domain, null, world);
		cbProblem.putVariable(c, constraintList, domain, null, world);
		cbProblem.putVariable(e, constraintList, domain, null, world);
		
	
		new Backtracker(cbProblem).runBacktrack(world);
	}
}
