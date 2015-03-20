package com.bhagya.bookaholic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.Toast;

public class TravelDetailsActivity extends BaseActivity {

	ExpandableListAdapter listAdapter;
	ExpandableListView expListView;
	List<String> listDataHeader;
	HashMap<String, List<String>> listDataChild;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.travel);

		// get the listview
		expListView = (ExpandableListView) findViewById(R.id.eList);

		// preparing list data
		prepareListData();

		listAdapter = new ExpandableListAdapter(this, listDataHeader,
				listDataChild);

		// setting list adapter
		expListView.setAdapter(listAdapter);

		// Listview on child click listener
		expListView.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				// TODO Auto-generated method stub
				Toast.makeText(
						getApplicationContext(),
						listDataHeader.get(groupPosition)
								+ " : "
								+ listDataChild.get(
										listDataHeader.get(groupPosition)).get(
										childPosition), Toast.LENGTH_SHORT)
						.show();
				return false;
			}
		});
	}

	// Preparing the list data to be displayed
	private void prepareListData() {
		Intent intent = getIntent();

		listDataHeader = new ArrayList<String>();
		listDataChild = new HashMap<String, List<String>>();

		// Adding parent data
		ArrayList<String> tempHeader = intent
				.getStringArrayListExtra("bookshops");
		ArrayList<String> tempChild = intent.getStringArrayListExtra("books");

		for (int i = 0; i < tempHeader.size(); i++) {
			if (listDataHeader.contains(tempHeader.get(i))) {
				continue;
			}
			listDataHeader.add(tempHeader.get(i));
		}

		// Adding child data
		for (int i = 0; i < listDataHeader.size(); i++) {
			List<String> temp = new ArrayList<String>();
			for (int j = 0; j < tempChild.size(); j++) {
				if (listDataHeader.get(i).equals(tempHeader.get(j))) {
					temp.add(tempChild.get(j));
				}
			}
			listDataChild.put(listDataHeader.get(i), temp);
		}
	}
}
