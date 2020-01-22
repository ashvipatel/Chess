package com.example.application;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class RecordedGameScreen extends AppCompatActivity implements DrawDialogFragment.DrawDialogListener {

    private Button quit;
    private Button back;
    private Button deleteGame;
    private Button watchGame;
    public int selectedPosition;

    ListView listView;
    Spinner spinner;
    ArrayList<RecordedGame> games;
    String [] spinnerList = {"Sort By Date", "Sort By Title"};
    ArrayList <String> listViewData = new ArrayList<String>();
    ArrayAdapter<String> adapter, listAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_of_games);

        back = findViewById(R.id.back);
        quit = findViewById(R.id.quit);
        deleteGame = findViewById(R.id.deleteGame);
        watchGame = findViewById(R.id.watchRecordedGame);

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

        for(int i=0; i < games.size(); i++){
            listViewData.add(games.get(i).toString());
        }

        initializeViews();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                moveTaskToBack(true);
            }
        });

        deleteGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(games.size() == 0) {
                    Bundle bundle = new Bundle();
                    bundle.putString(ChessDialogFragment.MESSAGE_KEY, "There are no games to delete.");
                    DialogFragment newFragment = new ChessDialogFragment();
                    newFragment.setArguments(bundle);
                    newFragment.show(getSupportFragmentManager(),"Dialog");
                    return;
                }

                Bundle bundle = new Bundle();
                bundle.putString(DrawDialogFragment.MESSAGE_KEY, "Are you sure you want to delete this game?");
                DialogFragment newFragment = new DrawDialogFragment();
                newFragment.setArguments(bundle);
                newFragment.show(getSupportFragmentManager(),"Dialog");

            }
        });

        watchGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                watchGame();

            }
        });





    }

    public void onDialogPositiveClick(DialogFragment dialog){

        games.remove(selectedPosition);
        listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, sortByDate()));

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
                Date date = games.get(i).getDate();
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



    }

    public void onDialogNegativeClick(DialogFragment dialog){
    }

    private void watchGame(){
        if(games.size() == 0) {
            Bundle bundle = new Bundle();
            bundle.putString(ChessDialogFragment.MESSAGE_KEY, "There are no games to watch.");
            DialogFragment newFragment = new ChessDialogFragment();
            newFragment.setArguments(bundle);
            newFragment.show(getSupportFragmentManager(),"Dialog");
            return;
        }

        Bundle bundle = new Bundle();
        bundle.putIntegerArrayList("moves",games.get(selectedPosition).getMoves());
        Intent myIntent = new Intent(getApplicationContext(), PlayRecordedGame.class);
        myIntent.putExtras(bundle);
        startActivity(myIntent);
    }

    private void initializeViews() {

        spinner = findViewById(R.id.spinner);
        spinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, spinnerList));

        listView = findViewById(R.id.listView);
        listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, sortByDate()));


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                View v = listView.getChildAt(selectedPosition);
                v.setBackgroundColor(Color.parseColor("#00008080"));
                selectedPosition = position;
                view.setBackgroundColor(Color.parseColor("#FF008080"));
            }

        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView <?> adapterView, View view, int position, long itemID) {
                if (position >= 0 && position < spinnerList.length) {
                    getSelectedCategoryData(position);
                } else {
                    Toast.makeText(RecordedGameScreen.this, "Selected Category Does not Exist!", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void getSelectedCategoryData(int categoryID) {
        if(categoryID == 0)
        {
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, sortByDate());
        }else if(categoryID == 1){

            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, sortByTitle());
        }
        listView.setAdapter(adapter);
    }

    private ArrayList<String> sortByDate() {
        games.sort(Comparator.comparing(RecordedGame::getDate));
        listViewData.clear();
        for(int i=0; i < games.size(); i++){
            listViewData.add(games.get(i).toString());
        }
        return listViewData;
    }

    private ArrayList<String> sortByTitle() {
        games.sort(Comparator.comparing(RecordedGame::getTitle, String.CASE_INSENSITIVE_ORDER));
        listViewData.clear();
        for(int i=0; i < games.size(); i++){
            listViewData.add(games.get(i).toString());
        }

        return listViewData;
    }

    private void quit() {

    }


}


