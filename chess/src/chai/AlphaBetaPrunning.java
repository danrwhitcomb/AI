package chai;


import chesspresso.position.Position;
import chesspresso.move.IllegalMoveException;

import java.util.HashMap;

public class AlphaBetaPrunning implements ChessAI {
	
	private GameAudit ga;
	private int MAX_DEPTH = 7;
	private short bestMove;
	private int statesVisited;
	private int maxDepthVisited;
	private HashMap<Long, ValueNode> transTable;

	public AlphaBetaPrunning(int player, GameAudit n_Ga){
		ga = n_Ga;
	}
	
	private class ValueNode{
		public int value;
		public int depth;
		
		public ValueNode(int n_val, int n_depth){
			value = n_val;
			depth = n_depth;
		}
	}

	public short getMove(Position p){
		Position newP = new Position(p);

		bestMove = Short.MIN_VALUE;
		statesVisited = 0;
		maxDepthVisited = 0;
		transTable = new HashMap<Long, ValueNode>();
		
		abPrunning(newP, MAX_DEPTH, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
		ga.printStats(statesVisited, maxDepthVisited, MAX_DEPTH);
		
		return bestMove;
	}
	
	private int abPrunning(Position p, int depth, int alpha, int beta, boolean player){
		if(ga.cutOffTest(p ,depth)){
			Integer val = ga.getBoardValue(p, depth, MAX_DEPTH);
			
			if(depth < maxDepthVisited){
				maxDepthVisited = depth;
			}
			return val;
		}
		
		int bestAlpha = Integer.MIN_VALUE;

		if(player){
			for(short move : p.getAllMoves()){

				try{
					p.doMove(move);
				} catch(IllegalMoveException exc){
					System.out.println("Illegal Move Exception");
					continue;
				}
				statesVisited++;
				Long hash = p.getHashCode();
				ValueNode node = transTable.get(hash);
				if(node != null && node.depth < depth){
					alpha = Integer.max(alpha, node.value);
				} else{
					alpha = Integer.max(alpha, abPrunning(p, depth - 1, alpha, beta, false));
				}

				if(depth == MAX_DEPTH && alpha > bestAlpha){
					bestMove = move;
					bestAlpha = alpha;
				}
				
				if(beta <= alpha){
					p.undoMove();
					break;
				}
				
				p.undoMove();
				
			}
			if(depth == MAX_DEPTH){System.out.println("Best value: "  + alpha);}
			

			Long hash = p.getHashCode();
			ValueNode node;
			node = transTable.get(hash);
			if(node != null){
				if(node.depth <= depth){ 
					node.depth = depth;
				}
			} else {
				transTable.put(hash, new ValueNode(alpha, depth));
			}
			
			
			return alpha;
		} else {
			for(short move : p.getAllMoves()){

				try{
					p.doMove(move);
				} catch(IllegalMoveException exc){
					continue;
				}
				statesVisited++;
				Long hash = p.getHashCode();
				ValueNode node = transTable.get(hash);
				if(node != null && node.depth < depth){
					beta = Integer.min(beta, node.value);
				} else{
					beta = Integer.min(beta, abPrunning(p, depth - 1, alpha, beta, true));
				}
				
				if(beta <= alpha){
					p.undoMove();
					break;
				}
				
				p.undoMove();
				
			}
			
			Long hash = p.getHashCode();
			ValueNode node;
			node = transTable.get(hash);
			if(node != null){
				if(node.depth <= depth){ 
					node.depth = depth;
				}
			} else {
				transTable.put(hash, new ValueNode(beta, depth));
			}
			
			return beta;	
		}
	}
}
