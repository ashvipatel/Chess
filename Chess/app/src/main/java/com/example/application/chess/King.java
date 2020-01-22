package com.example.application.chess;

import android.os.Parcel;

import java.util.ArrayList;
/**
 * Extends ChessPiece; Includes full implementation for King piece
 * @author Nicole McGowan
 * @author Ashvi Patel
 *
 */
public class King extends ChessPiece{
	
	/**
	 * Boolean used for determining whether checkLegalMove/checkHelper are being called from move or from isCheck/isMate
	 */
	public boolean moving;
	
	/**
	 * Boolean used to indicate that the special move castling is being performed and the Rook needs to be moved too
	 */
	public boolean castling;
	
	/**
	 * Initializes variables to default and sets color
	 * @param c Color of piece
	 */
	public King(char c){
		this.color = c;
		hasMoved = false;
		moving = false;
		castling = false;
	}
	
	/**
	 * Moves a piece if it is legal to do so
	 * @param start Start position of the piece to be moved
	 * @param end End position of the piece to be moved
	 * @param board Object representing state of board and used for calling Check and checking CheckMate/StaleMate condition
	 * @return boolean array of four variables; First value is true if move is legal; Second value is true if opponent is in Check; Third value is true if opponent is in CheckMate; Fourth value is true if opponent is in Stalemate
	 */
	@Override
	public boolean [] move(int[] start, int[] end, ChessBoard board) {
		boolean [] answer = new boolean [4];
		moving = true;
		answer[0] = checkLegalMove(start, end, board);
		if(answer[0] == false){
			moving = false;
			castling = false;
			return answer;
		}
		board.setPiece(end[0],end[1],board.getPiece(start[0],start[1]));
		board.setPiece(start[0],start[1],null);
		
		if(castling){
			if(end[1]==6){
				board.setPiece(start[0],5,board.getPiece(start[0],7));
				board.setPiece(start[0],7,null);
			}else{
				board.setPiece(start[0],3,board.getPiece(start[0],0));
				board.setPiece(start[0],0,null);
			}
			castling = false;
		}
		boolean[] b;	
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
		if(!hasMoved){
			hasMoved = true;
		}
		
		Pawn.enPassantReset(board);
		
		moving = false;
		
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
			castling = false;
			return false;
		}
		ChessPiece p = board.getPiece(end[0],end[1]);
		board.setPiece(end[0],end[1],board.getPiece(start[0],start[1]));
		board.setPiece(start[0],start[1],null);
		if(castling){
			if(end[1]==6){
				board.setPiece(start[0],5,board.getPiece(start[0],7));
				board.setPiece(start[0],7,null);
			}else{
				board.setPiece(start[0],3,board.getPiece(start[0],0));
				board.setPiece(start[0],0,null);
			}
		}
		if(board.isCheck(color)){
			answer = false;
		}
		board.setPiece(start[0],start[1],board.getPiece(end[0],end[1]));
		board.setPiece(end[0],end[1],p);
		if(castling){
			if(end[1]==6){
				board.setPiece(start[0],7,board.getPiece(start[0],5));
				board.setPiece(start[0],5,null);
			}else{
				board.setPiece(start[0],0,board.getPiece(start[0],3));
				board.setPiece(start[0],3,null);
			}
		}
		if(!moving){
			castling = false;
		}
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
		if(Math.abs(start[0]-end[0])<=1 && Math.abs(start[1]-end[1])<=1){
			return true;
		}else if(start[0]-end[0] == 0 && Math.abs(start[1]-end[1])==2){
			if(hasMoved){
				return false;
			}
			
			castling = true;
			
			ChessPiece p;
			if(end[1] == 6){
				p = board.getPiece(start[0], 7);
				if(p instanceof Rook){
					if(((Rook) p).hasMoved){
						return false;
					}
					if(board.getPiece(start[0],5)!=null && board.getPiece(start[0],6)!=null){
						castling = false;
						return false;
					}
					if(board.isCheck(this.color)){
						return false;
					}
					board.setPiece(start[0],5,board.getPiece(start[0],4));
					board.setPiece(start[0],4,null);
					if(board.isCheck(this.color)){
						board.setPiece(start[0],4,board.getPiece(start[0],5));
						board.setPiece(start[0],5,null);
						return false;
					}
					board.setPiece(start[0],6,board.getPiece(start[0],5));
					board.setPiece(start[0],5,null);
					if(board.isCheck(this.color)){
						board.setPiece(start[0],4,board.getPiece(start[0],6));
						board.setPiece(start[0],6,null);
						return false;
					}else{
						board.setPiece(start[0],4,board.getPiece(start[0],6));
						board.setPiece(start[0],6,null);
						return true;
					}
				}
			}else{
				p = board.getPiece(start[0], 0);
				if(p instanceof Rook){
					if(((Rook) p).hasMoved){
						return false;
					}
					if(board.getPiece(start[0],3)!=null && board.getPiece(start[0],2)!=null){
						castling = false;
						return false;
					}
					if(board.isCheck(this.color)){
						return false;
					}
					board.setPiece(start[0],3,board.getPiece(start[0],4));
					board.setPiece(start[0],4,null);
					if(board.isCheck(this.color)){
						board.setPiece(start[0],4,board.getPiece(start[0],3));
						board.setPiece(start[0],3,null);
						return false;
					}
					board.setPiece(start[0],2,board.getPiece(start[0],3));
					board.setPiece(start[0],3,null);
					if(board.isCheck(this.color)){
						board.setPiece(start[0],4,board.getPiece(start[0],2));
						board.setPiece(start[0],2,null);
						return false;
					}else{
						board.setPiece(start[0],4,board.getPiece(start[0],2));
						board.setPiece(start[0],2,null);
						return true;
					}
				}
			}
			
		}
		
		return false;
	}
	
	/**
	 * Returns an ArrayList of pseudo-legal moves for use in evaluating CheckMate/StaleMate
	 * @param start Start position of the piece to be evaluated
	 * @param board Object representing state of board and used for calling Check and implementing special moves
	 * @return ArrayList; Contains all pseudo-legal moves for use in evaluating CheckMate/StaleMate
	 */
	@Override
	public ArrayList<int[]> possibleSpaces(int [] start, ChessBoard board) {
		ArrayList<int[]> a = new ArrayList<int[]>();
		if(start[0]-1>=0){
			int [] b = new int[2];
			b[0] = start[0]-1;
			b[1] = start[1];
			a.add(b);
			if(start[1]-1>=0){
				b = new int[2];
				b[0] = start[0]-1;
				b[1] = start[1]-1;
				a.add(b);
			}
			if(start[1]+1<=7){
				b = new int[2];
				b[0] = start[0]-1;
				b[1] = start[1]+1;
				a.add(b);
			}
		}
		if(start[1]-1>=0){
			int [] b = new int[2];
			b[0] = start[0];
			b[1] = start[1]-1;
			a.add(b);
		}
		if(start[1]+1<=7){
			int [] b = new int[2];
			b[0] = start[0];
			b[1] = start[1]+1;
			a.add(b);
		}
		if(start[0]+1<=7){
			int [] b = new int[2];
			b[0] = start[0]+1;
			b[1] = start[1];
			a.add(b);
			if(start[1]-1>=0){
				b = new int[2];
				b[0] = start[0]+1;
				b[1] = start[1]-1;
				a.add(b);
			}
			if(start[1]+1<=7){
				b = new int[2];
				b[0] = start[0]+1;
				b[1] = start[1]+1;
				a.add(b);
			}
		}
		if(!hasMoved){ 
			int [] b = new int[2];
			b[0] = start[0];
			b[1] = 2;
			a.add(b);
			b = new int[2];
			b[0] = start[0];
			b[1] = 6;
			a.add(b);
		}
		
		return a;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(CLASS_TYPE_ONE);
		super.writeToParcel(dest, flags);
		dest.writeByte(this.moving ? (byte) 1 : (byte) 0);
		dest.writeByte(this.castling ? (byte) 1 : (byte) 0);
	}

	protected King(Parcel in) {
		super(in);
		this.moving = in.readByte() != 0;
		this.castling = in.readByte() != 0;
	}

	public static final Creator<King> CREATOR = new Creator<King>() {
		@Override
		public King createFromParcel(Parcel source) {
			return new King(source);
		}

		@Override
		public King[] newArray(int size) {
			return new King[size];
		}
	};
}
