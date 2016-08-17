package com.iteadstudio;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends MyActivity {
	ImageView logo;
	Button btnSetting;
	Button btnSearchDevice;
	Button btnMonitor;
	Button btnAbout;
	Button btnExit;
	/* Get Default Adapter */
	private BluetoothAdapter _bluetooth = BluetoothAdapter.getDefaultAdapter();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
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

		//Setting
		btnSetting = (Button) findViewById(R.id.btnSetting);
		btnSetting.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, SettingActivity.class);
				startActivity(intent);
			}
		});

		//SearchDevice
		btnSearchDevice = (Button) findViewById(R.id.btnSearchDevice);
		btnSearchDevice.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, SearchDeviceActivity.class);
				startActivity(intent);
			}
		});

		//Monitor
		btnMonitor = (Button) findViewById(R.id.btnMonitor);
		btnMonitor.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				_bluetooth.enable();
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, MonitorActivity.class);
				startActivity(intent);
			}
		});

		//About
		btnAbout = (Button) findViewById(R.id.btnAbout);
		btnAbout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, AboutActivity.class);
				startActivity(intent);
			}
		});

		//Exit
		btnExit = (Button) findViewById(R.id.btnExit);
		btnExit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog();
			}
		});
	}

	/**
	 * ��ȡ���ذ�ť�¼�
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			dialog();
			return false;
		}
		return false;
	}

	/**
	 * ��ʾ��
	 */
	protected void dialog() {
		AlertDialog.Builder build = new AlertDialog.Builder(MainActivity.this);
		build.setTitle(R.string.message);
		build.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						if (_bluetooth.isEnabled()) {
							_bluetooth.disable();
						}
						SocketApplication app = (SocketApplication) getApplicationContext();
						app.setDevice(null);
						MainActivity.this.finish();
					}
				});
		build.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		build.create().show();
	}

	@Override
	/**
	 * �˳�ʱ��ս���
	 */
	public void onDestroy() {
		super.onDestroy();
		System.exit(0);
	}
}