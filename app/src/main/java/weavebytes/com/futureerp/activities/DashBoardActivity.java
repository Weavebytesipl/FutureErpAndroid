/*

  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program.  If not, see <http://www.gnu.org/licenses/>.

  Under research & development by Weavebytes, weavebytes@gmail.com
 */

package weavebytes.com.futureerp.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import weavebytes.com.futureerp.utils.Config;
import weavebytes.com.futureerp.utils.ConvertResponse_TO_JSON;
import weavebytes.com.futureerp.adapter.MyAdapter;
import weavebytes.com.futureerp.R;

/*
class displayed to valid user after login
 */
public class DashBoardActivity extends AppCompatActivity {

    ProgressDialog progress;
    public static ArrayList<String> arrayList = new ArrayList();
    ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progress = new ProgressDialog(DashBoardActivity.this);
        arrayList.clear();
        JsonParsing();
    }

    // JasonParsing for retrieved values and setting values to textFields
    public void JsonParsing() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progress.setMessage("Please Wait..");
                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progress.setCancelable(true);
                progress.show();
            }

            @Override
            protected String doInBackground(Void... params) {

                if (isOnline()) {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpGet httpget = new HttpGet(Config.URL+Config.U_ID);
                    HttpResponse response = null;
                    try {
                        response = httpClient.execute(httpget);
                        //Converting Response To JsonString
                        return ConvertResponse_TO_JSON.entityToString(response.getEntity());
                    } catch (ClientProtocolException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return "Bad Network";
                } else {
                    return "Check Your Connection";
                }
            }

            @Override
            protected void onPostExecute(String JasonString) {
                try {
                    progress.dismiss();

                    try {
                        JSONArray JArray;
                        JArray = new JSONArray(JasonString);
                        for (int i = 0; i < JArray.length(); i++) {

                            JSONObject obj = JArray.optJSONObject(i);
                            String name = obj.optString("name").toString();
                            arrayList.add(name);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    MyAdapter myAdapter = new MyAdapter(DashBoardActivity.this,arrayList,Config.IMG_ID);
                    list = (ListView)(findViewById(R.id.list));
                    list.setAdapter(myAdapter);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    // checking internet
    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            try {
                URL url = new URL("http://www.google.com");
                HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                urlc.setConnectTimeout(2000);
                urlc.connect();
                if (urlc.getResponseCode() == 200) {
                    return new Boolean(true);
                }
            } catch (MalformedURLException e1) {
                e1.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}//DashBoardActivity
