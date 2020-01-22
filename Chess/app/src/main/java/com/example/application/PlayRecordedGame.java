package com.example.application;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.example.application.chess.ChessBoard;
import com.example.application.chess.Pawn;

import java.util.ArrayList;

public class PlayRecordedGame extends AppCompatActivity {

    private Button next;
    private Button back;
    private GridView gridView;
    private TextView check;
    private TextView turn;

    private ArrayList<Integer> moves;
    private ChessBoard board;
    private int count;
    private boolean isWhiteTurn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.play_recorded_game);

        Bundle bundle = getIntent().getExtras();


        moves = bundle.getIntegerArrayList("moves");
        board = new ChessBoard();
        count = 0;
        isWhiteTurn = true;


        next = findViewById(R.id.nextButton);
        back = findViewById(R.id.backButton);
        gridView = findViewById(R.id.gridView);
        gridView.setAdapter(new ChessAdapter(this, board.board));
        check = findViewById(R.id.check);
        turn = findViewById(R.id.turn);
        turn.setText("White player's turn");

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(count == moves.size() || count == moves.size()-1){
                    Bundle bundle = new Bundle();
                    bundle.putString(ChessDialogFragment.MESSAGE_KEY, "Game over, please select back");
                    DialogFragment newFragment = new ChessDialogFragment();
                    newFragment.setArguments(bundle);
                    newFragment.show(getSupportFragmentManager(),"Dialog");
                    return;
                }else{
                    next();
                }

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void next(){
        int[] start = getIntArrayFromPosition(moves.get(count));
        count++;
        int[] end = getIntArrayFromPosition(moves.get(count));
        count++;
        boolean[] result;
        if(count < moves.size()-1 && moves.get(count)>=70){
            int position = moves.get(count);
            count++;
            String promoteTo = "q";
            if (position == 70) {
                promoteTo = "q";
            } else if (position == 71) {
                promoteTo = "r";
            } else if (position == 72) {
                promoteTo = "b";
            } else if (position == 73) {
                promoteTo = "n";
            }
            result = ((Pawn) board.getPiece(start[0],start[1])).promotion(start,end, board, promoteTo);
        }else{
            result =  board.getPiece(start[0],start[1]).move(start,end, board);
        }

        gridView.setAdapter(new ChessAdapter(this, board.board));

        if(result[2]){
            //Checkmate
            Bundle bundle = new Bundle();
            if(isWhiteTurn){
                check.setText("White player wins");
            }else{
                check.setText("Black player wins");
            }

        }else if(result[3]){
            //Stalemate
            check.setText("Stalemate");
        }else if(result[1]){
            if(isWhiteTurn){
                check.setText("Black player is in check");
            }else{
                check.setText("White player is in check");
            }
        }else{
            if(count == moves.size() || count == moves.size()-1){
                check.setText("Game over");
            }else{
                check.setText("");
            }
        }

        if(isWhiteTurn){
            turn.setText("Black player's turn");
            isWhiteTurn = false;
        }else{
            turn.setText("White player's turn");
            isWhiteTurn = true;
        }
    }

    private int[] getIntArrayFromPosition(int p){
        int[] a = new int[2];
        a[0] = 7 - Math.floorDiv(p,8);
        a[1] = p%8;
        return a;
    }
}
