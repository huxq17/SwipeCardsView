package com.huxq17.example;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.huxq17.example.base.BaseActivity;
import com.huxq17.example.fragment.MeiziFragment;

public class MainActivity extends BaseActivity {
    private MeiziFragment meiziFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        meiziFragment = MeiziFragment.getInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, meiziFragment).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

       if (id == R.id.action_left) {
            meiziFragment.doLeftOut();
            return true;
        } else if (id == R.id.action_right) {
            meiziFragment.doRightOut();
            return true;
        }else if(id==R.id.action_retry){
           meiziFragment.doRetry();
       }

        return super.onOptionsItemSelected(item);
    }
}
