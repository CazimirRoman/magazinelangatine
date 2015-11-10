package magazinelangatine.util;

import magazinelangatine.MLTMainActivity;
import magazoo.magazine.langa.tine.R;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.sax.StartElementListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class DialogCreator {
	private Activity activity;

	private Dialog dialog;

	public DialogCreator(Activity activity) {
		this.activity = activity;

	}

	public void showError(String messageText) {
		dialog = new Dialog(activity);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.popup_yes);

		Typeface tf = Typeface.createFromAsset(this.activity.getAssets(),
				"fonts/comfortaa-regular.ttf");

		Button btnOk = (Button) dialog.findViewById(R.id.btnOk);
		btnOk.setTypeface(tf);

		TextView oops = (TextView) dialog.findViewById(R.id.tvOops);
		TextView message = (TextView) dialog.findViewById(R.id.message);

		btnOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();

			}
		});
		dialog.show();
	}

	public void showOK(String title, String message, final String calling) {

		dialog = new Dialog(activity);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.popup_yes);

		dialog.getWindow().setBackgroundDrawableResource(
				android.R.color.transparent);

		Typeface tf = Typeface.createFromAsset(this.activity.getAssets(),
				"fonts/comfortaa-regular.ttf");

		Button btnYes = (Button) dialog.findViewById(R.id.btnYes);
		TextView titleOK = (TextView) dialog.findViewById(R.id.tvTitle);
		TextView messageOK = (TextView) dialog.findViewById(R.id.tvMessage);

		btnYes.setTypeface(tf);
		titleOK.setTypeface(tf);
		messageOK.setTypeface(tf);

		titleOK.setText(title);
		messageOK.setText(message);

		btnYes.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				dialog.dismiss();

				if (calling.equals("save")) {
					Intent i = new Intent(activity, MLTMainActivity.class);
					activity.startActivity(i);
					activity.finish();
				}

			}
		});

		dialog.show();

	}

	public void showYesNoAdauga(String title, String message) {
		dialog = new Dialog(activity);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

		dialog.setContentView(R.layout.popup_yes_no);

		dialog.getWindow().setBackgroundDrawableResource(
				android.R.color.transparent);

		Typeface tf = Typeface.createFromAsset(this.activity.getAssets(),
				"fonts/comfortaa-regular.ttf");

		Button btnYes = (Button) dialog.findViewById(R.id.btnYes);
		Button btnNo = (Button) dialog.findViewById(R.id.btnNo);

		TextView titleYesNo = (TextView) dialog.findViewById(R.id.tvTitle);
		TextView messageYesNo = (TextView) dialog.findViewById(R.id.tvMessage);

		btnYes.setTypeface(tf);
		btnNo.setTypeface(tf);

		titleYesNo.setTypeface(tf);
		titleYesNo.setTypeface(titleYesNo.getTypeface(), Typeface.BOLD);

		messageYesNo.setTypeface(tf);

		titleYesNo.setText(title);
		messageYesNo.setText(message);

		btnYes.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				SharedPreferences prefs = PreferenceManager
						.getDefaultSharedPreferences(activity);

				int currentcounter = prefs.getInt("addcounter", 0);

				SharedPreferences.Editor editor = prefs.edit();

				editor.putInt("addcounter", currentcounter - 1);
				editor.commit();

				Intent i = new Intent(activity, MLTMainActivity.class);

				activity.startActivity(i);
				activity.finish();

			}
		});

		btnNo.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();

			}
		});

		dialog.show();
	}
	
	public void showInternet(String title, String message) {

		dialog = new Dialog(activity);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.popup_yes);

		dialog.getWindow().setBackgroundDrawableResource(
				android.R.color.transparent);

		Typeface tf = Typeface.createFromAsset(this.activity.getAssets(),
				"fonts/comfortaa-regular.ttf");

		Button btnYes = (Button) dialog.findViewById(R.id.btnYes);
		TextView titleOK = (TextView) dialog.findViewById(R.id.tvTitle);
		TextView messageOK = (TextView) dialog.findViewById(R.id.tvMessage);

		btnYes.setTypeface(tf);
		titleOK.setTypeface(tf);
		messageOK.setTypeface(tf);

		titleOK.setText(title);
		messageOK.setText(message);

		btnYes.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				dialog.dismiss();

				activity.startActivity(new Intent(android.net.wifi.WifiManager.ACTION_PICK_WIFI_NETWORK));

			}
		});

		dialog.show();

	}

}
