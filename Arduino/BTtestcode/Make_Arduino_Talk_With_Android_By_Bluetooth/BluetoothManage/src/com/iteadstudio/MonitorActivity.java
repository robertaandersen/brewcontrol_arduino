package com.iteadstudio;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class MonitorActivity extends MyActivity {

	private static final int REQUEST_DISCOVERY = 0x1;;
	private final String TAG = "MonitorActivity";
	private Handler _handler = new Handler();
	private final int maxlength = 2048;

	private BluetoothDevice device = null;
	private BluetoothSocket socket = null;
	private EditText sEditText;
	private TextView sTextView;
	private OutputStream outputStream;
	private InputStream inputStream;
	CheckBox cbxHexSend;
	CheckBox cbxHexView;
	CheckBox cbxNewLine;

	public static StringBuffer hexString = new StringBuffer();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
				WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
		setContentView(R.layout.monitor);
		cbxHexSend = (CheckBox) findViewById(R.id.cbxHexSend);
		cbxHexView = (CheckBox) findViewById(R.id.cbxHexView);
		cbxNewLine = (CheckBox) findViewById(R.id.cbxNewLine);
		sTextView = (TextView) findViewById(R.id.sTextView);
		sEditText = (EditText) findViewById(R.id.sEditText);

		cbxHexView.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					sTextView.setText(hexString.toString());
				} else {
					String str = bufferStrToHex(hexString.toString(), false);
					if (!str.startsWith("-->") && !str.startsWith("<--")) {
						str = str.substring(2);
					}
					sTextView.setText(str);
				}
			}
		});

		cbxHexSend.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					cbxNewLine.setChecked(false);
					cbxNewLine.setEnabled(false);
				} else {
					cbxNewLine.setChecked(true);
					cbxNewLine.setEnabled(true);
				}
			}
		});

		BluetoothDevice finalDevice = this.getIntent().getParcelableExtra(
				BluetoothDevice.EXTRA_DEVICE);
		//查看是否有已经连接过的设备
		SocketApplication app = (SocketApplication) getApplicationContext();
		device = app.getDevice();
		if (finalDevice == null) {
			if (device == null) {
				Intent intent = new Intent(this, SearchDeviceActivity.class);
				startActivity(intent);
				finish();
				return;
			}
		} else if (finalDevice != null) {
			app.setDevice(finalDevice);
			device = app.getDevice();
		}
		new Thread() {
			public void run() {
				connect(device);
			};
		}.start();
	}

	/**
	 * Send
	 * @param view
	 */
	public void onButtonClicksend(View view) {
		String editText = sEditText.getText().toString();
		String tempHex = "";
		byte bytes[] = editText.getBytes();
		//如果Hex发送
		if (cbxHexSend.isChecked()) {
			try {
				bytes = SamplesUtils.hexToByte(editText);
			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(getBaseContext(),
						getResources().getString(R.string.number),
						Toast.LENGTH_SHORT).show();
				return;
			}
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < editText.length(); i = i + 2) {
				sb.append(editText.substring(i, i + 2)).append(" ");
			}
			tempHex = sb.toString();
		} else {
			//非Hex发送
			tempHex = SamplesUtils.stringToHex(editText);
			if (cbxNewLine.isChecked()) {
				tempHex += " 0d 0a ";
				bytes=(editText+"\r\n").getBytes();
			}
		}
		//给HexString增加-->或者<--
		String hex = hexString.toString();
		if (hex == "") {
			hexString.append("-->");
		} else {
			if (hex.lastIndexOf("-->") < hex.lastIndexOf("<--")) {
				hexString.append("\n-->");
			}
		}
		hexString.append(tempHex);
		//如果超过指定大小 则清除前面的文字
		hex = hexString.toString();
		if (hex.length() > maxlength) {
			try {
				hex = hex.substring(hex.length() - maxlength, hex.length());
				hex = hex.substring(hex.indexOf(" "));
				hex = "-->" + hex;
				hexString = new StringBuffer();
				hexString.append(hex);
			} catch (Exception e) {
				e.printStackTrace();
				Log.e(TAG, "e", e);
			}
		}
		//根据是否Hex显示来显示不同的内容
		if (cbxHexView.isChecked()) {
			sTextView.setText(hexString.toString());
		} else {
			sTextView.setText(bufferStrToHex(hexString.toString(), false)
					.trim());
		}
		try {
			if (outputStream != null) {
				outputStream.write(bytes);
			} else {
				Toast.makeText(getBaseContext(),
						getResources().getString(R.string.wait),
						Toast.LENGTH_SHORT).show();
			}
		} catch (IOException e) {
			Log.e(TAG, ">>", e);
			e.printStackTrace();
		}
	}

	//清空
	public void onButtonClickclear(View view) throws IOException {
		hexString = new StringBuffer();
		sTextView.setText(hexString.toString());
	}

	/* after select, connect to device */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode != REQUEST_DISCOVERY) {
			finish();
			return;
		}
		if (resultCode != RESULT_OK) {
			finish();
			return;
		}
		final BluetoothDevice device = data
				.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
		new Thread() {
			public void run() {
				connect(device);
			};
		}.start();
	}

	protected void onDestroy() {
		super.onDestroy();
		try {
			if (socket != null) {
				socket.close();
			}
		} catch (IOException e) {
			Log.e(TAG, ">>", e);
		}
	}

	/**
	 * 连接设备并用handle获取input
	 * @param device
	 */
	protected void connect(BluetoothDevice device) {
		try {
			// Create a Socket connection: need the server's UUID number of
			// registered
			socket = device.createRfcommSocketToServiceRecord(UUID
					.fromString("00001101-0000-1000-8000-00805F9B34FB"));

			socket.connect();
			Log.d(TAG, ">>Client connectted");
			inputStream = socket.getInputStream();
			outputStream = socket.getOutputStream();
			int read = -1;
			final byte[] bytes = new byte[2048];
			for (; (read = inputStream.read(bytes)) > -1;) {
				final int count = read;
				_handler.post(new Runnable() {
					public void run() {
						String str = "";
						str = SamplesUtils.byteToHex(bytes, count);
						Log.d(TAG, "test1:" + str);
						String hex = hexString.toString();
						if (hex == "") {
							hexString.append("<--");
						} else {
							if (hex.lastIndexOf("<--") < hex.lastIndexOf("-->")) {
								hexString.append("\n<--");
							}
						}
						hexString.append(str);
						hex = hexString.toString();
						Log.d(TAG, "test2:" + hex);
						if (hex.length() > maxlength) {
							try {
								hex = hex.substring(hex.length() - maxlength,
										hex.length());
								hex = hex.substring(hex.indexOf(" "));
								hex = "<--" + hex;
								hexString = new StringBuffer();
								hexString.append(hex);
							} catch (Exception e) {
								e.printStackTrace();
								Log.e(TAG, "e", e);
							}
						}
						if (cbxHexView.isChecked()) {
							sTextView.setText(hexString.toString());
						} else {
							sTextView.setText(bufferStrToHex(
									hexString.toString(), false).trim());
						}
					}
				});
			}

		} catch (IOException e) {
			Log.e(TAG, ">>", e);
			Toast.makeText(getBaseContext(),
					getResources().getString(R.string.ioexception),
					Toast.LENGTH_SHORT).show();
			return;
		} finally {
			if (socket != null) {
				try {
					Log.d(TAG, ">>Client Socket Close");
					socket.close();
					finish();
					return;
				} catch (IOException e) {
					Log.e(TAG, ">>", e);
				}
			}
		}
	}

	/**
	 * 带-->和<--的Hex与String互转
	 * @param buffer 需要转换的字串
	 * @param flag	true：String->Hex false:Hex->String
	 * @return
	 */
	public String bufferStrToHex(String buffer, boolean flag) {
		String all = buffer;
		StringBuffer sb = new StringBuffer();
		String[] ones = all.split("<--");
		for (int i = 0; i < ones.length; i++) {
			if (ones[i] != "") {
				String[] twos = ones[i].split("-->");
				for (int j = 0; j < twos.length; j++) {
					if (twos[j] != "") {
						if (flag) {
							sb.append(SamplesUtils.stringToHex(twos[j]));
						} else {
							sb.append(SamplesUtils.hexToString(twos[j]));
						}
						if (j != twos.length - 1) {
							if (sb.toString() != "") {
								sb.append("\n");
							}
							sb.append("-->");
						}
					}
				}
				if (i != ones.length - 1) {
					if (sb.toString() != "") {
						sb.append("\n");
					}
					sb.append("<--");
				}
			}
		}
		return sb.toString();
	}

}