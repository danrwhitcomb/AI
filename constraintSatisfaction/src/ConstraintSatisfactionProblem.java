
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

public class ConstraintSatisfactionProblem {
	
	//These all will need objects eventually
	//I wanted to make it more general than 
	//an array of ints
	
	//keys are variables
	//Value is HashMap with two values:
	//map.get("constraints") gets constraint set
	//map.get("domain") gets domain set
	public int nodes;
	HashSet<Variable> returnedVars;

	
	public ConstraintSatisfactionProblem(){
	
	}
	
	public HashMap<String, List<Object>> putVariable(Variable variable, List<Object> constraints, 
													List<Object> domain, List<Object> neighbors,
													HashMap<Variable, HashMap<String, List<Object>>> world){
		
		HashMap<String, List<Object>> newMap = new HashMap<String, List<Object>>();
		newMap.put("constraints", constraints);
		newMap.put("domain", domain);

		if(neighbors != null){
			newMap.put("neighbors", neighbors);
		}
		
		world.put(variable, newMap);
		
		return newMap;
	}
	
	public boolean constraintsFulfilled(Variable variable, HashMap<Variable, HashMap<String, List<Object>>> world){
		List<Object> constraints = world.get(variable).get("constraints");
		
		for(Object c : constraints){
			if(!((Constraint)c).isSatisfied(variable)){
				return false;
			}
		}
		
		return true;
		
	}
	
	public boolean constraintsGood(LinkedList<Variable> list, HashMap<Variable, HashMap<String, List<Object>>> world){
		
		if(list.size() == 0){
			return true;
		}
		
		for(Variable var : list){
			if(!constraintsFulfilled(var, world))
				return false;
		}
		return true;
	}
	
	public void printSolvedProblem(LinkedList<Variable> variables){
		System.out.println("====================");
		for(Variable var : variables){
			System.out.println(var.toString());
		}
	}
	
	public Variable getNext(Variable currentVar){
		return null;
	}
	
	public Variable randomVariable(Set<Variable> list){
		int size = list.size();
		int rand = new Random().nextInt(size);
		int i = 0;
		
		for(Variable var : list){
			if(i == rand){
				return var;
			}
			i++;
		}
		
		return null; //It will never reach this point.
	}
	
	public void updateDomains(LinkedList<Variable> filled, LinkedList<Variable> unfilled){
		return;
	}
}
