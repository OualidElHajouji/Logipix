import java.util.*;
import java.awt.Color;
import java.io.*;


public class Logipix {
	
	public int n; /* height */
	public int m; /* width */
	
	public Couple[][] grid; /* grid that contains the couple (clue,color) */
	public LinkedList<LinkedList<Point>> paths; /* list of the paths between the clues */
	public static int referenceClue = 5;
	
	public Logipix(int n, int m, Couple[][] grid) {
		this.n=n;
		this.m=m;
		this.grid=grid;
		this.paths = new LinkedList<>();
	}
	
	public Logipix(int n, int m, Couple[][] grid, LinkedList<LinkedList<Point>> paths ) {
		this.n=n;
		this.m=m;
		this.grid=grid;
		this.paths = paths;
	}
	
	public Logipix() {}
	
	public void initialize(String filename) throws FileNotFoundException {
		/*Task 1 */
		File file = new File(filename);
		Scanner sc = new Scanner(file);
		this.m=sc.nextInt();
		this.n = sc.nextInt();
		this.grid = new Couple[n][m];
		this.paths = new LinkedList<>();
		for (int i = 0;i<n;i++) {
			for(int j =0; j<m;j++) {
				int clue = sc.nextInt();
				this.grid[i][j]=new Couple(clue); /* the colors are automatically chosen */
				if (clue == 1) {
					LinkedList<Point> ls = new LinkedList<>();
					ls.add(new Point(i,j));						/*we know that the clues equal to 1 correspond to a path with only one point */
					this.paths.add(ls);
				} 
			}
		}
		sc.close();
	}
	
	public void display() {
		/* Task 2 */
		final int a = 60; /* side of a square in the grid : not going to be modified in the program */
		@SuppressWarnings("unused")
		PuzzleFrame frame = new PuzzleFrame(a,n,m,grid,paths); /*The constructor displays the grid directly */ 
	}
	
	
	@SuppressWarnings({ "unchecked" })
	public LinkedList<Point> [] getPoints(){
		/* returns a tab of list of points such as, at the index l, there is the list of points that contain the clue l. This program also finds the maximal clue max, and puts a list equal to {new Point(-1,0)} at index max + 1  */
		LinkedList<Point> [] PointsOfClues = (LinkedList<Point> []) new LinkedList[n*m]; /* we know that a clue cannot be superior to n*m */
		int max = 0;
		for (int k = 0; k<n*m;k++) {
			PointsOfClues[k]= new LinkedList<Point>();      
		}
		for(int i = 0; i<n; i++) {
			for (int j = 0 ; j<m; j++) {
				if (grid[i][j].clue> max) max = grid[i][j].clue;
				PointsOfClues[grid[i][j].clue].add(new Point(i,j));
			}
		}
		LinkedList<Point> lastList = new LinkedList<>();
		lastList.add(new Point(-1,0));
		PointsOfClues[max+1] = lastList;
		int size;
		for(int k = 2; k<= max; k++) { /* we change the order of one list out of two so that the points are not too far from each other when we consider them in solvev2 */
			if (k%2 == 0) {
				size = PointsOfClues[k].size();
				for (int a = 1; a< size; a++) PointsOfClues[k].addFirst(PointsOfClues[k].remove(a));
			}
		}
		return(PointsOfClues);
	}
	
	public boolean isReachable(Point p1, Point p2, int d){ /* returns true if a path of length d can be theoretically drawn (if we're not sure it cannot be) between p1 and p2 and if p1 is not equal to p2*/ 
		int x0= p1.j ;
		int y0= p1.i ;
		int x= p2.j;
		int y= p2.i;
		int r = Math.abs(x-x0) + Math.abs(y-y0);
		return((r>0)&(r <= d-1)&& ((d-1 -r)%2 == 0) ); /* d-1 -r has to be pair in order for a path to be possible between the two points */
	}	
	
	public boolean inGrid(Point p) { /* says whether a point is in the grid */
		return ((p.i>=0)&&(p.i<n)&&(p.j>=0)&&(p.j<m)); 
	}
	
	public Couple get(Point p) {
		return grid[p.i][p.j];
	}
	
	public boolean[][] initializeFlags(){
		/* initializes the tab of flags, used at the very beginning of the solver : the meaning is explained in the line below */
		boolean[][] flags = new boolean[n][m]; /* the cells corresponding to the ones with no clue or with a clue equal to 1 have a false value : whenever a cell with a non-equal to 1 clue gets linked to another cell, we will set the value to false. */
		for (int k = 0; k<n;k++) {
			for (int l = 0; l<m; l++) {
				if (grid[k][l].clue > 1) {
					flags[k][l] = true;
				}
			}
		}
		return(flags);
		
	}
	public LinkedList<LinkedList<Point>> auxGeneratePaths(Point p1, Point p2, int d, boolean[][] flags){
		/* generates the list of paths of length d between p1 and p2 in a recursive way. The list can be empty */
		LinkedList<LinkedList<Point>> res = new LinkedList<LinkedList<Point>>();
		if (d==1) { /* base case */
			if (p1.equals(p2)) {
				LinkedList<Point> l1 = new LinkedList<Point>();
				l1.add(p1);
				res.add(l1);
				return(res);
			}
			else {
				return(res);   /* if d=1 and they're not equal, there is no path between the 2 points */
			}
		}
		else {
			int i1 = p1.i;
			int j1 = p1.j;
			Point pUp = new Point(i1-1,j1); 
			Point pDown = new Point(i1+1,j1);
			Point pLeft = new Point(i1,j1-1);
			Point pRight = new Point(i1,j1+1);
			Point [] tabOfPoints = {pUp, pDown, pLeft, pRight};
			for (Point p : tabOfPoints) { 
				/* a path from p1 to p2 is a path from one of the points of tabOfPoints (if eligible) to p2, to which we add p1 */
			
				if (((this.inGrid(p))&&(this.get(p).color.equals(Color.WHITE))&& (isReachable(p,p2, d-1)))||( (p.equals(p2))&& (d==2)&&(flags[p.i][p.j]))) {
					/* the point p is eligible for a path if it's part of the grid, not part of another path, if it can reach p2 or if it's the last point before p2, p2 being free */
					this.get(p).color = Color.LIGHT_GRAY; /* in order to have paths that go only once through p */
					LinkedList<LinkedList<Point>> res1 = auxGeneratePaths(p,p2,d-1, flags);
					for (LinkedList<Point> l1 : res1) {
						/* we add p1 to all the paths found */
						LinkedList<Point> l = l1;
						l.addFirst(p1); 
						res.add(l);
					}
					if (p.equals(p2)) this.get(p).color = Color.BLACK;  /* we have to set back the color to WHITE if the cell doesn't correspond to the arrival, otherwise we make it BLACK again */
					else this.get(p).color = Color.WHITE;
				}
				
			}
			
			return(res);
			}
	}
	public LinkedList<LinkedList<Point>> generateBrokenLines(Point p, LinkedList<Point> [] PointsOfClues, boolean[][] flags){
		/* Task 3 : we use the auxiliary function auxGeneratePaths between p and each reachable point with same clue as p */
		int clue = grid[p.i][p.j].clue;
		LinkedList<LinkedList<Point>> res = new LinkedList<LinkedList<Point>>();
		if (clue ==1) { /* in this case, the list to return is obvious. */
			LinkedList<Point> l = new LinkedList<Point>();
			l.add(p);
			res.add(l);
			return(res);
		}
		
		else {
		LinkedList<Point> auxpoints = PointsOfClues[clue]; /* all points that have the same clue as p */
		
		LinkedList<Point> points = new LinkedList<Point>();  /* all points that have the same clue as p and reachable from p */
		for(Point q: auxpoints) {
			if(isReachable(p,q,clue))	{
				points.add(q);
			}
		}
		for (Point q : points) {
			res.addAll(auxGeneratePaths(p,q,clue, flags));
		}
		return(res);
	}
		
	
	}
	
    public Point nextPointv1(Point p, boolean[][] flags)  {
    	/* gives the point after p that can be considered, given the tab flags that indicates which cells are free : we go through the cells in the order of the grid */ 
    	int i = p.i;
    	int j = p.j;
    	while(i<n) {
    		while(j<m) {
    			if (flags[i][j]) return (new Point(i,j));
    			j++;
    		}
    		j=0;
    		i++;
    	}
    	return(new Point(-1,0)); /* if there is no more free cell, we return a point that is not in the grid, which will indicate the grid has been solved */
    }	
    

    
    public PointAndIndex nextPointv2(Point p, boolean[][] flags, LinkedList<Point>[] PointsOfClues, int index) {
    	/* choice in increasing order of clues : we need to save the index of the point we consider in the list of points with the same clue */
    	int clue = this.get(p).clue;
    	
    	LinkedList<Point> l = PointsOfClues[clue];
    	int size = l.size();
    	Point nextPoint = p;
    	while(size != 1) { /* if we find a list of points of size 1, it means we have already examined the maximal clue */
    		while (index < size) {
    			nextPoint = l.get(index);
    			if (flags[nextPoint.i][nextPoint.j]) return new PointAndIndex(nextPoint,index);
    			index++;
    		}
    		clue++;
    		l = PointsOfClues[clue];
    		size = l.size();
    		index = 0;
    	}
    	return(new PointAndIndex(new Point(-1,0),0));
    }
    
    public PointAndIndex nextPointv3(Point p, boolean[][] flags, LinkedList<Point>[] PointsOfClues, int index) {
    	/* we start with the clues that are inferior to the "reference" (a global variable) in an increasing order, then we go through the clues in the order of the grid */
    	int clue = this.get(p).clue;
    	PointAndIndex nextPoint;
    	if (clue <= referenceClue) {
    		nextPoint = nextPointv2(p, flags, PointsOfClues, index);
    		if ((nextPoint.p.i == -1)||(this.get(nextPoint.p).clue<= referenceClue)) return nextPoint;
    		else return new PointAndIndex(nextPointv1(new Point(0,0), flags), -1);
    	}
    	else {
    		return new PointAndIndex(nextPointv1(p, flags), -1);
    	}

    }
    
    public LinkedList<LinkedList<Point>> auxGeneratePathsCombination(Point pointOfBeginning, Point p1, Point p2, int d, boolean[][] flags, Point[][] clueRelatedTo, boolean[][] secondFlags){
		/* generates the list of paths of length d between p1 and p2 in a recursive way. The list can be empty. This program takes into account the combination (black cells) */
		LinkedList<LinkedList<Point>> res = new LinkedList<LinkedList<Point>>();
		
		if (d==1) { /* base case */
			if (p1.equals(p2)) {
				LinkedList<Point> l1 = new LinkedList<Point>();
				l1.add(p1);
				res.add(l1);
				return(res);
			}
			else {
				return(res);   /* if d=1 and they're not equal, there is no path between the 2 points */
			}
		}
		else {
			
			int i1 = p1.i;
			int j1 = p1.j;
			Point pUp = new Point(i1-1,j1); 
			Point pDown = new Point(i1+1,j1);
			Point pLeft = new Point(i1,j1-1);
			Point pRight = new Point(i1,j1+1);
			Point [] tabOfPoints = {pUp, pDown, pLeft, pRight};
			
			for (Point p : tabOfPoints) { 
				/* a path from p1 to p2 is a path from one of the points of tabOfPoints (if eligible) to p2, to which we add p1 */
				
				if ((this.inGrid(p)&&! this.get(p).color.equals(Color.LIGHT_GRAY)&&(this.get(p).color.equals(Color.WHITE)|| (clueRelatedTo[p.i][p.j].equals(p2) && ! p.equals(p2)&&! p.equals(pointOfBeginning))||(clueRelatedTo[p.i][p.j].equals(pointOfBeginning))&& ! p.equals(pointOfBeginning)))&& (isReachable(p,p2, d-1))||((p.equals(p2))&& (d==2)&&(flags[p.i][p.j]))) { 
				/* that is an extremely complicated boolean, but it works */
					
					
					this.get(p).color = Color.LIGHT_GRAY; /* in order to have paths that go only once through p */
					LinkedList<LinkedList<Point>> res1 = auxGeneratePathsCombination(pointOfBeginning, p,p2,d-1, flags, clueRelatedTo, secondFlags);
					for (LinkedList<Point> l1 : res1) {
						/* we add p1 to all the paths found */
						LinkedList<Point> l = l1;
						l.addFirst(p1); 
						res.add(l);
					}
					if (p.equals(p2)||clueRelatedTo[p.i][p.j].equals(p2)||clueRelatedTo[p.i][p.j].equals(pointOfBeginning)) this.get(p).color = Color.BLACK;  /* we have to set back the color to WHITE if the cell doesn't correspond to the arrival, otherwise we make it BLACK again */
					else this.get(p).color = Color.WHITE;
				}
				
			}
			
			return(res);
			}
	}
	public LinkedList<LinkedList<Point>> generateBrokenLinesCombination(Point p, LinkedList<Point> [] PointsOfClues, boolean[][] flags, Point[][] clueRelatedTo){
		/* We use the auxiliary function auxGeneratePathsCombination between p and each reachable point with same clue as p */
		int clue = grid[p.i][p.j].clue;
		boolean[][] secondFlags = new boolean[n][m];
		LinkedList<LinkedList<Point>> res = new LinkedList<LinkedList<Point>>();
		if (clue ==1) { /* in this case, the list to return is obvious. */
			LinkedList<Point> l = new LinkedList<Point>();
			l.add(p);
			res.add(l);
			return(res);
		}
		
		else {
		LinkedList<Point> auxpoints = PointsOfClues[clue]; /* all points that have the same clue as p */
		
		LinkedList<Point> points = new LinkedList<Point>();  /* all points that have the same clue as p and reachable from p */
		for(Point q: auxpoints) {
			if(isReachable(p,q,clue))	{
				points.add(q);
			}
		}
		for (Point q : points) {
			res.addAll(auxGeneratePathsCombination(p,p,q,clue, flags, clueRelatedTo, secondFlags));
		}
		return(res);
	}
		
	
	}
    public  Point nextPointCombination(Point p) {
    	/* gives the next point we consider in combination algorithm, without considerations about flags */
    	int i = p.i;
    	int j = p.j;
    	if (j==m-1) {
    		i++;
    		j=0;
    	}
    	else {
    		j++;
    	}
    	while(i<n) {
    		while(j<m) {
    			if (this.grid[i][j].clue>1) return (new Point(i,j));
    			j++;
    		}
    		j=0;
    		i++;
    	}
    	return(new Point(-1,0));
    }
    
    public LinkedList<Point> findIntersectionTwo(LinkedList<Point> list1, LinkedList<Point> list2){
    	/* finds the intersection between two lists of points */
    	LinkedList<Point> res = new LinkedList<Point>();
    	for (Point p : list1) {
    		if (list2.contains(p)) res.add(p);
    	}
    	return res;
    }
    public LinkedList<Point> findIntersection(LinkedList<LinkedList<Point>> listOflists){
    	/* recursive algorithm that returns the intersection of a given number of lists thanks to the algorithm above */
    	int size = listOflists.size();
    	if (size <= 2) {
    		return findIntersectionTwo(listOflists.getFirst(), listOflists.getLast()); /* this works in case the size is one or two */
    	}
    	else {
    		LinkedList<Point> A = listOflists.removeFirst();
    		LinkedList<Point> B = listOflists.removeFirst();
    		return findIntersectionTwo(findIntersectionTwo(A,B),findIntersection(listOflists));
    	}
    	
    }
    public void combination(boolean [][] flags, LinkedList<Point>[] PointsOfClues, Point[][] clueRelatedTo) {
    	/* Task 6 */
    	Point p = new Point(0,0);
    	if (this.get(p).clue <= 1) p = nextPointCombination(p);
    	
    	LinkedList<Point> intersection;
    	while(p.i != -1) {
    		LinkedList<LinkedList<Point>> listOflists = generateBrokenLinesCombination(p, PointsOfClues, flags, clueRelatedTo);
    	
    		intersection = findIntersection(listOflists);
    		for (Point q : intersection) {
    			this.get(q).color = Color.BLACK;
    			clueRelatedTo[q.i][q.j] = p; /* we have to indicate that q is in a path that is related to the cell p */
    		}
    		
    		p = nextPointCombination(p);
    	}
    	
    }
   
	
    
    
    
    
    															/* SOLVERS */
    
    
    
    
    
    
    
    public void solvev1() {
		/* Task 4 : using nextPointv1 */
    	LinkedList<Point> [] PointsOfClues = this.getPoints();
		boolean[][] flags = initializeFlags();
		
		
		Point p = nextPointv1(new Point(0,0), flags); /* if (0,0) is the first point we consider, it is taken into account by nextPoint */
		LinkedList<Point> pointsToConsider = new LinkedList<>(); /* this list will enable us to backtrack */
		LinkedList<LinkedList<Point>> brokenLines = this.generateBrokenLines(p,PointsOfClues, flags);
		LinkedList<LinkedList<LinkedList<Point>>> listOfBrokenLines = new LinkedList<>(); /* this list is associated to pointsToConsider : at index i, there is the list of BrokenLines associated to the point pointsToConsider.get(i) */
		int rank = 0; /* the rank of the currentPath in the list of Broken Lines associated to point p */
		LinkedList<Integer> listOfRanks = new LinkedList<>(); /* same as listOfBrokenLines but with the ranks */
		LinkedList<Point> currentPath; /* indicates the path we're currently considered */
		
		LinkedList<LinkedList<Point>> nextBrokenLines = new LinkedList<LinkedList<Point>>();
		Point lastPoint = new Point();
		Point nextPoint = new Point();
		LinkedList<Point> previousPath; 
		boolean bool = false; /* the meaning of this boolean is explained below */
		
		
		while(p.i != -1) {
			int nbOfPaths = brokenLines.size();
			
			if (rank == nbOfPaths) {
				/* there is no more path to consider for p, we have to go back to the previous point */
				currentPath = this.paths.removeLast();  /* there is still a path corresponding to p that has to be deleted */
				lastPoint = currentPath.getLast(); 
				for(Point pPath : currentPath) {
	    			if ( (! pPath.equals(p))&&(!pPath.equals(lastPoint)))
	    				this.get(pPath).color = Color.WHITE;        /* we delete the path, except the ends of it because we know we will consider an other path */
	    		}	
				bool = true;
				flags[p.i][p.j] = true;
	    		flags[lastPoint.i][lastPoint.j] = true;  /* the cells joined by the path are free */
	    		p = pointsToConsider.removeLast(); 
				rank = listOfRanks.removeLast() + 1;
				brokenLines = listOfBrokenLines.removeLast();
	    	}
			else {
				if (rank > 0 && bool) { /* bool is true when the previous point is not the same as the one considered now */
					previousPath = this.paths.removeLast();
	    			lastPoint = previousPath.getLast();
	    			flags[lastPoint.i][lastPoint.j] = true;
	    			for(Point pPath : previousPath) {
	        			if ( (! pPath.equals(p))&&(!pPath.equals(lastPoint)))   this.get(pPath).color = Color.WHITE;        /* we delete the previous path */
	        		}
	    		}
				currentPath = brokenLines.get(rank);
				lastPoint = currentPath.getLast(); 
	    		flags[p.i][p.j] = false;
	    		flags[lastPoint.i][lastPoint.j] = false;  /* we have to indicate that the points are part of a path */
	    		nextPoint = nextPointv1(p, flags);
	    	
	    		for(Point pPath : currentPath) {
	    			if ( (! pPath.equals(p))&&(!pPath.equals(lastPoint)))   this.get(pPath).color = Color.LIGHT_GRAY;        /* we temporarily color the cells of the path */
	    		}
	    		
	    		if (nextPoint.i != -1)  nextBrokenLines = this.generateBrokenLines(nextPoint,PointsOfClues, flags); 
				
	    		
	    		if ((nextPoint.i != -1) && (nextBrokenLines.size() == 0)) {
	    			/* if we can't keep on the track */
	    			
	    			for(Point pPath : currentPath) {
		    			if ( (! pPath.equals(p))&&(!pPath.equals(lastPoint)))
		    				this.get(pPath).color = Color.WHITE;        /* we delete the path */
		    		}			
	    			if (rank == (nbOfPaths -1)) {
	    				bool = true; /* see above why we set this value to true */
	    				flags[p.i][p.j] = true;
	    	    		flags[lastPoint.i][lastPoint.j] = true; /* the cells of the path are now free */
	    				p = pointsToConsider.removeLast();
	    				rank= listOfRanks.removeLast() + 1;
	    				brokenLines = listOfBrokenLines.removeLast();  
	    			}
	    			else { /* we try the next path corresponding to the same point */
	    				bool = false;
	    				flags[lastPoint.i][lastPoint.j] = true;
	    				rank+=1;
	    			}
	    		
	    		}
	    		else {  /* if we can keep on the track or if it is the end of it (nextPoint.i = -1) */
	    			this.paths.add(currentPath);
	        		pointsToConsider.add(p);
		        	listOfBrokenLines.add(brokenLines);
		        	listOfRanks.add(rank);
	        	
	        		rank = 0;
	        		brokenLines = nextBrokenLines;
	        		p = nextPoint;  /* we go to the next point */
	    			}
				}
		
			}
		/* at the end, there is no more maybe-colored */
		for(LinkedList<Point> path : paths) {
			for(Point q : path) {
				this.get(q).color = Color.BLACK;
			}
		}
		return;
	}
 
    public void solvev2() {
    	/* Task 5 : using nextPointv2*/
    	LinkedList<Point> [] PointsOfClues = this.getPoints();
		boolean[][] flags = initializeFlags();
		
		int index = 0;
		int clue = 2;
		while(PointsOfClues[clue].isEmpty()) clue++;
		Point p = PointsOfClues[clue].getFirst();
		
		
		LinkedList<PointAndIndex> pointsToConsider = new LinkedList<>(); /* this list will enable us to backtrack */
		LinkedList<LinkedList<Point>> brokenLines = this.generateBrokenLines(p,PointsOfClues, flags);
		LinkedList<LinkedList<LinkedList<Point>>> listOfBrokenLines = new LinkedList<>(); /* this list is associated to pointsToConsider : at index i, there is the list of BrokenLines associated to the point pointsToConsider.get(i) */
		int rank = 0; /* the rank of the currentPath in the list of Broken Lines associated to point p */
		LinkedList<Integer> listOfRanks = new LinkedList<>(); /* same as listOfBrokenLines but with the ranks */
		LinkedList<Point> currentPath; /* indicates the path we're currently considered */
		
		LinkedList<LinkedList<Point>> nextBrokenLines = new LinkedList<LinkedList<Point>>();
		Point lastPoint = new Point();
		Point nextPoint = new Point();
		LinkedList<Point> previousPath; 
		boolean bool = false; /* the meaning of this boolean is explained below */
		
		
		while(p.i != -1) {
			
			int nbOfPaths = brokenLines.size();
			
			if (rank == nbOfPaths) {
				/* there is no more path to consider for p, we have to go back to the previous point */
				currentPath = this.paths.removeLast();  /* there is still a path corresponding to p that has to be deleted */
				lastPoint = currentPath.getLast(); 
				for(Point pPath : currentPath) {
	    			if ( (! pPath.equals(p))&&(!pPath.equals(lastPoint)))
	    				this.get(pPath).color = Color.WHITE;        /* we delete the path, except the ends of it because we know we will consider an other path */
	    		}	
				bool = true;
				flags[p.i][p.j] = true;
	    		flags[lastPoint.i][lastPoint.j] = true;  /* the cells joined by the path are free */
	    		index = pointsToConsider.getLast().index;
	    		p = pointsToConsider.removeLast().p;  
				rank = listOfRanks.removeLast() + 1;
				brokenLines = listOfBrokenLines.removeLast();
	    	}
			else {
				if (rank > 0 && bool) { /* bool is true when the previous point is not the same as the one considered now */
					previousPath = this.paths.removeLast();
	    			lastPoint = previousPath.getLast();
	    			flags[lastPoint.i][lastPoint.j] = true;
	    			for(Point pPath : previousPath) {
	        			if ( (! pPath.equals(p))&&(!pPath.equals(lastPoint)))   this.get(pPath).color = Color.WHITE;        /* we delete the previous path */
	        		}
	    		}
				currentPath = brokenLines.get(rank);
				lastPoint = currentPath.getLast(); 
	    		flags[p.i][p.j] = false;
	    		flags[lastPoint.i][lastPoint.j] = false;  /* we have to indicate that the points are part of a path */
	    		PointAndIndex pointandindex =nextPointv2(p, flags, PointsOfClues, index);
	    		nextPoint = pointandindex.p;
	    		
	    	
	    		for(Point pPath : currentPath) {
	    			if ( (! pPath.equals(p))&&(!pPath.equals(lastPoint)))   this.get(pPath).color = Color.LIGHT_GRAY;        /* we temporarily color the cells of the path */
	    		}
	    		
	    		if (nextPoint.i != -1)
	    			nextBrokenLines = this.generateBrokenLines(nextPoint,PointsOfClues, flags); 
	    		
	    		if ((nextPoint.i != -1) && (nextBrokenLines.size() == 0)) {
	    			/* if we can't keep on the track */
	    			
	    			for(Point pPath : currentPath) {
		    			if ( (! pPath.equals(p))&&(!pPath.equals(lastPoint)))
		    				this.get(pPath).color = Color.WHITE;        /* we delete the path */
		    		}			
	    			if (rank == (nbOfPaths -1)) {
	    				bool = true; /* see above why we set this value to true */
	    				flags[p.i][p.j] = true;
	    	    		flags[lastPoint.i][lastPoint.j] = true; /* the cells of the path are now free */
	    	    		index = pointsToConsider.getLast().index;
	    	    		p = pointsToConsider.removeLast().p;
	    				rank= listOfRanks.removeLast() + 1;
	    				brokenLines = listOfBrokenLines.removeLast();  
	    			}
	    			else { /* we try the next path corresponding to the same point */
	    				bool = false;
	    				flags[lastPoint.i][lastPoint.j] = true;
	    				rank+=1;
	    			}
	    		
	    		}
	    		else {  /* if we can keep on the track or if it is the end of it (nextPoint.i = -1) */
	    			this.paths.add(currentPath);
	        		pointsToConsider.add(new PointAndIndex(p, index));
		        	listOfBrokenLines.add(brokenLines);
		        	listOfRanks.add(rank);
		        	
		        	index = pointandindex.index;
	        		rank = 0;
	        		brokenLines = nextBrokenLines;
	        		p = nextPoint;  /* we go to the next point */
	    			
	    			}
	    		
				}
			}
		/* at the end, there is no more maybe-colored */
		for(LinkedList<Point> path : paths) {
			for(Point q : path) {
				this.get(q).color = Color.BLACK;
			}
		}
		return;
    	
    }
	
    public void solvev3() {
    	/* Task 5 : using nextPointv3 */
    	LinkedList<Point> [] PointsOfClues = this.getPoints();
		boolean[][] flags = initializeFlags();
		
		int index = 0;
		int clue = 2;
		while(PointsOfClues[clue].isEmpty()) clue++;
		Point p = PointsOfClues[clue].getFirst();
		
		
		LinkedList<PointAndIndex> pointsToConsider = new LinkedList<>(); /* this list will enable us to backtrack */
		LinkedList<LinkedList<Point>> brokenLines = this.generateBrokenLines(p,PointsOfClues, flags);
		LinkedList<LinkedList<LinkedList<Point>>> listOfBrokenLines = new LinkedList<>(); /* this list is associated to pointsToConsider : at index i, there is the list of BrokenLines associated to the point pointsToConsider.get(i) */
		int rank = 0; /* the rank of the currentPath in the list of Broken Lines associated to point p */
		LinkedList<Integer> listOfRanks = new LinkedList<>(); /* same as listOfBrokenLines but with the ranks */
		LinkedList<Point> currentPath; /* indicates the path we're currently considered */
		
		LinkedList<LinkedList<Point>> nextBrokenLines = new LinkedList<LinkedList<Point>>();
		Point lastPoint = new Point();
		Point nextPoint = new Point();
		LinkedList<Point> previousPath; 
		boolean bool = false; /* the meaning of this boolean is explained below */
		
		
		while(p.i != -1) {
			
			int nbOfPaths = brokenLines.size();
			
			if (rank == nbOfPaths) {
				/* there is no more path to consider for p, we have to go back to the previous point */
				currentPath = this.paths.removeLast();  /* there is still a path corresponding to p that has to be deleted */
				lastPoint = currentPath.getLast(); 
				for(Point pPath : currentPath) {
	    			if ( (! pPath.equals(p))&&(!pPath.equals(lastPoint)))
	    				this.get(pPath).color = Color.WHITE;        /* we delete the path, except the ends of it because we know we will consider an other path */
	    		}	
				bool = true;
				flags[p.i][p.j] = true;
	    		flags[lastPoint.i][lastPoint.j] = true;  /* the cells joined by the path are free */
	    		index = pointsToConsider.getLast().index;
	    		p = pointsToConsider.removeLast().p;  
				rank = listOfRanks.removeLast() + 1;
				brokenLines = listOfBrokenLines.removeLast();
	    	}
			else {
				if (rank > 0 && bool) { /* bool is true when the previous point is not the same as the one considered now */
					previousPath = this.paths.removeLast();
	    			lastPoint = previousPath.getLast();
	    			flags[lastPoint.i][lastPoint.j] = true;
	    			for(Point pPath : previousPath) {
	        			if ( (! pPath.equals(p))&&(!pPath.equals(lastPoint)))   this.get(pPath).color = Color.WHITE;        /* we delete the previous path */
	        		}
	    		}
				currentPath = brokenLines.get(rank);
				lastPoint = currentPath.getLast(); 
	    		flags[p.i][p.j] = false;
	    		flags[lastPoint.i][lastPoint.j] = false;  /* we have to indicate that the points are part of a path */
	    		PointAndIndex pointandindex =nextPointv3(p, flags, PointsOfClues, index);
	    		nextPoint = pointandindex.p;
	    
	    		for(Point pPath : currentPath) {
	    			if ( (! pPath.equals(p))&&(!pPath.equals(lastPoint)))   this.get(pPath).color = Color.LIGHT_GRAY;        /* we temporarily color the cells of the path */
	    		}
	    		
	    		if (nextPoint.i != -1)
	    			nextBrokenLines = this.generateBrokenLines(nextPoint,PointsOfClues, flags); 
	    	
	    		if ((nextPoint.i != -1) && (nextBrokenLines.size() == 0)) {
	    			/* if we can't keep on the track */
	    			
	    			for(Point pPath : currentPath) {
		    			if ( (! pPath.equals(p))&&(!pPath.equals(lastPoint)))
		    				this.get(pPath).color = Color.WHITE;        /* we delete the path */
		    		}			
	    			if (rank == (nbOfPaths -1)) {
	    				bool = true; /* see above why we set this value to true */
	    				flags[p.i][p.j] = true;
	    	    		flags[lastPoint.i][lastPoint.j] = true; /* the cells of the path are now free */
	    	    		index = pointsToConsider.getLast().index;
	    	    		p = pointsToConsider.removeLast().p;
	    				rank= listOfRanks.removeLast() + 1;
	    				brokenLines = listOfBrokenLines.removeLast();  
	    			}
	    			else { /* we try the next path corresponding to the same point */
	    				bool = false;
	    				flags[lastPoint.i][lastPoint.j] = true;
	    				rank+=1;
	    			}
	    		
	    		}
	    		else {  /* if we can keep on the track or if it is the end of it (nextPoint.i = -1) */
	    			this.paths.add(currentPath);
	        		pointsToConsider.add(new PointAndIndex(p, index));
		        	listOfBrokenLines.add(brokenLines);
		        	listOfRanks.add(rank);
		        	
		        	index = pointandindex.index;
	        		rank = 0;
	        		brokenLines = nextBrokenLines;
	        		p = nextPoint;  /* we go to the next point */
	    			
	    			}
	    		
				}
			
			
			
			}
		
		/* at the end, there is no more maybe-colored */
		for(LinkedList<Point> path : paths) {
			for(Point q : path) {
				this.get(q).color = Color.BLACK;
			}
		}
		return;
    	
	}
    
    public void solveWithCombination() {
    	/* Task 7 */
    	
    	LinkedList<Point> [] PointsOfClues = this.getPoints();
		boolean[][] flags = initializeFlags();
		Point[][] clueRelatedTo = new Point[n][m];
    	for(int i = 0; i<n;i++) {
    		for(int j = 0; j<m;j++) {
    			if(this.grid[i][j].clue > 0)  clueRelatedTo[i][j] = new Point(i,j);
    			else clueRelatedTo[i][j] = new Point(-1,0);
    		}
    	}
		
    	this.combination(flags,PointsOfClues, clueRelatedTo); /* we use combination at the beginning */
		
		
		int index = 0;
		int clue = 2;
		while(PointsOfClues[clue].isEmpty()) clue++;
		Point p = PointsOfClues[clue].getFirst(); /* choice of the first point */
		
		
		LinkedList<PointAndIndex> pointsToConsider = new LinkedList<>(); /* this list will enable us to backtrack */
		LinkedList<LinkedList<Point>> brokenLines = this.generateBrokenLinesCombination(p,PointsOfClues, flags, clueRelatedTo);
		LinkedList<LinkedList<LinkedList<Point>>> listOfBrokenLines = new LinkedList<>(); /* this list is associated to pointsToConsider : at index i, there is the list of BrokenLines associated to the point pointsToConsider.get(i) */
		int rank = 0; /* the rank of the currentPath in the list of Broken Lines associated to point p */
		LinkedList<Integer> listOfRanks = new LinkedList<>(); /* same as listOfBrokenLines but with the ranks */
		LinkedList<Point> currentPath; /* indicates the path we're currently considered */
		
		LinkedList<LinkedList<Point>> nextBrokenLines = new LinkedList<LinkedList<Point>>();
		Point lastPoint = new Point();
		Point nextPoint = new Point();
		LinkedList<Point> previousPath; 
		boolean bool = false; /* the meaning of this boolean is explained below */
		
		
		while(p.i != -1) {
			
			int nbOfPaths = brokenLines.size();
			
			
			if (rank == nbOfPaths) {
				/* there is no more path to consider for p, we have to go back to the previous point */
				currentPath = this.paths.removeLast();  /* there is still a path corresponding to p that has to be deleted */
				lastPoint = currentPath.getLast();
				for(Point pPath : currentPath) {
	    			if ( (! this.get(pPath).color.equals(Color.BLACK)))
	    				this.get(pPath).color = Color.WHITE;        /* we delete the path, except the cells that are in the solution for sure (the black ones) */
	    		}	
				bool = true;
				flags[p.i][p.j] = true;
	    		flags[lastPoint.i][lastPoint.j] = true;  /* the cells joined by the path are free */
	    		index = pointsToConsider.getLast().index;
	    		p = pointsToConsider.removeLast().p;  
				rank = listOfRanks.removeLast() + 1;
				brokenLines = listOfBrokenLines.removeLast();
	    	}
			else {
				if (rank > 0 && bool) { /* bool is true when the previous point is not the same as the one considered now */
					previousPath = this.paths.removeLast();
	    			lastPoint = previousPath.getLast();
	    			flags[lastPoint.i][lastPoint.j] = true;
	    			for(Point pPath : previousPath) {
	        			if ( (! pPath.equals(p))&&(!pPath.equals(lastPoint)))   this.get(pPath).color = Color.WHITE;        /* we delete the previous path */
	        		}
	    		}
				currentPath = brokenLines.get(rank);
				lastPoint = currentPath.getLast(); 
	    		flags[p.i][p.j] = false;
	    		flags[lastPoint.i][lastPoint.j] = false;  /* we have to indicate that the points are part of a path */
	    		PointAndIndex pointandindex =nextPointv3(p, flags, PointsOfClues, index);
	    		nextPoint = pointandindex.p;
	    	
	    
	    		for(Point pPath : currentPath) {
	    			if ( (! this.get(pPath).color.equals(Color.BLACK)))   this.get(pPath).color = Color.LIGHT_GRAY;        /* we temporarily color the cells of the path that are not already colored in black */
	    		}
	    		
	    		if (nextPoint.i != -1)
	    			nextBrokenLines = this.generateBrokenLinesCombination(nextPoint,PointsOfClues, flags, clueRelatedTo); 
	    	
	    		if ((nextPoint.i != -1) && (nextBrokenLines.size() == 0)) {
	    			
	    			/* if we can't keep on the track */
	    			
	    			for(Point pPath : currentPath) {
		    			if ( (! this.get(pPath).color.equals(Color.BLACK)))
		    				this.get(pPath).color = Color.WHITE;        /* we delete the path */
		    		}			
	    			if (rank == (nbOfPaths -1)) {
	    				bool = true; /* see above why we set this value to true */
	    				flags[p.i][p.j] = true;
	    	    		flags[lastPoint.i][lastPoint.j] = true; /* the cells of the path are now free */
	    	    		index = pointsToConsider.getLast().index;
	    	    		p = pointsToConsider.removeLast().p;
	    				rank= listOfRanks.removeLast() + 1;
	    				brokenLines = listOfBrokenLines.removeLast();  
	    			}
	    			else { /* we try the next path corresponding to the same point */
	    				bool = false;
	    				flags[lastPoint.i][lastPoint.j] = true;
	    				rank+=1;
	    			}
	    		
	    		}
	    		else {  /* if we can keep on the track or if it is the end of it (nextPoint.i = -1) */
	    			this.paths.add(currentPath);
	        		pointsToConsider.add(new PointAndIndex(p, index));
		        	listOfBrokenLines.add(brokenLines);
		        	listOfRanks.add(rank);
		        	
		        	index = pointandindex.index;
	        		rank = 0;
	        		brokenLines = nextBrokenLines;
	        		p = nextPoint;  /* we go to the next point */
	    			
	    			}
	    		
				}
			}
		
		/* at the end, there is no more maybe-colored */
		for(LinkedList<Point> path : paths) {
			
			for(Point q : path) {
				this.get(q).color = Color.BLACK;
			}
		}
		return;
    }
 
	
}
