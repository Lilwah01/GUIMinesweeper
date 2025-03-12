package minesweeper;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class MyFrame implements MouseListener{
	
	private JButton buttons[][];
	private int size;
	private int numBombs;
	private int flagged;
	private Board actualBoard;
	private Board displayBoard;
	private JFrame frame;

		MyFrame() {
		
		frame = new JFrame("MineSweeper");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel fieldPanel = new JPanel();
		size = 20;
		numBombs = 50;
		fieldPanel.setLayout(new GridLayout(size,size,0,0));
		buttons = new JButton[size][size];
		
		displayBoard = new Board(size);
		actualBoard = new Board(size);
		
		for(int i=0; i<size; i++) {
			for(int j=0; j<size; j++) {
				JButton button = new JButton(""+displayBoard.getTileValue(i, j));
				button.setBounds(i*25,j*25,25,25);
				button.setOpaque(true);
				button.setFont(new Font("Arial",Font.BOLD,20));
				fieldPanel.add(button);
				buttons[i][j] = button;
				fieldPanel.add(button);
				button.addMouseListener(this);
				recolor(i,j);
			}
		}
		frame.add(fieldPanel);
		frame.pack();
		Dimension frameSize = frame.getSize();
		frameSize.setSize(frameSize.getWidth()*1.1,frameSize.getHeight()*1.1);
		frame.setSize(frameSize);
		frame.setVisible(true);

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		
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
					} else if(displayBoard.getTileValue(i,j) == '?') {
						displayBoard.setTileValue(i, j, 'F');
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
	}

	private boolean reveal(int column, int row) {
		
		boolean successful = true;
		//check if tile is a bomb
		if(actualBoard.getTileValue(column, row) == '*') {
			displayBoard.setTileValue(column, row, '*');
			successful = false;
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
		
		return successful;
	}//reveal

	private void recolor(int column, int row) {
		
		char value = displayBoard.getTileValue(column, row);
		
		if(value=='?') {
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
			
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}
