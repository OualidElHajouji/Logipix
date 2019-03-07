The goal is to solve Logipix puzzles, which are represented by grids of numbers. 
The rules of the game are explained in the file Logipix_explainer

The different versions of the solvers are solvev1, solvev2, solvev3, solveWithCombination.
They are methodes that can be run this way : puzzle.solve(), puzzle being from the class Logipix (created like this : puzzle = new Logipix() )


A puzzle can be initialized with the data of a given .txt file this way : puzzle.initialize(String address of the file) 
-> For example, puzzle.initialize("C:\\Users\\admin\\eclipse-workspace\\Logipix\\src\\test1.txt")


A puzzle can be displayed this way : puzzle.display().
