package com.example.application.chess;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 *  Abstract class ChessPiece - Super class for the six individual pieces: King, Queen, Rook, Knight, Bishop and Pawn
 * @author Nicole McGowan
 * @author Ashvi Patel
 *
 */
public abstract class ChessPiece implements Parcelable {
	
	/**
	 * Either 'b' or 'w' to determine color of ChessPiece
	 */
	public char color;
	
	/**
	 * Used for determining if a piece has moved from its starting position for evaluating special moves such as castling
	 */
	public boolean hasMoved;

	public static final int CLASS_TYPE_ONE = 1;
	public static final int CLASS_TYPE_TWO = 2;
	public static final int CLASS_TYPE_THREE = 3;
	public static final int CLASS_TYPE_FOUR = 4;
	public static final int CLASS_TYPE_FIVE = 5;
	public static final int CLASS_TYPE_SIX = 6;
	
	/**
	 * Moves a piece if it is legal to do so
	 * @param start Start position of the piece to be moved
	 * @param end End position of the piece to be moved
	 * @param board Object representing state of board and used for calling Check and checking CheckMate/StaleMate condition
	 * @return boolean array of four variables; First value is true if move is legal; Second value is true if opponent is in Check; Third value is true if opponent is in CheckMate; Fourth value is true if opponent is in Stalemate
	 */
	public abstract boolean [] move(int [] start, int [] end, ChessBoard board);
	
	/**
	 * Calls checkHelper; if that is true, determines if a move would cause Check
	 * @param start Start position of the piece to be evaluated
	 * @param end End position of the piece to be evaluated
	 * @param board Object representing state of board and used for calling Check and checking CheckMate/StaleMate condition
	 * @return boolean; True if move is legal
	 */
	public abstract boolean checkLegalMove(int [] start, int [] end, ChessBoard board);
	
	/**
	 * Evaluates all conditions for a legal move except the Check condition
	 * @param start Start position of the piece to be evaluated
	 * @param end End position of the piece to be evaluated
	 * @param board Object representing state of board and used for calling Check and checking CheckMate/StaleMate condition
	 * @return boolean; True if move is legal
	 */
	public abstract boolean checkHelper(int [] start, int [] end, ChessBoard board);
	
	/**
	 * Returns an ArrayList of pseudo-legal moves for use in evaluating CheckMate/StaleMate
	 * @param start Start position of the piece to be evaluated
	 * @param board Object representing state of board and used for calling Check and implementing special moves
	 * @return ArrayList; Contains all pseudo-legal moves for use in evaluating CheckMate/StaleMate
	 */
	public abstract ArrayList<int[]> possibleSpaces(int [] start, ChessBoard board);

	public ChessPiece() {
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.color);
		dest.writeByte(this.hasMoved ? (byte) 1 : (byte) 0);
	}

	protected ChessPiece(Parcel in) {
		this.color = (char) in.readInt();
		this.hasMoved = in.readByte() != 0;
	}

	public static final Creator<ChessPiece> CREATOR = new Creator<ChessPiece>() {
		@Override
		public ChessPiece createFromParcel(Parcel source) {
			return ChessPiece.getConcreteClass(source);
		}

		@Override
		public ChessPiece[] newArray(int size) {
			return new ChessPiece[size];
		}
	};

	public static ChessPiece getConcreteClass(Parcel source) {

		switch (source.readInt()) {
			case CLASS_TYPE_ONE:
				return new King(source);
			case CLASS_TYPE_TWO:
				return new Queen(source);
			case CLASS_TYPE_THREE:
				return new Rook(source);
			case CLASS_TYPE_FOUR:
				return new Bishop(source);
			case CLASS_TYPE_FIVE:
				return new Knight(source);
			case CLASS_TYPE_SIX:
				return new Pawn(source);
			default:
				return null;
		}
	}
}