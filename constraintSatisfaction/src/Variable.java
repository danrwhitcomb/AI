
public class Variable {
	
	public String designation;
	public Value val = null;
	
	//This should always be overriden!!!
	public Variable copy(){
		return null;
	}
	
	public String toString(){
		String str = designation + ": " + val;
		return str;
	}
}
