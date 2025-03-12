package minesweeper;
//supports minesweeper class
class Board {
	
	private Tile[][] board;
	private static int size;
	private static int bombs;
	
    //constructor to create a board of size size with only unrevealed tiles
	public Board(int boardSize) {
		size = boardSize;
		board = new Tile[size][size];
		for(int i=0; i<size; i++) {
			for(int j=0; j<size; j++) {
				board[i][j] = new Tile('?');
			}
		}
	}
	
	//constructor to create a board of size size with number of bombs 
	//and setting all non bomb tiles to the number of bombs next to the tile
	public Board(int boardsize, int numBombs, int safe) {
		
		bombs = numBombs;
		board = new Tile[size][size];
		
		//create empty board
		for(int i=0; i<size; i++) {
			for(int j=0; j<size; j++) {
				board[i][j] = new Tile('?');
			}
		}//Board
		
			
		//fill board with bpmbs
		int[] array = new int[size*size];
		int maxAmount = (size*size)-1;
		int targetAmount = numBombs;

		int[] results = new int[targetAmount];
		
		for(int i=0; i<array.length; i++) {
			array[i] = i;
		}
		
		swap(array,safe,maxAmount);
		maxAmount-=1;
		
		for(int i=0; i<targetAmount; i++) {
			int key = (0+(int)(((maxAmount)-1)*(Math.random())));
			results[i] = array[key];
			swap(array, key, maxAmount);
			maxAmount-=1;
		}
		
		for(int i=0; i<results.length; i++) {
			int column = results[i]/size;
			int row = results[i]%size;
			board[row][column] = new Tile('*');
		}

		
		/*
		 * This sets the value of all non mine tiles to the number of surrounding mines
		 * making sure to not check out of bounds tiles
		 */
		for(int i=0; i<size; i++) {
			for(int j=0; j<size; j++) {
				if(board[i][j].getValue() != '*') {
					int surBombs = 0;
					if(i != size-1 && board[i+1][j].getValue() == '*') {
						surBombs++;
					}
					if(i != 0 && board[i-1][j].getValue() == '*') {
						surBombs++;
					}
					if(j != size-1 && board[i][j+1].getValue() == '*') {
						surBombs++;
					}
					if(j != 0 && board[i][j-1].getValue() == '*') {
						surBombs++;
					}
					if(i != size-1 && j != 0 && board[i+1][j-1].getValue() == '*') {
						surBombs++;
					}
					if(i != size-1 && j != size-1 && board[i+1][j+1].getValue() == '*') {
						surBombs++;
					}
					if(i != 0 && j != size-1 && board[i-1][j+1].getValue() == '*') {
						surBombs++;
					}
					if(i !=0 && j!=0 && board[i-1][j-1].getValue() == '*') {
						surBombs++;
					}
					board[i][j].setValue((char)(surBombs+48));
				}
			}
		}//change tiles to num of bombs next to tile
	}//Board
	
	//return size of board
	public static int getSize() {
		return size;
	}//getSize
	
	public static void setBombs(int numBombs) {
		bombs = numBombs;
	}
	//return number of bombs on the board
	public static int getNumBombs() {
		return bombs;
	}//getNumBombs
	
	//returns char value of given tile
	public char getTileValue(int column, int row) {
		return board[column][row].getValue();
	}//getTileValue
	
	//changes char value of given tile
	public void setTileValue(int column, int row, char value) {
		board[column][row].setValue(value);
		
	}//setTileValue
	
	private void swap(int[] array, int a, int b) {
	int temp = array[a];
	array[a] = array[b];
	array[b] = temp;
}
}//Board
