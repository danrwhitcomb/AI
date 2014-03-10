package chai;

import chesspresso.position.Position;
import chesspresso.*;
import java.util.LinkedList;


public class GameAudit {
	
	LinkedList<Short> moves;
	
	public GameAudit(){
		moves = new LinkedList<Short>();
	}

	public boolean cutOffTest(Position p, int depth){
		if(depth == 0 || p.isMate() || p.isStaleMate()){
			return true;
		}
		return false;
	}

	public int getBoardValue(Position p, int depth, int maxDepth){
		
		int mateCheckMod = 1; //The closer a mate is, the higher value it will be assigned
		
		if(p.isMate()){
			int player = p.getToPlay();
			if(player == 0){
				return depth != 0 ? 7000 * depth : 5000;
			} else {
				return depth != 0 ? -7000 * depth : -5000;
			}
		}
		
		int modifier = maxDepth % 2 == 0 ? 1 : -1;
		
		int val =  p.getMaterial();
		val +=  p.getDomination();
		
		return (int)Math.pow((modifier * val), mateCheckMod);
	}
	
	public void recordMove(short move){
		moves.addFirst(move);
	}
	
	public boolean moveCanBeUsed(short move){
		if(moves.size() > 20){
			
			short player1Move1 = move;
			short player2Move1 = moves.get(0);
			
			short player1Move2 = moves.get(1);
			short player2Move2 = moves.get(2);
			
			for(int i = 1; i < 3; i++){
				if(player1Move1 == moves.get(i * 3) && player2Move1 == moves.get(i * 3 + 1) &&
						player1Move2 == moves.get(i * 3 + 2) && player2Move2 == moves.get(i * 3 + 3)){
					return false;
				}
			}
		}
		
		return true;
	}
	
	public void printStats(int states, int reached, int max){
		System.out.println("Number of states visited: " + states);
		System.out.println("Maximum depth set: " + max);
		System.out.println("Maximum depth reached: " + (max - reached));
	}
	
}
