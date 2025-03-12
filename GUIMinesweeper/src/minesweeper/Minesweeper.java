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
	
	private JButton buttons[][];
	private JButton startOver;
	private JButton endGame;
	private boolean gameOver = false;
	private int size;
	private int numBombs;
	private int flagged;
	private JLabel bombsLeft;
	private Board actualBoard;
	private Board displayBoard;
	private JFrame frame;
	private JPanel field;
	private JPanel top;
	private JTextField sizeInput;
	private JButton sizeButton;
	private JTextField bombInput;
	private JButton bombButton;

	Minesweeper() {
		
		askSize();

	}
		
	public void startGame() {
		
		if(frame != null) {
			frame.dispose();
		}
		
		frame  = new JFrame("MineSweeper");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		gameOver = false;
		flagged = 0;
		
		Dimension frameSize = new Dimension(size*65,size*65+75);
		
		frame.setMinimumSize(frameSize);
		frame.setLayout(null);
		
		top = new JPanel(new FlowLayout());
		top.setBackground(new Color(25,90,10));
		bombsLeft = new JLabel("Bombs left: "+(numBombs-flagged));
		bombsLeft.setFont(new Font("Arial", Font.BOLD, 20));
		top.add(bombsLeft);
		field = new JPanel(new GridLayout(size,size,0,0));
		
		buttons = new JButton[size][size];
		
		displayBoard = new Board(size);
		actualBoard = new Board(size);
		
		for(int i=0; i<size; i++) {
			for(int j=0; j<size; j++) {
				JButton button = new JButton(""+displayBoard.getTileValue(i, j));
				button.setBounds(i*25,j*25,25,25);
				button.setOpaque(true);
				button.setFont(new Font("Arial",Font.BOLD,20));
				field.add(button);
				buttons[i][j] = button;
				field.add(button);
				button.addMouseListener(this);
				recolor(i,j);
			}
		}
		
		field.setBounds(0,100,frame.getWidth(),frame.getHeight()-100);
		top.setBounds(0,0,frame.getWidth(),100);
		frame.add(field);
		frame.add(top);
		frameSize = new Dimension(field.getWidth(),field.getHeight()+140);
		frame.setSize(frameSize);
		frame.setVisible(true);
	}
	
	public void askSize() {
		if(frame != null) {
			frame.dispose();
		}
		
		frame  = new JFrame("MineSweeper");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.setSize(500,500);
		
		JLabel label = new JLabel("Please enter size of board between 5 and 20: ");
		sizeInput = new JTextField(2);
		sizeButton = new JButton("Confirm");
		
		sizeButton.addActionListener(this);
		
		JPanel panel = new JPanel();
		
		panel.add(label);
		panel.add(sizeInput);
		panel.add(sizeButton);
		frame.add(panel);
		frame.setVisible(true);
	}
	
	public void askBombs() {
		if(frame != null) {
			frame.dispose();
		}
		
		frame  = new JFrame("MineSweeper");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.setSize(500,500);
		
		JLabel label = new JLabel("Please enter number of bombs between 1 and "+(size*size-1));
		bombInput = new JTextField(4);
		bombButton = new JButton("Confirm");
		
		bombButton.addActionListener(this);
		
		JPanel panel = new JPanel();
		
		panel.add(label);
		panel.add(bombInput);
		panel.add(bombButton);
		frame.add(panel);
		frame.setVisible(true);
	}

	@Override
 	public void mouseClicked(MouseEvent e) {
		if(!gameOver) {
			for(int i=0; i<size; i++) {
				for(int j=0; j<size; j++) {
					if(e.getSource()==buttons[i][j] && SwingUtilities.isLeftMouseButton(e) && displayBoard.getTileValue(i, j) == '?') {
						if(actualBoard.getTileValue(i, j)=='?') {
							actualBoard = new Board(size,numBombs,j*size+i);
						}
						reveal(i,j);
					} else if(e.getSource()==buttons[i][j] && SwingUtilities.isRightMouseButton(e)){
						if(displayBoard.getTileValue(i, j) == 'F') {
							displayBoard.setTileValue(i, j, '?');
							flagged--;
							bombsLeft.setText("Bombs left: "+(numBombs-flagged));
						} else if(displayBoard.getTileValue(i,j) == '?') {
							displayBoard.setTileValue(i, j, 'F');
							flagged++;
							bombsLeft.setText("Bombs left: "+(numBombs-flagged));
						}
					}
				}
			}
			
			for(int i=0; i<size; i++) {
				for(int j=0; j<size; j++) {
					buttons[i][j].setText(""+displayBoard.getTileValue(i, j));
					recolor(i,j);
				}
			}
			checkWin();
		} else if(e.getSource() == startOver && SwingUtilities.isLeftMouseButton(e)) {
			askSize();
		} else if(e.getSource() == endGame && SwingUtilities.isLeftMouseButton(e)) {
			frame.dispose();
		}
		
	}

	private void reveal(int column, int row) {
		if(displayBoard.getTileValue(column, row) != 'F') {
			//check if tile is a bomb
			if(actualBoard.getTileValue(column, row) == '*') {
				displayBoard.setTileValue(column, row, '*');
				gameOver = true;
				revealBoard();
				JLabel label = new JLabel("You clicked a bomb, gameover. ");
				top.add(label);
				playAgain();
			}
			else {
				//else reveal tile by setting its value to corresponding value in actualboard
				displayBoard.setTileValue(column, row, actualBoard.getTileValue(column, row));
			}
			
			//if revealed tile is 0, reveal all surrounding tiles, if tile is not already revealed to 0 and is in bounds
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

	private void recolor(int column, int row) {
		
		char value = displayBoard.getTileValue(column, row);
		
		if(value=='?' || value=='F') {
			buttons[column][row].setBackground(new Color(0,100,0));
			if((column%2==1 && row%2==1) || (column%2!=1 && row%2!=1)) {
				buttons[column][row].setBackground(new Color(0,150,0));
			}
		} else {
			buttons[column][row].setBackground(new Color(175,155,115));
			if((column%2==1 && row%2==1) || (column%2!=1 && row%2!=1)) {
				buttons[column][row].setBackground(new Color(215,190,140));
			}
		}
		
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

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}
	

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == sizeButton) {
			Scanner scanner = new Scanner(sizeInput.getText());
			if(scanner.hasNextInt()) {
				size=scanner.nextInt();
			}
			scanner.close();
			if(size<5 || size>20) {
				askSize();
			} else {
				askBombs();
			}
		} else if(e.getSource() == bombButton) {
			Scanner scanner = new Scanner(bombInput.getText());
			if(scanner.hasNextInt()) {
				numBombs = scanner.nextInt();
			}
			scanner.close();
			if(numBombs<1 || numBombs>=size*size) {
				askBombs();
			} else {
				startGame();
			}
		}
	}

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

	public void checkWin() {
		
		final int TILES_FOR_WIN = (int)Math.pow(size,2)-numBombs;
		int numRevealedTiles = 0;
		
		for(int i=0; i<size; i++) {
			for(int j=0; j<size; j++) {
				char tileValue = displayBoard.getTileValue(i, j);
				if(tileValue != '?' && tileValue != 'F' && tileValue != '*') {
					numRevealedTiles++;
				}
			}
		}
		
		if(numRevealedTiles >= TILES_FOR_WIN) {
			gameOver = true;
			revealBoard();
			JLabel label = new JLabel("You win, Yay");
			top.add(label);
			playAgain();
		}
		
	}
	
	public void playAgain() {
		JLabel label = new JLabel("Would you like to play again?");
		startOver = new JButton("Yes");
		endGame = new JButton("No");
		startOver.addMouseListener(this);
		endGame.addMouseListener(this);
		top.add(label);
		top.add(startOver);
		top.add(endGame);
	}
}

	
