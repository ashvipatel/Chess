package com.example.application;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;


public class MainActivity extends AppCompatActivity {

    private Button startNewGame;
    private Button watchRecordedGame;
    private Button quit;
    private ArrayList<RecordedGame> games;

    public static final String ACTION = "action";
    public static final String GAMES = "games";
    public static final int GAME_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startNewGame = (Button) findViewById(R.id.startNewGame);

        startNewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startGame();
            }
        });

        watchRecordedGame = (Button) findViewById(R.id.watchRecordedGame);
        watchRecordedGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                watchRecordedGame();
            }
        });

        Button quit = (Button) findViewById(R.id.quit);
        quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.this.finish();
            }
        });

        try {
            File file = new File(getApplicationContext().getFilesDir(),"games.dat");
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            String title;
            long date;
            games = new ArrayList<RecordedGame>();
            while((line = br.readLine()) != null){
                title = line;
                date = Long.parseLong(br.readLine());
                ArrayList<Integer> game = new ArrayList<Integer>();
                while(!(line = br.readLine()).equals("end")){
                    game.add(Integer.parseInt(line));
                }
                games.add(new RecordedGame(game,title,new Date(date)));
            }
            br.close();
        }catch(IOException e) {
            games = new ArrayList<RecordedGame>();
        }
    }

    private void startGame(){
        Bundle bundle = new Bundle();
        bundle.putString(ACTION,"main");
        Intent newGameIntent = new Intent(this, Play.class);
        newGameIntent.putExtras(bundle);
        startActivityForResult(newGameIntent,GAME_CODE);
    }

    private void watchRecordedGame(){
        Bundle bundle = new Bundle();
        Intent watchGameIntent = new Intent(this, RecordedGameScreen.class);
        watchGameIntent.putExtras(bundle);
        startActivity(watchGameIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode != RESULT_OK) {
            return;
        }
        Bundle bundle = intent.getExtras();
        ArrayList<Integer> allMoves = bundle.getIntegerArrayList(Results.ALL_MOVES);
        String title = bundle.getString(Results.TITLE);
        Date date = new Date();
        date.setTime(bundle.getLong(Results.DATE));
        RecordedGame rg = new RecordedGame(allMoves, title, date);
        games.add(rg);

        try {
            File file = new File(getApplicationContext().getFilesDir(),"games.dat");
            if(!file.exists()) {
                file.createNewFile();
            }
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            bw.write("");

            for(int i = 0; i<games.size(); i++){
                StringBuilder sb = new StringBuilder();
                sb.append(games.get(i).getTitle()).append("\n");
                date = games.get(i).getDate();
                sb.append(date.getTime()).append("\n");
                ArrayList<Integer> game = games.get(i).getMoves();
                for(int j = 0; j<game.size(); j++){
                    sb.append(game.get(j)).append("\n");
                }
                sb.append("end\n");
                String out = sb.toString();
                bw.append(out);
            }


            bw.close();
        }catch(IOException e){
            System.out.println("Failed to save data");
        }
        return;
    }

}
