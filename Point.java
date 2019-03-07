
public class Point {
	/* Class of the coordinates of the grid*/
	int i;
	int j;
	public  Point(int i, int j) {
		this.i = i;
		this.j = j;
	}
	public Point() {}
	@Override
	public boolean equals(Object thing) {
		if (! (thing instanceof Point) )return false;
		else {
		Point thing1 = (Point) thing;
		return((this.i == thing1.i) && (this.j==thing1.j));
		
		}
	}
	
	public Point sum(Point q) {
		return(new Point(i+q.i, j+q.j));
	}
}
