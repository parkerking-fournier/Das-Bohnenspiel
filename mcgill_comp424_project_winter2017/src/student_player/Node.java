package student_player;

import bohnenspiel.BohnenspielBoardState;
import bohnenspiel.BohnenspielMove;


/**This class is used when making the principal variation search tree
 * each Node contains a board state, the best move from that node according to the
 * principal variation search, and a score that is calculated according to the heuristic
 */
public class Node extends BohnenspielBoardState{

	private BohnenspielBoardState board_state;
	private BohnenspielMove best_move;
	private double score;
	private double[] parameters;
	
	/**Constructor with no arguments
	 */
	public Node(){
		this.board_state = null;
		this.best_move = null;
		this.score = Double.NEGATIVE_INFINITY;
		this.parameters = new double[] {0,0,0};
	}
	
	/**Constructor with arguments
	 */
	public Node(BohnenspielBoardState board_state, BohnenspielMove best_move, double score){
		
		this.board_state = board_state;
		this.best_move = best_move;
		this.parameters = new double[] {0,0,0};
		this.score = score;
	}
	
	/**SET METHODS
	 */
	public void setBoardState(BohnenspielBoardState board_state){
		this.board_state = board_state;
	}
	
	public void setBestMove(BohnenspielMove move){
		this.best_move = move;
	}
	
	
	public void setScore(double score){
		this.score = score;
	}
	
	public void setParameters(int index, double value){
		this.parameters[index] = value;
	}

	
	
	
	/**GET METHODS
	 */
	public BohnenspielBoardState getBoardState(){
		return this.board_state;
	}
	
	public BohnenspielMove getBestMove(){
		return this.best_move;
	}
	
	
	public double getScore(){
		return this.score;
	}
	
	public double[] getParameters(){
		return this.parameters;
	}
}

