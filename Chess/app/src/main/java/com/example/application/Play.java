package com.example.application;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.application.chess.ChessBoard;
import com.example.application.chess.ChessPiece;
import com.example.application.chess.King;
import com.example.application.chess.Pawn;

import java.util.ArrayList;


public class Play extends AppCompatActivity implements DrawDialogFragment.DrawDialogListener, PromotionDialogFragment.PromotionDialogListener {

    private GridView gridView;
    private Button resign;
    private Button draw;
    private Button ai;
    private Button undo;
    private TextView check;
    private TextView turn;
    private Spinner promotionSpinner;


    private ChessBoard chessBoard;
    private ArrayList<Integer> allMoves;
    private boolean canUndo;
    private ChessPiece lastCapturedPiece;
    private Integer lastCapturedPosition;
    private String[] spinnerList = {"Select","Queen","Rook","Bishop","Knight"};
    private boolean firstSelect;

    private boolean pieceSelected;
    private boolean isWhiteTurn;
    private boolean promotion = false;
    private String promoteTo;
    private int[] promotionStart;
    private int[] promotionEnd;
    private boolean enpassant = false;
    private boolean castling = false;
    private boolean firstMove = false;


    public static final String ALL_MOVES = "all_moves";
    public static final String ACTION = "action";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_screen);

        Bundle bundle = getIntent().getExtras();
        String action = bundle.getString(ACTION);



        chessBoard = new ChessBoard();
        allMoves = new ArrayList<Integer>();
        canUndo = false;
        lastCapturedPiece = null;
        lastCapturedPosition = -1;
        pieceSelected = false;
        isWhiteTurn = true;



        gridView = findViewById(R.id.board);
        gridView.setAdapter(new ChessAdapter(this, chessBoard.board));

        resign = findViewById(R.id.resignButton);
        draw = findViewById(R.id.drawButton);
        ai = findViewById(R.id.aiButton);
        undo = findViewById(R.id.undoButton);

        promotionSpinner = findViewById(R.id.promotionSpinner);

        check = findViewById(R.id.inCheckText);
        turn = findViewById(R.id.playerTurnText);
        turn.setText("White player's turn");

        if(!pieceSelected) {
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    selectPiece(position);
                }
            });
        }else{
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    selectDestination(position);
                }
            });
        }

        resign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resign();
            }
        });

        draw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                draw();
            }
        });

        ai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ai();
            }
        });

        undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                undo();
            }
        });

    }

    private void promotion(){
        promotionSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, spinnerList));
        promotionSpinner.setVisibility(View.VISIBLE);
        firstSelect = false;

        promotionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView <?> adapterView, View view, int position, long itemID) {
                if(firstSelect) {
                    if (position >= 0 && position < spinnerList.length) {
                        if (position == 1) {
                            promoteTo = "q";
                        } else if (position == 2) {
                            promoteTo = "r";
                        } else if (position == 3) {
                            promoteTo = "b";
                        } else if (position == 4) {
                            promoteTo = "n";
                        } else {
                            promoteTo = "q";
                        }
                        reactivate();
                    } else {
                        Toast.makeText(Play.this, "Selected Category Does not Exist!", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    firstSelect = true;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                deactivated();
            }
        });


        resign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deactivated();
            }
        });

        draw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deactivated();
            }
        });

        ai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deactivated();
            }
        });

        undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deactivated();
            }
        });

    }

    private void deactivated(){
        Bundle bundle = new Bundle();
        bundle.putString(ChessDialogFragment.MESSAGE_KEY, "Please select promotion type first");
        DialogFragment newFragment = new ChessDialogFragment();
        newFragment.setArguments(bundle);
        newFragment.show(getSupportFragmentManager(),"Dialog");
        return;
    }

    private void reactivate(){
        promotionSpinner.setVisibility(View.INVISIBLE);

        boolean[] results;
        results = ((Pawn)chessBoard.getPiece(promotionStart[0],promotionStart[1])).promotion(promotionStart,promotionEnd,chessBoard,promoteTo);

        gridView.setAdapter(new ChessAdapter(this, chessBoard.board));

        allMoves.add(getPositionFromIntArray(promotionEnd));
        if (promoteTo.equals("q")) {
            allMoves.add(70);
        }else if (promoteTo.equals("r")) {
            allMoves.add(71);
        }else if(promoteTo.equals("b")){
            allMoves.add(72);
        }else{
            allMoves.add(73);
        }
        canUndo = true;
        if(results[2]){
            //Checkmate
            Bundle bundle = new Bundle();
            if(isWhiteTurn) {
                bundle.putString(ACTION, "bc");
            }else{
                bundle.putString(ACTION, "wc");
            }
            bundle.putIntegerArrayList(ALL_MOVES,allMoves);
            Intent intent = new Intent(getApplicationContext(),Results.class);
            intent.putExtras(bundle);
            startActivityForResult(intent,MainActivity.GAME_CODE);
        }else if(results[3]){
            //Stalemate
            Bundle bundle = new Bundle();
            bundle.putString(ACTION,"s");
            bundle.putIntegerArrayList(ALL_MOVES,allMoves);
            Intent intent = new Intent(getApplicationContext(),Results.class);
            intent.putExtras(bundle);
            startActivityForResult(intent,MainActivity.GAME_CODE);
        }else if(results[1]){
            if(isWhiteTurn){
                check.setText("Black player is in check");
            }else{
                check.setText("White player is in check");
            }
        }else{
            check.setText("");
        }

        pieceSelected = false;
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                selectPiece(position);
            }
        });
        if(isWhiteTurn){
            isWhiteTurn = false;
            turn.setText("Black player's turn");
        }else{
            isWhiteTurn = true;
            turn.setText("White player's turn");
        }


        resign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resign();
            }
        });

        draw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                draw();
            }
        });

        ai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ai();
            }
        });

        undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                undo();
            }
        });
    }

    private void selectPiece(int p){
        ChessPiece cP = ((ChessAdapter)gridView.getAdapter()).board[p];
        if(cP == null){
            Bundle bundle = new Bundle();
            bundle.putString(ChessDialogFragment.MESSAGE_KEY, "Cannot select blank space");
            DialogFragment newFragment = new ChessDialogFragment();
            newFragment.setArguments(bundle);
            newFragment.show(getSupportFragmentManager(),"Dialog");
            return;
        }else if((isWhiteTurn && cP.color == 'b')||(!isWhiteTurn && cP.color == 'w')){
            Bundle bundle = new Bundle();
            bundle.putString(ChessDialogFragment.MESSAGE_KEY, "Wrong color piece selected");
            DialogFragment newFragment = new ChessDialogFragment();
            newFragment.setArguments(bundle);
            newFragment.show(getSupportFragmentManager(),"Dialog");
            return;
        }else{
            boolean legalMove = false;
            ArrayList<int[]> possibleMoves = cP.possibleSpaces(getIntArrayFromPosition(p), chessBoard);
            int lengthOfArray = possibleMoves.size();
            for(int k = 0; k < lengthOfArray; k++) {
                boolean result = cP.checkLegalMove(getIntArrayFromPosition(p), possibleMoves.get(k), chessBoard);
                if(result) {
                    legalMove = true;
                    break;
                }
            }
            if(legalMove){
                View view = gridView.getChildAt(p);
                view.setBackgroundColor(Color.parseColor("#FF008080"));
                allMoves.add(p);
                pieceSelected = true;
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                        selectDestination(position);
                    }
                });
            }else{
                Bundle bundle = new Bundle();
                bundle.putString(ChessDialogFragment.MESSAGE_KEY, "This piece has no legal moves");
                DialogFragment newFragment = new ChessDialogFragment();
                newFragment.setArguments(bundle);
                newFragment.show(getSupportFragmentManager(),"Dialog");
                return;
            }
        }

    }

    private void selectDestination(int p){

        if(allMoves.size() != 0 && p == allMoves.get(allMoves.size()-1)){
            allMoves.remove(allMoves.size()-1);
            pieceSelected = false;
            View view = gridView.getChildAt(p);
            view.setBackgroundColor(Color.parseColor("#00008080"));
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    selectPiece(position);
                }
            });
            return;
        }
        int [] start = getIntArrayFromPosition(allMoves.get(allMoves.size()-1));
        int [] end = getIntArrayFromPosition(p);

        boolean legal = chessBoard.getPiece(start[0],start[1]).checkLegalMove(start,end,chessBoard);
        if(!legal) {
            Bundle bundle = new Bundle();
            bundle.putString(ChessDialogFragment.MESSAGE_KEY, "Not a legal move");
            DialogFragment newFragment = new ChessDialogFragment();
            newFragment.setArguments(bundle);
            newFragment.show(getSupportFragmentManager(),"Dialog");
            return;
        }
        if(!checkForSpecialMoves(start,end)) {
            lastCapturedPiece = chessBoard.getPiece(end[0], end[1]);
            lastCapturedPosition = p;
        }
        if(!chessBoard.getPiece(start[0],start[1]).hasMoved){
            firstMove = true;
        }else{
            firstMove = false;
        }
        boolean[] results;
        if(promotion){
            promotionStart = start;
            promotionEnd = end;
            promotion();
            return;
        }else{
            results = chessBoard.getPiece(start[0],start[1]).move(start,end,chessBoard);
        }


        gridView.setAdapter(new ChessAdapter(this, chessBoard.board));

        allMoves.add(p);
        canUndo = true;
        if(results[2]){
            //Checkmate
            Bundle bundle = new Bundle();
            if(isWhiteTurn) {
                bundle.putString(ACTION, "bc");
            }else{
                bundle.putString(ACTION, "wc");
            }
            bundle.putIntegerArrayList(ALL_MOVES,allMoves);
            Intent intent = new Intent(getApplicationContext(),Results.class);
            intent.putExtras(bundle);
            startActivityForResult(intent,MainActivity.GAME_CODE);
        }else if(results[3]){
            //Stalemate
            Bundle bundle = new Bundle();
            bundle.putString(ACTION,"s");
            bundle.putIntegerArrayList(ALL_MOVES,allMoves);
            Intent intent = new Intent(getApplicationContext(),Results.class);
            intent.putExtras(bundle);
            startActivityForResult(intent,MainActivity.GAME_CODE);
        }else if(results[1]){
            if(isWhiteTurn){
                check.setText("Black player is in check");
            }else{
                check.setText("White player is in check");
            }
        }else{
            check.setText("");
        }

        pieceSelected = false;
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                selectPiece(position);
            }
        });
        if(isWhiteTurn){
            isWhiteTurn = false;
            turn.setText("Black player's turn");
        }else{
            isWhiteTurn = true;
            turn.setText("White player's turn");
        }


    }

    private int[] getIntArrayFromPosition(int p){
        int[] a = new int[2];
        a[0] = 7 - Math.floorDiv(p,8);
        a[1] = p%8;
        return a;
    }

    private Integer getPositionFromIntArray(int[] a){
        Integer p = ((7 - a[0])*8)+a[1];
        return p;
    }

    private void resign(){
        Bundle bundle = new Bundle();
        if(isWhiteTurn){
            bundle.putString(ACTION,"wr");
        }else{
            bundle.putString(ACTION,"br");
        }
        bundle.putIntegerArrayList(ALL_MOVES,allMoves);
        Intent i = new Intent(getApplicationContext(), Results.class);
        i.putExtras(bundle);
        startActivityForResult(i,MainActivity.GAME_CODE);
    }

    private void draw(){
        Bundle bundle = new Bundle();
        bundle.putString(DrawDialogFragment.MESSAGE_KEY, "Would you like to accept the draw?");
        DialogFragment newFragment = new DrawDialogFragment();
        newFragment.setArguments(bundle);
        newFragment.show(getSupportFragmentManager(),"Dialog");
    }

    public void onDialogPositiveClick(DialogFragment dialog){
        Bundle bundle = new Bundle();
        bundle.putString(ACTION,"d");
        bundle.putIntegerArrayList(ALL_MOVES,allMoves);
        Intent i = new Intent(getApplicationContext(), Results.class);
        i.putExtras(bundle);
        startActivityForResult(i,MainActivity.GAME_CODE);
    }

    public void onDialogNegativeClick(DialogFragment dialog){}

    public void onDialogClick(DialogFragment dialog){
        this.promoteTo = ((PromotionDialogFragment)dialog).promoteTo;
    }

    private void ai() {
        int [] currentPosition = new int[2];
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                if(chessBoard.getPiece(i, j) != null && ((isWhiteTurn && chessBoard.getPiece(i, j).color == 'w') || (!isWhiteTurn && chessBoard.getPiece(i,j).color == 'b'))) {
                    currentPosition[0] = i;
                    currentPosition[1] = j;
                    ArrayList<int[]> possibleMoves = chessBoard.getPiece(i, j).possibleSpaces(currentPosition, chessBoard);
                    int lengthOfArray = possibleMoves.size();
                    for(int k = 0; k < lengthOfArray; k++) {
                        boolean result = chessBoard.getPiece(i, j).checkLegalMove(currentPosition, possibleMoves.get(k), chessBoard);
                        if(result) {
                            if(!checkForSpecialMoves(currentPosition,possibleMoves.get(k))){
                                lastCapturedPiece = chessBoard.getPiece(possibleMoves.get(k)[0],possibleMoves.get(k)[1]);
                                lastCapturedPosition = getPositionFromIntArray(possibleMoves.get(k));
                            }
                            if(!chessBoard.getPiece(currentPosition[0],currentPosition[1]).hasMoved){
                                firstMove = true;
                            }else{
                                firstMove = false;
                            }

                            allMoves.add(getPositionFromIntArray(currentPosition));
                            allMoves.add(getPositionFromIntArray(possibleMoves.get(k)));

                            boolean[] results;
                            if(promotion){
                                results = ((Pawn)chessBoard.getPiece(i,j)).promotion(currentPosition,possibleMoves.get(k),chessBoard,"q");
                                allMoves.add(70);
                            }else{
                                results = chessBoard.getPiece(i,j).move(currentPosition,possibleMoves.get(k),chessBoard);
                            }



                            canUndo = true;

                            if(results[2]){
                                //Checkmate
                                Bundle bundle = new Bundle();
                                if(isWhiteTurn){
                                    bundle.putString(ACTION,"bc");
                                }else{
                                    bundle.putString(ACTION,"wc");
                                }
                                bundle.putIntegerArrayList(ALL_MOVES,allMoves);
                                Intent intent = new Intent(getApplicationContext(),Results.class);
                                intent.putExtras(bundle);
                                startActivityForResult(intent,MainActivity.GAME_CODE);
                            }else if(results[3]){
                                //Stalemate
                                Bundle bundle = new Bundle();
                                bundle.putString(ACTION,"s");
                                bundle.putIntegerArrayList(ALL_MOVES,allMoves);
                                Intent intent = new Intent(getApplicationContext(),Results.class);
                                intent.putExtras(bundle);
                                startActivityForResult(intent,MainActivity.GAME_CODE);
                            }else if(results[1]){
                                if(isWhiteTurn){
                                    check.setText("Black player is in check");
                                }else{
                                    check.setText("White player is in check");
                                }
                            }else{
                                check.setText("");
                            }

                            pieceSelected = false;
                            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                                    selectPiece(position);
                                }
                            });
                            if(isWhiteTurn){
                                isWhiteTurn = false;
                                turn.setText("Black player's turn");
                            }else{
                                isWhiteTurn = true;
                                turn.setText("White player's turn");
                            }
                            gridView.setAdapter(new ChessAdapter(this, chessBoard.board));
                            return;
                        }
                    }
                }
            }
        }
    }

    private void undo(){
        if(allMoves.size() == 0 || allMoves.size() == 1){
            Bundle bundle = new Bundle();
            bundle.putString(ChessDialogFragment.MESSAGE_KEY, "No moves to undo");
            DialogFragment newFragment = new ChessDialogFragment();
            newFragment.setArguments(bundle);
            newFragment.show(getSupportFragmentManager(),"Dialog");
            return;
        }else if(!canUndo) {
            Bundle bundle = new Bundle();
            bundle.putString(ChessDialogFragment.MESSAGE_KEY, "Cannot undo more than one move in a row");
            DialogFragment newFragment = new ChessDialogFragment();
            newFragment.setArguments(bundle);
            newFragment.show(getSupportFragmentManager(),"Dialog");
            return;
        }
        else{
            if(pieceSelected){
                allMoves.remove(allMoves.size()-1);
            }
            if(allMoves.get(allMoves.size()-1)>=70){
                allMoves.remove(allMoves.size()-1);
            }
            Integer s = allMoves.remove(allMoves.size()-1);
            Integer e = allMoves.remove(allMoves.size()-1);
            int [] start = getIntArrayFromPosition(s);
            int [] end = getIntArrayFromPosition(e);
            if(enpassant || promotion || castling){
                if(enpassant){
                    chessBoard.setPiece(end[0],end[1],chessBoard.getPiece(start[0],start[1]));
                    chessBoard.setPiece(start[0],start[1],null);
                    int [] last = getIntArrayFromPosition(lastCapturedPosition);
                    chessBoard.setPiece(last[0],last[1],lastCapturedPiece);
                }else if(promotion){
                    chessBoard.setPiece(end[0],end[1],new Pawn(chessBoard.getPiece(start[0],start[1]).color));
                    chessBoard.getPiece(end[0],end[1]).hasMoved = true;
                    chessBoard.setPiece(start[0],start[1],lastCapturedPiece);
                }else if(castling){
                    chessBoard.setPiece(end[0],end[1],chessBoard.getPiece(start[0],start[1]));
                    chessBoard.setPiece(start[0],start[1],null);
                    chessBoard.getPiece(end[0],end[1]).hasMoved = false;
                    if(start[1] == 2){
                        chessBoard.setPiece(end[0],0,chessBoard.getPiece(start[0],3));
                        chessBoard.setPiece(start[0],3,null);
                        chessBoard.getPiece(end[0],0).hasMoved = false;
                    }else{
                        chessBoard.setPiece(end[0],7,chessBoard.getPiece(start[0],5));
                        chessBoard.setPiece(start[0],5,null);
                        chessBoard.getPiece(end[0],7).hasMoved = false;
                    }
                }
            }else{
                chessBoard.setPiece(end[0],end[1],chessBoard.getPiece(start[0],start[1]));
                if(firstMove){
                    chessBoard.getPiece(end[0],end[1]).hasMoved = false;
                }
                if(lastCapturedPiece == null){
                    chessBoard.setPiece(start[0],start[1],null);
                }else{
                    chessBoard.setPiece(start[0],start[1], lastCapturedPiece);
                }
            }

            canUndo = false;
            gridView.setAdapter(new ChessAdapter(this, chessBoard.board));
            pieceSelected = false;
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    selectPiece(position);
                }
            });

            if(isWhiteTurn){
                isWhiteTurn = false;
                turn.setText("Black player's turn");
            }else{
                isWhiteTurn = true;
                turn.setText("White player's turn");
            }

        }
    }



    private boolean checkForSpecialMoves(int[] start, int[] end){
        ChessPiece cP = chessBoard.getPiece(start[0],start[1]);
        if(cP instanceof Pawn){
            if((cP.color == 'w' && start[0] == 6 && end[0] == 7)||(cP.color == 'b' && start[0] == 1 && end[0] == 0)){
                promotion = true;
                lastCapturedPiece = chessBoard.getPiece(end[0],end[1]);
                lastCapturedPosition = getPositionFromIntArray(end);
                return true;
            }
            if((cP.color == 'b') && (start[0]-end[0] == 1) && (start[1]-end[1] == 1 || start[1]-end[1] == -1)) {
                if(chessBoard.getPiece(end[0],end[1])!=null){
                    return false;
                }
                enpassant = true;
                lastCapturedPiece = chessBoard.getPiece(start[0],end[1]);
                int [] a = new int[2];
                a[0] = start[0];
                a[1] = end[1];
                lastCapturedPosition = getPositionFromIntArray(a);
                return true;
            }else if((cP.color == 'w') && (start[0]-end[0] == -1) && (start[1]-end[1] == 1 || start[1]-end[1] == -1)){
                if(chessBoard.getPiece(end[0],end[1])!=null){
                    return false;
                }
                enpassant = true;
                lastCapturedPiece = chessBoard.getPiece(start[0],end[1]);
                int [] a = new int[2];
                a[0] = start[0];
                a[1] = end[1];
                lastCapturedPosition = getPositionFromIntArray(a);
                return true;
            }
        }else if(cP instanceof King){
            if((cP.color == 'w') && (start[0] == 0) && (start[1] == 4)){
                if(end[1] == 2 || end[1] == 6){
                    castling = true;
                    lastCapturedPiece = null;
                    lastCapturedPosition = -1;
                    return true;
                }
            }else if((cP.color == 'b') && (start[0] == 7) && (start[1] == 4)){
                if(end[1] == 2 || end[1] == 6){
                    castling = true;
                    lastCapturedPiece = null;
                    lastCapturedPosition = -1;
                    return true;
                }
            }
        }
        promotion = false;
        castling = false;
        enpassant = false;
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        setResult(resultCode, intent);
        finish();
    }

}


