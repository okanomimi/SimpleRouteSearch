package com.example.okano.simpleroutesearch;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    public String travelMode = "driving";//default
    private int touchNum = 0 ;      // the number of touch
    private LatLng fromPoint ;      // the point of from
    private LatLng toPoint ;      // the point of to
    private static final int MENU_A = 0;
    private static final int MENU_B = 1;
    private static final int MENU_c = 2;
    public static String posinfo = "";
    public static String info_A = "";
    public static String info_B = "";
    public ArrayList<LatLng> markerPoints = new ArrayList<LatLng>();
    public ProgressDialog progressDialog;
    public static MarkerOptions options;

    public PopupWindow mPopupWindow ;
    public String routeDataText = "" ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_maps);
        setContentView(R.layout.activity_main);

        setUpMapIfNeeded();

        //プログレス
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("検索中だす......");
        progressDialog.hide();

    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {

        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));

        CameraUpdate cu = CameraUpdateFactory.newLatLng(new LatLng(43.0675, 141.350784));
        mMap.moveCamera(cu);

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
               //最初のタッチの処理
                if (markerPoints.size() < 1) {
                    markerPoints.add(latLng);
                    mMap.addMarker(new MarkerOptions().position(latLng).title("Marker"));
                }else if(markerPoints.size() == 1) {
                    markerPoints.add(latLng) ;
                    mMap.addMarker(new MarkerOptions().position(latLng).title("Marker"));
                    routeSearch();
                    markerPoints = new ArrayList<LatLng>();
                }
               //二回目のタッチの処理
            }
        }) ;

    }


    /**
     * ルート検索メソッド
//     * @param from
//     * @param to
     */
//    private void searchRoot(LatLng from, LatLng to){
        private void searchRoot(){
        String fromLat = "35.681382";
        String fromLng= "139.7660842";
        String toLat= "35.684752";
        String toLng= "139.707937";

        Intent intent = new Intent() ;
        intent.setAction(Intent.ACTION_VIEW);
//        intent.setClassName("com.google.android.apps.maps","com.google.android.maps.MapsActivity");
//            intent.setClassName("com.example.okano.simpleroutesearch","com.example.okano.simpleroutesearch.MapsActivity");
            intent.setClassName("com.google.android.apps.maps","com.google.android.maps.MapsActivity");
//        String fromLat = Double.toString(from.latitude) ;
//        String fromLng = Double.toString(from.longitude) ;
//        String toLat = Double.toString(from.latitude) ;
//        String toLng = Double.toString(from.longitude) ;
        intent.setData(Uri.parse("http://maps.google.com/maps?saddr=" + fromLat + "," + fromLng + "&daddr=" + toLat + "," + toLng));
            startActivity(intent) ;

    }

    private void routeSearch(){
        progressDialog.show();

        LatLng origin = markerPoints.get(0);
        LatLng dest = markerPoints.get(1);


        String url = getDirectionsUrl(origin, dest);

        DownloadTask downloadTask = new DownloadTask();


        downloadTask.execute(url);

    }

    private String getDirectionsUrl(LatLng origin,LatLng dest){

        String str_origin = "origin="+origin.latitude+","+origin.longitude;
        String str_dest = "destination="+dest.latitude+","+dest.longitude;
        String sensor = "sensor=false";

        //パラメータ
        String parameters = str_origin+"&"+str_dest+"&"+sensor + "&language=ja" + "&mode=" + travelMode;

        //JSON指定
        String output = "json";


        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

        return url;
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            Log.d("test", strUrl) ;
            urlConnection = (HttpURLConnection) url.openConnection();


            urlConnection.connect();


            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while( ( line = br.readLine()) != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private class DownloadTask extends AsyncTask<String, Void, String>{
        //非同期で取得

        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }


        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            parserTask.execute(result);
        }
    }



    /*parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> > {


        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jObject = new JSONObject(jsonData[0]);
               ParceJsonDirectionAPI parser = new ParceJsonDirectionAPI();


                routes = parser.parse(jObject);
                routeDataText = parser.getRouteInfo() ;
//                File file = new File("/home/okano/test.txt");

//                PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
//                pw.println(jsonData[0]) ;
//                Log.d("test", jsonData[0]) ;
//                pw.close() ;
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }

        //ルート検索で得た座標を使って経路表示
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {


            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            //ここで別途でルート情報を取得して保存しておく
//            routeDataText = getRouteTextFromJson(result);



            if(result.size() != 0){

                for(int i=0;i<result.size();i++){
                    points = new ArrayList<LatLng>();
                    lineOptions = new PolylineOptions();


                    List<HashMap<String, String>> path = result.get(i);


//                    routeDataText = routeDataText +path.toString() ;
                    for(int j=0;j<path.size();j++){
                        HashMap<String,String> point = path.get(j);

//                        routeDataText = routeDataText +path.toString() ;
                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);

                        points.add(position);
                    }

                    //ポリライン
                    lineOptions.addAll(points);
                    lineOptions.width(10);
                    lineOptions.color(0x550000ff);

                }

                //描画
                mMap.addPolyline(lineOptions);
            }else{
                mMap.clear();
                Toast.makeText(MapsActivity.this, "ルート情報を取得できませんでした", Toast.LENGTH_LONG).show();
            }
            progressDialog.hide();

        }
    }


    //ルートのjsonデータからルートの詳細をテキスト形式で保存するメソッド
    public String getRouteTextFromJson(List jData){
        return null ;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        menu.add(0, MENU_A,   0, "Info");
        menu.add(0, MENU_B,   0, "Legal Notices");
        menu.add(0, MENU_c,   0, "Mode");
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch ( item.getItemId() )
        {
            case MENU_A:
                showMapInfo();
                //show_mapInfo();
                return true;

            case MENU_B:
                //Legal Notices(免責事項)

                String LicenseInfo = GooglePlayServicesUtil.getOpenSourceSoftwareLicenseInfo(getApplicationContext());
                AlertDialog.Builder LicenseDialog = new AlertDialog.Builder(MapsActivity.this);
                LicenseDialog.setTitle("Legal Notices");
                LicenseDialog.setMessage(LicenseInfo);
                LicenseDialog.show();

                return true;

            case MENU_c:
                //show_settings();
                return true;

        }
        return false;
    }

    private void showMapInfo(){

        Toast.makeText(MapsActivity.this, "ルート情報を取得できませんでした", Toast.LENGTH_LONG).show();
        mPopupWindow = new PopupWindow(MapsActivity.this);
        // レイアウト設定
//        View popupView = getLayoutInflater().inflate(R.layout.popup_route_info, null);
        View popupView =  getLayoutInflater().inflate(R.layout.popup_route_info, null);

//        TextView textt = (TextView) popupView.findViewById(R.id.routeText);
        TextView textt = (TextView) popupView.findViewById(R.id.routeText);
        CharSequence htmlRoute = Html.fromHtml(routeDataText) ;
//        textt.setText(routeDataText);
        textt.setText(htmlRoute);
        mPopupWindow.setWindowLayoutMode(
                ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT
        );
        mPopupWindow.setContentView(popupView);
//        mPopupWindow.showAsDropDown(popupView, 10,-10);
        mPopupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
        mPopupWindow.setFocusable(true);
    }

    //リ･ルート検索
    private void re_routeSearch(){
        progressDialog.show();

        LatLng origin = markerPoints.get(0);
        LatLng dest = markerPoints.get(1);

        //
        mMap.clear();

        //マーカー
        //A
        options = new MarkerOptions();
        options.position(origin);
//        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.green));
        options.title("A");
        options.draggable(true);
        mMap.addMarker(options);
        //B
        options = new MarkerOptions();
        options.position(dest);
//        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.red));
        options.title("B");
        options.draggable(true);
        mMap.addMarker(options);


        String url = getDirectionsUrl(origin, dest);

        DownloadTask downloadTask = new DownloadTask();


        downloadTask.execute(url);

    }

}
