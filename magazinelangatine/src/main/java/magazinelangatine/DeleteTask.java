package magazinelangatine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;


public class DeleteTask extends AsyncTask<Object, Void, Void> {

	String collNumeMag;
	Double lat;
	Double lng;
	Boolean collNonStop;
	VeziMag activity;
	String tipmagsel;
	Context context;
	String collDescriereMag;
	Activity DeleteTask;
	String LVsel; 
	String id_mag;
	String Sambatasel; 
	String Duminicasel;
	
	@Override
	protected void onPreExecute() {

	}

	

	// imi trebuie un CONSTRUCTOR nou pentru atatea detalii pe care trebuie sa
	// le pasez si sa le salvez in baza
	public DeleteTask(String id_mag, VeziMag activity) {
		this.id_mag = id_mag;
		this.activity = activity;
	

	}

	protected Void doInBackground(Object... params) {

		String idmag = (String) params[0];
		
		String strUrl = "http://atelieruldegermana.ro/android_connect/delete_shop.php";
		URL url = null;
		try {
			url = new URL(strUrl);

			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
					connection.getOutputStream());

			outputStreamWriter.write("&id=" + idmag);
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
//dupa ce adaug in baza imi apare o fereastra cu OK!
	
	@Override
	protected void onPostExecute(Void result) {
		// TODO Auto-generated method stub
		//super.onPostExecute(result);
		AlertDialog StergeMag = new AlertDialog.Builder(activity).create();
		StergeMag.setTitle("Succes!");
		StergeMag.setMessage("Magazin sters...");
		StergeMag.setButton(AlertDialog.BUTTON_POSITIVE, "OK!",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						Intent i = new Intent (activity, MLTMainActivity.class);
						activity.startActivity(i);
						
					};

				});

		StergeMag.show();
		
	}

}
