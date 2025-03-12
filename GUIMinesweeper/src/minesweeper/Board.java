package minesweeper;
//supports minesweeper class
class Board {
	
	private Tile[][] board;
	private static int size;
	private static int bombs;
	
	//these are the ANSI color codes for all text and background colors
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_WHITE = "\u001B[37m";
    
    public static final String BLACK_BRIGHT = "\033[0;90m";  // BLACK
    public static final String RED_BRIGHT = "\033[0;91m";    // RED
    public static final String GREEN_BRIGHT = "\033[0;92m";  // GREEN
    public static final String YELLOW_BRIGHT = "\033[0;93m"; // YELLOW
    public static final String BLUE_BRIGHT = "\033[0;94m";   // BLUE
    public static final String PURPLE_BRIGHT = "\033[0;95m"; // PURPLE
    public static final String CYAN_BRIGHT = "\033[0;96m";   // CYAN
    public static final String WHITE_BRIGHT = "\033[0;97m";  // WHITE
    
    public static final String BLACK_BACKGROUND = "\033[40m";  // BLACK
    public static final String RED_BACKGROUND = "\033[41m";    // RED
    public static final String YELLOW_BACKGROUND = "\033[43m"; // YELLOW
    public static final String WHITE_BACKGROUND = "\033[47m";  // WHITE
    
    public static final String BLACK_BACKGROUND_BRIGHT = "\033[0;100m";// BLACK
    public static final String LIGHT_BACKGROUND = "\033[48;5;244m";// GRAY
	
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
	
	//moves the console screen down to refresh game board
	private static void clearScreen() {  
	    System.out.print("\033[H\033[2J");  
	    System.out.flush();  
	}//clearScreen
	
	//print given board to the console and clearscreen if true
	public void displayBoard(boolean clearScreen) {
		
		//i want to clear screen to have illusion of screen changing seamlessly,
		//but i want to show multiple boards at the end of the game
		if(clearScreen) {
			clearScreen();
		}
		
		//print A-Z at the top for ease of use
		System.out.printf("%6c  ",'A');
		for(int i=1; i<size; i++) {
			System.out.printf("%-3c",(char)(i+65));
		}
		System.out.println("\n");
		
		//loop through whole board to display each tile
		for(int i=0; i<size; i++) {
			//print numbers on the side for ease of use
			System.out.printf("%-5d",i);
			
			for(int j=0; j<size; j++) {
				
				char value = board[i][j].getValue();
				
				//creates a grid to make tile locating easier
				String background = LIGHT_BACKGROUND;
				if((i%2 == 1 && j%2 == 1) || (i%2 != 1 && j%2 != 1)) {
					background = BLACK_BACKGROUND;
				}
				
				//determine the color of text and background based on what value is displayed
				String color = ANSI_WHITE;
				switch(value) {
				case '0':
					//creates grid for empty tiles so we can still easily look to see tile locations
					background = "\u001b[48;5;71m";
					if((i%2 == 1 && j%2 == 1) || (i%2 != 1 && j%2 != 1)) {
						background = "\u001b[48;5;34m";
					}
					color=ANSI_RESET;
					break;
				case '1':
					color=BLUE_BRIGHT;
					break;
				case '2':
					color=GREEN_BRIGHT;
					break;
				case '3':
					color=RED_BRIGHT;
					break;
				case '4':
					color=PURPLE_BRIGHT;
					break;
				case '5':
					color=ANSI_YELLOW;
					break;
				case '6':
					color=CYAN_BRIGHT;
					break;
				case '7':
					color=BLACK_BRIGHT;
					break;
				case '8':
					color=ANSI_RESET;
					break;
				case 'F':
					background=YELLOW_BACKGROUND;
					color=YELLOW_BRIGHT;
					break;
				case '?':
					color=WHITE_BRIGHT;
					break;
				case '*':
					background=RED_BACKGROUND;
					color=YELLOW_BRIGHT;
					break;
				default:
					color=YELLOW_BRIGHT;
					break;
					
				}
				
				//print the value with colors
				System.out.printf("%s%s%-3c",color,background,value);
				
			}
			
			//end of row make new line and reset color
			System.out.println(ANSI_RESET);
		}
	}//displayBoard
	
	private void swap(int[] array, int a, int b) {
	int temp = array[a];
	array[a] = array[b];
	array[b] = temp;
}
}//Board
