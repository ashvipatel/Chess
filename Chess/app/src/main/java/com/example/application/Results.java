package com.example.application;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.Date;

public class Results extends AppCompatActivity {

    private ArrayList<Integer> allMoves;
    private long date;
    private String title;

    private Button yes;
    private Button no;
    private Button save;
    private Button cancel;

    private TextView message;
    private TextView recordedGameQuestion;
    private TextView namePrompt;

    private EditText titleEditText;

    public static final String ALL_MOVES = "all_moves";
    public static final String ACTION = "action";
    public static final String TITLE = "title";
    public static final String DATE = "date";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.results);

        Bundle bundle = getIntent().getExtras();
        String action = bundle.getString(ACTION);
        allMoves = bundle.getIntegerArrayList(ALL_MOVES);

        recordedGameQuestion = findViewById(R.id.recordGameQuestion);
        namePrompt = findViewById(R.id.namePrompt);
        message = findViewById(R.id.winnerTextView);
        yes = findViewById(R.id.yesButton);
        no = findViewById(R.id.noButton);
        save = findViewById(R.id.save);
        cancel = findViewById(R.id.cancel);
        titleEditText = findViewById(R.id.gameNameText);

        if(action.equals("br")||action.equals("bc")){
            message.setText("White wins!");
        }else if(action.equals("wr")||action.equals("wc")){
            message.setText("Black wins!");
        }else if(action.equals("d")){
            message.setText("Draw");
        }else if(action.equals("s")){
            message.setText("Stalemate");
        }

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                yes();
            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                setResult(RESULT_CANCELED,i);
                finish();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancel();
            }
        });

    }

    private void save(){
        title = titleEditText.getText().toString();
        if(title == null || title.equals("")){
            Bundle bundle = new Bundle();
            bundle.putString(ChessDialogFragment.MESSAGE_KEY, "Must specify title");
            DialogFragment newFragment = new ChessDialogFragment();
            newFragment.setArguments(bundle);
            newFragment.show(getSupportFragmentManager(),"Dialog");
            return;
        }
        date = System.currentTimeMillis();

        Bundle bundle = new Bundle();
        bundle.putIntegerArrayList(ALL_MOVES,allMoves);
        bundle.putString(TITLE,title);
        bundle.putLong(DATE,date);
        Intent i = new Intent();
        i.putExtras(bundle);
        setResult(RESULT_OK,i);
        finish();
    }

    private void yes(){
        titleEditText.setVisibility(View.VISIBLE);
        recordedGameQuestion.setVisibility(View.INVISIBLE);
        namePrompt.setVisibility(View.VISIBLE);
        yes.setVisibility(View.INVISIBLE);
        no.setVisibility(View.INVISIBLE);
        save.setVisibility(View.VISIBLE);
        cancel.setVisibility(View.VISIBLE);

    }

    private void cancel(){
        titleEditText.setVisibility(View.INVISIBLE);
        recordedGameQuestion.setVisibility(View.VISIBLE);
        namePrompt.setVisibility(View.INVISIBLE);
        yes.setVisibility(View.VISIBLE);
        no.setVisibility(View.VISIBLE);
        save.setVisibility(View.INVISIBLE);
        cancel.setVisibility(View.INVISIBLE);
    }
}
