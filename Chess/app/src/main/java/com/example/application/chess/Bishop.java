package com.example.application.chess;

import android.os.Parcel;

import java.util.ArrayList;

/**
 * Extends ChessPiece; Includes full implementation for Bishop piece
 * @author Nicole McGowan
 * @author Ashvi Patel
 *
 */
public class Bishop extends ChessPiece {

	/**
	 * Initializes variables to default and sets color
	 * @param c Color of piece
	 */
	public Bishop(char c){
		this.color = c;
	}
	
	/**
	 * Moves a piece if it is legal to do so
	 * @param start Start position of the piece to be moved
	 * @param end End position of the piece to be moved
	 * @param board Object representing state of board and used for calling Check and checking CheckMate/StaleMate condition
	 * @return boolean array of four variables; First value is true if move is legal; Second value is true if opponent is in Check; Third value is true if opponent is in CheckMate; Fourth value is true if opponent is in Stalemate
	 */
	@Override
	public boolean[] move(int[] start, int[] end, ChessBoard board) {
		boolean [] answer = new boolean [4];
		answer[0] = checkLegalMove(start, end, board);
		if(answer[0] == false){
			return answer;
		}
		board.setPiece(end[0],end[1],board.getPiece(start[0],start[1]));
		board.setPiece(start[0],start[1],null);
		
		boolean [] b;
		if(color == 'b'){
			answer[1] = board.isCheck('w');
			b = board.isMate('w');
			answer[2] = b[0];
			answer[3] = b[1];
		}else{
			answer[1] = board.isCheck('b');
			b = board.isMate('b');
			answer[2] = b[0];
			answer[3] = b[1];
		}
		
		Pawn.enPassantReset(board);
		
		return answer;
	}

	/**
	 * Calls checkHelper; if that is true, determines if a move would cause Check
	 * @param start Start position of the piece to be evaluated
	 * @param end End position of the piece to be evaluated
	 * @param board Object representing state of board and used for calling Check and checking CheckMate/StaleMate condition
	 * @return boolean; True if move is legal
	 */
	@Override
	public boolean checkLegalMove(int[] start, int[] end, ChessBoard board) {
		boolean answer = checkHelper(start, end, board);
		if(answer == false){
			return false;
		}
		ChessPiece p = board.getPiece(end[0],end[1]);
		board.setPiece(end[0],end[1],board.getPiece(start[0],start[1]));
		board.setPiece(start[0],start[1],null);
		if(board.isCheck(color)){
			answer = false;
		}
		board.setPiece(start[0],start[1],board.getPiece(end[0],end[1]));
		board.setPiece(end[0],end[1],p);
		return answer;
	}

	/**
	 * Evaluates all conditions for a legal move except the Check condition
	 * @param start Start position of the piece to be evaluated
	 * @param end End position of the piece to be evaluated
	 * @param board Object representing state of board and used for calling Check and checking CheckMate/StaleMate condition
	 * @return boolean; True if move is legal
	 */
	@Override
	public boolean checkHelper(int[] start, int[] end, ChessBoard board) {
		if(board.getPiece(end[0],end[1])!= null && board.getPiece(end[0],end[1]).color == this.color){
			return false;
		}
		if(Math.abs(start[0]-end[0]) != Math.abs(start[1]-end[1])){
			return false;
		}
		
		if(start[0]<end[0] && start[1]<end[1]){
			int j = start[1]+1;
			for(int i = start[0]+1; i<end[0]; i++){
				if(board.getPiece(i, j)!=null){
					return false;
				}
				j++;
			}
		}else if(start[0]<end[0] && start[1]>end[1]){
			int j = start[1]-1;
			for(int i = start[0]+1; i<end[0]; i++){
				if(board.getPiece(i, j)!=null){
					return false;
				}
				j--;
			}
		}else if(start[0]>end[0] && start[1]<end[1]){
			int j = start[1]+1;
			for(int i = start[0]-1; i>end[0]; i--){
				if(board.getPiece(i, j)!=null){
					return false;
				}
				j++;
			}
		}else{
			int j = start[1]-1;
			for(int i = start[0]-1; i>end[0]; i--){
				if(board.getPiece(i, j)!=null){
					return false;
				}
				j--;
			}
		}
		
		return true;
	}

	/**
	 * Returns an ArrayList of pseudo-legal moves for use in evaluating CheckMate/StaleMate
	 * @param start Start position of the piece to be evaluated
	 * @param board Object representing state of board and used for calling Check and implementing special moves
	 * @return ArrayList; Contains all pseudo-legal moves for use in evaluating CheckMate/StaleMate
	 */
	@Override
	public ArrayList<int[]> possibleSpaces(int[] start, ChessBoard board) {
		ArrayList<int[]> a = new ArrayList<int[]>();
		int i = start[0]-1;
		int j = start[1]-1;
		while(i >= 0 && j>=0){
			int [] b = new int[2];
			b[0] = i;
			b[1] = j;
			a.add(b);
			i--;
			j--;
		}
		
		i = start[0]+1;
		j = start[1]+1;
		while(i <=7 && j<=7){
			int [] b = new int[2];
			b[0] = i;
			b[1] = j;
			a.add(b);
			i++;
			j++;
		}
		
		i = start[0]+1;
		j = start[1]-1;
		while(i <=7 && j>=0){
			int [] b = new int[2];
			b[0] = i;
			b[1] = j;
			a.add(b);
			i++;
			j--;
		}
		
		i = start[0]-1;
		j = start[1]+1;
		while(i >= 0 && j<=7){
			int [] b = new int[2];
			b[0] = i;
			b[1] = j;
			a.add(b);
			i--;
			j++;
		}
		
		return a;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(CLASS_TYPE_FOUR);
		super.writeToParcel(dest, flags);
	}

	protected Bishop(Parcel in) {
		super(in);
	}

	public static final Creator<Bishop> CREATOR = new Creator<Bishop>() {
		@Override
		public Bishop createFromParcel(Parcel source) {
			return new Bishop(source);
		}

		@Override
		public Bishop[] newArray(int size) {
			return new Bishop[size];
		}
	};
}
