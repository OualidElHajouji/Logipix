import java.awt.Color;

public class Couple {
	
	int clue;
	Color color;  /* WHITE = not-colored, LIGHT_GRAY = maybe-colored, BLACK = colored */
	
	public  Couple(int clue, Color color) {
		this.clue = clue;
		this.color = color;
	}
	
	public Couple(int clue) { /*constructor to automatically select the first value of the color given the clue */
		this.clue = clue;
		if (clue >0) {  
			this.color = Color.BLACK; /* the cells with clues are eventually colored */
		}
		else {
			this.color = Color.WHITE;
		}
	}
}
