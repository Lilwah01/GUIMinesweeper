package minesweeper;
//supports Board class
public class Tile {
	
	private char value;
		
	public Tile(char value) {
		this.value = value;
	}
		
	public char getValue() {
		return value;
	}
	
	public void setValue(char value){
		this.value = value;
	}
}
