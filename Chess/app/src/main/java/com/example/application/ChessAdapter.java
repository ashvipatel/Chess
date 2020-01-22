package com.example.application;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import com.example.application.chess.ChessPiece;
import com.example.application.chess.King;
import com.example.application.chess.Queen;
import com.example.application.chess.Bishop;
import com.example.application.chess.Knight;
import com.example.application.chess.Rook;
import com.example.application.chess.Pawn;
import android.widget.ImageView;


public class ChessAdapter extends BaseAdapter {

    private Context mContext;
    private GridView parent;

    public int count = 64;

    private int[] white = {R.drawable.whitekingblackbg, R.drawable.whitequeenblackbg, R.drawable.whitebishopblackbg, R.drawable.whiteknightblackbg,
                            R.drawable.whiterookblackbg, R.drawable.whitepawnblackbg, R.drawable.whitekingwhitebg, R.drawable.whitequeenwhitebg,
                            R.drawable.whitebishopwhitebg, R.drawable.whiteknightwhitebg, R.drawable.whiterookwhitebg, R.drawable.whitepawnwhitebg,};
    private int[] black = {R.drawable.blackkingblackbg, R.drawable.blackqueenblackbg, R.drawable.blackbishopblackbg, R.drawable.blackknightblackbg,
                            R.drawable.blackrookblackbg, R.drawable.blackpawnblackbg, R.drawable.blackkingwhitebg, R.drawable.blackqueenwhitebg,
                            R.drawable.blackbishopwhitebg, R.drawable.blackknightwhitebg, R.drawable.blackrookwhitebg, R.drawable.blackpawnwhitebg,};
    private int[] blank = {R.drawable.blackbg, R.drawable.whitebg};

    public ChessPiece [] board;
    public ChessAdapter(Context context, ChessPiece [][] chessBoard) {
        board = new ChessPiece[64];
        int k = 0;
        for(int i = 7; i >= 0; i--){
            for(int j = 0; j < 8; j++){
                board[k] = chessBoard [i][j];
                k++;
            }
        }
        mContext = context;
    }

    public ChessAdapter(Context c) {
        mContext = c;
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public void setParent(ViewGroup parent) {
        this.parent = (GridView)parent;
    }

    public boolean getBackgroundColor(int position){

        if((position >= 0 && position < 8) || (position >= 16 && position < 24) || (position >= 32 && position < 40) || (position >=48 && position < 56)){
            if(position % 2 == 0){
                return false; // Returns true for black background
            }
            else {
                return true;
            }
        }else {
            if(position % 2 == 0){
                return true;
            }
            else{
                return false;
            }
        }

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        ChessPiece piece = board[position];

        if (convertView == null) {  // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else{
            imageView = (ImageView) convertView;
        }

        if(piece == null){
            if(getBackgroundColor(position)){
                imageView.setImageResource(blank[0]);
            } else {
                imageView.setImageResource(blank[1]);
            }

        } else if(piece.color == 'w'){
            if(getBackgroundColor(position)){
                if(piece instanceof King){
                    imageView.setImageResource(white[0]);
                }
                if(piece instanceof Queen){
                    imageView.setImageResource(white[1]);
                }
                if(piece instanceof Bishop){
                    imageView.setImageResource(white[2]);
                }
                if(piece instanceof Knight){
                    imageView.setImageResource(white[3]);
                }
                if(piece instanceof Rook){
                    imageView.setImageResource(white[4]);
                }
                if(piece instanceof Pawn){
                    imageView.setImageResource(white[5]);
                }
            } else {
                if(piece instanceof King){
                    imageView.setImageResource(white[6]);
                }
                if(piece instanceof Queen){
                    imageView.setImageResource(white[7]);
                }
                if(piece instanceof Bishop){
                    imageView.setImageResource(white[8]);
                }
                if(piece instanceof Knight){
                    imageView.setImageResource(white[9]);
                }
                if(piece instanceof Rook){
                    imageView.setImageResource(white[10]);
                }
                if(piece instanceof Pawn){
                    imageView.setImageResource(white[11]);
                }
            }

        } else {
            if(getBackgroundColor(position)){
                if(piece instanceof King){
                    imageView.setImageResource(black[0]);
                }
                if(piece instanceof Queen){
                    imageView.setImageResource(black[1]);
                }
                if(piece instanceof Bishop){
                    imageView.setImageResource(black[2]);
                }
                if(piece instanceof Knight){
                    imageView.setImageResource(black[3]);
                }
                if(piece instanceof Rook){
                    imageView.setImageResource(black[4]);
                }
                if(piece instanceof Pawn){
                    imageView.setImageResource(black[5]);
                }
            }else {
                if(piece instanceof King){
                    imageView.setImageResource(black[6]);
                }
                if(piece instanceof Queen){
                    imageView.setImageResource(black[7]);
                }
                if(piece instanceof Bishop){
                    imageView.setImageResource(black[8]);
                }
                if(piece instanceof Knight){
                    imageView.setImageResource(black[9]);
                }
                if(piece instanceof Rook){
                    imageView.setImageResource(black[10]);
                }
                if(piece instanceof Pawn){
                    imageView.setImageResource(black[11]);
                }
            }
        }
        return imageView;
    }

}
