
public class Bot1 implements BotAPI {

    //Written by: Joanne Reilly - 17485602

    private PlayerAPI me, opponent;
    private BoardAPI board;
    private CubeAPI cube;
    private MatchAPI match;
    private InfoPanelAPI info;
	
	private Players players;

    Bot1 (PlayerAPI me, PlayerAPI opponent, BoardAPI board, CubeAPI cube, MatchAPI match, InfoPanelAPI info) {
        this.me = me;
        this.opponent = opponent;
        this.board = board;
        this.cube = cube;
        this.match = match;
        this.info = info;
		
		players = new Players();
    }

    public String getName() {
        
    	return "Bot1"; // must match the class name
    }

    public String getCommand(Plays possiblePlays) {
    	//Algorithm:
		//Generate a list of all legal plays
		//Generate all possible resulting board positions
		//Go through the board positions and get score
		//Pick play with the highest score
		return Integer.toString(pickPlay(possiblePlays));
        
    }

    public String getDoubleDecision() {
        // Add your code here
        return "y";
    }
	
	private double makePlay(Player player, Play play) {
		int [][] b = board.get();
		//copy of move methods in Board.java
		for (Move move : play) {
			b[player.getId()][move.getFromPip()]--;
	        b[player.getId()][move.getToPip()]++;
	        int opposingPlayerId = players.getOpposingPlayer(player).getId();
	        if (move.getToPip()<Board.BAR && move.getToPip()>Board.BEAR_OFF &&
	                b[opposingPlayerId][calculateOpposingPip(move.getToPip())] == 1) {
	            b[opposingPlayerId][calculateOpposingPip(move.getToPip())]--;
	            b[opposingPlayerId][Board.BAR]++;
	        }
        }
		
		return score(b, play);
	}
	
	private double score(int [][] currentBoard, Play play) {
		//This method gives state of board a score
		double probability = probabilityOfWin(currentBoard);
		
		//also take into account other factors such as a hit
		int hit = 0;
		for(int i = 0; i < play.numberOfMoves(); i++)
		{
			if(play.getMove(i).isHit())
			{
				hit++;
			}
		}
		return probability + (hit*10);//return score
	}
	
	private double probabilityOfWin(int [][] currentBoard) {
		//gets probability of win for state of board
		int numberOfFeatures = 9;
		//Identify board features & score them
		double [] features = new double [numberOfFeatures];
			//pip count difference
			features[0] = pipCountDiff(currentBoard);
			//block-blot difference
			features[1] = blockBlotDiff(currentBoard);
			//number of home board blocks
			features[2] = homeBlocksNum(currentBoard);
			//length of prime with captured checker
			features[3] = primeLength(currentBoard);
			//anchors in opponent's home board
			features[4] = anchorsInOpponentsHome(currentBoard);
			//number of escaped checkers
			features[5] = escapedCheckersNum(currentBoard);
			//number of checkers in your home board
			features[6] = homeBoardCheckersNum(currentBoard);
			//number of points covered
			features[7] = pointsCoveredNum(currentBoard);
			//number of checkers taken off
			features[8] = checkersTakenOff(currentBoard);
		//Get weights
		double [] weights = getWeights();
		//Multiply features by weights and sum together
		double total = 0;
		for(int i = 0; i < numberOfFeatures; i++)
		{
			total += (features[i]*weights[i]);
		}
		return total;
	}
	
	private int pickPlay(Plays possiblePlays) {
		double scores [] = new double [possiblePlays.number()];
        
    	for(int i = 0; i < possiblePlays.number(); i++)
    	{
    		scores[i] = makePlay(players.get(me.getId()), possiblePlays.get(i));
    	}
		
		//pick move with highest score
		int bestPlay = 0;
		double bestScore = 0;
		
		for(int i = 0; i < possiblePlays.number(); i++)
    	{
    		if(scores[i] >= bestScore)
			{
				bestPlay = i;
				bestScore = scores[i];
			}
		}
		
		return bestPlay + 1;
	}
	
	private double pipCountDiff(int [][] currentBoard) {
		int[][] checkers = currentBoard;
		double myPipCount = 0;
		double opponentsPipCount = 0;
		for(int i = 1; i < 25; i++)
		{
			myPipCount += checkers[me.getId()][i];
		}
		
		for(int i = 1; i < 25; i++)
		{
			opponentsPipCount += checkers[opponent.getId()][i];
		}
		
		return opponentsPipCount - myPipCount;
	}
	
	private double blockBlotDiff(int [][] currentBoard) {
		int[][] checkers = currentBoard;
		//number of blocks by me
		double blocks = 0;
		//number of blots by me
		double blots = 0;
		for(int i = 1; i < 25; i++)
		{
			if(checkers[me.getId()][i] > 1)
			{
				blocks++;
			}
			
			if(checkers[me.getId()][i] < 2)
			{
				blots++;
			}
		}
		
		return blocks - blots;
	}
	
	private double homeBlocksNum(int [][] currentBoard) {
		
		double homeBoardBlocks = 0;
		for(int i = 1; i < 7; i++)
		{
			if(currentBoard[me.getId()][i] > 1)
			{
				homeBoardBlocks++;
			}
		}
		return homeBoardBlocks;
	}
	
	private double primeLength(int [][] currentBoard) {
		//my longest prime
		boolean [] blocks = new boolean [24];
		//mark all pips that are blocked as true;
		for(int i = 1; i < 25; i++)
		{
			if(currentBoard[me.getId()][i] > 1)
			{
				blocks[i-1] = true;
			}
		}
		
		int l = longestSequence(blocks);
		return (double) l;
	}
	
	private double anchorsInOpponentsHome(int [][] currentBoard) {
		//get number of anchors in Opponents Home Board
		int anchors = 0;
		for(int i = 19; i < 25; i++)
		{
			if(currentBoard[me.getId()][i] > 1)
			{
				anchors++;
			}
		}
		
		return anchors;
	}
	
	private double escapedCheckersNum(int [][] currentBoard) {
		//get the number of my checkers outside blockade
		int [] indexsOfPrime = indexsOfPrime(currentBoard);
		if(indexsOfPrime[0] == indexsOfPrime[1])//there's no prime
		{
			int count = 0;
			for(int i = 0; i < 25; i++)
			{
				count += currentBoard[me.getId()][i];
			}
			return count;
		}
		else
		{
			int escaped = 0;
			for(int i = 0; i < (25 - indexsOfPrime[1]); i++)
			{
				if(currentBoard[me.getId()][i] > 0)
				{
					escaped++;
				}
			}
			return escaped;
		}
	}
	
	private double homeBoardCheckersNum(int [][] currentBoard) {
		int[][] checkers = currentBoard;
		double checkerCount = 0;
		for(int i = 1; i < 7; i++)
		{
			checkerCount += checkers[me.getId()][i];
		}
		
		return checkerCount;
	}
	
	private double checkersTakenOff(int [][] currentBoard)
	{
		int[][] checkers = currentBoard;
		
		return checkers[me.getId()][Board.BEAR_OFF];
	}
	
	private double pointsCoveredNum(int [][] currentBoard) {
		//number of triangles covered
		int points = 0;
		for(int i = 1; i < 25; i++)
		{
			if(currentBoard[me.getId()][i] > 0)
			{
				points++;
			}
		}
		return points;
	}
	
	private double [] getWeights()
	{
		double [] weights = {5,7,7,8,2,3,9,10,8};
		
		//have bot play against itself to change weights
		
		return weights;
	}
	
	private int calculateOpposingPip(int pip) {
        return Board.NUM_PIPS-pip+1;
    }
	
	private int longestSequence(boolean [] a)
	{
		int countTrues = 0;
		for(int i = 0; i < a.length; i++)
		{
			if(a[i] == true)
			{
				countTrues++;
			}
		}
		
		if(countTrues == 0)
		{
			return 0;
		}
		else
		{
		    int count = 1, max = 1;
		    for(int i = 1; i < a.length; i++)
		    {
		        if(a[i] == true && a[i - 1]==true)
		        {
		            count++;
		        } else {
		            if (count > max)
		            {
		                max = count;
		            }
		            count = 1;
		        }
		    }
	
		    if (count> max)
		    {
		        return count;
		    } else
		    {
		        return max;
		    } 
		}
	}
	
	private int [] indexsOfPrime(int [][] currentBoard)
	{
		//Gets indexs of opponent's blockade
		int [] indexs = {0,0};
		//Get length of their prime
		boolean [] blocks = new boolean [24];
		for(int i = 1; i < 25; i++)
		{
			if(currentBoard[opponent.getId()][i] > 1)
			{
				blocks[i-1] = true;
			}
		}
		int l = longestSequence(blocks);
		if(l == 0)
		{
			return indexs;
		}
		else
		{
			//Model the sequence
			boolean [] allIndexs = new boolean [l];
			for(int i = 0; i < l; i++)
			{
				allIndexs[i] = true;
			}
			
			//find match to sequence
			boolean match;
			for(int i = 0; i < (24 - l); i++)
			{
				match = true;
				for(int j = 0; j < allIndexs.length; j++)
				{
					if(allIndexs[j] != blocks[i+j])
					{
						match = false;
						j = allIndexs.length;
					}
				}
				if(match == true)
				{
					indexs[0] = i + 1;
					indexs[1] = i + l;
					i = 24;
				}
			}
			//index 0 should always be smaller
			if(indexs[0] > indexs[1])
			{
				int tmp = indexs[0];
				indexs[0] = indexs[1];
				indexs[1] = tmp;
			}
			
			return indexs;
		}
	}

	//Notes:
	//maybe make two versions of methods that return positive and negatives
}
