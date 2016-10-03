package com.markandersonix.memorygame;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nineoldandroids.animation.Animator;

import butterknife.BindView;

public class GameActivity extends AppCompatActivity {
    ArrayList<ImageButton> buttons;
    ArrayList<String> images;
    HashMap<String, Integer> tileMap;
    HashSet<Integer> clearedTiles;
    long guessCooldown = 1000;
    boolean canGuess = true;
    int guessButton1 = 0;
    int guessButton2 = 0;
    int points = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        String tileMapString = prefs.getString("tileMap","empty");
        String clearedTilesString = prefs.getString("clearedTiles","empty");
        tileMap = new HashMap<String, Integer>();
        clearedTiles = new HashSet<Integer>();
        Log.e("TILEMAPSTRING: ", tileMapString );
        Log.e("points", ""+points );
        if(prefs.contains("points")){
            points = prefs.getInt("points",0);
        }
        if(tileMapString != "empty" && tileMapString != "{}" && points > 0 && points < 10){
            TextView pointsText = (TextView) findViewById(R.id.pointsText);
            pointsText.setText("Points: "+points);
            buttons = getButtons();
            try {
                JSONObject tileMapJSON = new JSONObject(tileMapString);
                Iterator<String> iter = tileMapJSON.keys();
                Log.e("JSON: ", tileMapJSON.toString());
                while(iter.hasNext()){
                    String next = iter.next();
                    Log.e("tileMap iterator: ", next );
                    tileMap.put(next, tileMapJSON.getInt(next));
                }
                String[] clearedTilesArray = clearedTilesString.replace("[","").replace("]","").replace(" ","").split(",");
                if(clearedTilesArray.length > 1){
                    for(String value:clearedTilesArray) {
                        clearedTiles.add(Integer.parseInt(value));
                    }
                }
            }catch(JSONException e){
                Log.e("onCreate: ", e.getMessage() );
            }
            Resources res = getResources();
            for (ImageButton button : buttons) {
                if (clearedTiles.contains(button.getId())) {
                    button.setVisibility(View.INVISIBLE);
                }
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getResources().getString(R.string.new_game_title));
            builder.setMessage(getResources().getString(R.string.new_game_message));
            builder.setPositiveButton(getResources().getString(R.string.continue_game), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    return;
                }
            });
            builder.setNegativeButton(getResources().getString(R.string.new_game), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    setBoard();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }else {
            setBoard();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_gameactivity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_newgame){
            setBoard();
        }else if(item.getItemId() == R.id.action_shuffle){
            shuffleBoard();
        }
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putSerializable("tileMap", tileMap);
        outState.putSerializable("clearedTiles", clearedTiles);
        outState.putInt("points", points);
        Log.e("","OnSave" );
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        buttons = getButtons();
        if (savedInstanceState.containsKey("tileMap")) {
            tileMap = (HashMap) savedInstanceState.getSerializable("tileMap");
            clearedTiles = (HashSet) savedInstanceState.getSerializable("clearedTiles");
            Resources res = getResources();
            for (ImageButton button : buttons) {
                try {
                    button.setImageResource(tileMap.get(Integer.toString(button.getId())));
                } catch (Exception e) {
                    Log.e("onRestore: ", "Could not resolve tilemap data");
                }
                if (clearedTiles.contains(button.getId())) {
                    button.setVisibility(View.INVISIBLE);
                }
            }
        }
        if (savedInstanceState.containsKey("points")) {
            points = savedInstanceState.getInt("points");
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        JSONObject tileMapJson = new JSONObject(tileMap);
        prefsEditor.remove("tileMap").commit();
        prefsEditor.remove("clearedTiles").commit();
        prefsEditor.putString("tileMap", tileMapJson.toString());
        prefsEditor.putString("clearedTiles",clearedTiles.toString());
        prefsEditor.putInt("points",points);
        prefsEditor.commit();
    }
    protected ArrayList<ImageButton> getButtons(){
        ArrayList<ImageButton> buttons = new ArrayList<ImageButton>();
        buttons.add((ImageButton) findViewById(R.id.button1));
        buttons.add((ImageButton) findViewById(R.id.button2));
        buttons.add((ImageButton) findViewById(R.id.button3));
        buttons.add((ImageButton) findViewById(R.id.button4));
        buttons.add((ImageButton) findViewById(R.id.button5));
        buttons.add((ImageButton) findViewById(R.id.button6));
        buttons.add((ImageButton) findViewById(R.id.button7));
        buttons.add((ImageButton) findViewById(R.id.button8));
        buttons.add((ImageButton) findViewById(R.id.button9));
        buttons.add((ImageButton) findViewById(R.id.button10));
        buttons.add((ImageButton) findViewById(R.id.button11));
        buttons.add((ImageButton) findViewById(R.id.button12));
        buttons.add((ImageButton) findViewById(R.id.button13));
        buttons.add((ImageButton) findViewById(R.id.button14));
        buttons.add((ImageButton) findViewById(R.id.button15));
        buttons.add((ImageButton) findViewById(R.id.button16));
        buttons.add((ImageButton) findViewById(R.id.button17));
        buttons.add((ImageButton) findViewById(R.id.button18));
        buttons.add((ImageButton) findViewById(R.id.button19));
        buttons.add((ImageButton) findViewById(R.id.button20));
        return buttons;
    }
    protected void setBoard(){
        points = 0;
        TextView pointsText = (TextView) findViewById(R.id.pointsText);
        pointsText.setText("Points: "+points);
        buttons = getButtons();
        Resources res = getResources();
        TypedArray images = getResources().obtainTypedArray(R.array.images);
        Random rand = new Random();
        int buttonNumber;
        for(int i=0; i<images.length();i++){
            for(int j=0; j<2; j++) {
                buttonNumber = rand.nextInt(buttons.size());
                //add the button id and its corresponding image id to the map
                tileMap.put(Integer.toString(buttons.get(buttonNumber).getId()),
                        images.getResourceId(i,0));
                buttons.get(buttonNumber).setVisibility(View.VISIBLE);
                buttons.get(buttonNumber).setImageResource(R.drawable.cardback);
                buttons.remove(buttonNumber);
            }
        }
    }

    protected void shuffleBoard(){
        ArrayList<ImageButton> buttons = getButtons();
        ArrayList<Integer> visibleImages = new ArrayList<>();
        clearedTiles.clear();
        for(ImageButton btn: buttons){
            btn.setImageResource(R.drawable.cardback);
            if(btn.getVisibility() == View.VISIBLE){
                visibleImages.add(tileMap.get(Integer.toString(btn.getId())));
            }
        }
        Collections.shuffle(visibleImages);
        for(Integer img: visibleImages){
            if(!buttons.isEmpty()){
                buttons.get(0).setVisibility(View.VISIBLE);
                tileMap.put(Integer.toString(buttons.get(0).getId()),img);
                buttons.remove(0);
            }
        }
        for(ImageButton btn: buttons){
            btn.setVisibility(View.INVISIBLE);
            clearedTiles.add(btn.getId());
        }
    }

    protected void onTileClick(View view){
        if(canGuess){
            ImageButton button = (ImageButton) view;
            Resources res = getResources();
            button.setImageResource(tileMap.get(Integer.toString(button.getId())));
            YoYo.with(Techniques.FlipInX).duration(1000).playOn(button);
            Log.e("Tilemap: ",""+Integer.toString(button.getId()));
            if(guessButton1 == 0){
                guessButton1 = button.getId();
            }else if(guessButton1 == button.getId()){
                return;
            }else{
                canGuess = false;
                guessButton2 = button.getId();
                final boolean correctGuess = tileMap.get(Integer.toString(guessButton1))
                        .equals(tileMap.get(Integer.toString(guessButton2)))?true:false;
                //delay
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable(){
                    @Override
                    public void run(){
                        ImageButton btn1 = (ImageButton) findViewById(guessButton1);
                        ImageButton btn2 = (ImageButton) findViewById(guessButton2);
                        Log.e("correctGuess: ", ""+correctGuess);
                        if(correctGuess){
                            btn1.setVisibility(View.INVISIBLE);
                            btn2.setVisibility(View.INVISIBLE);
                            clearedTiles.add(btn1.getId());
                            clearedTiles.add(btn2.getId());
                            points++;
                            TextView pointsText = (TextView) findViewById(R.id.pointsText);
                            pointsText.setText("Points: "+ points);
                            if(points == 10){
                                Toast toast = Toast.makeText(getApplicationContext(),"You've matched all the tiles!", Toast.LENGTH_SHORT);
                                toast.show();
                                setBoard();
                                points = 0;
                                tileMap.clear();
                                finish();
                            }
                        }else{
                            btn1.setImageResource(R.drawable.cardback);
                            btn2.setImageResource(R.drawable.cardback);
                            YoYo.with(Techniques.FlipInX).duration(1000).playOn(btn1);
                            YoYo.with(Techniques.FlipInX).duration(1000).playOn(btn2);
                        }
                        guessButton1 = 0;
                        guessButton2 = 0;
                        canGuess = true;
                    }}, guessCooldown);
                }
        }
    }
}
