package magazinelangatine;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import hirondelle.date4j.DateTime;
import magazinelangatine.MyLocation.LocationResult;
import magazinelangatine.model.Store;
import magazinelangatine.util.ConnectionDetector;
import magazinelangatine.util.ConnectionUtils;
import magazinelangatine.util.DialogCreator;
import magazinelangatine.util.MarkerJSONParser;
import magazoo.magazine.langa.tine.R;

@SuppressWarnings("deprecation")
public class MLTMainActivity extends FragmentActivity implements
        OnMapClickListener, OnInfoWindowClickListener, OnMapLongClickListener {

    private RelativeLayout rlNavBar;

    private int mAddCounter;
    private Boolean mWait = true;
    private String mCurrDate;
    private String gotDate4diff;

    private GoogleMap mMap;
    @SuppressWarnings("rawtypes")
    private Map<Marker, Class> allMarkersMap = new HashMap<Marker, Class>();

    private Marker lastOpened = null;
    private Marker mMarkerOnTouch = null;

    private String responseString;

    private ProgressDialog mProgressDialog;

    private ImageButton btnFilter;
    private ImageButton ibAddStore;

    private Button btnGotIt;

    private ImageButton ibClose;

    private PopupWindow mPopupFilterWindow;
    private PopupWindow mPopupAddStore;
    private PopupWindow mPopupAdd;

    private String NElat2;
    private String NElon2;
    private String SWlat2;
    private String SWlon2;

    private TextView tvPlease;

    private CheckBox chkToate;
    private CheckBox chkNonstop;
    private CheckBox chkPOS;

    private ImageButton ibPiata;
    private ImageButton ibSupermarket;
    private ImageButton ibDeCartier;
    private ImageButton ibBenzinarie;

    private TextView tvPiata;
    private TextView tvSupermarket;
    private TextView tvDeCartier;
    private TextView tvBenzinarie;
    private TextView tvNavBarTitle;
    private TextView tvNavBarAdd;

    @SuppressWarnings("deprecation")
    private boolean isResumed = false;

    private UiLifecycleHelper uiHelper;

    private boolean doubleBackToExitPressedOnce = false;

    Double amount1;
    Double amount2;
    Marker marker;
    LatLng locatie;
    // flag for Internet connection status
    Boolean isInternetPresent = false;

    // Connection detector class
    ConnectionDetector cd;

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private TextView tvFBUserID;
    private TextView tvFBUserName;

    Store mStore = new Store();

    private String mFBUserId;
    private String mFBUserName;

    private LinearLayout llPaginaHarta;

    private DialogCreator mDialog = new DialogCreator(this);

    private RelativeLayout rlNavBar_Add;

    // ONCREATE!!

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);

        // for FB login maintain session state
        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);

        setContentView(R.layout.pagina_harta);

        initialize();

        cd = new ConnectionDetector(getApplicationContext());
        // get Internet status
        isInternetPresent = cd.isConnectingToInternet();

        // if no internet
        if (isInternetPresent == false) {
            buildAlertMessageNoNetwork();
        } else {
            if (checkPlayServices()) {

                setUpMap();

            }

            // VERIFICAM daca merge GPS

            final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                buildAlertMessageNoGps();
            }

        }


        // check if daily quota for adding shops has been reached
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(MLTMainActivity.this);

        mCurrDate = prefs.getString("date", "");

        if (prefs != null) {

            // if date present in shared prefs
            if (mCurrDate != null) {

                Boolean anewday = diffDate();

                // if a new day, reset counter & set new date in shared prefs

                if (anewday) {

                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putInt("addcounter", 0);
                    editor.putString("date", gotDate4diff);
                    editor.commit();

                }

            }

        }

        Session session = Session.getActiveSession();
        makeMeRequest(session);

        chkNonstop.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (chkToate.isChecked()) {
                    chkToate.setChecked(false);
                }

                return false;
            }
        });

        chkPOS.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (chkToate.isChecked()) {
                    chkToate.setChecked(false);
                }

                return false;
            }
        });

        btnFilter.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (mPopupFilterWindow.isShowing()) {
                    mPopupFilterWindow.dismiss();
                } else {
                    mPopupFilterWindow.showAsDropDown(btnFilter, 50, -30);
                }

            }

            ;
        });

        ibAddStore.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (mPopupAddStore.isShowing()) {
                    mPopupAddStore.dismiss();
                } else {
                    mPopupAddStore.showAtLocation(ibAddStore, Gravity.CENTER,
                            0, 0);
                }

                rlNavBar_Add.setVisibility(View.GONE);
                rlNavBar.setVisibility(View.GONE);

                mMap.clear();

                mMap.animateCamera(CameraUpdateFactory.zoomTo(19));

                mMap.setOnMapLongClickListener(new OnMapLongClickListener() {

                    @Override
                    public void onMapLongClick(LatLng arg0) {

                        // do nothing

                    }
                });

                mMap.setOnCameraChangeListener(new OnCameraChangeListener() {

                    @Override
                    public void onCameraChange(CameraPosition arg0) {
                        // do nothing

                    }
                });

                mMap.setOnMapLongClickListener(MLTMainActivity.this);

            }
        });

        chkToate.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if (isChecked) {
                    chkNonstop.setChecked(false);
                    chkPOS.setChecked(false);

                    LatLngBounds bounds = MLTMainActivity.this.mMap
                            .getProjection().getVisibleRegion().latLngBounds;

                    DecimalFormat df = new DecimalFormat("##.######");
                    DecimalFormatSymbols dfs = new DecimalFormatSymbols();
                    dfs.setDecimalSeparator('.');
                    df.setDecimalFormatSymbols(dfs);

                    Double NElat = bounds.northeast.latitude;

                    NElat2 = df.format(NElat).toString();

                    Double NElon = bounds.northeast.longitude;
                    NElon2 = df.format(NElon).toString();

                    Double SWlat = bounds.southwest.latitude;
                    SWlat2 = df.format(SWlat).toString();

                    Double SWlon = bounds.southwest.longitude;
                    SWlon2 = df.format(SWlon).toString();

                    mMap.clear();

                    Toast.makeText(
                            MLTMainActivity.this,
                            getResources().getString(R.string.filtru_toate_mag),
                            Toast.LENGTH_SHORT).show();

                    Log.d("Magazoo",
                            "Retrieve Task called from on checked change");
                    if (ConnectionUtils.isConnected(MLTMainActivity.this)) {
                        RetrieveTask toate = (RetrieveTask) new RetrieveTask()
                                .execute(NElat2, NElon2, SWlat2, SWlon2);
                    } else {
                        buildAlertMessageNoNetwork();
                    }

                }

            }
        });

        chkNonstop.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                // daca e bifat
                if (isChecked) {

                    if (!chkPOS.isChecked()) {

                        LatLngBounds bounds = MLTMainActivity.this.mMap
                                .getProjection().getVisibleRegion().latLngBounds;

                        DecimalFormat df = new DecimalFormat("##.######");
                        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
                        dfs.setDecimalSeparator('.');
                        df.setDecimalFormatSymbols(dfs);

                        Double NElat = bounds.northeast.latitude;

                        NElat2 = df.format(NElat).toString();

                        Double NElon = bounds.northeast.longitude;
                        NElon2 = df.format(NElon).toString();

                        Double SWlat = bounds.southwest.latitude;
                        SWlat2 = df.format(SWlat).toString();

                        Double SWlon = bounds.southwest.longitude;
                        SWlon2 = df.format(SWlon).toString();

                        mMap.clear();

                        Toast.makeText(MLTMainActivity.this,
                                getResources().getString(R.string.filtru_ns),
                                Toast.LENGTH_SHORT).show();

                        if (ConnectionUtils.isConnected(MLTMainActivity.this)) {
                            RetrieveTaskNS NS = (RetrieveTaskNS) new RetrieveTaskNS()
                                    .execute(NElat2, NElon2, SWlat2, SWlon2);
                        } else {
                            buildAlertMessageNoNetwork();
                        }

                    } else {

                        LatLngBounds bounds = MLTMainActivity.this.mMap
                                .getProjection().getVisibleRegion().latLngBounds;

                        DecimalFormat df = new DecimalFormat("##.######");
                        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
                        dfs.setDecimalSeparator('.');
                        df.setDecimalFormatSymbols(dfs);

                        Double NElat = bounds.northeast.latitude;

                        NElat2 = df.format(NElat).toString();

                        Double NElon = bounds.northeast.longitude;
                        NElon2 = df.format(NElon).toString();

                        Double SWlat = bounds.southwest.latitude;
                        SWlat2 = df.format(SWlat).toString();

                        Double SWlon = bounds.southwest.longitude;
                        SWlon2 = df.format(SWlon).toString();

                        mMap.clear();

                        // RetrieveTaskNS_POS NS_cucard = (RetrieveTaskNS_POS)
                        // new RetrieveTaskNS_POS()
                        // .execute(NElat2, NElon2, SWlat2, SWlon2);

                        chkNonstop.setChecked(false);
                        chkPOS.setChecked(false);
                        chkToate.setChecked(true);

                    }

                    // uncheck action
                } else {

                    if (chkPOS.isChecked() == false
                            && chkToate.isChecked() == false) {

                        LatLngBounds bounds = MLTMainActivity.this.mMap
                                .getProjection().getVisibleRegion().latLngBounds;

                        DecimalFormat df = new DecimalFormat("##.######");
                        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
                        dfs.setDecimalSeparator('.');
                        df.setDecimalFormatSymbols(dfs);

                        Double NElat = bounds.northeast.latitude;

                        NElat2 = df.format(NElat).toString();

                        Double NElon = bounds.northeast.longitude;
                        NElon2 = df.format(NElon).toString();

                        Double SWlat = bounds.southwest.latitude;
                        SWlat2 = df.format(SWlat).toString();

                        Double SWlon = bounds.southwest.longitude;
                        SWlon2 = df.format(SWlon).toString();

                        mMap.clear();

                        chkToate.setChecked(true);

                    } else if (chkPOS.isChecked() == true
                            && chkToate.isChecked() == false) {

                        mMap.clear();

                        Toast.makeText(MLTMainActivity.this,
                                getResources().getString(R.string.filtru_pos),
                                Toast.LENGTH_SHORT).show();

                        Log.d("Magazoo",
                                "Retrieve Task called from on checked change");
                        if (ConnectionUtils.isConnected(MLTMainActivity.this)) {
                            RetrieveTask cucard = (RetrieveTask) new RetrieveTask()
                                    .execute(NElat2, NElon2, SWlat2, SWlon2);
                        } else {
                            buildAlertMessageNoNetwork();
                        }

                    }

                }

            }
        });

        chkPOS.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                // bifat
                if (isChecked) {

                    if (!chkNonstop.isChecked()) {

                        LatLngBounds bounds = MLTMainActivity.this.mMap
                                .getProjection().getVisibleRegion().latLngBounds;

                        DecimalFormat df = new DecimalFormat("##.######");
                        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
                        dfs.setDecimalSeparator('.');
                        df.setDecimalFormatSymbols(dfs);

                        Double NElat = bounds.northeast.latitude;

                        NElat2 = df.format(NElat).toString();

                        Double NElon = bounds.northeast.longitude;
                        NElon2 = df.format(NElon).toString();

                        Double SWlat = bounds.southwest.latitude;
                        SWlat2 = df.format(SWlat).toString();

                        Double SWlon = bounds.southwest.longitude;
                        SWlon2 = df.format(SWlon).toString();

                        mMap.clear();

                        Toast.makeText(MLTMainActivity.this,
                                getResources().getString(R.string.filtru_pos),
                                Toast.LENGTH_SHORT).show();

                        if (ConnectionUtils.isConnected(MLTMainActivity.this)) {
                            RetrieveTaskPOS cucard = (RetrieveTaskPOS) new RetrieveTaskPOS()
                                    .execute(NElat2, NElon2, SWlat2, SWlon2);
                        } else {
                            buildAlertMessageNoNetwork();
                        }

                    } else {

                        LatLngBounds bounds = MLTMainActivity.this.mMap
                                .getProjection().getVisibleRegion().latLngBounds;

                        DecimalFormat df = new DecimalFormat("##.######");
                        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
                        dfs.setDecimalSeparator('.');
                        df.setDecimalFormatSymbols(dfs);

                        Double NElat = bounds.northeast.latitude;

                        NElat2 = df.format(NElat).toString();

                        Double NElon = bounds.northeast.longitude;
                        NElon2 = df.format(NElon).toString();

                        Double SWlat = bounds.southwest.latitude;
                        SWlat2 = df.format(SWlat).toString();

                        Double SWlon = bounds.southwest.longitude;
                        SWlon2 = df.format(SWlon).toString();

                        mMap.clear();

                        Toast.makeText(
                                MLTMainActivity.this,
                                getResources().getString(
                                        R.string.filtru_ns_card),
                                Toast.LENGTH_SHORT).show();

                        if (ConnectionUtils.isConnected(MLTMainActivity.this)) {
                            RetrieveTaskNS_POS NS_cucard = (RetrieveTaskNS_POS) new RetrieveTaskNS_POS()
                                    .execute(NElat2, NElon2, SWlat2, SWlon2);
                        } else {
                            buildAlertMessageNoNetwork();
                        }
                    }
                    // uncheck action
                } else {

                    if (chkNonstop.isChecked() == true
                            && chkToate.isChecked() == false) {

                        LatLngBounds bounds = MLTMainActivity.this.mMap
                                .getProjection().getVisibleRegion().latLngBounds;

                        DecimalFormat df = new DecimalFormat("##.######");
                        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
                        dfs.setDecimalSeparator('.');
                        df.setDecimalFormatSymbols(dfs);

                        Double NElat = bounds.northeast.latitude;

                        NElat2 = df.format(NElat).toString();

                        Double NElon = bounds.northeast.longitude;
                        NElon2 = df.format(NElon).toString();

                        Double SWlat = bounds.southwest.latitude;
                        SWlat2 = df.format(SWlat).toString();

                        Double SWlon = bounds.southwest.longitude;
                        SWlon2 = df.format(SWlon).toString();

                        mMap.clear();

                        Toast.makeText(MLTMainActivity.this,
                                getResources().getString(R.string.filtru_ns),
                                Toast.LENGTH_SHORT).show();

                        if (ConnectionUtils.isConnected(MLTMainActivity.this)) {
                            RetrieveTaskNS NS = (RetrieveTaskNS) new RetrieveTaskNS()
                                    .execute(NElat2, NElon2, SWlat2, SWlon2);
                        } else {
                            buildAlertMessageNoNetwork();
                        }

                    } else if (chkNonstop.isChecked() == false
                            && chkToate.isChecked() == false) {

                        mMap.clear();
                        chkToate.setChecked(true);

                    }
                }

            }
        });

        map_camera_change();
    }

    private Boolean diffDate() {

        DateTime pagerdate = DateTime.now(TimeZone.getDefault());
        gotDate4diff = pagerdate.format("DD-MM-YYYY").toString();

        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(MLTMainActivity.this);

        String spDate = prefs.getString("date", "");

        if (gotDate4diff.equals(spDate)) {
            Log.d("Diferenta de data", "Aceeasi zi");
            return false;

        } else {
            Log.d("Diferenta de data", "O noua zi");
            return true;
        }

    }

    private void initialize() {

        mMap = ((SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map)).getMap();
        rlNavBar = (RelativeLayout) findViewById(R.id.rlNavBar);
        rlNavBar_Add = (RelativeLayout) findViewById(R.id.rlNavBar_Add);
        tvNavBarTitle = (TextView) findViewById(R.id.tvNavBarTitle);
        tvNavBarAdd = (TextView) findViewById(R.id.tvAddMode);
        btnFilter = (ImageButton) findViewById(R.id.btnFilter);
        ibAddStore = (ImageButton) findViewById(R.id.ibStoreType);
        ibClose = (ImageButton) findViewById(R.id.ibClose);

        ibClose.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                rlNavBar_Add.setVisibility(View.GONE);
                rlNavBar.setVisibility(View.VISIBLE);

                mMap.clear();

                mMap.getUiSettings().setZoomGesturesEnabled(true);

                // activate camera change listener
                map_camera_change();

                // disable on Map Long Click

                mMap.setOnMapLongClickListener(new OnMapLongClickListener() {

                    @Override
                    public void onMapLongClick(LatLng arg0) {
                        // disable on Map Long Click

                    }
                });

                final ProgressDialog asteptam = new ProgressDialog(
                        MLTMainActivity.this);
                asteptam.setTitle(getString(R.string.title_waiting_location));
                asteptam.setMessage(getString(R.string.text_waiting_location));
                asteptam.setCancelable(true);
                asteptam.show();

                try {
                    // Acquire a reference to the system Location Manager

                    MyLocation myLocation = new MyLocation();

                    LocationResult locationResult = new LocationResult() {
                        @Override
                        public void gotLocation(Location location) {

                            if (location != null) {

                                // map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                // new LatLng(location.getLatitude(), location
                                // .getLongitude()), 17));

                                CameraPosition cameraPosition = new CameraPosition.Builder()
                                        .target(new LatLng(location
                                                .getLatitude(), location
                                                .getLongitude())) // Sets
                                                // the
                                                // center
                                                // of
                                                // the
                                                // map
                                                // to
                                                // location
                                                // user
                                        .zoom(16) // Sets the zoom
                                        .build(); // Creates a CameraPosition
                                // from
                                // the
                                // builder
                                mMap.animateCamera(CameraUpdateFactory
                                        .newCameraPosition(cameraPosition));
                                asteptam.dismiss();

                            }

                        }
                    };

                    myLocation
                            .getLocation(MLTMainActivity.this, locationResult);

                } finally {
                    // show all shops

                    LatLngBounds bounds = MLTMainActivity.this.mMap
                            .getProjection().getVisibleRegion().latLngBounds;

                    DecimalFormat df = new DecimalFormat("##.######");
                    DecimalFormatSymbols dfs = new DecimalFormatSymbols();
                    dfs.setDecimalSeparator('.');
                    df.setDecimalFormatSymbols(dfs);

                    Double NElat = bounds.northeast.latitude;

                    NElat2 = df.format(NElat).toString();

                    Double NElon = bounds.northeast.longitude;
                    NElon2 = df.format(NElon).toString();

                    Double SWlat = bounds.southwest.latitude;
                    SWlat2 = df.format(SWlat).toString();

                    Double SWlon = bounds.southwest.longitude;
                    SWlon2 = df.format(SWlon).toString();

                    mMap.clear();

                    Toast.makeText(
                            MLTMainActivity.this,
                            getResources().getString(R.string.filtru_toate_mag),
                            Toast.LENGTH_SHORT).show();

                    Log.d("Magazoo",
                            "Retrieve Task called from on checked change");

                    if (ConnectionUtils.isConnected(MLTMainActivity.this)) {

                        RetrieveTask toate = (RetrieveTask) new RetrieveTask()
                                .execute(NElat2, NElon2, SWlat2, SWlon2);
                    } else {

                        buildAlertMessageNoNetwork();

                    }
                }

            }


        });

        mProgressDialog = new ProgressDialog(MLTMainActivity.this);
        mProgressDialog.setTitle(getString(R.string.title_loading_shops));
        mProgressDialog.setMessage(getString(R.string.text_loading_shops));
        mProgressDialog.setCancelable(true);



        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);

        Typeface tf = Typeface.createFromAsset(getAssets(),
                "fonts/comfortaa-regular.ttf");

        // filter popup
        View filter_popup = layoutInflater.inflate(R.layout.popup_filter, null);

        mPopupFilterWindow = new PopupWindow(filter_popup,
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        chkToate = (CheckBox) mPopupFilterWindow.getContentView().findViewById(
                R.id.chkToate);
        chkNonstop = (CheckBox) mPopupFilterWindow.getContentView()
                .findViewById(R.id.chkNonStop);
        chkPOS = (CheckBox) mPopupFilterWindow.getContentView().findViewById(
                R.id.chkPOS);
        tvPlease = (TextView) mPopupFilterWindow.getContentView().findViewById(
                R.id.tvPlease);

        tvNavBarTitle.setTypeface(tf);
        tvNavBarAdd.setTypeface(tf);

        chkToate.setTypeface(tf);
        chkNonstop.setTypeface(tf);
        chkPOS.setTypeface(tf);
        tvPlease.setTypeface(tf);

        // store type popup
        View add_popup = layoutInflater.inflate(R.layout.popup_add, null);

        mPopupAddStore = new PopupWindow(add_popup, LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        mPopupAddStore.setOutsideTouchable(isLoggedIn());

        btnGotIt = (Button) add_popup.findViewById(R.id.btnGotIt);

        btnGotIt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (isLoggedIn()) {
                    mPopupAddStore.dismiss();
                } else {
                    mDialog.showOK("Not logged in",
                            "Please login with Facebook \n to add shops.",
                            "main");
                }

                rlNavBar_Add.setVisibility(View.VISIBLE);
                mMap.getUiSettings().setZoomGesturesEnabled(false);
                // rlNavBar.setVisibility(View.VISIBLE);

            }
        });

        // ibPiata = (ImageButton) type_popup.findViewById(R.id.ibPiata);
        // ibSupermarket = (ImageButton) type_popup
        // .findViewById(R.id.ibSupermarket);
        // ibDeCartier = (ImageButton)
        // type_popup.findViewById(R.id.ibDeCartier);
        // ibBenzinarie = (ImageButton)
        // type_popup.findViewById(R.id.ibBenzinarie);
        //
        // tvPiata = (TextView) type_popup.findViewById(R.id.tvPiata);
        // tvSupermarket = (TextView)
        // type_popup.findViewById(R.id.tvSupermarket);
        // tvDeCartier = (TextView) type_popup.findViewById(R.id.tvdeCartier);
        // tvBenzinarie = (TextView) type_popup.findViewById(R.id.tvBenz);
        //
        // tvPiata.setTypeface(tf);
        // tvSupermarket.setTypeface(tf);
        // tvDeCartier.setTypeface(tf);
        // tvBenzinarie.setTypeface(tf);

        // ibPiata.setOnClickListener(new View.OnClickListener() {
        //
        // @Override
        // public void onClick(View v) {
        //
        // mMap.clear();
        //
        // LatLngBounds bounds = MLTMainActivity.this.mMap.getProjection()
        // .getVisibleRegion().latLngBounds;
        //
        // DecimalFormat df = new DecimalFormat("##.######");
        // DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        // dfs.setDecimalSeparator('.');
        // df.setDecimalFormatSymbols(dfs);
        // Double NElat = bounds.northeast.latitude;
        //
        // String NElat2 = df.format(NElat).toString();
        //
        // Double NElon = bounds.northeast.longitude;
        // String NElon2 = df.format(NElon).toString();
        //
        // Double SWlat = bounds.southwest.latitude;
        // String SWlat2 = df.format(SWlat).toString();
        //
        // Double SWlon = bounds.southwest.longitude;
        // String SWlon2 = df.format(SWlon).toString();
        //
        // RetrieveTaskPiata piata = (RetrieveTaskPiata) new
        // RetrieveTaskPiata();
        // piata.execute(NElat2, NElon2, SWlat2, SWlon2);
        //
        // mPopupTypeWindow.dismiss();
        //
        // }
        // });
        //
        // ibSupermarket.setOnClickListener(new View.OnClickListener() {
        //
        // @Override
        // public void onClick(View v) {
        //
        // mMap.clear();
        //
        // LatLngBounds bounds = MLTMainActivity.this.mMap.getProjection()
        // .getVisibleRegion().latLngBounds;
        //
        // DecimalFormat df = new DecimalFormat("##.######");
        // DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        // dfs.setDecimalSeparator('.');
        // df.setDecimalFormatSymbols(dfs);
        // Double NElat = bounds.northeast.latitude;
        //
        // String NElat2 = df.format(NElat).toString();
        //
        // Double NElon = bounds.northeast.longitude;
        // String NElon2 = df.format(NElon).toString();
        //
        // Double SWlat = bounds.southwest.latitude;
        // String SWlat2 = df.format(SWlat).toString();
        //
        // Double SWlon = bounds.southwest.longitude;
        // String SWlon2 = df.format(SWlon).toString();
        //
        // RetrieveTaskSupermarket supermarket = (RetrieveTaskSupermarket) new
        // RetrieveTaskSupermarket();
        // supermarket.execute(NElat2, NElon2, SWlat2, SWlon2);
        //
        // mPopupTypeWindow.dismiss();
        //
        // }
        // });
        //
        // ibBenzinarie.setOnClickListener(new View.OnClickListener() {
        //
        // @Override
        // public void onClick(View v) {
        //
        // mMap.clear();
        //
        // LatLngBounds bounds = MLTMainActivity.this.mMap.getProjection()
        // .getVisibleRegion().latLngBounds;
        //
        // DecimalFormat df = new DecimalFormat("##.######");
        // DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        // dfs.setDecimalSeparator('.');
        // df.setDecimalFormatSymbols(dfs);
        // Double NElat = bounds.northeast.latitude;
        //
        // String NElat2 = df.format(NElat).toString();
        //
        // Double NElon = bounds.northeast.longitude;
        // String NElon2 = df.format(NElon).toString();
        //
        // Double SWlat = bounds.southwest.latitude;
        // String SWlat2 = df.format(SWlat).toString();
        //
        // Double SWlon = bounds.southwest.longitude;
        // String SWlon2 = df.format(SWlon).toString();
        //
        // RetrieveTaskBenz benzinarie = (RetrieveTaskBenz) new
        // RetrieveTaskBenz();
        // benzinarie.execute(NElat2, NElon2, SWlat2, SWlon2);
        //
        // mPopupTypeWindow.dismiss();
        //
        // }
        // });
        //
        // ibDeCartier.setOnClickListener(new View.OnClickListener() {
        //
        // @Override
        // public void onClick(View v) {
        //
        // mMap.clear();
        //
        // LatLngBounds bounds = MLTMainActivity.this.mMap.getProjection()
        // .getVisibleRegion().latLngBounds;
        //
        // DecimalFormat df = new DecimalFormat("##.######");
        // DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        // dfs.setDecimalSeparator('.');
        // df.setDecimalFormatSymbols(dfs);
        // Double NElat = bounds.northeast.latitude;
        //
        // String NElat2 = df.format(NElat).toString();
        //
        // Double NElon = bounds.northeast.longitude;
        // String NElon2 = df.format(NElon).toString();
        //
        // Double SWlat = bounds.southwest.latitude;
        // String SWlat2 = df.format(SWlat).toString();
        //
        // Double SWlon = bounds.southwest.longitude;
        // String SWlon2 = df.format(SWlon).toString();
        //
        // RetrieveTaskCartier decartier = (RetrieveTaskCartier) new
        // RetrieveTaskCartier();
        // decartier.execute(NElat2, NElon2, SWlat2, SWlon2);
        //
        // mPopupTypeWindow.dismiss();
        //
        // }
        // });

        tvFBUserID = (TextView) findViewById(R.id.tvFBID);
        tvFBUserName = (TextView) findViewById(R.id.tvFBName);



    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                String TAG = null;
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        // Monitor launch times and interval from installation
        // RateThisApp.onStart(this);
        // If the criteria is satisfied, "Rate this app" dialog will be shown
        // RateThisApp.showRateDialogIfNeeded(this);
    }

    private void setUpMapIfNeeded() {

        if (mMap == null) {
            mMap = ((SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map)).getMap();

            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            } else {
                GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
            }
        } else {
            setUpMap();
        }
    }

    private void setUpMap() {

        mWait = true;

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        mMap.setOnMyLocationButtonClickListener(new OnMyLocationButtonClickListener() {

            @Override
            public boolean onMyLocationButtonClick() {

                final ProgressDialog asteptam = new ProgressDialog(
                        MLTMainActivity.this);
                asteptam.setTitle(getString(R.string.title_waiting_location));
                asteptam.setMessage(getString(R.string.text_waiting_location));
                asteptam.setCancelable(true);
                asteptam.show();

                // Acquire a reference to the system Location Manager

                MyLocation myLocation = new MyLocation();

                LocationResult locationResult = new LocationResult() {
                    @Override
                    public void gotLocation(Location location) {

                        if (location != null) {

                            // map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                            // new LatLng(location.getLatitude(), location
                            // .getLongitude()), 17));

                            CameraPosition cameraPosition = new CameraPosition.Builder()
                                    .target(new LatLng(location.getLatitude(),
                                            location.getLongitude())) // Sets
                                            // the
                                            // center
                                            // of
                                            // the
                                            // map
                                            // to
                                            // location
                                            // user
                                    .zoom(16) // Sets the zoom
                                    .build(); // Creates a CameraPosition from
                            // the
                            // builder
                            mMap.moveCamera(CameraUpdateFactory
                                    .newCameraPosition(cameraPosition));


                        }

                    }
                };

                myLocation.getLocation(MLTMainActivity.this, locationResult);

                asteptam.dismiss();

                mWait = false;

                return false;

            }

        });
        mMap.setOnMapClickListener(this);
        // mMap.getUiSettings().setZoomGesturesEnabled(false);

        // mMap.setOnMapLongClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.setInfoWindowAdapter(new InfoWindowAdapter() {

                                      @Override
                                      public View getInfoWindow(Marker marker) {
                                          // TODO Auto-generated method stub
                                          return null;
                                      }

                                      @Override
                                      public View getInfoContents(Marker marker) {
                                          // TODO Auto-generated method stub
                                          View v = getLayoutInflater().inflate(
                                                  R.layout.custom_info_window, null);
                                          v.setLayoutParams(new RelativeLayout.LayoutParams(
                                                  RelativeLayout.LayoutParams.WRAP_CONTENT,
                                                  RelativeLayout.LayoutParams.WRAP_CONTENT));

                                          String numemag = marker.getTitle();
                                          String idmagstrg = marker.getSnippet();

                                          Typeface tf = Typeface.createFromAsset(getAssets(),
                                                  "fonts/comfortaa-regular.ttf");
                                          TextView numeMag = (TextView) v.findViewById(R.id.tv_name);
                                          TextView idMag = (TextView) v.findViewById(R.id.tv_id);
                                          TextView tvPOstatic = (TextView) v
                                                  .findViewById(R.id.tvPOSstatic);
                                          Button btnTest = (Button) v.findViewById(R.id.btnTest);

                                          btnTest.setOnClickListener(new View.OnClickListener() {
                                              @Override
                                              public void onClick(View v) {

                                                  Toast.makeText(MLTMainActivity.this, "Test", Toast.LENGTH_SHORT).show();


                                              }

                                          });

                                          numeMag.setText(numemag);
                                          numeMag.setTypeface(tf);
                                          tvPOstatic.setTypeface(tf);
                                          idMag.setText(idmagstrg);

                                          return v;
                                      }
                                  }

        );

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()

                                      {

                                          @Override
                                          public boolean onMarkerClick(Marker marker) {

                                              // Nu mai centra markerul pe harta la click pe el

                                              // Check if there is an open info window
                                              if (lastOpened != null) {
                                                  // Close the info window
                                                  lastOpened.hideInfoWindow();

                                                  // Is the marker the same marker that was already open
                                                  if (lastOpened.equals(marker)) {
                                                      // Nullify the lastOpened object
                                                      lastOpened = null;
                                                      // Return so that the info window isn't opened again
                                                      return true;
                                                  }
                                              }

                                              // Open the info window for the marker
                                              marker.showInfoWindow();
                                              // Re-assign the last opened such that we can close it later
                                              lastOpened = marker;

                                              return true;
                                          }
                                      }

        );


    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        uiHelper.onPause();

    }

    // metoda care ma gaseste automat cand porneste aplicatia

    private void zoomonuser() {

        final ProgressDialog asteptam = new ProgressDialog(MLTMainActivity.this);
        asteptam.setTitle(getString(R.string.title_waiting_location));
        asteptam.setMessage(getString(R.string.text_waiting_location));
        asteptam.setCancelable(true);
        asteptam.show();

        // Acquire a reference to the system Location Manager

        MyLocation myLocation = new MyLocation();

        LocationResult locationResult = new LocationResult() {
            @Override
            public void gotLocation(Location location) {

                if (location != null) {

                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(new LatLng(location.getLatitude(), location
                                    .getLongitude())) // Sets the
                                    // center of
                                    // the
                                    // map to
                                    // location
                                    // user
                            .zoom(16) // Sets the zoom
                            .build(); // Creates a CameraPosition from
                    // the
                    // builder
                    Log.d("Magazoo", "map moved from zoomuser");
                    mMap.moveCamera(CameraUpdateFactory
                            .newCameraPosition(cameraPosition));

                    mWait = false;

                    if (asteptam.isShowing()) {
                        asteptam.dismiss();
                    }

                }

            }
        };

        myLocation.getLocation(this, locationResult);

        asteptam.dismiss();

        // LatLngBounds bounds = MLTMainActivity.this.mMap.getProjection()
        // .getVisibleRegion().latLngBounds;
        //
        // DecimalFormat df = new DecimalFormat("##.######");
        // DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        // dfs.setDecimalSeparator('.');
        // df.setDecimalFormatSymbols(dfs);
        // Double NElat = bounds.northeast.latitude;
        //
        // String NElat2 = df.format(NElat).toString();
        //
        // Double NElon = bounds.northeast.longitude;
        // String NElon2 = df.format(NElon).toString();
        //
        // Double SWlat = bounds.southwest.latitude;
        // String SWlat2 = df.format(SWlat).toString();
        //
        // Double SWlon = bounds.southwest.longitude;
        // String SWlon2 = df.format(SWlon).toString();
        //
        // Log.d("Magazoo", "Retrieve Task called from zoomonuser");
        // RetrieveTask toatemag = (RetrieveTask) new RetrieveTask();
        // toatemag.execute(NElat2, NElon2, SWlat2, SWlon2);

    }

    private void buildAlertMessageNoNetwork() {

        mDialog.showInternet(getString(R.string.internet_down_title),
                getString(R.string.internet_down));

    }

    private void buildAlertMessageNoGps() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.gps_down))
                .setCancelable(true)
                .setPositiveButton(getString(R.string.positive),
                        new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog,
                                                final int id) {
                                startActivity(new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            }
                        })
                .setNegativeButton(getString(R.string.negative),
                        new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog,
                                                final int id) {
                                dialog.cancel();
                            }
                        });
        final AlertDialog alert = builder.create();
        alert.show();

    }

    @Override
    public void onMapLongClick(LatLng point) {
        // TODO Auto-generated method stub

        mMap.clear();

        mMarkerOnTouch = mMap.addMarker(new MarkerOptions()
                .position(point)
                .title(getResources().getString(R.string.add_me))
                .icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.icoana_adauga)));
        // adaugam numele markerului asta in Hash si setam oninfoclick pe
        // "this"
        allMarkersMap.put(mMarkerOnTouch, AdaugaMag.class);
        mMarkerOnTouch.showInfoWindow();
        mMap.setOnInfoWindowClickListener(this);

    }

    @Override
    public void onMapClick(LatLng point) {

        if (mPopupFilterWindow.isShowing()) {
            mPopupFilterWindow.dismiss();
        }

        if (mPopupAddStore.isShowing()) {
            mPopupAddStore.dismiss();
        }

        if (mMarkerOnTouch != null && mMarkerOnTouch.isInfoWindowShown()) {
            mMap.clear();

            LatLngBounds bounds = MLTMainActivity.this.mMap.getProjection()
                    .getVisibleRegion().latLngBounds;

            DecimalFormat df = new DecimalFormat("##.######");
            DecimalFormatSymbols dfs = new DecimalFormatSymbols();
            dfs.setDecimalSeparator('.');
            df.setDecimalFormatSymbols(dfs);

            Double NElat = bounds.northeast.latitude;

            NElat2 = df.format(NElat).toString();

            Double NElon = bounds.northeast.longitude;
            NElon2 = df.format(NElon).toString();

            Double SWlat = bounds.southwest.latitude;
            SWlat2 = df.format(SWlat).toString();

            Double SWlon = bounds.southwest.longitude;
            SWlon2 = df.format(SWlon).toString();

            mMap.clear();

            Toast.makeText(MLTMainActivity.this, "Arat toate magazinele",
                    Toast.LENGTH_SHORT).show();

            Log.d("Magazoo", "Retrieve Task called from on map click");
            if (ConnectionUtils.isConnected(MLTMainActivity.this)) {
                RetrieveTask toate = (RetrieveTask) new RetrieveTask().execute(
                        NElat2, NElon2, SWlat2, SWlon2);
            } else {
                buildAlertMessageNoNetwork();
            }
        }

    }

    @Override
    public void onInfoWindowClick(Marker marker) {

        @SuppressWarnings("rawtypes")
        Class cls = allMarkersMap.get(marker);
        Intent intent = new Intent(MLTMainActivity.this, cls);

        // adaugamag
        if (cls == AdaugaMag.class) {
            // /ma duce la activitatea AdaugaMag.java pentru adaugarea
            // magazinului

            SharedPreferences prefs = PreferenceManager
                    .getDefaultSharedPreferences(MLTMainActivity.this);

            // no store added
            if (prefs != null) {

                // check if more than 5 stores added
                mAddCounter = prefs.getInt("addcounter", 0);

                if (mAddCounter > 5) {

                    mDialog.showOK(
                            getResources().getString(R.string.add_limit_title),
                            getResources()
                                    .getString(R.string.add_limit_message),
                            "main");

                    // < 5 stores added
                } else {

                    mAddCounter++;

                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putInt("addcounter", mAddCounter);
                    editor.commit();

                    LatLng position = marker.getPosition();
                    Double lat = position.latitude;
                    Double lon = position.longitude;

                    // cream un Bundle
                    Bundle transferLatLong = new Bundle();

                    // punem lat long si fbname, fbid in Bundle
                    transferLatLong.putDouble("lat", lat);
                    transferLatLong.putDouble("long", lon);
                    transferLatLong.putString("fb_id", mStore.user_id);
                    transferLatLong.putString("fb_name", mStore.user_name);

                    // pregatim Bundle-ul pentru urmatoarea
                    // activitate(AdaugaMag.java)
                    intent.putExtras(transferLatLong);

                }

            }

            // if click on existing store
        } else {

            Bundle markerInfotoDM = new Bundle();

            String idmag = marker.getSnippet();

            markerInfotoDM.putString("idmag", idmag);

            Location findme = mMap.getMyLocation();
            double latitude = findme.getLatitude();
            double longitude = findme.getLongitude();

            // pentru navigatie

            markerInfotoDM.putDouble("userlat", latitude);
            markerInfotoDM.putDouble("userlon", longitude);

            // pentru navigatie
            LatLng shopposition = marker.getPosition();
            Double shoplat = shopposition.latitude;
            Double shoplon = shopposition.longitude;

            markerInfotoDM.putDouble("shoplat", shoplat);
            markerInfotoDM.putDouble("shoplon", shoplon);

            intent.putExtras(markerInfotoDM);

        }
        // porneste urmatoarea activitate doar daca ai extras pe bundle
        if (intent.getExtras() != null) {
            startActivity(intent);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }

    public void addMarker(String id, String name, LatLng latlng,
                          String tipmagsel, String descmag, String nonstopflag) {

        // diferentiem si noi de magazine non stop si normale

        Marker markerdinDB = null;

        MarkerOptions de_cartier = new MarkerOptions()
                .position(latlng)
                .title(name)
                .snippet(id)
                .icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.icoana_harta_normal));

        MarkerOptions supermarket = new MarkerOptions()
                .position(latlng)
                .title(name)
                .snippet(id)
                .icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.icoana_harta_supermarket));

        MarkerOptions hipermarket = new MarkerOptions()
                .position(latlng)
                .title(name)
                .snippet(id)
                .icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.icoana_harta_hipermarket));

        MarkerOptions piata = new MarkerOptions()
                .position(latlng)
                .title(name)
                .snippet(id)
                .icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.icoana_harta_piata));

        MarkerOptions benzinarie = new MarkerOptions()
                .position(latlng)
                .title(name)
                .snippet(id)
                .icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.icoana_harta_benz));

        MarkerOptions nonstop = new MarkerOptions()
                .position(latlng)
                .title(name)
                .snippet(id)
                .icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.icoana_harta_nonstop));

        switch (tipmagsel) {
            case "de cartier":

                if (nonstopflag.equals("da")) {

                    markerdinDB = mMap.addMarker(nonstop);

                } else {
                    markerdinDB = mMap.addMarker(de_cartier);
                }

                break;
            case "supermarket":
                markerdinDB = mMap.addMarker(supermarket);

                break;
            case "hipermarket":
                markerdinDB = mMap.addMarker(hipermarket);

                break;

            case "piata":
                markerdinDB = mMap.addMarker(piata);

                break;

            case "benzinarie":
                markerdinDB = mMap.addMarker(benzinarie);

                break;

        }

        allMarkersMap.put(markerdinDB, VeziMag.class);
        mMap.setOnInfoWindowClickListener(this);

    }

    // Background task to retrieve locations from remote mysql server
    public class RetrieveTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            mProgressDialog.show();

        }

        @Override
        protected String doInBackground(String... params) {

            String NElat = params[0];
            String NElon = params[1];
            String SWlat = params[2];
            String SWlon = params[3];

            String strUrl = "http://atelieruldegermana.ro/android_connect/get_all_shops_new.php"
                    .concat("?").concat("NElat=").concat(NElat).concat("&")
                    .concat("NElon=").concat(NElon).concat("&")
                    .concat("SWlat=").concat(SWlat).concat("&")
                    .concat("SWlon=").concat(SWlon);

            URL url = null;
            StringBuffer sb = new StringBuffer();
            try {
                url = new URL(strUrl);
                HttpURLConnection connection = (HttpURLConnection) url
                        .openConnection();
                connection.connect();
                connection.setConnectTimeout(5000);

                InputStream iStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(iStream));
                String line = "";
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }

                reader.close();
                iStream.close();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (SocketTimeoutException e) {
                e.printStackTrace();

            } catch (IOException e) {
                e.printStackTrace();
            }
            return sb.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (result.contains("Nu am gasit magazine")) {

                mDialog.showOK(
                        getResources()
                                .getString(R.string.no_shops_around_title),
                        getResources().getString(R.string.no_shops_around),
                        "main");

                if (mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
            } else {

                ParserTask PT = (ParserTask) new ParserTask();
                PT.execute(result);

            }

        }
    }

    public class RetrieveTaskBenz extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {

            String NElat = params[0];
            String NElon = params[1];
            String SWlat = params[2];
            String SWlon = params[3];

            String strUrl = "http://atelieruldegermana.ro/android_connect/get_all_shops_benz.php"
                    .concat("?").concat("NElat=").concat(NElat).concat("&")
                    .concat("NElon=").concat(NElon).concat("&")
                    .concat("SWlat=").concat(SWlat).concat("&")
                    .concat("SWlon=").concat(SWlon);

            URL url = null;
            StringBuffer sb = new StringBuffer();
            try {
                url = new URL(strUrl);
                HttpURLConnection connection = (HttpURLConnection) url
                        .openConnection();
                connection.connect();
                InputStream iStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(iStream));
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
            return sb.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (result.contains("Nu am gasit magazine")) {
                mDialog.showOK(
                        getResources()
                                .getString(R.string.no_shops_around_title),
                        getResources().getString(R.string.no_shops_around),
                        "main");

                if (mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }

            } else {
                ParserTask PT = (ParserTask) new ParserTask();
                PT.execute(result);
            }

        }
    }

    public class RetrieveTaskPiata extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {

            String NElat = params[0];
            String NElon = params[1];
            String SWlat = params[2];
            String SWlon = params[3];

            String strUrl = "http://atelieruldegermana.ro/android_connect/get_all_shops_piata.php"
                    .concat("?").concat("NElat=").concat(NElat).concat("&")
                    .concat("NElon=").concat(NElon).concat("&")
                    .concat("SWlat=").concat(SWlat).concat("&")
                    .concat("SWlon=").concat(SWlon);

            URL url = null;
            StringBuffer sb = new StringBuffer();
            try {
                url = new URL(strUrl);
                HttpURLConnection connection = (HttpURLConnection) url
                        .openConnection();
                connection.connect();
                InputStream iStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(iStream));
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

            return sb.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (result.contains("Nu am gasit magazine")) {

                mDialog.showOK(
                        getResources()
                                .getString(R.string.no_shops_around_title),
                        getResources().getString(R.string.no_shops_around),
                        "main");

                if (mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }

            } else {
                ParserTask PT = (ParserTask) new ParserTask();
                PT.execute(result);
            }
        }
    }

    public class RetrieveTaskCartier extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {

            String NElat = params[0];
            String NElon = params[1];
            String SWlat = params[2];
            String SWlon = params[3];

            String strUrl = "http://atelieruldegermana.ro/android_connect/get_all_shops_decartier.php"
                    .concat("?").concat("NElat=").concat(NElat).concat("&")
                    .concat("NElon=").concat(NElon).concat("&")
                    .concat("SWlat=").concat(SWlat).concat("&")
                    .concat("SWlon=").concat(SWlon);

            URL url = null;
            StringBuffer sb = new StringBuffer();
            try {
                url = new URL(strUrl);
                HttpURLConnection connection = (HttpURLConnection) url
                        .openConnection();
                connection.connect();
                InputStream iStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(iStream));
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

            return sb.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (result.contains("Nu am gasit magazine")) {

                mDialog.showOK(
                        getResources()
                                .getString(R.string.no_shops_around_title),
                        getResources().getString(R.string.no_shops_around),
                        "main");

                if (mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }

            } else {
                ParserTask PT = (ParserTask) new ParserTask();
                PT.execute(result);
            }
        }
    }

    public class RetrieveTaskSupermarket extends
            AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {

            String NElat = params[0];
            String NElon = params[1];
            String SWlat = params[2];
            String SWlon = params[3];

            String strUrl = "http://atelieruldegermana.ro/android_connect/get_all_shops_supermarket.php"
                    .concat("?").concat("NElat=").concat(NElat).concat("&")
                    .concat("NElon=").concat(NElon).concat("&")
                    .concat("SWlat=").concat(SWlat).concat("&")
                    .concat("SWlon=").concat(SWlon);

            URL url = null;
            StringBuffer sb = new StringBuffer();
            try {
                url = new URL(strUrl);
                HttpURLConnection connection = (HttpURLConnection) url
                        .openConnection();
                connection.connect();
                InputStream iStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(iStream));
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

            return sb.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (result.contains("Nu am gasit magazine")) {
                mDialog.showOK(
                        getResources()
                                .getString(R.string.no_shops_around_title),
                        getResources().getString(R.string.no_shops_around),
                        "main");

                if (mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }

            } else {
                ParserTask PT = (ParserTask) new ParserTask();
                PT.execute(result);
            }
        }
    }

    public class RetrieveTaskNS extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {

            String NElat = params[0];
            String NElon = params[1];
            String SWlat = params[2];
            String SWlon = params[3];

            String strUrl = "http://atelieruldegermana.ro/android_connect/get_all_shops_ns.php"
                    .concat("?").concat("NElat=").concat(NElat).concat("&")
                    .concat("NElon=").concat(NElon).concat("&")
                    .concat("SWlat=").concat(SWlat).concat("&")
                    .concat("SWlon=").concat(SWlon);

            URL url = null;
            StringBuffer sb = new StringBuffer();
            try {
                url = new URL(strUrl);
                HttpURLConnection connection = (HttpURLConnection) url
                        .openConnection();
                connection.connect();
                connection.setConnectTimeout(5000);
                InputStream iStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(iStream));
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
            return sb.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result.contains("Nu am gasit magazine")) {

                mDialog.showOK(
                        getResources()
                                .getString(R.string.no_shops_around_title),
                        getResources().getString(R.string.no_shops_around),
                        "main");

                if (mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }

            } else {
                ParserTask PT = (ParserTask) new ParserTask();
                PT.execute(result);
            }

        }
    }

    public class RetrieveTaskPOS extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {

            String NElat = params[0];
            String NElon = params[1];
            String SWlat = params[2];
            String SWlon = params[3];

            String strUrl = "http://atelieruldegermana.ro/android_connect/get_all_shops_card.php"
                    .concat("?").concat("NElat=").concat(NElat).concat("&")
                    .concat("NElon=").concat(NElon).concat("&")
                    .concat("SWlat=").concat(SWlat).concat("&")
                    .concat("SWlon=").concat(SWlon);

            URL url = null;
            StringBuffer sb = new StringBuffer();
            try {
                url = new URL(strUrl);
                HttpURLConnection connection = (HttpURLConnection) url
                        .openConnection();
                connection.connect();
                connection.setConnectTimeout(5000);
                InputStream iStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(iStream));
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
            return sb.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result.contains("Nu am gasit magazine")) {

                mDialog.showOK(
                        getResources()
                                .getString(R.string.no_shops_around_title),
                        getResources().getString(R.string.no_shops_around),
                        "main");

                if (mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }

            } else {
                ParserTask PT = (ParserTask) new ParserTask();
                PT.execute(result);
            }

        }
    }

    public class RetrieveTaskNOPOS extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {

            String NElat = params[0];
            String NElon = params[1];
            String SWlat = params[2];
            String SWlon = params[3];

            String strUrl = "http://atelieruldegermana.ro/android_connect/get_all_shops_nocard.php"
                    .concat("?").concat("NElat=").concat(NElat).concat("&")
                    .concat("NElon=").concat(NElon).concat("&")
                    .concat("SWlat=").concat(SWlat).concat("&")
                    .concat("SWlon=").concat(SWlon);

            URL url = null;
            StringBuffer sb = new StringBuffer();
            try {
                url = new URL(strUrl);
                HttpURLConnection connection = (HttpURLConnection) url
                        .openConnection();
                connection.connect();
                InputStream iStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(iStream));
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
            return sb.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result.contains("Nu am gasit magazine")) {

                mDialog.showOK(
                        getResources()
                                .getString(R.string.no_shops_around_title),
                        getResources().getString(R.string.no_shops_around),
                        "main");

                if (mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
            } else {
                ParserTask PT = (ParserTask) new ParserTask();
                PT.execute(result);
            }

        }
    }

    public class RetrieveTaskNS_POS extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {

            String NElat = params[0];
            String NElon = params[1];
            String SWlat = params[2];
            String SWlon = params[3];

            String strUrl = "http://atelieruldegermana.ro/android_connect/get_all_shops_ns_card.php"
                    .concat("?").concat("NElat=").concat(NElat).concat("&")
                    .concat("NElon=").concat(NElon).concat("&")
                    .concat("SWlat=").concat(SWlat).concat("&")
                    .concat("SWlon=").concat(SWlon);

            URL url = null;
            StringBuffer sb = new StringBuffer();
            try {
                url = new URL(strUrl);
                HttpURLConnection connection = (HttpURLConnection) url
                        .openConnection();
                connection.connect();
                connection.setConnectTimeout(5000);
                InputStream iStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(iStream));
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
            return sb.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result.contains("Nu am gasit magazine")) {

                mDialog.showOK(
                        getResources()
                                .getString(R.string.no_shops_around_title),
                        getResources().getString(R.string.no_shops_around),
                        "main");

                if (mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }

            } else {
                ParserTask PT = (ParserTask) new ParserTask();
                PT.execute(result);
            }

        }
    }

    public class RetrieveTaskTotal extends AsyncTask<String, String, String> {

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
                Toast.makeText(MLTMainActivity.this,
                        "Caught ClientProtocolException", Toast.LENGTH_SHORT)
                        .show();
            } catch (IOException e) {
                Toast.makeText(MLTMainActivity.this, "Exception",
                        Toast.LENGTH_SHORT).show();
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null) {
                Toast.makeText(getApplicationContext(), "Total: " + result,
                        Toast.LENGTH_SHORT).show();

            }
        }
    }

    // Background thread to parse the JSON data retrieved from MySQL server
    public class ParserTask extends
            AsyncTask<String, Void, List<HashMap<String, String>>> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            // mProgressDialog.show();
        }

        @Override
        protected List<HashMap<String, String>> doInBackground(String... params) {
            MarkerJSONParser markerParser = new MarkerJSONParser();
            JSONObject json = null;
            try {
                json = new JSONObject(params[0]);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            List<HashMap<String, String>> markersList = markerParser
                    .parse(json);
            return markersList;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> result) {
            if (result != null) {

                for (int i = 0; i < result.size(); i++) {
                    HashMap<String, String> marker = result.get(i);

                    String id = marker.get("id");
                    String name = (marker.get("name")); // chemam dupa key-ul
                    // name
                    // din clasa
                    // MarkerJSONParser

                    LatLng latlng = new LatLng(Double.parseDouble(marker
                            .get("lat")), Double.parseDouble(marker.get("lng")));
                    String tipmagsel = (marker.get("tip"));

                    String descmag = (marker.get("desc"));
                    String nonstopflag = (marker.get("nonstop"));

                    addMarker(id, name, latlng, tipmagsel, descmag, nonstopflag);

                }
            } else {
                Toast.makeText(MLTMainActivity.this, "No results",
                        Toast.LENGTH_SHORT);
                return;

            }
            if (mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            } else {
                return;
            }

        }

    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, getString(R.string.back_exit), Toast.LENGTH_SHORT)
                .show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;

        // switch (event.getAction()) {
        // case MotionEvent.ACTION_DOWN: {
        //
        // startY = event.getY();
        // break;
        // }
        // case MotionEvent.ACTION_UP: {
        // float endY = event.getY();
        //
        // if (endY < startY) {
        // // System.out.println("Move UP");
        // ll.setVisibility(View.VISIBLE);
        // ll.startAnimation(animUp);
        // llArata.setVisibility(View.GONE);
        // llAscunde.setVisibility(View.VISIBLE);
        //
        // } else {
        // ll.startAnimation(animDown);
        // ll.setVisibility(View.GONE);
        // llArata.setVisibility(View.VISIBLE);
        // llAscunde.setVisibility(View.GONE);
        //
        // }
        // }
        //
        // }
        // return true;
    }

    // @Override
    // public void onCameraChange(CameraPosition arg0) {
    // CheckBox Toate = (CheckBox) findViewById(R.id.chkToate);
    // CheckBox Nonstop = (CheckBox) findViewById(R.id.chkNonstop);
    // CheckBox cuPOS = (CheckBox) findViewById(R.id.chkPOSda);
    //
    // boolean isToateChecked = Toate.isChecked();
    // boolean isNonstopChecked = Nonstop.isChecked();
    // boolean iscuPOSChecked = cuPOS.isChecked();
    //
    // if (isToateChecked == true && isNonstopChecked == true
    // && iscuPOSChecked == true) {
    // Toast.makeText(getApplicationContext(),
    // getString(R.string.filtru_toate_mag), Toast.LENGTH_SHORT).show();
    // if (map != null) {
    // map.clear();
    // }
    // RetrieveTask toatemag = (RetrieveTask) new RetrieveTask();
    // toatemag.execute();
    // // RetrieveTaskTotal totalmag = (RetrieveTaskTotal) new
    // // RetrieveTaskTotal().execute();
    // }
    //
    // // if (Toate.isChecked() && iscuPOSChecked == true) {
    // // Toast.makeText(getApplicationContext(),
    // // "Arat toate magazinele cu POS", Toast.LENGTH_SHORT).show();
    // // if (map != null) {
    // // map.clear();
    // // }
    // // RetrieveTask toatemag = (RetrieveTask) new RetrieveTask()
    // // .execute();
    // // RetrieveTaskTotal totalmag = (RetrieveTaskTotal) new
    // // RetrieveTaskTotal().execute();
    // // }
    //
    // if (isToateChecked == false && isNonstopChecked == true
    // && iscuPOSChecked == true) {
    // Toast.makeText(getApplicationContext(),
    // getString(R.string.filtru_ns_card), Toast.LENGTH_SHORT)
    // .show();
    // map.clear();
    // RetrieveTaskPOSCard cucard = (RetrieveTaskPOSCard) new
    // RetrieveTaskPOSCard()
    // .execute();
    // // RetrieveTaskTotalNScuPOS totalnscupos =
    // // (RetrieveTaskTotalNScuPOS) new
    // // RetrieveTaskTotalNScuPOS().execute();
    // }
    // if (isToateChecked == false && isNonstopChecked == false
    // && iscuPOSChecked == true) {
    // Toast.makeText(getApplicationContext(),
    // getString(R.string.filtru_pos), Toast.LENGTH_SHORT).show();
    // map.clear();
    // RetrieveTaskPOS cucard = (RetrieveTaskPOS) new RetrieveTaskPOS()
    // .execute();
    // // RetrieveTaskTotalPOS totalcupos = (RetrieveTaskTotalPOS)
    // // new RetrieveTaskTotalPOS().execute();
    // }
    //
    // if (isToateChecked == false && isNonstopChecked == true
    // && iscuPOSChecked == false) {
    // Toast.makeText(getApplicationContext(),
    // getString(R.string.filtru_ns), Toast.LENGTH_SHORT).show();
    // map.clear();
    // RetrieveTaskNS nonstop = (RetrieveTaskNS) new RetrieveTaskNS()
    // .execute();
    // // RetrieveTaskTotalNS totalns = (RetrieveTaskTotalNS) new
    // // RetrieveTaskTotalNS().execute();
    // }
    //
    // if (isToateChecked == false && isNonstopChecked == false
    // && iscuPOSChecked == false) {
    // Toast.makeText(getApplicationContext(),
    // getString(R.string.filtru_none),
    // Toast.LENGTH_SHORT).show();
    // }
    // }

    private void onSessionStateChange(Session session, SessionState state,
                                      Exception exception) {

        if (session != null && session.isOpened()) {
            // Intent intent = new Intent(PaginaHarta.this,
            // PaginaHarta.class);
            // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
            // | Intent.FLAG_ACTIVITY_NEW_TASK);
            // startActivity(intent);
            // finish();
        } else {
            Intent intent = new Intent(this, Login_screen.class);

            // LoginButton fb_login = (LoginButton)
            // findViewById(R.id.btnFacebook_loggedin);
            // fb_login.setVisibility(View.GONE);

            startActivity(intent);
        }

    }

    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state,
                         Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        Session.getActiveSession().onActivityResult(this, requestCode,
                resultCode, data);
        if (Session.getActiveSession() != null
                || Session.getActiveSession().isOpened()) {
            Intent i = new Intent(MLTMainActivity.this, Login_screen.class);
            startActivity(i);
        }
        uiHelper.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    protected void onResume() {
        super.onResume();
        // checkGooglePlayServicesAvailability();
        // setUpMapIfNeeded();
        uiHelper.onResume();

    }

    // CLASSE GRAPH

    private void makeMeRequest(final Session session) {
        // Make an API call to get user data and define a
        // new callback to handle the response.
        Request request = Request.newMeRequest(session,
                new Request.GraphUserCallback() {
                    @Override
                    public void onCompleted(GraphUser user, Response response) {
                        // If the response is successful
                        if (session == Session.getActiveSession()) {
                            if (user != null) {
                                // Set the id for the ProfilePictureView
                                // view that in turn displays the profile
                                // picture.

                                tvFBUserName.setText(user.getName());
                                mStore.user_id = user.getId();
                                // tvFBUserID.setText(user.getId());
                                mStore.user_name = user.getName();

                            }
                        }
                        if (response.getError() != null) {
                            // Handle errors, will do so later.
                        }
                    }
                });
        request.executeAsync();
    }

    protected void map_clear() {
        mMap.clear();

        LatLngBounds bounds = MLTMainActivity.this.mMap.getProjection()
                .getVisibleRegion().latLngBounds;

        DecimalFormat df = new DecimalFormat("##.######");
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setDecimalSeparator('.');
        df.setDecimalFormatSymbols(dfs);

        Double NElat = bounds.northeast.latitude;

        NElat2 = df.format(NElat).toString();

        Double NElon = bounds.northeast.longitude;
        NElon2 = df.format(NElon).toString();

        Double SWlat = bounds.southwest.latitude;
        SWlat2 = df.format(SWlat).toString();

        Double SWlon = bounds.southwest.longitude;
        SWlon2 = df.format(SWlon).toString();

        mMap.clear();

        Log.d("Magazoo", "Retrieve Task called from on back from adauga");
        if (ConnectionUtils.isConnected(MLTMainActivity.this)) {
            RetrieveTask toate = (RetrieveTask) new RetrieveTask().execute(
                    NElat2, NElon2, SWlat2, SWlon2);
        } else {
            buildAlertMessageNoNetwork();
        }
    }

    protected void map_camera_change() {

        mMap.setOnCameraChangeListener(new OnCameraChangeListener() {

            @Override
            public void onCameraChange(CameraPosition position) {

                float maxZoom = 19.0f;
                float minZoom = 15.0f;

                if (!mWait) {

                    if (chkToate.isChecked()) {

                        LatLngBounds bounds = MLTMainActivity.this.mMap
                                .getProjection().getVisibleRegion().latLngBounds;

                        DecimalFormat df = new DecimalFormat("##.######");
                        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
                        dfs.setDecimalSeparator('.');
                        df.setDecimalFormatSymbols(dfs);

                        Double NElat = bounds.northeast.latitude;

                        NElat2 = df.format(NElat).toString();

                        Double NElon = bounds.northeast.longitude;
                        NElon2 = df.format(NElon).toString();

                        Double SWlat = bounds.southwest.latitude;
                        SWlat2 = df.format(SWlat).toString();

                        Double SWlon = bounds.southwest.longitude;
                        SWlon2 = df.format(SWlon).toString();

                        mMap.clear();

                        Log.d("Magazoo",
                                "Retrieve Task called from on camera change");
                        if (ConnectionUtils.isConnected(MLTMainActivity.this)) {
                            RetrieveTask toate = (RetrieveTask) new RetrieveTask()
                                    .execute(NElat2, NElon2, SWlat2, SWlon2);
                        } else {
                            buildAlertMessageNoNetwork();
                        }

                    } else if (chkNonstop.isChecked() && !chkPOS.isChecked()) {
                        LatLngBounds bounds = MLTMainActivity.this.mMap
                                .getProjection().getVisibleRegion().latLngBounds;

                        DecimalFormat df = new DecimalFormat("##.######");
                        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
                        dfs.setDecimalSeparator('.');
                        df.setDecimalFormatSymbols(dfs);

                        Double NElat = bounds.northeast.latitude;

                        NElat2 = df.format(NElat).toString();

                        Double NElon = bounds.northeast.longitude;
                        NElon2 = df.format(NElon).toString();

                        Double SWlat = bounds.southwest.latitude;
                        SWlat2 = df.format(SWlat).toString();

                        Double SWlon = bounds.southwest.longitude;
                        SWlon2 = df.format(SWlon).toString();

                        mMap.clear();

                        if (ConnectionUtils.isConnected(MLTMainActivity.this)) {
                            RetrieveTaskNS nonstop = (RetrieveTaskNS) new RetrieveTaskNS()
                                    .execute(NElat2, NElon2, SWlat2, SWlon2);

                        } else {
                            buildAlertMessageNoNetwork();
                        }
                    } else if (chkPOS.isChecked() && !chkNonstop.isChecked()) {
                        LatLngBounds bounds = MLTMainActivity.this.mMap
                                .getProjection().getVisibleRegion().latLngBounds;

                        DecimalFormat df = new DecimalFormat("##.######");
                        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
                        dfs.setDecimalSeparator('.');
                        df.setDecimalFormatSymbols(dfs);

                        Double NElat = bounds.northeast.latitude;

                        NElat2 = df.format(NElat).toString();

                        Double NElon = bounds.northeast.longitude;
                        NElon2 = df.format(NElon).toString();

                        Double SWlat = bounds.southwest.latitude;
                        SWlat2 = df.format(SWlat).toString();

                        Double SWlon = bounds.southwest.longitude;
                        SWlon2 = df.format(SWlon).toString();

                        mMap.clear();

                        if (ConnectionUtils.isConnected(MLTMainActivity.this)) {
                            RetrieveTaskPOS POS = (RetrieveTaskPOS) new RetrieveTaskPOS()
                                    .execute(NElat2, NElon2, SWlat2, SWlon2);
                        } else {
                            buildAlertMessageNoNetwork();
                        }
                    } else if (chkPOS.isChecked() && chkNonstop.isChecked()) {

                        LatLngBounds bounds = MLTMainActivity.this.mMap
                                .getProjection().getVisibleRegion().latLngBounds;

                        DecimalFormat df = new DecimalFormat("##.######");
                        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
                        dfs.setDecimalSeparator('.');
                        df.setDecimalFormatSymbols(dfs);

                        Double NElat = bounds.northeast.latitude;

                        NElat2 = df.format(NElat).toString();

                        Double NElon = bounds.northeast.longitude;
                        NElon2 = df.format(NElon).toString();

                        Double SWlat = bounds.southwest.latitude;
                        SWlat2 = df.format(SWlat).toString();

                        Double SWlon = bounds.southwest.longitude;
                        SWlon2 = df.format(SWlon).toString();

                        mMap.clear();

                        if (ConnectionUtils.isConnected(MLTMainActivity.this)) {
                            RetrieveTaskNS_POS nonstop_pos = (RetrieveTaskNS_POS) new RetrieveTaskNS_POS()
                                    .execute(NElat2, NElon2, SWlat2, SWlon2);
                        } else {
                            buildAlertMessageNoNetwork();
                        }
                    }

                    if (position.zoom > maxZoom) {
                        Log.d("Magazoo", "map moved maxzoom");
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(maxZoom));
                    } else if (position.zoom < minZoom) {
                        Log.d("Magazoo", "map moved minzoom");
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(minZoom));
                    }

                    if (mPopupFilterWindow.isShowing()) {
                        mPopupFilterWindow.dismiss();
                    }

                    if (mPopupFilterWindow.isShowing()) {
                        mPopupFilterWindow.dismiss();
                    }

                    if (mPopupAddStore.isShowing()) {
                        mPopupAddStore.dismiss();
                    }

                }
            }

        });

    }

    public boolean isLoggedIn() {
        Session session = Session.getActiveSession();
        return (session != null && session.isOpened());
    }

}
