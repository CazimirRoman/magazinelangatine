package magazinelangatine;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import magazinelangatine.util.ConnectionDetector;
import magazinelangatine.util.ConnectionUtils;
import magazinelangatine.util.DialogCreator;
import magazoo.magazine.langa.tine.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

public class Splash extends Activity {

	private ConnectionUtils mInternetOn;

	String responseString;
	Boolean isInternetPresent = false;
	ConnectionDetector cd;
	
	private DialogCreator mDialog = new DialogCreator(this);

	String language = Locale.getDefault().getDisplayLanguage();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		int layoutId = 0;
		if (language.equals("română")) {
			layoutId = R.layout.splash_rom;
		} else {
			layoutId = R.layout.splash;
		}
		setContentView(layoutId);

		Typeface tf = Typeface.createFromAsset(getAssets(),
				"fonts/comfortaa-regular.ttf");
		TextView NumarMag = (TextView) findViewById(R.id.tvNumarMag2);
		TextView Astazi = (TextView) findViewById(R.id.tvDuma);
		TextView demag = (TextView) findViewById(R.id.tvdemagazine);

		Astazi.setTypeface(tf);
		NumarMag.setTypeface(tf);
		demag.setTypeface(tf);

		if (ConnectionUtils.isConnected(Splash.this)) {
			RetrieveTaskNumarMag Cheamanrmag = (RetrieveTaskNumarMag) new RetrieveTaskNumarMag();
			Cheamanrmag.execute();
		} else {
			mDialog.showInternet(getString(R.string.internet_down_title), getString(R.string.internet_down));
		}

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		finish();
	}

	public class RetrieveTaskNumarMag extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... uri) {
			HttpClient httpClient = new DefaultHttpClient();
			String url = "http://atelieruldegermana.ro/android_connect/get_all_shops_count.php";
			HttpGet httpGet = new HttpGet(url);
			try {
				HttpResponse response = httpClient.execute(httpGet);
				StatusLine statusLine = response.getStatusLine();
				if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
					HttpEntity entity = response.getEntity();
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					entity.writeTo(out);
					out.close();
					responseString = out.toString();

				} else {
					// Closes the connection.
					response.getEntity().getContent().close();
					throw new IOException(statusLine.getReasonPhrase());
				}
			} catch (ClientProtocolException e) {
				Toast.makeText(Splash.this, "Caught ClientProtocolException",
						Toast.LENGTH_SHORT).show();
			} catch (IOException e) {
				Toast.makeText(Splash.this, "Exception", Toast.LENGTH_SHORT)
						.show();
			}

			return responseString.trim();
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (result != null) {
				TextView NumarMag = (TextView) findViewById(R.id.tvNumarMag2);
				NumarMag.setText(result);

				Intent openStartingPoint = new Intent(Splash.this,
						Login_screen.class);
				startActivity(openStartingPoint);

			}
		}
	}

	private void buildAlertMessageNoNetwork() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(Splash.this);
		builder.setMessage(getString(R.string.internet_down))
				.setCancelable(false)
				.setPositiveButton(getString(R.string.positive),
						new DialogInterface.OnClickListener() {
							public void onClick(final DialogInterface dialog,
									final int id) {
								startActivity(new Intent(
										android.provider.Settings.ACTION_WIRELESS_SETTINGS));
							}
						});

		final AlertDialog alert = builder.create();
		alert.show();

	}

}
