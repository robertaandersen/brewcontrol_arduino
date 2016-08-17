package com.iteadstudio;

import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class AboutActivity extends MyActivity {
	ImageView logo;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        
        logo = (ImageView) findViewById(R.id.logo);
		logo.setClickable(true);
		logo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					Uri uri = Uri.parse(getResources().getString(R.string.uri));
					Intent intent = new Intent(Intent.ACTION_VIEW, uri);
					startActivity(intent);
				} catch (NotFoundException e) {
					e.printStackTrace();
					Log.e("ACTION_VIEW", e.getMessage());
				}
			}
		});
    }
}