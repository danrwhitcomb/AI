import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;

public class Backtracker{
	
	public ConstraintSatisfactionProblem problem;
	public int nodesVisited = 0;
	
	public Backtracker(ConstraintSatisfactionProblem n_problem){
		problem = n_problem;
	}
	
	public void runBacktrack(HashMap<Variable, HashMap<String, List<Object>>> newWorld){
		
		/*Copy of world. MAy not need
		HashMap<Variable, HashMap<String, List<Object>>> workingWorld = 
				new HashMap<Variable, HashMap<String, List<Object>>>();
		
		//Make a copy of the world so it can be run multiple times without fucking shit up
		for(Variable var : newWorld.keySet()){
			HashMap<String, List<Object>> newMap =  new HashMap<String, List<Object>>();
			List<Object> list = newWorld.get(var).get("constraints");
			List<Object> newList = new ArrayList<Object>();
			for(Object obj : list){
				newList.add(((Constraint)obj).copy());
			}
			
			newMap.put("constraints", newList);
			
			list = newWorld.get(var).get("domain");
			newList = new ArrayList<Object>();
			for(Object obj : list){
				newList.add(((Value)obj).copy());
			}
			
			newMap.put("domain", newList);
			
			workingWorld.put(var.copy(), newMap);
		}
		*/
		nodesVisited = 0;
		LinkedList<Variable> unfilled = new LinkedList<Variable>();
		LinkedList<Variable> filled = new LinkedList<Variable>();
		unfilled.addAll(newWorld.keySet());		
		
		backtracking(newWorld, filled, unfilled, null);
		
		System.out.println("Solutions found: " + problem.nodes);
		System.out.println("Nodes Visited: " + nodesVisited);
		
	}
	
	public void backtracking(HashMap<Variable, HashMap<String, List<Object>>> world,
			LinkedList<Variable> filled, LinkedList<Variable> unfilled, Variable currentVar){
		
		problem.updateDomains(filled, unfilled);
		nodesVisited++;

		if(problem.constraintsGood(filled, world)){
			if(unfilled.size() == 0){
				problem.printSolvedProblem(filled);
				problem.nodes++;
				return;
			}
		} else {
			return;
		}
		
		Variable var = problem.getNext(currentVar);
				
		if(var == null){
			var = unfilled.pop();
		} else {
			unfilled.remove(var);
		}
		
		filled.push(var);
		
		for(Object val : world.get(var).get("domain")){
			
			if(!((Value)val).toBeConsidered){
				continue;
			}
			
			var.val = ((Value)val);
	
			backtracking(world, filled, unfilled, var);
			
		}
		
		var.val = null;
		filled.remove(var);
		problem.returnedVars.remove(var);
		unfilled.add(var);
	}
}
