package magazinelangatine;

import magazinelangatine.model.Store;
import magazinelangatine.util.DialogCreator;
import magazinelangatine.util.HourValidator;
import magazoo.magazine.langa.tine.R;


import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;

import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class AdaugaMag extends Activity {

    private String mAddLimiter;

    private String addCounter;

    private String mDescription_store;
    private String mName_store;
    private EditText etName_store;
    private EditText etDescription_store;
    private Button btnAdd;

    private Store mStore = new Store();

    private TextView tvNavbar;
    private TextView tvLV;
    private TextView tvS;
    private TextView tvD;

    private EditText etLVStart;
    private EditText etLVEnd;
    private EditText etSStart;
    private EditText etSEnd;
    private EditText etDStart;
    private EditText etDEnd;

    private Boolean LVstartok;
    private Boolean LVendok;
    private Boolean SStartok;
    private Boolean SEndok;
    private Boolean DStartok;
    private Boolean DEndok;

    private String gotLVstart;
    private String gotLVend;
    private String gotSstart;
    private String gotSend;
    private String gotDstart;
    private String gotDend;

    private String mGotLV;
    private String mGotS;
    private String mGotD;

    private Spinner spTypeStore;
    private String mTypeStore;

    private CheckBox chkPOS;
    private CheckBox chkNS;

    private String POSFlag;
    private String NSFlag;

    Double vreaulat;
    Double vreaulong;
    String fb_id;
    String fb_name;

    private DialogCreator mDialog = new DialogCreator(this);

    private MLTMainActivity mMain = new MLTMainActivity();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.adauga_mag);

        initialize();

        Bundle coordinates = getIntent().getExtras();

        vreaulat = coordinates.getDouble("lat");
        vreaulong = coordinates.getDouble("long");
        fb_id = coordinates.getString("fb_id");
        fb_name = coordinates.getString("fb_name");

        etLVStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });

    }

    private void initialize() {

        Typeface tf = Typeface.createFromAsset(getAssets(),
                "fonts/comfortaa-regular.ttf");

        tvNavbar = (TextView) findViewById(R.id.tvNavbar);

        etName_store = (EditText) findViewById(R.id.etNumeMag);
        spTypeStore = (Spinner) findViewById(R.id.spTipMag);

        tvLV = (TextView) findViewById(R.id.tvLuniVineri);
        tvS = (TextView) findViewById(R.id.tvSambata);
        tvD = (TextView) findViewById(R.id.tvDuminica);

        etLVStart = (EditText) findViewById(R.id.etLVStart);
        etLVEnd = (EditText) findViewById(R.id.etLVEnd);
        etSStart = (EditText) findViewById(R.id.etSStart);
        etSEnd = (EditText) findViewById(R.id.etSEnd);
        etDStart = (EditText) findViewById(R.id.etDStart);
        etDEnd = (EditText) findViewById(R.id.etDEnd);
        chkPOS = (CheckBox) findViewById(R.id.chkPOSda);
        chkNS = (CheckBox) findViewById(R.id.chkNSda);

        etDescription_store = (EditText) findViewById(R.id.etDescriere);

        btnAdd = (Button) findViewById(R.id.btnAdaugaMag);

        tvNavbar.setTypeface(tf);
        etName_store.setTypeface(tf);

        tvLV.setTypeface(tf);
        tvS.setTypeface(tf);
        tvD.setTypeface(tf);

        etLVStart.setTypeface(tf);
        etLVEnd.setTypeface(tf);

        etSStart.setTypeface(tf);
        etSEnd.setTypeface(tf);

        etDStart.setTypeface(tf);
        etDEnd.setTypeface(tf);

        chkPOS.setTypeface(tf);
        chkNS.setTypeface(tf);

        etDescription_store.setTypeface(tf);

        btnAdd.setTypeface(tf);

        // listenere
        btnAdd.setOnClickListener(new View.OnClickListener() { // pt butonul
            // de Adauga

            @Override
            public void onClick(View v) {

                mName_store = etName_store.getText().toString();

                mTypeStore = spTypeStore.getSelectedItem().toString();

                switch (mTypeStore) {

                    case "small store":
                        mTypeStore = "de cartier";
                        break;

                    case "hypermarket":
                        mTypeStore = "hipermarket";
                        break;

                    case "farmers market":
                        mTypeStore = "piata";
                        break;

                    case "gas station":
                        mTypeStore = "benzinarie";
                        break;

                }

                if (mName_store.matches("")) {
                    mDialog.showOK(
                            getResources().getString(
                                    R.string.name_missing_title),
                            getResources().getString(R.string.name_missing), "main");
                    return;
                } else if (mTypeStore.equals("...")) {
                    mDialog.showOK(
                            getResources().getString(
                                    R.string.type_missing_title),
                            getResources().getString(R.string.type_missing), "main");
                } else {

                    checkHours();

                }

                // alerta daca userul incearca sa bage mai multe
                // charactere // NU MERGE INCA
                etName_store.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void onTextChanged(CharSequence s, int start,
                                              int before, int count) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start,
                                                  int count, int after) {
                        if (s.length() > 15) {
                            Toast.makeText(AdaugaMag.this, "sdjhsdkfh",
                                    Toast.LENGTH_LONG).show();
                        }

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        // TODO Auto-generated method stub

                    }
                });

            }

        });

        spTypeStore.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

                mDialog.showOK(
                        getResources().getString(R.string.type_missing_title),
                        getResources().getString(R.string.type_missing), "main");

            }

        });

    }

    ;

    public void sendToServer(Activity activity, Store store) {
        // chemam clasa SaveTask pentru executie si salvare in DB
        // new SaveTask(collNumeMag, lat, lng, mContext).execute(collNumeMag,
        // lat, lng, mContext);
        SaveTask task = (SaveTask) new SaveTask(activity, store).execute();

        // Intent i = new Intent(AdaugaMag.this, PaginaHarta.class);
        // startActivity(i);

    }

    @Override
    public void onBackPressed() {

        mDialog.showYesNoAdauga(
                this.getResources().getString(R.string.cancel_add_title), this
                        .getResources().getString(R.string.cancel_add));

    }

    // pentru butonul de back din navbar
    public void onBackPressed(View view) {

        mDialog.showYesNoAdauga(
                this.getResources().getString(R.string.cancel_add_title), this
                        .getResources().getString(R.string.cancel_add));

    }

    protected void checkHours() {

        gotLVstart = etLVStart.getText().toString();
        LVstartok = new HourValidator().validate(gotLVstart);
        gotLVend = etLVEnd.getText().toString();
        LVendok = new HourValidator().validate(gotLVend);

        gotSstart = etSStart.getText().toString();
        SStartok = new HourValidator().validate(gotSstart);
        gotSend = etSEnd.getText().toString();
        SEndok = new HourValidator().validate(gotSend);

        gotDstart = etDStart.getText().toString();
        DStartok = new HourValidator().validate(gotDstart);
        gotDend = etDEnd.getText().toString();
        DEndok = new HourValidator().validate(gotDend);

        if (!LVstartok || !LVendok || !SStartok || !SEndok || !DStartok
                || !DEndok) {

            mDialog.showOK(
                    getResources().getString(R.string.time_format_title_err),
                    getResources().getString(R.string.time_format_err), "main");

            return;

        } else {
            mGotLV = gotLVstart + "-" + gotLVend;
            mGotS = gotSstart + "-" + gotSend;
            mGotD = gotDstart + "-" + gotDend;

            POSFlag = "-NA-";

            if (chkPOS.isChecked()) {
                POSFlag = "da";
            } else {
                POSFlag = "nu";
            }

            NSFlag = "-NA-";

            if (chkNS.isChecked()) {
                NSFlag = "da";
            } else {
                NSFlag = "nu";
            }

            mStore.name = mName_store;
            mStore.lat = vreaulat.toString();
            mStore.lon = vreaulong.toString();
            mStore.type = mTypeStore;
            mStore.pos = POSFlag;
            mStore.nonstop = NSFlag;
            mStore.description = etDescription_store.getText().toString();
            mStore.monfri = mGotLV;
            mStore.saturday = mGotS;
            mStore.sunday = mGotD;
            mStore.user_name = fb_name;
            mStore.user_id = fb_id;

            sendToServer(AdaugaMag.this, mStore);

        }

    }

};
