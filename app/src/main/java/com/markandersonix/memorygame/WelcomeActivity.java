package com.markandersonix.memorygame;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;

public class WelcomeActivity extends AppCompatActivity {
    Bundle saveState = new Bundle();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        RulesFragment fragment = new RulesFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.rulesframe, fragment).commit();
    }

    @OnClick(R.id.action_newgame)
    public void startGame(View view) {
        Intent intent = new Intent(this,GameActivity.class);
        startActivity(intent);
    }
//    public void showRules(View view) {
//        Intent intent = new Intent(this,RulesActivity.class);
//        startActivity(intent);
//    }
}
