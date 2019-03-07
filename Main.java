import java.io.FileNotFoundException;
import java.util.*;

@SuppressWarnings("unused")
class Main {
	
	public static void main(String[] args) throws FileNotFoundException {
		    demo(4);
		    
		    System.out.println("solvev1 : " + averagetimeAllGrids(1));
		    System.out.println("solvev2 : " + averagetimeAllGrids(2));
		    System.out.println("solvev3 : " +averagetimeAllGrids(3));
		    System.out.println("solveWithCombination : " +averagetimeAllGrids(4)); 
			
		    System.out.println("solvev1 : " +averagetimeDifficultGrid(1));
		    System.out.println("solvev2 : " +"Trop long");
		    System.out.println("solvev3 : " +averagetimeDifficultGrid(3));
		    System.out.println("solveWithCombination : " +averagetimeDifficultGrid(4)); 
			
	}
	
	public static void demo(int i) throws FileNotFoundException {
		Logipix puzzle1 = new Logipix();
		Logipix puzzle2 = new Logipix();
		puzzle1.initialize("C:\\Users\\admin\\eclipse-workspace\\Logipix\\src\\test" + String.valueOf(i)+".txt");
		puzzle2.initialize("C:\\Users\\admin\\eclipse-workspace\\Logipix\\src\\test" + String.valueOf(i)+".txt");
		puzzle2.solveWithCombination();
		puzzle1.display();
		puzzle2.display();
		return;
	}
	
	public static long averagetimeDifficultGrid(int i) throws FileNotFoundException {
		int N = 30;
		long time;
		long totaltime = 0;
		Logipix puzzle = new Logipix();
		if (i==1) {
			for (int j = 0; j<N;j++) {
					puzzle.initialize("C:\\Users\\admin\\eclipse-workspace\\Logipix\\src\\test2.txt");
					time = System.currentTimeMillis();
					puzzle.solvev1();
					time =  System.currentTimeMillis() - time;
					totaltime += time;
				}
			return (totaltime/(N));
		}
		if(i==2) {
			N = 1;
			for (int j = 0; j<N;j++) {
				puzzle.initialize("C:\\Users\\admin\\eclipse-workspace\\Logipix\\src\\test2.txt");
				time = System.currentTimeMillis();
				puzzle.solvev2();
				time =  System.currentTimeMillis() - time;
				totaltime += time;
			}
			return (totaltime/(N));
		}
		if(i==3) {
			for (int j = 0; j<N;j++) {
				puzzle.initialize("C:\\Users\\admin\\eclipse-workspace\\Logipix\\src\\test2.txt");
				time = System.currentTimeMillis();
				puzzle.solvev3();
				time =  System.currentTimeMillis() - time;
				totaltime += time;
			}
			return (totaltime/(N));
		}
		if(i==4) {
			for (int j = 0; j<N;j++) {
				puzzle.initialize("C:\\Users\\admin\\eclipse-workspace\\Logipix\\src\\test2.txt");
				time = System.currentTimeMillis();
				puzzle.solveWithCombination();
				time =  System.currentTimeMillis() - time;
				totaltime += time;
			}
			return (totaltime/(N));
		}
		return ((long) 0);
	}
	
	public static long averagetimeAllGrids(int i) throws FileNotFoundException {
		int N = 30;
		long time;
		long totaltime = 0;
		Logipix puzzle = new Logipix();
		if (i==1) {
			for (int j = 0; j<N;j++) {
				for (int k = 1; k<6;k++) {
					puzzle.initialize("C:\\Users\\admin\\eclipse-workspace\\Logipix\\src\\test" + String.valueOf(k)+".txt");
					time = System.currentTimeMillis();
					puzzle.solvev1();
					time =  System.currentTimeMillis() - time;
					totaltime += time;
				}
			}
			return (totaltime/(5*N));
		}
		if(i==2) {
			N = 1;
			for (int j = 0; j<N;j++) {
				for (int k = 1; k<6;k++) {
					puzzle.initialize("C:\\Users\\admin\\eclipse-workspace\\Logipix\\src\\test" + String.valueOf(k)+".txt");
					time = System.currentTimeMillis();
					puzzle.solvev2();
					time =  System.currentTimeMillis() - time;
					totaltime += time;
				}
			}
			return (totaltime/(5*N));
		}
		if(i==3) {
			for (int j = 0; j<N;j++) {
				for (int k = 1; k<6;k++) {
					puzzle.initialize("C:\\Users\\admin\\eclipse-workspace\\Logipix\\src\\test" + String.valueOf(k)+".txt");
					time = System.currentTimeMillis();
					puzzle.solvev3();
					time =  System.currentTimeMillis() - time;
					totaltime += time;
				}
			}
			return (totaltime/(5*N));
		}
		if(i==4) {
			for (int j = 0; j<N;j++) {
				for (int k = 1; k<6;k++) {
					puzzle.initialize("C:\\Users\\admin\\eclipse-workspace\\Logipix\\src\\test" + String.valueOf(k)+".txt");
					time = System.currentTimeMillis();
					puzzle.solveWithCombination();
					time =  System.currentTimeMillis() - time;
					totaltime += time;
				}
			}
			return (totaltime/(5*N));
		}
		return ((long) 0);
	}
}
