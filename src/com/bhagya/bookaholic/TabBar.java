package com.bhagya.bookaholic;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.widget.TabHost;
import android.app.TabActivity;
import android.widget.TabHost.OnTabChangeListener;

public class TabBar extends TabActivity implements OnTabChangeListener{

    /** Called when the activity is first created. */
    TabHost tabHost;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_bar_main);

        // Get TabHost Reference
        tabHost = getTabHost();

        // Set TabChangeListener called when tab changed
        tabHost.setOnTabChangedListener(this);

        TabHost.TabSpec spec;
        Intent intent;

        /************* TAB1 ************/
        // Create  Intents to launch an Activity for the tab (to be reused)
        intent = new Intent().setClass(this, BooksActivity.class);
        spec = tabHost.newTabSpec("First").setIndicator("")
                .setContent(intent);

        //Add intent to tab
        tabHost.addTab(spec);

        /************* TAB2 ************/
        intent = new Intent().setClass(this, BookshopsActivity.class);
        spec = tabHost.newTabSpec("Second").setIndicator("")
                .setContent(intent);
        tabHost.addTab(spec);

        /************* TAB3 ************/
        intent = new Intent().setClass(this, MapActivity.class);
        spec = tabHost.newTabSpec("Third").setIndicator("")
                .setContent(intent);
        tabHost.addTab(spec);

        /************* TAB4 ************/
        intent = new Intent().setClass(this, MyBookListsActivity.class);
        spec = tabHost.newTabSpec("Fourth").setIndicator("")
                .setContent(intent);
        tabHost.addTab(spec);

        // Set drawable images to tab
        tabHost.getTabWidget().getChildAt(1).setBackgroundResource(R.drawable.ic_launcher);
        tabHost.getTabWidget().getChildAt(2).setBackgroundResource(R.drawable.ic_launcher);

        // Set Tab1 as Default tab and change image
        tabHost.getTabWidget().setCurrentTab(0);
        tabHost.getTabWidget().getChildAt(0).setBackgroundResource(R.drawable.ic_launcher);


    }

    @Override
    public void onTabChanged(String tabId) {

        /************ Called when tab changed *************/

        //********* Check current selected tab and change according images *******/

        for(int i=0;i<tabHost.getTabWidget().getChildCount();i++)
        {
            if(i==0)
                tabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.ic_launcher);
            else if(i==1)
                tabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.ic_launcher);
            else if(i==2)
                tabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.ic_launcher);
        }


        Log.i("tabs", "CurrentTab: "+tabHost.getCurrentTab());

        if(tabHost.getCurrentTab()==0)
            tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab()).setBackgroundResource(R.drawable.ic_launcher);
        else if(tabHost.getCurrentTab()==1)
            tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab()).setBackgroundResource(R.drawable.ic_launcher);
        else if(tabHost.getCurrentTab()==2)
            tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab()).setBackgroundResource(R.drawable.ic_launcher);

    }

}