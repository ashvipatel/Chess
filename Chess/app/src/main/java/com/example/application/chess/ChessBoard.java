package com.example.application.chess;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Initializes the chess board and contains methods for evaluating its state.
 * @author Ashvi Patel
 * @author Nicole McGowan
 *
 */

public class ChessBoard implements Parcelable {
	

	/**
	 * 2D array that represents the chess board
	 */
	
	public ChessPiece [][] board;
	private ChessPiece [] cP;

	/**
	 * Default constructor which initializes the chess board
	 */
	
	public ChessBoard() {

		board = new ChessPiece [8][8];
		
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				board[x][y] = null;
			}
		}
		
		board[0][0] = new Rook('w');
		board[0][1] = new Knight('w');
		board[0][2] = new Bishop('w');
		board[0][3] = new Queen('w');
		board[0][4] = new King('w');
		board[0][5] = new Bishop('w');
		board[0][6] = new Knight('w');
		board[0][7] = new Rook('w');
		
		board[7][0] = new Rook('b');
		board[7][1] = new Knight('b');
		board[7][2] = new Bishop('b');
		board[7][3] = new Queen('b');
		board[7][4] = new King('b');
		board[7][5] = new Bishop('b');
		board[7][6] = new Knight('b');
		board[7][7] = new Rook('b');
		
		board[1][0] = new Pawn('w');
		board[1][1] = new Pawn('w');
		board[1][2] = new Pawn('w');
		board[1][3] = new Pawn('w');
		board[1][4] = new Pawn('w');
		board[1][5] = new Pawn('w');
		board[1][6] = new Pawn('w');
		board[1][7] = new Pawn('w');
		
		board[6][0] = new Pawn('b');
		board[6][1] = new Pawn('b');
		board[6][2] = new Pawn('b');
		board[6][3] = new Pawn('b');
		board[6][4] = new Pawn('b');
		board[6][5] = new Pawn('b');
		board[6][6] = new Pawn('b');
		board[6][7] = new Pawn('b');
		

	}
	
	/**
	 * This method checks if the King is in check.
	 * @param c which is the color of the King
	 * @return boolean value that indicates if King is in check
	 */
	
	public boolean isCheck(char c){
		boolean result = false;
		char opponentColor;
		int [] positionArray = new int[2];
		int [] currentPosition = new int[2]; 
		if(c == 'b'){
			positionArray = getKingFileRank('b');
			opponentColor = 'w';
		}else {
			positionArray = getKingFileRank('w');
			opponentColor = 'b';
		}
		
		for(int i = 0; i < 8; i++) {
			for(int j = 0; j < 8; j++) {
				if(this.getPiece(i, j) != null && this.getPiece(i, j).color == opponentColor) {
					ChessPiece p = this.getPiece(i, j);
					currentPosition [0] = i;
					currentPosition [1] = j;
					result = p.checkHelper(currentPosition, positionArray, this);
					if(result) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * This method gets the fileRank of the King.
	 * @param color of the King whose fileRank is requested
	 * @return integer array which holds the position of the king on the board
	 */
	
	public int [] getKingFileRank(char color) {
		int [] positionArray = new int [2];
		for(int i = 0; i < 8; i++) {
			for(int j = 0; j < 8; j++) {
				if(color == 'b') {
					if(this.getPiece(i, j) != null && this.getPiece(i, j) instanceof King && this.getPiece(i,j).color == 'b') {
						positionArray[0] = i;
						positionArray[1] = j;
					}
				}else {
					if(this.getPiece(i, j) != null && this.getPiece(i, j)instanceof King && this.getPiece(i,j).color == 'w') {
						positionArray[0] = i;
						positionArray[1] = j;					}
				}
			}
		}
		return positionArray;
	}
	
	/**
	 * This method indicates whether the player makes a move that would lead to checkmate or stalemate.
	 * @param c color we are confirming to be in checkmate or stalemate
	 * @return boolean array that indicates whether or checkmate or stalemate is true or not
	 */
	
	public boolean [] isMate(char c){
		int [] currentPosition = new int[2];
		boolean [] arrayResult = new boolean [2];
		for(int i = 0; i < 8; i++) {
			for(int j = 0; j < 8; j++) {
				if(this.getPiece(i, j) != null && this.getPiece(i, j).color == c) {
					currentPosition[0] = i;
					currentPosition[1] = j;
					ArrayList<int[]> possibleMoves = this.getPiece(i, j).possibleSpaces(currentPosition, this);
					int lengthOfArray = possibleMoves.size();
					for(int k = 0; k < lengthOfArray; k++) {
						boolean result = this.getPiece(i, j).checkLegalMove(currentPosition, possibleMoves.get(k), this);
						if(result) {
							arrayResult [0] = false;
							arrayResult [1] = false;
							return arrayResult;
						}
					}
				}
			}
		}
		if(isCheck(c)){
			arrayResult [0] = true;
			arrayResult [1] = false;
			return arrayResult;
		}
		arrayResult [0] = false;
		arrayResult [1] = true;
		return arrayResult;
	}
	
	/**
	 * This is a getter method through you can obtain the position of the piece on the board.
	 * @param i the integer value of the column of the piece being requested
	 * @param j the integer value of the column of the piece being requested
	 * @return returns the piece on the board at the given location
	 */
	
	public ChessPiece getPiece(int i, int j){
		return board[i][j];
	}
	
	/**
	 * This is a setter method through you set a piece to a position.
	 * @param i integer value of the row where you want to set the piece
	 * @param j integer value of the column where you want to set the piece
	 * @param p which is the chess piece you want to set at position i,j
	 */
	public void setPiece(int i, int j, ChessPiece p){
		board[i][j] = p;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		cP = new ChessPiece[64];
		int k = 0;
		for(int i = 0; i<8; i++){
			for(int j = 0; j<8; j++){
				cP[k] = board[i][j];
				k++;
			}
		}
		dest.writeTypedArray(this.cP, flags);
	}

	protected ChessBoard(Parcel in) {
		this.cP = in.createTypedArray(ChessPiece.CREATOR);
		this.board = new ChessPiece [8][8];
		int k = 0;
		for(int i = 0; i<8; i++){
			for(int j = 0; j<8; j++){
				this.board[i][j] = cP[k];
				k++;
			}
		}
	}

	public static final Creator<ChessBoard> CREATOR = new Creator<ChessBoard>() {
		@Override
		public ChessBoard createFromParcel(Parcel source) {
			return new ChessBoard(source);
		}

		@Override
		public ChessBoard[] newArray(int size) {
			return new ChessBoard[size];
		}
	};
}
