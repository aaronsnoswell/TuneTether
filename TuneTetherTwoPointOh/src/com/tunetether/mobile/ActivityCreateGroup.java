package com.tunetether.mobile;

import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class ActivityCreateGroup extends Activity {
	EditText edt_group_name;
	Spinner spn_select_song;
	Button btn_next;
	
	String[] group_names = {
		"Got Great Company?",
		"#YOLOOOOOOOO",
		"My Mum Thinks I'm Cool",
		"I Got Swag",
		"Bromance ftw",
		"I <3 Unicorns"
	};
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_group);

        edt_group_name = ((EditText)findViewById(R.id.group_name));
        spn_select_song = ((Spinner)findViewById(R.id.select_song));
        btn_next = ((Button)findViewById(R.id.next));
        
        edt_group_name.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(((EditText)v).getText().toString().length() != 0) {
        			btn_next.setEnabled(true);
		        } else {
        			btn_next.setEnabled(false);
		        }
				
				return false;
			}
		});
        
        
        btn_next.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				String name = edt_group_name.getText().toString();
				String song_name = spn_select_song.getSelectedItem().toString();
				
				// Next page please
			}
		});
        
        Random rnd = new Random();
        edt_group_name.setText(group_names[rnd.nextInt(group_names.length)]);
    }
    
    
    private String TAG = "TUNETETHER";
}