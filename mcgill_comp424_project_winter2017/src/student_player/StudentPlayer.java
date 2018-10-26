package student_player;

import java.util.ArrayList;
import bohnenspiel.BohnenspielBoardState;
import bohnenspiel.BohnenspielMove;
import bohnenspiel.BohnenspielPlayer;
import bohnenspiel.BohnenspielMove.MoveType;

/**This class implements the project for this class... COMP424
 * @author parkerkingfournier
 */

/** A Hus player submitted by a student. */
public class StudentPlayer extends BohnenspielPlayer {

	double[] parameters = {0,0,0,0};
	
    /** You must modify this constructor to return your student number.
     * This is important, because this is what the code that runs the
     * competition uses to associate you with your agent.
     * The constructor should do nothing else. */
    public StudentPlayer(){ 
    	super("260556983");
    }

    /**This method gets the current board state, calls it "boardState" and
     * passes it to the principalVarationSearch method which will decide the 
     * best move.
     */
    public BohnenspielMove chooseMove(BohnenspielBoardState board_state)
    {
    	
    	
    	BohnenspielMove best_move;
    	Node pvs_result;
    	Node root_node;
    	double alpha = Double.NEGATIVE_INFINITY;
		double beta = Double.POSITIVE_INFINITY;
		int depth = 6;
		int color = 1;
		BohnenspielMove move = new BohnenspielMove("skip", board_state.getTurnPlayer());
		
		root_node = new Node(board_state, move, Double.NEGATIVE_INFINITY);
	    pvs_result = principalVariationSearch(root_node, depth, alpha, beta, color);
	    best_move = pvs_result.getBestMove();
    	
    	/**Return the best choice
    	 * 
    	*/
	    
        return best_move;
    }
    
    
    /** Principal variation search is a slightly better modification of alpha-beta pruning that aims to 
     * save time by reducing the amount of full alpha-beta searches that the algorithm does. It assumes that the first 
     * child of each node of the tree is the best one, or part of what is called the principal variation, and only checks
     * this one extensively. The other nodes, it only checks to make sure they are worse. If they're not worse, it researches 
     * them properly. More info can be found at these two sources:
     * 
     * 1) Wikipedia Page
     * 		https://en.wikipedia.org/wiki/Principal_variation_search
     * 
     * 2) chessprogamming.wikispaces.net
     * 		https://chessprogramming.wikispaces.com/Principal+Variation+Search
     * 
     */
    public Node principalVariationSearch(Node board_state_node, int depth, double alpha, double beta, int color){
    	
    	/**If the node being explored shows that I have one, return this node and give it a super high score.
    	 * Infinity works. Temporarily set the best move to be a "skip". This "skip" will never happen because the game is over.
    	 */
    	if(board_state_node.getBoardState().getWinner() == player_id){
    		board_state_node.setScore(Double.POSITIVE_INFINITY);
    		board_state_node.setBoardState(board_state_node.getBoardState());
    		BohnenspielMove move = new BohnenspielMove("skip", board_state_node.getBoardState().getTurnPlayer());
    		board_state_node.setBestMove(move);
    		return board_state_node;
    	}
    	/**Do the opposite of above if the node shows that I lose. Temporarily set the best move to be a "skip".
    	 * This "skip" will never happen because the game is over.
    	 */
    	else if(board_state_node.getBoardState().getWinner() == opponent_id){
    		board_state_node.setScore(Double.NEGATIVE_INFINITY);
    		board_state_node.setBoardState(board_state_node.getBoardState());
    		BohnenspielMove move = new BohnenspielMove("skip", board_state_node.getBoardState().getTurnPlayer());
    		board_state_node.setBestMove(move);
    		return board_state_node;
    	}
    	/**If the node has a board state that doesn't allow any moves, but the game isn't over yet
    	 * then set that nodes best move to be "skip" for whichever player's turn it is
    	 */
    	else if(board_state_node.getBoardState().getLegalMoves().size() == 0){
    		board_state_node.setScore(color*primaryHueristic(board_state_node.getBoardState()));
    		board_state_node.setBoardState(board_state_node.getBoardState());
    		BohnenspielMove move = new BohnenspielMove("skip", board_state_node.getBoardState().getTurnPlayer());
    		if(board_state_node.getBoardState().isLegal(move)){
    			board_state_node.setBestMove(move);
    		}
    		return board_state_node;
    	}
    	
    	/**If the node is at the max search depth, then find its heuristic value
    	 * and return that node. Temporarily set the best move from that node to be "skip".
    	 * This "skip" will be re-evaulated later. The only time a "skip" should happen is
    	 * if there are no available moves on the board. 
    	 */
    	else if(depth == 0){
    		board_state_node.setScore(color*primaryHueristic(board_state_node.getBoardState()));
    		board_state_node.setBoardState(board_state_node.getBoardState());
    		BohnenspielMove move = new BohnenspielMove("skip", board_state_node.getBoardState().getTurnPlayer());
    		board_state_node.setBestMove(move);
    		return board_state_node;
    	}
    	
    	/**Find all the legal moves from the current state, and sort them according
    	 * to the heuristic function.
    	 */
    	ArrayList<BohnenspielMove> legal_moves = board_state_node.getBoardState().getLegalMoves();
    	legal_moves = heuristicSort(legal_moves, board_state_node.getBoardState());
    	
    	/**Look through the moves searching the best ones first
    	 */
    	for(int i = 0; i < legal_moves.size(); i++){
    		
    		/**Apply the move to a clone of the current board state
    		 */
    		BohnenspielBoardState board_state_clone = (BohnenspielBoardState)board_state_node.getBoardState().clone();
    		board_state_clone.move(legal_moves.get(i));
    		
    		
    		/**Make a new node with that represents the board state after the move was applied
    		 * Temporarily set the best move to "skip". Again this "skip" will be re-evaluated with
    		 * deeper searches
    		 */
    		Node child_node;
    		BohnenspielMove move = new BohnenspielMove("skip", board_state_node.getBoardState().getTurnPlayer());
    		child_node = new Node(board_state_clone, move, Double.NEGATIVE_INFINITY);
    		
    		/**Every other move but the first move is probably worse than the first move. So just search
    		 * the associated nodes with a null window that is defined by the score from the best move.
    		 */
    		if(i != 0){
    			child_node = principalVariationSearch(child_node, depth-1, -(alpha-1), -alpha, -color);
    			
    			
    			/**If the score 
    			 * is bigger than the best move's score, then the best move wasn't the best, and we re-search
    			 * that node with a proper alpha-beta window.
    			 */
    			if(alpha < child_node.getScore() && child_node.getScore() < beta){
    				child_node = principalVariationSearch(child_node, depth-1, -beta, (-1)*child_node.getScore(), -color);
    			}
    		}
    		
    		/**The first move is probably the best move, so search it with a full alpha-beta window
    		 */
    		else{
    			child_node = principalVariationSearch(child_node, depth-1, -beta, -alpha, -color);
    		}
    		
    		/**It is necessary to do this because the minimax component of the principle variation search 
    		 * was turned into, for a lack of better term, a maximax algorithm which takes the max at 
    		 * tree depth. This is acceptable because the heuristic scores are reflected across 0 at each 
    		 * new tree depth and taking Minimum(all x) is equal to taking the Maximum(all -x). 
    		 */
    		child_node.setScore((-1)*child_node.getScore());
    		
    		
    		/**If the two sibling nodes have the same delta, or difference between scores, the tie is broken according
    		 * to a score calculated by learned parameters of the board state. See the method 'findStatistics' for more
    		 * information. The board_state node is updated accordingly if a "better" move is found according to the weighted 
    		 * primary statistic.
    		 */
    		if(child_node.getScore() == board_state_node.getScore() && i != 0 && legal_moves.get(i).getMoveType() != MoveType.SKIP){
    			double child_score = secondaryHueristic(child_node);
    			double board_score = secondaryHueristic(board_state_node);
    			if(child_score >= board_score){
    				board_state_node.setScore(child_node.getScore());
        			board_state_node.setBestMove(legal_moves.get(i));
        			alpha = board_state_node.getScore();
    			}
    		}
    		
    		/**If a node is found that has a score that is as good or better than the best score,
    		 * update this to reflect the new best move/node/board state/heuristic value
    		 */
    		else if((child_node.getScore() >= board_state_node.getScore()) && legal_moves.get(i).getMoveType() != MoveType.SKIP){
    			board_state_node.setScore(child_node.getScore());
    			board_state_node.setBestMove(legal_moves.get(i));
    			alpha = board_state_node.getScore();
    		}
    	
    		/**Beta cutoff
    		 */
    		if(alpha >= beta){
    			break;
    		}
    	}
    	
    	/**Find which statistics are representative of this board state
    	 */
    	findStatistics(board_state_node, legal_moves);
   
    	return board_state_node;
    }
    
    /**This method finds which parameters or attributes of how "good" a board state are apply to the
     * board state returned at each iteration of principal variation search. The array 'parameters' which is
     * a field of the class Node thus represents the "goodness" of a particular board state and can be used to
     * break ties between board states that have the same difference in score, which is being used as the primary
     * heuristic.
     */
    public void findStatistics(Node board_state_node, ArrayList<BohnenspielMove> legal_moves){
    	
    	int[][] sibling_array;
    	int[][] board_state_array = board_state_node.getBoardState().getPits();
    	
    	int number_of_moves = board_state_node.getBoardState().getLegalMoves().size();
    	int number_of_player_seeds = numberOfPlayerSeeds(board_state_array);
    	int number_of_opponent_seeds = numberOfOpponentSeeds(board_state_array);
    	
    	int theta1 = 1;
    	int theta2 = 1;
    	int theta3 = 1;

    	for(int i = 0; i < legal_moves.size(); i++){
    		
    		BohnenspielBoardState board_state_clone = (BohnenspielBoardState)board_state_node.getBoardState().clone();
    		board_state_clone.move(legal_moves.get(i));
    		sibling_array = board_state_clone.getPits();
    		
    		int sibling_number_of_moves = board_state_clone.getLegalMoves().size();
        	int sibling_number_of_player_seeds = numberOfPlayerSeeds(sibling_array);
        	int sibling_number_of_opponent_seeds = numberOfOpponentSeeds(sibling_array);
    		
        	if(number_of_moves < sibling_number_of_moves){
        		theta1 = 0;
        	}
        	if(number_of_player_seeds < sibling_number_of_player_seeds){
        		theta2 = 0;
        	}
        	if(number_of_opponent_seeds < sibling_number_of_opponent_seeds){
        		theta3 = 0;
        	}
    	}
    	
    	board_state_node.setParameters(0, theta1);
    	board_state_node.setParameters(1, theta2);
    	board_state_node.setParameters(2, theta3);


    	this.parameters[0] += theta1;
    	this.parameters[1] += theta2;
    	this.parameters[2] += theta3;
    	this.parameters[3]++;
    }
    
    
    
    /**I wanted to just make my own custom compareTo function, but do to class hierarchy confusion
     * I opted to implement a merge sort to sort the list of moves. This returns a sorted list of 
     * moves according to the heuristic function of the resulting board states.
     */
    public ArrayList<BohnenspielMove> heuristicSort(ArrayList<BohnenspielMove> whole, BohnenspielBoardState board_state){
    	ArrayList<BohnenspielMove> left = new ArrayList<BohnenspielMove>();
    	ArrayList<BohnenspielMove> right = new ArrayList<BohnenspielMove>();
    	int center;
    	if(whole.size() == 1){
    		return whole;
    	}
    	else{
    		center = whole.size()/2;
    		for(int i = 0; i < center; i++){
    			left.add(whole.get(i));
    		}
    		for(int i = center; i < whole.size(); i++){
    			right.add(whole.get(i));
    		}
    		left = heuristicSort(left, board_state);
    		right = heuristicSort(right, board_state);
    		merge(left, right, whole, board_state);
    	}
    	return whole;
    }
    
   /**The merge function merges two lists by comparing the heuristic value of each board state resulting from the moves
    * in the two lists called left and right.
    */
   private void merge(ArrayList<BohnenspielMove> left, ArrayList<BohnenspielMove> right, ArrayList<BohnenspielMove> whole, BohnenspielBoardState board_state) {
	    int left_point = 0;
	    int right_point = 0;
	    int whole_point = 0;
	    while (left_point < left.size() && right_point < right.size()) {
	    	BohnenspielBoardState left_clone = (BohnenspielBoardState)board_state.clone();
			left_clone.move((BohnenspielMove)left.get(left_point));
			double left_value = primaryHueristic(left_clone);
			BohnenspielBoardState right_clone = (BohnenspielBoardState)board_state.clone();
			right_clone.move((BohnenspielMove)right.get(right_point));
			double right_value = primaryHueristic(right_clone);
	        if ( left_value <= right_value) {
	            whole.set(whole_point, left.get(left_point));
	            left_point++;
	        } 
	        else{
	            whole.set(whole_point, right.get(right_point));
	            right_point++;
	        }
	        whole_point++;
	    }
	    ArrayList<BohnenspielMove> rest;
	    int restIndex;
	    if (left_point >= left.size()){
	        rest = right;
	        restIndex = right_point;
	    } 
	    else{
	        rest = left;
	        restIndex = left_point;
	    }
	    for (int i=restIndex; i<rest.size(); i++){
	        whole.set(whole_point, rest.get(i));
	        whole_point++;
	    }
	}
   
    
    
    /**This method takes a list of the legal moves, an empty array list of Nodes called children and the current board state
     * for each move, it applies it to a clone of the board state. it adds that resulting state and the move that
     * made it happen into a new node with a score value of negative infinity
     */
    public void loadChildren(ArrayList<BohnenspielMove> legal_moves, ArrayList<Node> children, BohnenspielBoardState board_state){
    	
    	for(int i = 0; i < legal_moves.size(); i++){
    		
    		BohnenspielBoardState cloned_board_state = (BohnenspielBoardState)board_state.clone();
   
    		cloned_board_state.move(legal_moves.get(i));
    		Node newNode = new Node(cloned_board_state, cloned_board_state.getLegalMoves().get(i), Double.NEGATIVE_INFINITY);
    		children.add(i, newNode);
    	}
    }
    
    /**This is a simple delta which is the score between the players. It will be a positive number
     * when I am winning, but a negative number when the other person is playing. Maximizing this value maximizes
     * my lead in the game. Since the goal of the game is to win, the player should always want to maximize their lead.
     * Thus this will be used as the primary heuristic and ties will be broken according to learned parameters that
     * represent the "goodness" of a board state. Hopefully the combination of primary and secondary heuristics will be a good 
     * enough move ordering function to really take advantage of principal variation search.
     */
    public double primaryHueristic(BohnenspielBoardState board_state){
    	double delta = board_state.getScore(player_id) - board_state.getScore(opponent_id);
    	return delta;
    }
    
    
    /**This method takes the total number of times that a board attribute (described above as theta) is 
     * present, or representative, of a "best" board state. It normalizes this to produce a percentage. 
     * Each percentage is then used as a weight to weight the total score of a certain node/board state
     * given that the board state has its 'parameters' field already evaulated. This weighted sum represents the
     * "goodness" of a board state and will break ties in the even that two states have the same difference in score.
     */
    public double secondaryHueristic(Node node){
 	   double score = 0;
 	   double[] parameters = node.getParameters();
 	   for(int i = 0; i < parameters.length; i++){
 		   score += parameters[i]*this.parameters[i]/this.parameters[parameters.length-1];
 	   }

 	   return score;
    }
   
    
   /**These methods are used in calculating the parameters or attributes indicating how "good" a board state is. I found that
    * they were good indicators of how much potential a board state has from personal experience playing the game
    */ 
   public int numberOfOnes(int[][] array){
	   int number_of_ones = 0;
	   for (int i = 0; i<array.length; i++){
		     for (int j = 0; j<array[i].length; j++){
		    	 if(array[i][j] == 1){
		    		 number_of_ones++;
		    	 }
		     } 
	   }
	   return number_of_ones;
   }
   
   public int numberOfThrees(int[][] array){
	   int number_of_threes = 0;
	   for (int i = 0; i<array.length; i++){
		     for (int j = 0; j<array[i].length; j++){
		    	 if(array[i][j] == 3){
		    		 number_of_threes++;
		    	 }
		     } 
	   }
	   return number_of_threes;
   }
   
   public int numberOfFives(int[][] array){
	   int number_of_fives = 0;
	   for (int i = 0; i<array.length; i++){
		     for (int j = 0; j<array[i].length; j++){
		    	 if(array[i][j] == 5){
		    		 number_of_fives++;
		    	 }
		     } 
	   }
	   return number_of_fives;
   }
   
   public int numberOfPlayerSeeds(int[][] array){
	   int seeds = 0;
	   int[] player_side = array[player_id];
	   for(int i = 0; i < player_side.length; i++){
		   seeds+=player_side[i];
	   }
	   return seeds;
   }
   
   public int numberOfOpponentSeeds(int[][] array){
	   int seeds = 0;
	   int[] opponent_side = array[opponent_id];
	   for(int i = 0; i < opponent_side.length; i++){
		   seeds+=opponent_side[i];
	   }
	   return seeds;
   }
   
   
}