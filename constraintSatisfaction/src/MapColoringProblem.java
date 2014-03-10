import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.LinkedList;


public class MapColoringProblem extends ConstraintSatisfactionProblem{
	
	HashMap<Variable, HashMap<String, List<Object>>> world;
	
	public MapColoringProblem(HashMap<Variable, HashMap<String, List<Object>>> n_world){
		world = n_world;
		returnedVars = new HashSet<Variable>();
	}
	
	@Override
	public Variable getNext(Variable currentVar){
		
		if(currentVar == null){
			Variable var = randomVariable(world.keySet());
			while(returnedVars.contains(var)){
				var = randomVariable(world.keySet());
			}
			returnedVars.add(var);
			return var;
		}
		
		for(Object var : world.get(currentVar).get("neighbors")){
			if(!returnedVars.contains(var)){
				returnedVars.add(((Variable)var));
				return (Variable)var;
			}
		}
		
		return null;
	}
	
	public void updateDomains(LinkedList<Variable> filled, LinkedList<Variable> unfilled){
		if(filled.size() == 0 || unfilled.size() == 0){
			return;
		}
		
		for(Variable var : filled){
			List<Object> neighbors = world.get(var).get("neighbors");
			if(neighbors == null){
				continue;
			}
			
			for(Object n : neighbors){
				MapVariable neighbor = (MapVariable)n;	
				
				for(Object v : world.get(neighbor).get("domain")){
					MapValue val = (MapValue)v;
					if(val.equals(var.val)){
						val.toBeConsidered = false;
					}
				}
			}
		}
		
		
		for(Object var : unfilled.toArray()){
			int i = 0;
			MapValue chosenVal = null;
			Variable variable = (Variable)var;
			for(Object v : world.get(variable).get("domain")){
				MapValue val = (MapValue)v;
				
				if(val.toBeConsidered){
					i++;
					chosenVal = val;
				}
			}
			
			if(i == 1 && chosenVal != null){
				variable.val = chosenVal;
				unfilled.remove(variable);
				filled.add(variable);
				returnedVars.add(variable);
			}
		}
		
		
		
		for(Variable var : filled){
			for(Object v : world.get(var).get("domain")){
				MapValue val = (MapValue)v;
				
				val.toBeConsidered = true;
			}
		}
	}
	
	private class MapVariable extends Variable{
		
		public MapVariable(String n_designation, Value n_val){
			designation = n_designation;
			val = n_val;
		}
		
		@Override
		public Variable copy(){
			MapVariable newVar = new MapVariable(designation, val);
			return newVar;
		}
	}
	
	private class MapConstraint implements Constraint{

		public boolean isSatisfied(Variable variable) {
			
			List<Object> adjList = world.get(variable).get("neighbors");
			if(adjList == null){
				return true;
			}
			
			for(Object neighbor : adjList){
				
				Value val = ((Variable)neighbor).val;
				if(val == null){
					continue;
				}
				if(variable.val.equals(val)){
					return false;
				}
			}
			
			return true;
		}

		public Constraint copy() {
			Constraint con = new MapConstraint();
			return con;
		}
		
	}
	
	private class MapValue extends Value{

		public String val;
		
		public MapValue(String n_val){
			val = n_val;
		}
		
		public Value copy() {
			return new MapValue(val);
		}

		public Object getValue() {
			return val;
		}
		
		public String toString(){
			return val;
		}
		
		public boolean equals(Object o){
			return val.equals(((MapValue)o).val);
		}
		
	}
	
	public List<Object> makeDomain(){
		Value rVal = new MapValue("R");
		Value gVal = new MapValue("G");
		Value bVal = new MapValue("B");
		
		return new ArrayList<Object>(Arrays.asList(rVal, gVal, bVal));
	}
	
	public static void main(String[] args){
		
		//World
		HashMap<Variable, HashMap<String, List<Object>>> world =
				new HashMap<Variable, HashMap<String, List<Object>>>();
		
		//Setup of variables
		MapColoringProblem mpProblem = new MapColoringProblem(world);
		MapVariable wa = mpProblem.new MapVariable("Western Australia", null);
		MapVariable nt = mpProblem.new MapVariable("Northern Territory", null);
		MapVariable q = mpProblem.new MapVariable("Queensland", null);
		MapVariable sa = mpProblem.new MapVariable("South Australia", null);
		MapVariable nsw = mpProblem.new MapVariable("New South Wales", null);
		MapVariable v = mpProblem.new MapVariable("Victoria", null);
		MapVariable t = mpProblem.new MapVariable("Tazmania", null);
		
		//Setup Neighbor lists
		List<Object> wa_neighbor = new ArrayList<Object>(Arrays.asList(nt, sa));
		List<Object> nt_neighbor = new ArrayList<Object>(Arrays.asList(wa, sa, q));
		List<Object> q_neighbor = new ArrayList<Object>(Arrays.asList(nt, sa, nsw));
		List<Object> sa_neighbor = new ArrayList<Object>(Arrays.asList(wa, nt, q, nsw, v));
		List<Object> nsw_neighbor = new ArrayList<Object>(Arrays.asList(q, sa, v));
		List<Object> v_neighbor = new ArrayList<Object>(Arrays.asList(sa, nsw));
		List<Object> t_neighbor = new ArrayList<Object>();
		
		//Setup constraint and domain
		//They all have the same constraint and domain so there only needs
		//to be one object for the whole group
		
		//Constraint checks
		MapConstraint mConstraint = mpProblem.new MapConstraint();
		List<Object> constraintList = new ArrayList<Object>(Arrays.asList(mConstraint));
		
		//Put variables in the world
		mpProblem.putVariable(wa, constraintList, mpProblem.makeDomain(), wa_neighbor, world);
		mpProblem.putVariable(nt, constraintList, mpProblem.makeDomain(), nt_neighbor, world);
		mpProblem.putVariable(q, constraintList, mpProblem.makeDomain(), q_neighbor, world);
		mpProblem.putVariable(sa, constraintList, mpProblem.makeDomain(), sa_neighbor, world);
		mpProblem.putVariable(nsw, constraintList, mpProblem.makeDomain(), nsw_neighbor, world);
		mpProblem.putVariable(v, constraintList, mpProblem.makeDomain(), v_neighbor, world);
		mpProblem.putVariable(t, constraintList, mpProblem.makeDomain(), t_neighbor, world);

		new Backtracker(mpProblem).runBacktrack(world);
	}
}
