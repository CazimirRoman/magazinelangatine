package magazinelangatine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import magazinelangatine.model.Store;
import magazinelangatine.util.DialogCreator;
import magazoo.magazine.langa.tine.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

public class SaveTask extends AsyncTask<Object, Void, Void> {

	Store mStore;
	Activity mActivity;
	DialogCreator mDialog;

	@Override
	protected void onPreExecute() {

	}

	public SaveTask(Activity activity, Store store) {

		this.mStore = store;
		this.mActivity = activity;
		this.mDialog = new DialogCreator(mActivity);

	}

	protected Void doInBackground(Object... params) {

		String collNumeMag = mStore.name;
		String lat = mStore.lat;
		String lng = mStore.lon;
		String tipmagsel = mStore.type;
		String POSFlag = mStore.pos;
		String NSFlag = mStore.nonstop;
		String collDescriereMag = mStore.description;
		String LV = mStore.monfri;
		String Sambata = mStore.saturday;
		String Duminica = mStore.sunday;
		String user_id = mStore.user_id;
		String user_name = mStore.user_name;

		String strUrl = "http://atelieruldegermana.ro/android_connect/save_shop.php";
		URL url = null;
		try {
			url = new URL(strUrl);

			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
					connection.getOutputStream());

			outputStreamWriter.write("&name=" + collNumeMag + "&lat=" + lat
					+ "&lng=" + lng + "&tip=" + tipmagsel + "&desc="
					+ collDescriereMag + "&pos=" + POSFlag + "&nonstop="
					+ NSFlag + "&lv=" + LV + "&sambata=" + Sambata
					+ "&duminica=" + Duminica + "&user_id=" + user_id
					+ "&user_name=" + user_name);
			outputStreamWriter.flush();
			outputStreamWriter.close();

			InputStream iStream = connection.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					iStream));

			StringBuffer sb = new StringBuffer();

			String line = "";

			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}

			reader.close();
			iStream.close();

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;

	}

	// dupa ce adaug in baza imi apare o fereastra cu OK!

	@Override
	protected void onPostExecute(Void result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		mDialog.showOK(
				this.mActivity.getResources().getString(R.string.added_title),
				this.mActivity.getResources().getString(R.string.added_message), "save");

		//mActivity.finish();

	}

}
