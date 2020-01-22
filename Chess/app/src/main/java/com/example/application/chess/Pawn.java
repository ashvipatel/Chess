package com.example.application.chess;

import android.os.Parcel;

import java.util.ArrayList;

/**
 * Extends ChessPiece; Includes full implementation for Pawn piece
 * @author Nicole McGowan
 * @author Ashvi Patel
 *
 */
public class Pawn extends ChessPiece {
	
	/**
	 * Boolean for indicating if a Pawn is a valid en passant target
	 */
	boolean enPassant;
	
	/**
	 *  Boolean used for determining whether checkLegalMove/checkHelper are being called from move or from isCheck/isMate
	 */
	boolean moving;
	
	/**
	 * Boolean used to determine if a Pawn will perform en passant when it moves
	 */
	boolean capturePiece;
	
	/**
	 * Indicates the rank of the piece captured by en passant
	 */
	int enPassantRank;
	
	/**
	 * Indicates the file of the piece captured by en passant
	 */
	int enPassantFile;
	
	/**
	 * Boolean used to determine if move is being called from promotion method
	 */
	boolean promoting;
	
	/**
	 * Initializes variables to default and sets color
	 * @param c Color of piece
	 */
	public Pawn(char c){
		this.color = c;
		hasMoved = false;
		enPassant = false;
		moving = false;
		capturePiece = false;
		enPassantRank = -1;
		enPassantFile = -1;
		promoting = false;
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
		moving = true;
		answer[0] = checkLegalMove(start, end, board);
		if(answer[0] == false){
			if(capturePiece){
				capturePiece = false;
				enPassantRank = -1;
				enPassantFile = -1;
			}
			moving = false;
			return answer;
		}
		board.setPiece(end[0],end[1],board.getPiece(start[0],start[1]));
		board.setPiece(start[0],start[1],null);
		
		if(capturePiece){
			board.setPiece(enPassantRank,enPassantFile, null);
			capturePiece = false;
			enPassantRank = -1;
			enPassantFile = -1;
		}
		
		if(!promoting){
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
		}
		if(hasMoved == false){
			hasMoved = true;
		}
		
		if(enPassant && answer[0]){
			Pawn.enPassantReset(board);
			this.enPassant = true;
		}else{
			Pawn.enPassantReset(board);
		}
		
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
		if(color == 'b'){
			if(start[0]-end[0] == 1){
				if(start[1] == end[1] && board.getPiece(end[0],end[1])==null){
					return true;
				}else if(start[1]-end[1] == 1 || start[1]-end[1] == -1){
					ChessPiece p = board.getPiece(start[0], end[1]);
					if(p instanceof Pawn && p.color != this.color){
						if(((Pawn) p).enPassant){
							if(moving){
								capturePiece = true;
								enPassantRank = start[0];
								enPassantFile = end[1];
							}
							return true;
						}
					}
					if(board.getPiece(end[0],end[1])!=null){
						return true;
					}
				}else{
					return false;
				}
			}else if(start[0]-end[0] == 2 && start[1] == end[1] && hasMoved == false){
				if(board.getPiece(start[0]-1,start[1])!= null || board.getPiece(end[0],end[1])!= null){
					return false;
				}
				if(moving){
					enPassant = true;
				}
				return true;
			}else{
				return false;
			}
		}else{
			if(start[0]-end[0] == -1){
				if(start[1] == end[1] && board.getPiece(end[0],end[1])==null){
					return true;
				}else if(start[1]-end[1] == 1 || start[1]-end[1] == -1){
					ChessPiece p = board.getPiece(start[0], end[1]);
					if(p instanceof Pawn && p.color != this.color){
						if(((Pawn) p).enPassant){
							if(moving){
								capturePiece = true;
								enPassantRank = start[0];
								enPassantFile = end[1];
							}
							return true;
						}
					}
					if(board.getPiece(end[0],end[1])!=null){
						return true;
					}
				}else{
					return false;
				}
			}else if(start[0]-end[0] == -2 && start[1] == end[1] && hasMoved == false){
				if(board.getPiece(start[0]+1,start[1])!= null || board.getPiece(end[0],end[1])!= null){
					return false;
				}
				if(moving){
					enPassant = true;
				}
				return true;
			}else{
				return false;
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
	public ArrayList<int[]> possibleSpaces(int[] start, ChessBoard board) {
		ArrayList<int[]> a = new ArrayList<int[]>();
		if(color == 'b'){
			int [] b = new int [2];
			b[0] = start[0]-1;
			b[1] = start[1];
			a.add(b);
			if(!hasMoved){
				b = new int [2];
				b[0] = start[0]-2;
				b[1] = start[1];
				a.add(b);
			}
			if(start[1]-1>=0){
				b = new int [2];
				b[0] = start[0]-1;
				b[1] = start[1]-1;
				a.add(b);
			}
			if(start[1]+1<=7){
				b = new int [2];
				b[0] = start[0]-1;
				b[1] = start[1]+1;
				a.add(b);
			}
		}else{
			int [] b = new int [2];
			b[0] = start[0]+1;
			b[1] = start[1];
			a.add(b);
			if(!hasMoved){
				b = new int [2];
				b[0] = start[0]+2;
				b[1] = start[1];
				a.add(b);
			}
			if(start[1]-1>=0){
				b = new int [2];
				b[0] = start[0]+1;
				b[1] = start[1]-1;
				a.add(b);
			}
			if(start[1]+1<=7){
				b = new int [2];
				b[0] = start[0]+1;
				b[1] = start[1]+1;
				a.add(b);
			}
		}
		
		return a;
	}
	
	/**
	 * Calls move and if move is legal, will promote the pawn based on contents of n; returns same values as move
	 * @param start Start position of the piece to be moved
	 * @param end End position of the piece to be moved
	 * @param board Object representing state of board and used for calling Check and checking CheckMate/StaleMate condition
	 * @param n String representing piece that Pawn should be promoted to
	 * @return boolean array of four variables; First value is true if move is legal; Second value is true if opponent is in Check; Third value is true if opponent is in CheckMate; Fourth value is true if opponent is in Stalemate
	 */
	public boolean[] promotion(int[] start, int[] end, ChessBoard board, String n){
		promoting = true;
		boolean [] answer = move(start, end, board);
		if(answer[0]){
			ChessPiece p;
			if(n.equals("q")){
				p = new Queen(this.color);
			}else if(n.equals("n")){
				p = new Knight(this.color);
			}else if(n.equals("r")){
				p = new Rook(this.color);
			}else{
				p = new Bishop(this.color);
			}
			board.setPiece(end[0],end[1], p);
		}
		
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
		
		promoting = false;
		
		return answer;
	}
	
	/**
	 * Static method for implementing the conditions of en passant being valid for one move only
	 * @param board Object representing state of the board for use in accessing the ChessPieces
	 */
	public static void enPassantReset(ChessBoard board){
		ChessPiece p;
		for(int i = 0; i<=7; i++){
			for(int j= 0; j<=7; j++){
				p = board.getPiece(i,j);
				if(p instanceof Pawn){
					if(((Pawn) p).enPassant){
						((Pawn) p).enPassant = false;
					}
				}
			}
		}
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(CLASS_TYPE_SIX);
		super.writeToParcel(dest, flags);
		dest.writeByte(this.enPassant ? (byte) 1 : (byte) 0);
		dest.writeByte(this.moving ? (byte) 1 : (byte) 0);
		dest.writeByte(this.capturePiece ? (byte) 1 : (byte) 0);
		dest.writeInt(this.enPassantRank);
		dest.writeInt(this.enPassantFile);
		dest.writeByte(this.promoting ? (byte) 1 : (byte) 0);
	}

	protected Pawn(Parcel in) {
		super(in);
		this.enPassant = in.readByte() != 0;
		this.moving = in.readByte() != 0;
		this.capturePiece = in.readByte() != 0;
		this.enPassantRank = in.readInt();
		this.enPassantFile = in.readInt();
		this.promoting = in.readByte() != 0;
	}

	public static final Creator<Pawn> CREATOR = new Creator<Pawn>() {
		@Override
		public Pawn createFromParcel(Parcel source) {
			return new Pawn(source);
		}

		@Override
		public Pawn[] newArray(int size) {
			return new Pawn[size];
		}
	};
}
