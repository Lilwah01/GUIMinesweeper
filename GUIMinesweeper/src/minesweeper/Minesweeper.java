package minesweeper;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class Minesweeper implements MouseListener, ActionListener{
	
	//all values that are needed by multiple functions for logic
	
	//One frame that allows to change whats on screen
	private JFrame frame;
	//field buttons
	private JButton buttons[][];
	//buttons for prompting new game or shutdown
	private JButton startOver;
	private JButton endGame;
	//bool to check if we can continue revealing
	private boolean gameOver = false;
	//info for creating boards, checking boards,
	private int size;
	private int numBombs;
	//info telling user how many bombs they have not flagged
	private int flagged;
	private JLabel bombsLeft;
	//actual game data for game logic, only computer knows
	private Board actualBoard;
	//game data shown to player in the buttons
	private Board displayBoard;
	//contains game field made of buttons
	private JPanel field;
	//contains anything else the user may need during gameplay
	private JPanel top;
	//get user size input
	private JTextField sizeInput;
	private JButton sizeButton;
	//get user numBombs input
	private JTextField bombInput;
	private JButton bombButton;

	Minesweeper() {
		//start the game loop
		askSize();

	}
	
	//creates a JFrame that contains the play field
	public void startGame() {
		
		//if frame exists get rid of it and make new one
		if(frame != null) {
			frame.dispose();
		}
		frame  = new JFrame("MineSweeper");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//start of game init
		gameOver = false;
		flagged = 0;
		
		//set min size of frame based on size of board
		Dimension frameSize = new Dimension(size*65,size*65+75);
		frame.setMinimumSize(frameSize);
		//set layout to null so I can put elements where they need to be
		frame.setLayout(null);
		
		//create top panel that contains game info
		top = new JPanel(new FlowLayout());
		top.setBackground(new Color(25,90,10));
		//add bombsleft label to top panel
		bombsLeft = new JLabel("Bombs left: "+(numBombs-flagged));
		bombsLeft.setFont(new Font("Arial", Font.BOLD, 20));
		top.add(bombsLeft);
		
		//Create the game field that is a grid that contains size*size elements
		field = new JPanel(new GridLayout(size,size,0,0));
		//create way to store buttons, so we can check what button was pressed
		buttons = new JButton[size][size];
		
		//Create two empty boards
		displayBoard = new Board(size);
		actualBoard = new Board(size);
		
		//loop through square 2d array of size
		for(int i=0; i<size; i++) {
			for(int j=0; j<size; j++) {
				//create a button with label of corresponding display board value
				JButton button = new JButton(""+displayBoard.getTileValue(i, j));
				button.setBounds(i*25,j*25,25,25);
				button.setOpaque(true);
				button.setFont(new Font("Arial",Font.BOLD,20));
				
				//add button to game field
				field.add(button);
				//add button to buttons array so we can refence later
				buttons[i][j] = button;
				
				//add mouse listener to detect when left or right clicked
				//see mouseClicked for click logic
				button.addMouseListener(this);
				
				//Changes button background and text color based on where it is and button value
				recolor(i,j);
			}
		}
		//resize and reposition panels to ensure they fit the frame
		field.setBounds(0,100,frame.getWidth(),frame.getHeight()-100);
		top.setBounds(0,0,frame.getWidth(),100);
		
		//add panels to frame
		frame.add(field);
		frame.add(top);
		
		//resize frame to ensure panels fit frame
		frameSize = new Dimension(field.getWidth(),field.getHeight()+140);
		frame.setSize(frameSize);
		frame.setVisible(true);
	}
	
	//creates a JFrame that asks user for board size
	public void askSize() {
		
		//if frame exists, get rid of it and create new onw
		if(frame != null) {
			frame.dispose();
		}
		frame  = new JFrame("MineSweeper");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(500,500);
		
		//ask user for input on size of board
		JLabel label = new JLabel("Please enter size of board between 5 and 20: ");
		sizeInput = new JTextField(2);
		sizeButton = new JButton("Confirm");
		sizeButton.addActionListener(this);
		
		//add label, textfield and button to frame
		JPanel panel = new JPanel();
		panel.add(label);
		panel.add(sizeInput);
		panel.add(sizeButton);
		frame.add(panel);
		frame.setVisible(true);
		
		//see actionPerformed on catching and checking input
	}
	
	//creates a JFrame that asks user for number of bombs on field
	public void askBombs() {
		
		//if the frame exists, get rid of it and create a new one
		if(frame != null) {
			frame.dispose();
		}
		frame  = new JFrame("MineSweeper");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(500,500);
		
		//ask user for input on number of bombs
		JLabel label = new JLabel("Please enter number of bombs between 1 and "+(size*size-1));
		bombInput = new JTextField(4);
		bombButton = new JButton("Confirm");
		bombButton.addActionListener(this);
		
		//add label, textfield and button to frame
		JPanel panel = new JPanel();
		panel.add(label);
		panel.add(bombInput);
		panel.add(bombButton);
		frame.add(panel);
		frame.setVisible(true);
		
		//see actionPerformed on catching and checking input
	}
	
	//logic for what to do when a certain button is prressed
	@Override
 	public void mouseClicked(MouseEvent e) {
		
		//if can reveal tiles or flag tiles
		if(!gameOver) {
			
			//loop through all buttons
			for(int i=0; i<size; i++) {
				for(int j=0; j<size; j++) {
					
					//check if e source is button in loop and if a left click
					if(e.getSource()==buttons[i][j] && SwingUtilities.isLeftMouseButton(e) && displayBoard.getTileValue(i, j) == '?') {
						
						//check if actualBoard is empty, to ensure that starting click is safe
						if(actualBoard.getTileValue(i, j)=='?') {
							//if is empty fill actualBoard with bombs that cannot be on current tile
							actualBoard = new Board(size,numBombs,j*size+i);
						}
						
						//reveal the tile
						reveal(i,j);
						
					//check if e source is button in loop and if a right click
					} else if(e.getSource()==buttons[i][j] && SwingUtilities.isRightMouseButton(e)){
						
						//if tile is flagged, unflag it
						if(displayBoard.getTileValue(i, j) == 'F') {
							displayBoard.setTileValue(i, j, '?');
							//decrement flagged
							flagged--;
							//update bombsLeft label
							bombsLeft.setText("Bombs left: "+(numBombs-flagged));
						//if tile is not flagged, flag it
						} else if(displayBoard.getTileValue(i,j) == '?') {
							displayBoard.setTileValue(i, j, 'F');
							//increment flagged
							flagged++;
							//update bombsLeft label
							bombsLeft.setText("Bombs left: "+(numBombs-flagged));
						}
					}
				}
			}
			//loop through all buttons and set them correct background and foreground color
			for(int i=0; i<size; i++) {
				for(int j=0; j<size; j++) {
					buttons[i][j].setText(""+displayBoard.getTileValue(i, j));
					recolor(i,j);
				}
			}
			//then check if all non bomb tiles have been revealed
			checkWin();
		//if cannot reveal tiles, then playAgain prompt has been activated
		} else if(e.getSource() == startOver && SwingUtilities.isLeftMouseButton(e)) {
			//restart game loop
			askSize();
		} else if(e.getSource() == endGame && SwingUtilities.isLeftMouseButton(e)) {
			//exit application
			frame.dispose();
		}
		
	}
	
	//reveal selected tiles, set displayValue to actualValue
	private void reveal(int column, int row) {
		//if tile is not flagged
		if(displayBoard.getTileValue(column, row) != 'F') {
			//check if tile is a bomb
			if(actualBoard.getTileValue(column, row) == '*') {
				//if tile is bomb
				//set display value to bomb
				displayBoard.setTileValue(column, row, '*');
				
				//set to no longer reveal mines
				gameOver = true;
				
				//reveal actualBoard to player
				revealBoard();
				
				//Game lost label
				JLabel label = new JLabel("You clicked a bomb, gameover. ");
				top.add(label);
				
				//prompt to play again
				playAgain();
			}
			else {
				//else reveal tile by setting its value to corresponding value in actualboard
				displayBoard.setTileValue(column, row, actualBoard.getTileValue(column, row));
			}
			
			//if revealed tile is 0, recursively reveal all surrounding tiles, if tile is not already revealed to 0 and is in bounds
			//since 0 cannot be next to a mine, this will never lead to accidentally losing
			if(displayBoard.getTileValue(column, row) == '0') {
				
				if(column != Board.getSize()-1 && displayBoard.getTileValue(column+1,row) != '0') {
					reveal(column+1, row);
				}
				if(column != 0 && displayBoard.getTileValue(column-1,row) != '0') {
					reveal(column-1, row);
				}
				if(row != Board.getSize()-1 && displayBoard.getTileValue(column,row+1) != '0') {
					reveal(column, row+1);
				}
				if(row != 0 && displayBoard.getTileValue(column,row-1) != '0') {
					reveal(column, row-1);
				}
				if(column != Board.getSize()-1 && row != 0 && displayBoard.getTileValue(column+1,row-1) != '0') {
					reveal(column+1, row-1);
				}
				if(column != Board.getSize()-1 && row != Board.getSize()-1 && displayBoard.getTileValue(column+1,row+1) != '0') {
					reveal(column+1, row+1);
				}
				if(column != 0 && row != Board.getSize()-1 && displayBoard.getTileValue(column-1,row+1) != '0') {
					reveal(column-1, row+1);
				}
				if(column !=0 && row !=0 && displayBoard.getTileValue(column-1,row-1) != '0') {
					reveal(column-1, row-1);
				}
			}
		}
		
	}//reveal
	
	//set button value
	private void recolor(int column, int row) {
		
		char value = displayBoard.getTileValue(column, row);
		
		//create grid pattern of light and dark for nonrevealed tile
		if(value=='?' || value=='F') {
			buttons[column][row].setBackground(new Color(0,100,0));
			if((column%2==1 && row%2==1) || (column%2!=1 && row%2!=1)) {
				buttons[column][row].setBackground(new Color(0,150,0));
			}
		//create grid pattern of light and dark for revealed tiles
		} else {
			buttons[column][row].setBackground(new Color(175,155,115));
			if((column%2==1 && row%2==1) || (column%2!=1 && row%2!=1)) {
				buttons[column][row].setBackground(new Color(215,190,140));
			}
		}
		
		//check value of tile and set button colors respectively
		switch(value) {
		case '0':
			buttons[column][row].setForeground(buttons[column][row].getBackground());
			break;
		case '1':
			buttons[column][row].setForeground(new Color(0,0,255));
			break;
		case '2':
			buttons[column][row].setForeground(new Color(25,110,10));
			break;
		case '3':
			buttons[column][row].setForeground(new Color(200,100,0));
			break;
		case '4':
			buttons[column][row].setForeground(new Color(200,0,200));
			break;
		case '5':
			buttons[column][row].setForeground(new Color(200,200,0));
			break;
		case '6':
			buttons[column][row].setForeground(new Color(0,200,200));
			break;
		case '7':
			buttons[column][row].setForeground(new Color(0,0,0));
			break;
		case '8':
			buttons[column][row].setForeground(new Color(100,100,100));
			break;
		case 'F':
			buttons[column][row].setForeground(new Color(255,0,0));
			break;
		case '*':
			buttons[column][row].setForeground(new Color(255,255,0));
			buttons[column][row].setBackground(new Color(255,0,0));
			break;
		default:
			buttons[column][row].setForeground(new Color(255,255,0));
			
		}
	}
	
	//set and check values of user input
	@Override
	public void actionPerformed(ActionEvent e) {
		
		//if the action comes from the size button
		if(e.getSource() == sizeButton) {
			
			//read the text from text field and store to size
			Scanner scanner = new Scanner(sizeInput.getText());
			if(scanner.hasNextInt()) {
				size=scanner.nextInt();
			}
			scanner.close();
			
			//check size is in bounds
			if(size<5 || size>20) {
				//if not in bounds, ask again
				askSize();
			} else {
				//if in bounds, prompt user number of bombs
				askBombs();
			}
			
		//else if the action comes from the bomb button
		} else if(e.getSource() == bombButton) {
			
			//read text from text field and store to bombs
			Scanner scanner = new Scanner(bombInput.getText());
			if(scanner.hasNextInt()) {
				numBombs = scanner.nextInt();
			}
			scanner.close();
			
			//check numBombs is in bounds
			if(numBombs<1 || numBombs>=size*size) {
				//if not in bounds ask again
				askBombs();
			} else {
				//if in bounds, we have all needed input so start game
				startGame();
			}
		}
	}

	//find differences between actualBoard and displayBoard and change displayBoard to correct value
	public void revealBoard() {
		for(int i=0; i<size; i++) {
			for(int j=0; j<size; j++) {
				if(actualBoard.getTileValue(i,j) == '*' && displayBoard.getTileValue(i,j) == '?') {
					displayBoard.setTileValue(i,j,'*');
				}
				else if(actualBoard.getTileValue(i, j) != '*' && displayBoard.getTileValue(i, j) == 'F') {
					displayBoard.setTileValue(i, j,'/');
				}
				recolor(i,j);
			}
		}
	}

	//check if number of revealed tiles has met threshold for winning
	public void checkWin() {
		
		//set values for checking
		final int TILES_FOR_WIN = (int)Math.pow(size,2)-numBombs;
		int numRevealedTiles = 0;
		
		//loop through display board, incrementing revelaedtile when a revealed tile is found
		for(int i=0; i<size; i++) {
			for(int j=0; j<size; j++) {
				char tileValue = displayBoard.getTileValue(i, j);
				if(tileValue != '?' && tileValue != 'F' && tileValue != '*') {
					numRevealedTiles++;
				}
			}
		}
		
		if(numRevealedTiles >= TILES_FOR_WIN) {
			//set cannot reveal more tiles
			gameOver = true;
			
			//reveal actual board
			revealBoard();
			
			//give game win message
			JLabel label = new JLabel("You win, Yay");
			top.add(label);
			
			//prompt to play again
			playAgain();
		}
		
	}
	
	//ask user if they want to play again
	public void playAgain() {
		//create label promptinf user to play again
		JLabel label = new JLabel("Would you like to play again?");
		
		//create buttons for yes or no
		startOver = new JButton("Yes");
		endGame = new JButton("No");
		//add listeners to buttons
		//see mouseClicked for logic on press
		startOver.addMouseListener(this);
		endGame.addMouseListener(this);
		
		//add label and buttons to top panel
		top.add(label);
		top.add(startOver);
		top.add(endGame);
	}
	
	//Don't use
	@Override
	public void mousePressed(MouseEvent e) {
	}
	
	//Don't use
	@Override
	public void mouseReleased(MouseEvent e) {
	}

	//Don't use
	@Override
	public void mouseEntered(MouseEvent e) {
	}
	
	//Don't use
	@Override
	public void mouseExited(MouseEvent e) {
	}
}

	
