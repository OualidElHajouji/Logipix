
import javax.swing.JFrame;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.LinkedList;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class PuzzleFrame extends JFrame {
	/* Creation of the frame of the puzzle */
	public PuzzleFrame(int a, int n, int m, Couple[][] grid, LinkedList<LinkedList<Point>> paths) {
		PuzzlePanel panel = new PuzzlePanel(a, n, m, grid, paths);
		this.setSize(a * (m + 2), a * (n + 3)); /* we want a window that is bigger than the grid */
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		this.setContentPane(panel);
		this.setVisible(true);
	}
}

@SuppressWarnings("serial")
class PuzzlePanel extends JPanel {

	private final int a, n, m;   // a is the length of a cell
	private Couple[][] grid;
	private LinkedList<LinkedList<Point>> paths;

	PuzzlePanel(int a, int n, int m, Couple[][] grid, LinkedList<LinkedList<Point>> paths) {
		this.a = a;
		this.n = n;
		this.m = m;
		this.grid = grid;
		this.paths = paths;
	}
	
	public void drawLine(Point p1, Point p2, Graphics2D g) {
		/* Draws a line between two successive cells in a path */
		int i1 = p1.i;
		int j1 = p1.j;
		int i2 = p2.i;
		int j2 = p2.j;
		g.drawLine((j1+1)*a+a/2,(i1+1)*a+a/2,(j2+1)*a+a/2, (i2+1)*a+a/2);
	}
	@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g; /* cast in order to use Graphics2d */
			this.setBackground(Color.WHITE);
			Font font = new Font("Courrier", Font.BOLD, a/2); 
			g2d.setFont(font);
			g2d.setStroke(new BasicStroke(a/20));
			for(int i = 0 ; i<n;i++) {   /* drawing of an empty colored grid */
				for(int j = 0; j<m;j++) {
					g2d.setColor(grid[i][j].color);
					g2d.fillRect((j+1)*a, (i+1)*a, a, a);
					g2d.setColor(Color.BLACK);
					g2d.drawRect((j+1)*a, (i+1)*a, a, a);
				}
			}
			g2d.setColor(Color.CYAN); /* color used for the paths */
			g2d.setStroke(new BasicStroke(a/10));
			for(LinkedList<Point> l : paths) {  /* drawing of the paths */
				if (l.size()==1) {  /* in case, the path's size is 1, we represent it with a circle */
					int i =l.getFirst().i;
					int j =l.getFirst().j;
					g2d.fillOval((j+1)*a+7*a/20, (i+1)*a+7*a/20, a/3, a/3); /* the odd proportions are due to the centering of the oval */
					}
				else {
					for(int k = 0; k<l.size()-1;k++) { 
						Point p1 = l.get(k);
						Point p2 = l.get(k+1);
						this.drawLine(p1,p2,g2d);
					}
				}
			}
			g2d.setColor(Color.WHITE);
			g2d.setStroke(new BasicStroke(a/20));
			for(int i = 0 ; i<n;i++) {  /* drawing of the clues */
				for(int j = 0; j<m;j++) {
					
					if(grid[i][j].clue != 0) {  /* if there is a 0, it means there is no clue */
						if (grid[i][j].clue < 10) { /*we have to adapt the position of the number depending on the number of digits */
							g2d.drawString(String.valueOf(grid[i][j].clue), (j+1)*a+(2*a/5), (i+1)*a+(7*a/10)); /*adjustments to center the clue */
						}
						else
							g2d.drawString(String.valueOf(grid[i][j].clue), (j+1)*a+(a/4), (i+1)*a+(7*a/10)); 
				
					}
				}
			}
			
		}
}