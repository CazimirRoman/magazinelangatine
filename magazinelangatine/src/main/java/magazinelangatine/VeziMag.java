package magazinelangatine;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import magazinelangatine.util.JSONParser;
import magazoo.magazine.langa.tine.R;

public class VeziMag extends Activity {

    // dfdff
    TextView NameDM;
    TextView MagIDDM;
    TextView TipMagDM;
    TextView LatDM;
    TextView LonDM;
    TextView DescDM;
    Button Duma;
    String name;
    String id_mag;
    Double userlat;
    Double userlon;
    Double shoplat;
    Double shoplon;

    private String voteType = "+", modifiedVal = "0";

    String uritext;
    String uritextmod;
    String userlatpars;
    String userlonpars;
    String shoplatpars;
    String shoplonpars;
    String desc;
    String idnou;
    JSONParser jsonParser = new JSONParser();
    JSONObject listamagazine;

    private ImageButton ibBack;
    private ImageButton ibLike;
    private ImageButton ibUnLike;

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MAGAZINE = "listamagazine";
    private static final String TAG_ID = "id";
    private static final String TAG_TIP = "tip";
    private static final String TAG_POS = "pos";
    private static final String TAG_NAME = "name";
    private static final String TAG_DESC = "desc";
    private static final String TAG_NONSTOP = "nonstop";
    private static final String TAG_LV = "lv";
    private static final String TAG_SAMBATA = "s";
    private static final String TAG_DUMINICA = "d";

    private static final String url_product_details = "http://atelieruldegermana.ro/android_connect/get_shop_details.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.vezi_mag);


        initialize();

//ibLike.setOnClickListener(new View.OnClickListener() {
//    @Override
//    public void onClick(View v) {
//
//        voteType = "+";
//        if (modifiedVal.equals("0"))
//            modifiedVal = "+1";
//        else if (modifiedVal.equals("-1"))
//            modifiedVal = "+2";
//        else if (modifiedVal.equals("-2"))
//            modifiedVal = "+2";
//        ibLike
//                .setBackgroundResource(R.drawable.lfs_like_btn_pressed);
//        btn_thumbs_down.setBackgroundResource(R.drawable.lfs_unlike);
//
//        new VoteFreebie(MainActivity.this, modifiedVal,
//                mFreebie.post_id).execute();
//        btn_thumbs_down.setEnabled(true);
//        btn_thumbs_up.setEnabled(false);
//
//    }
//});

        // Button Sterge = (Button) findViewById(R.id.btnSterge);
        // Sterge.setBackgroundResource(R.drawable.sterge);

        // Bundle de pe PaginaHarta (onInfoWindowsClick)
        Bundle lasosire = getIntent().getExtras();
        final String id_mag = lasosire.getString("idmag");
        Double userlat = lasosire.getDouble("userlat");
        final String userlatpars = userlat.toString();
        Double userlon = lasosire.getDouble("userlon");
        final String userlonpars = userlon.toString();
        Double shoplat = lasosire.getDouble("shoplat");
        final String shoplatpars = shoplat.toString();
        Double shoplon = lasosire.getDouble("shoplon");
        final String shoplonpars = shoplon.toString();

        final String uritext = "http://maps.google.com/maps?saddr="
                .concat(userlatpars).concat(", ").concat(userlonpars)
                .concat("&daddr=").concat(shoplatpars).concat(", ")
                .concat(shoplonpars);

        final String uritextmod = uritext.toString();

        // Sterge.setOnClickListener(new View.OnClickListener() {
        //
        // @Override
        // public void onClick(View v) {
        //
        //
        // final AlertDialog.Builder builder = new
        // AlertDialog.Builder(VeziMag.this);
        // builder.setMessage("Esti sigur ca vrei sa stergi magazinul?")
        // .setCancelable(false)
        // .setPositiveButton("Yes",
        // new DialogInterface.OnClickListener() {
        // public void onClick(final DialogInterface dialog,
        // final int id) {
        // sendToServer(id_mag);
        // }
        // })
        // .setNegativeButton("Nu", new DialogInterface.OnClickListener() {
        // public void onClick(final DialogInterface dialog,
        // final int id) {
        // dialog.cancel();
        // }
        // });
        // final AlertDialog alert = builder.create();
        // alert.show();
        //
        // }
        //
        //
        // }
        // );

        Duma.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // String uritext =
                // "http://maps.google.com/maps?saddr=".concat(Uri.encode(userlatpars)).concat(Uri.encode(", ")).concat(Uri.encode(userlonpars)).concat(Uri.encode("&daddr=")).concat(Uri.encode(shoplatpars)).concat(Uri.encode(", ")).concat(Uri.encode(shoplonpars));
                // String uritextmod = uritext.toString();
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse(uritextmod));
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);

            }
        });

        ibBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                finish();

            }
        });

        RetrieveTaskDM detaliiMag = new RetrieveTaskDM();
        detaliiMag.execute(id_mag);

    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();

    }

    // Background task to retrieve locations from remote mysql server
    public class RetrieveTaskDM extends AsyncTask<String, Object, JSONObject> {

        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(VeziMag.this);
            pDialog.setMessage(getString(R.string.loading_DM));
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... param) {

            int success;
            JSONObject listamagazine = null;

            String id2 = param[0];
            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("id", id2));

                // getting product details by making HTTP request
                // Note that product details url will use GET request
                JSONObject json = JSONParser.makeHttpRequest(
                        url_product_details, "GET", params);

                // check your log for json response
                Log.d("Single Product Details", json.toString());

                // json success tag
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    // successfully received product details
                    JSONArray productObj = json.getJSONArray(TAG_MAGAZINE); // JSON
                    // Array

                    // get first product object from JSON Array
                    listamagazine = productObj.getJSONObject(0);

                    // product with this pid found
                    // TextView NameDM = (TextView) findViewById(R.id.tvNumeDM);
                    // Edit Text
                    // NameDM.setText(listamagazine.getString(TAG_NAME));

                } else {
                    // product with pid not found
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return listamagazine;

        }

        @Override
        protected void onPostExecute(JSONObject result) {
            super.onPostExecute(result);
            TextView NameDM = (TextView) findViewById(R.id.tvNumeDM);
            try {
                NameDM.setText(result.getString(TAG_NAME));
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            TextView TipMagDM = (TextView) findViewById(R.id.tvTipDM);
            try {
                TipMagDM.setText(result.getString(TAG_TIP));
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            TextView LVDM = (TextView) findViewById(R.id.tvLVDM);
            try {
                LVDM.setText(result.getString(TAG_LV));
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            TextView SambataDM = (TextView) findViewById(R.id.tvSambataDM);
            try {
                SambataDM.setText(result.getString(TAG_SAMBATA));
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            TextView DuminicaDM = (TextView) findViewById(R.id.tvDuminicaDM);
            try {
                DuminicaDM.setText(result.getString(TAG_DUMINICA));
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            TextView POSDM = (TextView) findViewById(R.id.tvPOSDM);
            try {
                POSDM.setText(result.getString(TAG_POS));
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            pDialog.dismiss();
        }

    }



    public void sendToServer(String id_mag) {
        // chemam clasa SaveTask pentru executie si salvare in DB
        // new SaveTask(collNumeMag, lat, lng, mContext).execute(collNumeMag,
        // lat, lng, mContext);
        DeleteTask dtask = new DeleteTask(id_mag, VeziMag.this);
        dtask.execute(id_mag, VeziMag.this);
    }

    private void initialize() {
        Typeface tf = Typeface.createFromAsset(getAssets(),
                "fonts/comfortaa-regular.ttf");

        ibBack = (ImageButton) findViewById(R.id.ibBack);


        TextView NameDM = (TextView) findViewById(R.id.tvNumeDM);
        NameDM.setTypeface(tf);
        TextView TipMagDM = (TextView) findViewById(R.id.tvTipDM);
        TipMagDM.setTypeface(tf);
        TextView LVDM = (TextView) findViewById(R.id.tvLVDM);
        LVDM.setTypeface(tf);
        TextView SambataDM = (TextView) findViewById(R.id.tvSambataDM);
        SambataDM.setTypeface(tf);
        TextView DuminicaDM = (TextView) findViewById(R.id.tvDuminicaDM);
        DuminicaDM.setTypeface(tf);
        TextView POSDM = (TextView) findViewById(R.id.tvPOSDM);
        POSDM.setTypeface(tf);
        // TextView DescDM = (TextView) findViewById(R.id.tvDescDM);
        Duma = (Button) findViewById(R.id.btnDuma);
        Duma.setBackgroundResource(R.drawable.navigheaza);
        //TextView tvDuma = (TextView) findViewById(R.id.tvDuma);
        //tvDuma.setTypeface(tf);

        TextView tvtipstatic = (TextView) findViewById(R.id.tvTipstatic);
        tvtipstatic.setTypeface(tf);
        TextView tvPOSstatic = (TextView) findViewById(R.id.tvPOSstatic);
        tvPOSstatic.setTypeface(tf);
        TextView tvLVstatic = (TextView) findViewById(R.id.tvLVstatic);
        tvLVstatic.setTypeface(tf);
        TextView tvSambatastatic = (TextView) findViewById(R.id.tvSambatastatic);
        tvSambatastatic.setTypeface(tf);
        TextView tvDuminicastatic = (TextView) findViewById(R.id.tvDuminicastatic);
        tvDuminicastatic.setTypeface(tf);

        ibLike = (ImageButton) findViewById(R.id.ibLike);
        ibUnLike = (ImageButton) findViewById(R.id.ibUnlike);


    }

}