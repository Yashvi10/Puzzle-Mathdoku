/**
 * Assignment 4: Mathdoku Puzzle.
 * Banner ID:    B00870489
 * @author Yashvi
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


// Made class Matrix which stores the values of different cells
class Matrix {

	int value;
	char group;
	
        //constructor Matrix of class
	public Matrix(int value, char group) {
		this.value = value;
		this.group = group;
        }
}

// Made class Position for storing values x and y at different locations
class Position {

	int x;
	int y;
	
        //constructor Position of class
	public Position(int x, int y) {
		this.x = x;
		this.y = y;
	}
}

// Made class Section which stores groups expressions
class Section {

	int result;
	char op;
	
	// ArrayList list formed for each group
	ArrayList<Position> list;
	
	public Section(int result, char op, ArrayList<Position> list) {
		this.result = result;
		this.op = op;
		this.list = list;
	}
}

/*
 * Mathdoku Class consisting of different methods having different functionalities for puzzle operations
 */  
public class Mathdoku {

        //Declaration of matrix, other operators and map
	Matrix[][] cells;
	Map<Character, Section> cell_groupings;
	int choices;
	final private static char equality_op = '=';
	final private static char multiplication_op = '*';
	final private static char addition_op = '+';
	final private static char subtraction_op = '-';
	final private static char division_op = '/';

	// Constructor of class Mathdoku
	public Mathdoku() {
		this.cells = null;
		this.cell_groupings = null;
		this.choices = 0;
	}

	// loadPuzzle method will load the complete Puzzle with each group having its operator and result value
	public boolean loadPuzzle(BufferedReader stream) {

		this.cells = null;
		this.cell_groupings = null;
		this.choices = 0;

		if (stream == null) {
			return false;
		}

		try {

			String str = stream.readLine();

			if (str == null) {
				return false;
			}
			else {
                                //trims spaces if there are any in text file
				str = str.trim();
			}
                   
                        //converting size of matrix from String to Integer
			int size_of_str = Integer.parseInt(str);
                        str = stream.readLine();
                  
			if(str == null) {
                            return false;
			}
                        
                        //checks whether the length of str is less than size of matrix 
                        if(str.length() != size_of_str){
                            return false;
                        }

			// create a matrix of size n*n
			this.cells = new Matrix[size_of_str][size_of_str];
  
			for (int i=0; i< size_of_str; i++) {
				for (int j=0; j< size_of_str; j++) {                                  
                                    this.cells[i][j] = new Matrix(0, str.charAt(j)); 
				}
      
				if(i == size_of_str-1) {
					break;
				}

				str = stream.readLine();

				// if finds any blank line in between then skip it and start reading next line
				while(str != null && str.trim().isEmpty()) {
					str = stream.readLine();
				}

				if (str==null) {
					this.cells = null;
					return false;
				}

				str = str.trim();

				// comparing two strings, if not equal then might be wrong input
				if (str.length()!= size_of_str) {
					this.cells = null;
					return false;
				}
			}

			str = stream.readLine();
			String ConstraintData = "";

			// it checks and stores expressions in string str
			while(str != null) {

				// checks if the line is not empty, if found empty then skip and read next.
				if (str.trim().isEmpty()) {
					str = stream.readLine();
					continue;
				}

				ConstraintData += str.trim()+"\n";
				str = stream.readLine();
			}

			// calls method ConstraintsLoader for loading it into matrix
			ConstraintsLoader(ConstraintData);

		} catch (IOException e) {
			this.cells = null;
			this.cell_groupings = null;
			return false;
		}
		return true;
	}

	/*
         *validate method validates whether the puzzle in valid or not.
         *If it is not valid then it cannot be solved by user so it should not be loaded.
         */
	public boolean validate() {
            
                //checks if cells are null or groups are null and returns false
		if (cells == null || cell_groupings == null) {
			return false;
		}

		// checks whether cells are filled or not and returns true
		if(if_cells_filled())
		{
			return true;
		}

		Set<Character> set1 = new HashSet<>();

                //adds cell values into set1  
		for(int i=0; i< this.cells.length; i++) {
			for(int j=0; j< this.cells.length; j++) {
				set1.add(cells[i][j].group);
			}
		}

                //checks whether all the cell groups belonging to specific cell are available in constraints or not
		if (!this.cell_groupings.keySet().containsAll(set1)) {
			return false;
		}


		for (char c1: this.cell_groupings.keySet()) {
                    //ensures that the cell with = operator should be one
                    if (this.cell_groupings.get(c1).op == equality_op) {
                        if (this.cell_groupings.get(c1).list.size() != 1) {
                                return false;
                            }
                    }
		    //ensures that the cell with - operator should be two
                    if (this.cell_groupings.get(c1).op == subtraction_op) {
                            if (this.cell_groupings.get(c1).list.size() !=2 ) {
                                    return false;
                            }
                    }
                    //ensures that the cell with / operator should be two
                    else if (this.cell_groupings.get(c1).op == division_op) {
                            if (this.cell_groupings.get(c1).list.size() != 2) {
                                    return false;
                            }
                    }
		}

		//checks that the result of any cell is not zero or negative
		for (char c2: this.cell_groupings.keySet()) {
			if (this.cell_groupings.get(c2).result <= 0) {
				return false;
			}
		}

		return true;
	}

	// loading of constraints(operator and result value) into cell groups
	private void ConstraintsLoader(String ConstraintData) {

            //checks if data about constraints is empty or not
            if(!ConstraintData.isEmpty()) {

                    this.cell_groupings = new HashMap<Character, Section>();

                    //ArrayList Operators_Validity which stores valid operators
                    ArrayList<Character> Operators_Validity = 
                                    new ArrayList<>(Arrays.asList(multiplication_op, division_op, addition_op, subtraction_op, equality_op)); 

                    String[] str3 = ConstraintData.split("\n");

                    for (String str: str3) {
                            String[] str4 = str.split("\\s+");

                            if(str4.length != 3) {
                                    this.cell_groupings = null;
                                    return;
                            }
                            
                            //finds and gets group 
                            char group = str4[0].charAt(0);

                            int result;

                            try {
                                // stores result 
                                result = Integer.valueOf(str4[1]);
                            }
                            catch(NumberFormatException e){
                                    this.cell_groupings = null;
                                    return;
                            }

                            // finds and gets op
                            char op = str4[2].charAt(0);

                            if (!Operators_Validity.contains(op)) {
                                    this.cell_groupings = null;
                                    return;
                            }

                            ArrayList<Position> list1 = new ArrayList<>();

                            for(int i=0; i < this.cells.length; i++) {
                                    for (int j=0; j< this.cells.length; j++) {
                                        if (this.cells[i][j].group == group) {
                                                list1.add(new Position(i, j));
                                        }
                                    }
                            }

                            // this will put operator, group and result altogether
                            this.cell_groupings.put(group , new Section(result, op, list1));
                    }
            }
	}

	// solve method for solving puzzle
	public boolean solve() {

		if (this.cells == null || this.cell_groupings == null) {
			return false;
		}

		// checks whether cells are solved or not and returns true if are already solved
		if(if_cells_filled())
		{
			return true;
		}

		// resets choices to 0
		this.choices = 0;

                //set for storing valid group
		Set<Character> Groups_Validity = new HashSet<>();

		for(int i=0; i< this.cells.length; i++) {
			for(int j=0; j< this.cells.length; j++) {
				Groups_Validity.add(cells[i][j].group);
			}
		}

		// removal of extra cell groups 
		if (this.cell_groupings.size() != Groups_Validity.size()) {
			this.cell_groupings.keySet().retainAll(Groups_Validity);
		}

                //set for storing such constraint having = operator
		Set<Character> EqualityGroups = new HashSet<>();

		// for insertion of = constraints
		for (char ch: this.cell_groupings.keySet()) {
			if(this.cell_groupings.get(ch).op == equality_op) {
				Position p = this.cell_groupings.get(ch).list.get(0);
				int x = p.x;
				int y = p.y;
				int result = this.cell_groupings.get(ch).result;
                               
				// checks row and column values for conflicts
				if (!Conflicts(x, y, result)) {
					puzzle_resetter();
					return false;
				}
				else if (result > this.cells.length) {
					puzzle_resetter();
					return false;
				}
                                
                                //adds to set 
				this.cells[x][y].value = result;
				EqualityGroups.add(ch);
			}
		}

		// after filling of groups of = constraints they are removed
		this.cell_groupings.keySet().removeAll(EqualityGroups);

		ArrayList<ArrayList<Position>> Points_Grouping = new ArrayList<>();

		// for storing of positions into matrix
		for (char ch: this.cell_groupings.keySet()) {
			Points_Grouping.add(new ArrayList<Position>(this.cell_groupings.get(ch).list));
		}

		int[] list = new int[this.cells.length];

		// list of all values that are possible
		for(int i=0; i < this.cells.length; i++) {
			list[i] = i+1;
		}

		// cell solving
		if (Points_Grouping.size()!=0) {
			Puzzle_Solver(Points_Grouping, list, 0);
		}

		// if true that means cells are solved
		if(if_cells_filled())
		{
			return true;
		}

		// resetting of cells and choices if it returns false
		else {
			this.choices = 0;
			return false;
		}
                
	}

        //This method will solve the complete puzzle considering whole matrix with each cell and its respective values and positions
	private void Puzzle_Solver(ArrayList<ArrayList<Position>> Points_Grouping, int[] list, int index) {

		// checks if some cell is remaining or all visited. If all visited then returns 
		if (index == Points_Grouping.size()) {
			return;
		}

		// it will get first index
		int x = Points_Grouping.get(index).get(0).x;
		int y = Points_Grouping.get(index).get(0).y;

		// after getting first index it will get the group respectively
		char group = this.cells[x][y].group;

		// then it will get the operator of that respective group
		char op = this.cell_groupings.get(group).op;

		// it will get result of that group
		int result = this.cell_groupings.get(group).result;

		// returns places of the group in matrix
		ArrayList<Position> places = this.cell_groupings.get(group).list;

		int numberOfLocations = places.size();

		// In this arraylist it will store all the possible pair of values that can be considered for a particular group
		ArrayList<ArrayList<Integer>> Probable_Pairs = new ArrayList<>();

		// switch case for all operators that will be checked 
		switch(op) {
		case addition_op:
			Probable_Pairs = new MakingPair().PairsOfAddition(list,
					numberOfLocations, result, new ArrayList<>(), Probable_Pairs);
			break;
		case multiplication_op:
			Probable_Pairs = new MakingPair().PairsOfMultiplication(list,
					numberOfLocations, result, new ArrayList<>(), Probable_Pairs);
			break;
		case subtraction_op:
			if(numberOfLocations <= 2)
			Probable_Pairs = new MakingPair().PairsOfSubtraction(list,
					result, Probable_Pairs);
			break;
		case division_op:
			if(numberOfLocations <= 2)
			Probable_Pairs = new MakingPair().PairsOfDivision(list,
					result, Probable_Pairs);
			break;
		}
		
		for(int i=0; i < Probable_Pairs.size(); i++) {			

			boolean Pairs_Available = true;

			for (int j=0; j < places.size(); j++) {
                            
				int row = places.get(j).x;
				int column = places.get(j).y;

				// it will check possible pairs list and returns the value from it.
				int value = Probable_Pairs.get(i).get(j);

                                //after getting a pair of values from list it will check the conflicts,
                                //if any present value gets matched with existing value in any row or column,
                                //it will perform resetting of values to 0
                                //then it breaks and checks for another possible pair from the list
				if (!Conflicts(row, column, value)) {
					resetting_cells(places);
					Pairs_Available = false;
					break;
				}

				//this will insert the values in row and column if it is unique and not conflicting
				this.cells[row][column].value = value;
			}

			// checks whether pair found is without conflict or not
			if(Pairs_Available) {

				// checking for the next group
				Puzzle_Solver(Points_Grouping, list, index+1);

				// exiting loop because cell not empty
				if(if_cells_filled()) {
					break;
				}

				// try new combination after resetting it to 0
				else {
					resetting_cells(places);
					continue;
				}
			}

			//this will backtrack to previous groups in case if no values are fitting in to current group
			if (Pairs_Available == false && i == (Probable_Pairs.size()-1)) {

				this.choices++;
				return;
			}
		}
	}

	// cells are complete or not
	private boolean if_cells_filled() {
		
		if(this.cells == null) {
			return false;
		}
		
		for (int i=0; i< this.cells.length; i++) {
			for (int j=0; j< this.cells.length; j++) {
				if (this.cells[i][j].value==0) {
					return false;
				}
			}
		}
		return true;
	}

	// cells resetter
	private void puzzle_resetter() {
		for (int i=0; i< this.cells.length; i++) {
			for (int j=0; j< this.cells.length; j++) {
				this.cells[i][j].value = 0;
			}
		}
	}

	// cell value resetter to 0
	private void resetting_cells(ArrayList<Position> places) {
		for (Position loc: places) {
			cells[loc.x][loc.y].value = 0;
		}
	}

	// prints the resulted matrix 
	public String print() {

		if (this.cells == null) {
			return null;
		}

		String str2 = "";

		// printing values of completed cells
		if (if_cells_filled()) {
			for (int i=0; i< this.cells.length; i++) {
				for (int j=0; j< this.cells.length; j++) {
					 str2+= this.cells[i][j].value;
				}
				str2+="\n";
			}
		}
		
		// for incomplete cells prints group names
		else {
			for (int i=0; i< this.cells.length; i++) {
				for (int j=0; j< this.cells.length; j++) {
					char ch = this.cells[i][j].group;
					if(this.cell_groupings != null && this.cell_groupings.get(ch) != null && this.cell_groupings.get(ch).op == '=') {
						str2 += this.cell_groupings.get(ch).result;
					}
					else {
						str2 += this.cells[i][j].group;
					}
				}
				str2+="\n";
			}
		}

		return str2;
	}

	// number of backtracks
	public int choices() {
		return this.choices;
	}

	// checks conflicts in row, column
	private boolean Conflicts(int row, int column, int value) {

		// for row
		for(int i=0; i< this.cells.length; i++) {
			if(this.cells[row][i].value == value) {
				return false;
			}
		}

		// for column
		for(int i=0; i< this.cells.length; i++) {
			if(this.cells[i][column].value == value) {
				return false;
			}
		}
		return true;
	}
}

//class MakingPair makes various possible pairs for groups based on operations given
class MakingPair {

        //list for getting pairs for addition operations
	ArrayList<ArrayList<Integer>> PairsOfAddition(int[] list, int size_of_str, int result, 
			ArrayList<Integer> Output, ArrayList<ArrayList<Integer>> Complete_op){
		
		// checks if the completed pair satisfies the constraints
		if (Output.size() == size_of_str) {
			int sum = 0;
			for (int value: Output) {
				sum += value;
			}
			//adds pairs to output list if this returns true
			if (sum == result) {
				Complete_op.add(new ArrayList<Integer>(Output));
			}			
			return Complete_op;
		}

		// adding values to list
		for (int i: list) {
			
			Output.add(i);
			
			PairsOfAddition(list, size_of_str, result, Output, Complete_op);
			
			Output.remove(Output.size()-1);
		}
		
		return Complete_op;
	}

	//list for getting pairs for multiplication operations
	ArrayList<ArrayList<Integer>> PairsOfMultiplication(int[] list, int size_of_str, int result,
			ArrayList<Integer> Output, ArrayList<ArrayList<Integer>> Complete_op){
		
		// checks if the completed pair satisfies the constraints
		if (Output.size() == size_of_str) {
			int product = 1;
			for (int value: Output) {
				product *= value;
			}
			
			//adds pairs to output list if this returns true
			if (product == result) {
				Complete_op.add(new ArrayList<Integer>(Output));
			}			
			return Complete_op;
		}

                // adding values to list
		for (int i: list) {
			
			Output.add(i);
			
			PairsOfMultiplication(list, size_of_str, result, Output, Complete_op);
			
			Output.remove(Output.size()-1);
		}
		return Complete_op;
	}

	//list for getting pairs for subtraction operations
	ArrayList<ArrayList<Integer>> PairsOfSubtraction (int[] list, int result,
			ArrayList<ArrayList<Integer>> Complete_op) {
		
		for (int i=0; i < list.length; i++) {
			for (int j=i; j < list.length; j++) {
				
                                //checks whether the result of difference satisfies or not
                                //if satisfies then adding the pairs into Complete_op list in both ways
				if(list[j]-list[i] == result) {
					Complete_op.add(new ArrayList<Integer>(Arrays.asList(list[i], list[j])));
					Complete_op.add(new ArrayList<Integer>(Arrays.asList(list[j], list[i])));
				}
			}
		}
		return Complete_op;
	}

	//list for getting pairs for division operations
	ArrayList<ArrayList<Integer>> PairsOfDivision (int[] list, int result,
			ArrayList<ArrayList<Integer>> Complete_op) {
		
		for (int i=0; i < list.length; i++) {
			for (int j=i; j < list.length; j++) {
				
				//checks whether the result of division satisfies or not
                                //if satisfies then adding the pairs into Complete_op list in both ways
				if(list[j]%list[i] == 0 && list[j]/list[i] == result) {
					Complete_op.add(new ArrayList<Integer>(Arrays.asList(list[i], list[j])));
					Complete_op.add(new ArrayList<Integer>(Arrays.asList(list[j], list[i])));
				}
			}
		}
		return Complete_op;
	}
}
