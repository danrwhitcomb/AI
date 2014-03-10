package chai;


import chesspresso.Chess;
import chesspresso.position.Position;
import chesspresso.move.IllegalMoveException;

public class MiniMax implements ChessAI {

	private GameAudit ga;
	private int MAX_DEPTH = 6;
	private short bestMove = Short.MIN_VALUE;
	private int statesVisited;
	private int maxDepthVisited;

	public MiniMax(int player, GameAudit n_ga){
		ga = n_ga;
	}

	public short getMove(Position p){
		Position newP = new Position(p);
		
		bestMove = Short.MIN_VALUE;
		statesVisited = 0;
		maxDepthVisited = 0;
		depthFirstMiniMax(newP, MAX_DEPTH, true);
		
		ga.recordMove(bestMove);
		ga.printStats(statesVisited, maxDepthVisited, MAX_DEPTH);
		return bestMove;
	}

	private int depthFirstMiniMax(Position p, int depth, boolean player){
		if(ga.cutOffTest(p ,depth)){
			
			if(depth < maxDepthVisited){
				maxDepthVisited = depth;
			}
			
			return ga.getBoardValue(p, depth, MAX_DEPTH);
		}

		int bestValue;
		int val;

		if(player){
			bestValue = Integer.MIN_VALUE;
			for(short move : p.getAllMoves()){

				try{
					p.doMove(move);
				} catch(IllegalMoveException exc){
					continue;
				}

				statesVisited++;
				val = depthFirstMiniMax(p, depth - 1, false);
				
				if(Integer.max(bestValue, val) == val && ga.moveCanBeUsed(move)){
					bestValue = val;
					bestMove = depth == MAX_DEPTH ? move : bestMove;
				}
				
				p.undoMove();
				
			}
			if(depth == MAX_DEPTH){System.out.println("Best value: "  + bestValue);}
			return bestValue;
		} else {
			bestValue = Integer.MAX_VALUE;
			for(short move : p.getAllMoves()){

				try{
					p.doMove(move);
				} catch(IllegalMoveException exc){
					continue;
				}

				statesVisited++;
				val = depthFirstMiniMax(p, depth - 1, true);
				bestValue = Integer.min(bestValue, val);
				
				if(Integer.min(bestValue, val) == val && ga.moveCanBeUsed(move)){
					bestValue = val;
					bestMove = depth == MAX_DEPTH ? move : bestMove;
				}
				
				p.undoMove();
				
			}
			return bestValue;
			
		}
	}
}
