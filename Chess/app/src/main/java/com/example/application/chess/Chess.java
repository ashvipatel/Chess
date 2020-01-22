package com.example.application.chess;

import java.util.Scanner;
import java.util.StringTokenizer;

/**
 * Contains main method for playing chess.
 * @author Ashvi Patel
 * @author Nicole McGowan
 *
 */

public class Chess {

	/**
	 * This field indicates whether or not the board must be reprinted. It address the situation where there is an illegal move.
	 */
	public static boolean rePrintBoard = false;
	/**
	 * This field indicates which player's turn it is. If whiteTurn is true, it is the White player's turn, if not, it is the Black player's turn.
	 * 
	 */
	public static boolean whiteTurn = true;
	/**
	 * This field indicates whether or not a player has requested to draw.
	 * 
	 */
	public static boolean requestDraw  = false;
	/**
	 * This field is the position of the piece the user wants to move, given by the user.
	 * 
	 */
	public static String currentLocation = null;
	/**
	 * This field is the position of the piece the user wants to move to, given by the user. 
	 * 
	 */
	public static String newLocation = null;
	/**
	 * This field indicates whether there is a promotion argument given for the pawn.
	 * 
	 */
	public static String promotionArguement;
	/**
	 * This field is the chess board which the game is played on.
	 * 
	 */
	public static ChessBoard chessBoard = new ChessBoard();
	
	/**
	 * Main loop in which the players alternate to play chess.
	 * @param args  
	 * 
	 */
	
	public static void main(String[] args) {

		String [][] setBoard = displayBoard(chessBoard);
		printBoard(setBoard);
		setBoard = null;

		while(true) {
			if(whiteTurn) {
				System.out.println();
				System.out.print("White's move: ");
				whiteTurn = false;
			} else {
				System.out.println();
				System.out.print("Black's move: ");
				whiteTurn = true;
				
			}
			Scanner scanner = new Scanner(System.in);
			String input = scanner.nextLine();
			parseInput(input);
			
			
			if(rePrintBoard) {
				System.out.println();
				setBoard = displayBoard(chessBoard);
				printBoard(setBoard);
			}
		}	
	}

	/**
	 * Method in which the board prints out the chess game to the user with the ranks and the files on the right-most column and left-most row, respectively. 
	 * @param setBoard is a 2D String array that has the pieces written in ascii art.
	 * 
	 */
	
	public static void printBoard(String[][] setBoard) {
		int count = 8;
		for(int i = 7; i >= 0; i--) {
			for(int j = 0; j < 9; j++) {
				if(j == 8) {
					System.out.print(count);
					count--;
					continue;
				}
				System.out.print(setBoard[i][j]);
			}
			System.out.println();
		}
		System.out.print(" a  b  c  d  e  f  g  h\n");
	}
	
	/**
	 * Method in which the board writes the pieces on the chessBoard in ascii art. The method takes chessBoard as an argument and goes through each position in the 
	 * chessBoard to see what pieces exists, if any, at that position and writes them to setBoard in ascii art.
	 * @param chessBoard the chessBoard that the game is played on
	 * @return setBoard a String 2D array in which the the values of the pieces are written in ascii art
	 * 
	 */
	
	public static String [][] displayBoard(ChessBoard chessBoard) {
		String [][] setBoard = new String [8][8];
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {

				if(chessBoard.getPiece(i, j) == null) {
					if( ((i%2)==0 && (j%2)==0) || ((i%2)!=0 && (j%2)!=0) ) {
						setBoard[i][j] = "## ";
					}
					else {
						setBoard[i][j] = "   ";
					}
				} else {
					if(chessBoard.getPiece(i, j).color == 'w') {
						if(chessBoard.getPiece(i, j).getClass() == Rook.class) {
							setBoard[i][j] = "wR ";
						}
						if(chessBoard.getPiece(i, j).getClass() == Knight.class) {
							setBoard[i][j] = "wN ";
						}
						if(chessBoard.getPiece(i, j).getClass() == Bishop.class) {
							setBoard[i][j] = "wB ";
						}
						if(chessBoard.getPiece(i, j).getClass() == Queen.class) {
							setBoard[i][j] = "wQ ";
						}
						if(chessBoard.getPiece(i, j).getClass() == King.class) {
							setBoard[i][j] = "wK ";
						}
						if(chessBoard.getPiece(i, j).getClass() == Pawn.class) {
							setBoard[i][j] = "wp ";
						}
					}
					else if(chessBoard.getPiece(i, j).color == 'b') {
						if(chessBoard.getPiece(i, j).getClass() == Rook.class) {
							setBoard[i][j] = "bR ";
						}
						if(chessBoard.getPiece(i, j).getClass() == Knight.class) {
							setBoard[i][j] = "bN ";
						}
						if(chessBoard.getPiece(i, j).getClass() == Bishop.class) {
							setBoard[i][j] = "bB ";
						}
						if(chessBoard.getPiece(i, j).getClass() == Queen.class) {
							setBoard[i][j] = "bQ ";
						}
						if(chessBoard.getPiece(i, j).getClass() == King.class) {
							setBoard[i][j] = "bK ";
						}
						if(chessBoard.getPiece(i, j).getClass() == Pawn.class) {
							setBoard[i][j] = "bp ";
						}
					}
				}
			}
		}
		return setBoard;
	}

	/**
	 * This method takes account for all the possible inputs and accounts for edge cases where the inputs are invalid. 
	 * @param input from the user
	 * 
	 */
	public static void parseInput(String input){
		StringTokenizer tokens = null;
		int numberOfArguements = 0;
		input = input.toLowerCase();
		input = input.trim();
		String[] tokenArray = new String[20]; 
		tokens = new StringTokenizer(input);
		while (tokens.hasMoreTokens()) {
			tokenArray[numberOfArguements] = tokens.nextToken();
			numberOfArguements++;
		}
			if(numberOfArguements == 1){
				if(tokenArray[0].equals("resign")){
					if(whiteTurn){
						System.out.println("White wins");
						System.exit(1);
					}else{
						System.out.println("Black wins");
						System.exit(1);
					}
				}else if (tokenArray[0].equals("draw")){
					if (requestDraw == true){
						System.out.println("Draw");
						System.exit(1);
					}else{
						System.out.println("Ask opponent for draw!");
						rePrintBoard = false;
						if(whiteTurn) {
							whiteTurn = false;
						}else {
							whiteTurn = true;
						}
					}
				}else{
					System.out.println("Illegal move, try again.");
					rePrintBoard = false;
					if(whiteTurn) {
						whiteTurn = false;
					}else {
						whiteTurn = true;
					}
				}
			}else if (numberOfArguements == 2) {
				
				currentLocation = tokenArray[0];
				newLocation = tokenArray[1];
				
				if(currentLocation.equals(newLocation)) {
					System.out.println("Illegal move, try again.");	
					rePrintBoard = false;
					if(whiteTurn) {
						whiteTurn = false;
					}else {
						whiteTurn = true;
					}
				} else if ((currentLocation.length() == 2 && newLocation.length() == 2)){
					checksMove();
					
				}else{
					System.out.println("Illegal move, try again.");
					rePrintBoard = false;
					if(whiteTurn) {
						whiteTurn = false;
					}else {
						whiteTurn = true;
					}
				}

			}else if (numberOfArguements == 3){
				
				currentLocation = tokenArray[0];
				newLocation = tokenArray[1];

				
				if(currentLocation.equals(newLocation)) {
					
					System.out.println("Illegal move, try again.");	
					rePrintBoard = false;
					if(whiteTurn) {
						whiteTurn = false;
					}else {
						whiteTurn = true;
					}

				}else if ((currentLocation.length() == 2 && newLocation.length() == 2)){

					if (tokenArray[2].equals("draw?")){
						checksMove();
						requestDraw = true;
					}else if (tokenArray[2].equals("q") || tokenArray[2].equals("r") || tokenArray[2].equals("b") || tokenArray[2].equals("n")) {
						promotionArguement = tokenArray[2];
						checksMove();
					}else{
						System.out.println("Illegal move, try again");
						rePrintBoard = false;
						if(whiteTurn) {
							whiteTurn = false;
						}else {
							whiteTurn = true;
						}
					}
				}else{
					System.out.println("Illegal move, try again");
					rePrintBoard = false;
					if(whiteTurn) {
						whiteTurn = false;
					}else {
						whiteTurn = true;
					}
				}
			}
			else {
				System.out.println("Illegal move, try again");
				rePrintBoard = false;
				if(whiteTurn) {
					whiteTurn = false;
				}else {
					whiteTurn = true;
				}
			}

		currentLocation = null;
		newLocation = null;
		promotionArguement = null;

	}
	
	/**
	 * This method translates the file input given as alphabet from the user to a numeric file.
	 * @param file which is of type String obtained from the user
	 * @return the numeric version of file
	 */
	
	public static int interpretFile(String file){
		int result = ((int)file.toLowerCase().charAt(0) - (int)('a'));
		return result;
	}

	/**
	 * This method translates the rank input given as integer in and modifies by subtracting one because our chessBoard starts from (0, 0) at the bottom-left corner.
	 * @param rank which is of type String obtained from the user
	 * @return the rank of the move as a position corresponding to our board
	 */
	

	public static int interpretRank(String rank){
		int result = ((int)rank.toLowerCase().charAt(1) - (int)('1'));
		return result;
	}

	/**
	 * This method obtains information from the move(int[] start, int[] end, ChessBoard board) function to determine whether or not the pieces can be moved or not. If the pieces cannot be moved, the method prints the correct statement.
	 * The method takes in no argument and is called in the parseInput(String input) function.
	 * 
	 */
	
	
	public static void checksMove(){

		int currentFile = interpretFile(currentLocation);
		int currentRank = interpretRank(currentLocation);
		
		int newFile = interpretFile(newLocation);
		int newRank = interpretRank(newLocation);
		
		int [] start = new int[2];
		start[0] = currentRank;
		start[1] = currentFile;

		int [] end = new int [2];
		end[0] = newRank;
		end[1] = newFile;
		boolean [] promotionArray;
		ChessPiece p = chessBoard.getPiece(start[0],start[1]);
		boolean [] callMove = new boolean [4];
		if(promotionArguement == null) {
			promotionArguement = "q";
		}
		
		if( (p==null) || (p.color == 'w' && whiteTurn) || (p.color == 'b' && !whiteTurn)) {
			System.out.println("Illegal move, try again");
			if(whiteTurn) {
				whiteTurn = false;
			}else {
				whiteTurn = true;
			}
			rePrintBoard = false;
		} else {
			if(p instanceof Pawn && (((end[0]==0) && (p.color=='b')) || ((end[0]==7) && (p.color=='w')))){
				callMove = ((Pawn)p).promotion(start, end, chessBoard, promotionArguement);
			}else{
				callMove = chessBoard.getPiece(start[0], start[1]).move(start, end, chessBoard);
			}
		    
		    if(!callMove[0]) {
				System.out.println("Illegal move, try again");
				if(whiteTurn) {
					whiteTurn = false;
				}else {
					whiteTurn = true;
				}
				rePrintBoard = false;
		    }else {
		    	if(callMove[0] == true && callMove[1] == false && callMove[2] == false && callMove[3] == false) {
					rePrintBoard = true;
		    	}
		    	if(callMove[1] == true && callMove[2] == false) {
		    		System.out.println("Check");
					rePrintBoard = true;   //ask NICOle
		    	}
		    	if(callMove[2]) {
		    		System.out.println("Checkmate");
		    		if(p.color == 'b') {
			    		System.out.println("Black wins");
						System.exit(1);
		    		}else {
			    		System.out.println("White wins");
						System.exit(1);
		    		}
		    	}
		    	if(callMove[3]) {
		    		System.out.println("Stalemate");
		    		System.out.println("draw");
					System.exit(1);
		    	}
		    }
		}
	}
}



