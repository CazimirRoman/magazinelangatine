package magazinelangatine;

import magazoo.magazine.langa.tine.R;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.LoginButton;

public class Login_screen extends Activity {

	private LoginButton mButtonLogin;
	
	private TextView tvPleaseLogin;

	private UiLifecycleHelper uiHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		uiHelper = new UiLifecycleHelper(this, callback);
		uiHelper.onCreate(savedInstanceState);
		setContentView(R.layout.login_screen);
		
		Typeface tf = Typeface.createFromAsset(getAssets(),
				"fonts/comfortaa-regular.ttf");
		
		tvPleaseLogin = (TextView) findViewById(R.id.tv_please_login);
		
		tvPleaseLogin.setTypeface(tf);

		mButtonLogin = (LoginButton) findViewById(R.id.btnFacebook);

	}

	@Override
	public void onResume() {
		super.onResume();

		Session session = Session.getActiveSession();
		if (session != null && (session.isOpened() || session.isClosed())) {
			onSessionStateChange(session, session.getState(), null);
		}

		uiHelper.onResume();

	}

	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);

		Session.getActiveSession().onActivityResult(this, requestCode,
				resultCode, data);
		if (Session.getActiveSession() != null
				|| Session.getActiveSession().isOpened()) {
			Intent i = new Intent(Login_screen.this, MLTMainActivity.class);
			startActivity(i);
			finish();
		}
		uiHelper.onActivityResult(requestCode, resultCode, data);

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);

	}

	private void onSessionStateChange(Session session, SessionState state,
			Exception exception) {
		if (state.isOpened()) {

			Intent intent = new Intent(this, MLTMainActivity.class);

			startActivity(intent);
			this.finish();

		} else if (state.isClosed()) {

		}
	}

	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};

}
