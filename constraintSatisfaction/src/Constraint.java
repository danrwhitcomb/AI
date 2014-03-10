
public interface Constraint{
		
	//Allows the user of the constraint to call and
	//check all constraints for a type in one fell swoop.
	public boolean isSatisfied(Variable variable);
	public Constraint copy();
	
}
