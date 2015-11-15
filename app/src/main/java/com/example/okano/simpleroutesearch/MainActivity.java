package com.example.okano.simpleroutesearch;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

/**
 * Created by okano on 15/10/25.
 */
public class MainActivity extends Activity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        searchRoot();
    }

    @Override
    protected void onResume() {
        super.onResume();
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
        intent.setClassName("com.google.android.apps.maps","com.google.android.maps.MapsActivity");
//            intent.setClassName("com.example.okano.simpleroutesearch","com.example.okano.simpleroutesearch.MapsActivity");

//        String fromLat = Double.toString(from.latitude) ;
//        String fromLng = Double.toString(from.longitude) ;
//        String toLat = Double.toString(from.latitude) ;
//        String toLng = Double.toString(from.longitude) ;
        intent.setData(Uri.parse("http://maps.google.com/maps?saddr=" + fromLat + "," + fromLng + "&daddr=" + toLat + "," + toLng));
            startActivity(intent) ;

    }
}
