package com.bhagya.bookaholic;

import com.bhagya.bookaholic.R;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class GetSourceActivity extends BaseActivity {

	// Source and destination of the path to be set
	private String source, destination;

	// Elements in the layout
	private EditText etSource;
	private Button bGetDir;

	// Called at the beginning
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.getsource);
		// Enable the button on click listeners
		buttonEnable();
	}

	// Initialize and Set on click listeners of the buttons
	private void buttonEnable() {

		bGetDir = (Button) findViewById(R.id.bGetDir);

		// Get the direction of the path
		bGetDir.setOnClickListener(new View.OnClickListener() {

			// On click of the button
			@Override
			public void onClick(View arg0) {
				// Get the source input user has entered
				etSource = (EditText) findViewById(R.id.etSource);
				source = etSource.getText().toString();
				// Get the destination stall number from the previous activity
				destination = getIntent().getExtras().getString("stall");

				// Check for the correctness of the source input and if it's the
				// same as the destination
				if (isSourceValid(source)) {
					if (Integer.parseInt(source.substring(1)) == Integer
							.parseInt(destination.substring(1))) {
						// If it is valid and same as the destination then show
						// a dialog
						AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
								GetSourceActivity.this);
						// set title
						alertDialogBuilder.setTitle("You are there");
						// set dialog message
						alertDialogBuilder
								.setMessage(
										"You are already there at "
												+ destination + "!")
								.setCancelable(false)
								.setPositiveButton("OK",
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int id) {
												// if this button is clicked,
												// close
												// current activity
												GetSourceActivity.this.finish();
											}
										});
						// create alert dialog
						AlertDialog alertDialog = alertDialogBuilder.create();
						// show it
						alertDialog.show();
					} else {
						// If the destination is not the same as the source then
						// start an activity to get the path details
						try {
							Class actClass = Class
									.forName("com.bhagya.bookaholic.GetDirectionsActivity");
							Intent intent = new Intent(GetSourceActivity.this,
									actClass);

							intent.putExtra("source", source);
							intent.putExtra("destination", destination);
							startActivity(intent);
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						}
					}
				} else {
					// If the source is not valid show an error dialog
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
							GetSourceActivity.this);
					// set title
					alertDialogBuilder.setTitle("Invalid stall");
					// set dialog message
					alertDialogBuilder
							.setMessage(
									"Sorry! Stall number you entered is not valid!")
							.setCancelable(false)
							.setPositiveButton("OK",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {
											// if this button is
											// clicked,
											// close
											// current activity
											GetSourceActivity.this.finish();
										}
									});
					// create alert dialog
					AlertDialog alertDialog = alertDialogBuilder.create();
					// show it
					alertDialog.show();
				}
			}
		});
	}

	// Check for the validity of the user input
	private boolean isSourceValid(String source) {
		boolean isValid = false;
		// For demonstration I have used the "A" building only where there are
		// 1-89 stalls
		if (source.startsWith("A") || source.startsWith("a")) {
			int num = Integer.parseInt(source.substring(1));
			if (num >= 0 && num <= 89) {
				isValid = true;
			}
		}
		return isValid;
	}
}
