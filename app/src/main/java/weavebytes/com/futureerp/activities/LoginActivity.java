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
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.TextUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import weavebytes.com.futureerp.utils.Config;
import weavebytes.com.futureerp.utils.ConvertResponse_TO_JSON;
import weavebytes.com.futureerp.R;
/*
class to login with username and password
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Button          login;
    private Button          register;
    private EditText        name;
    private EditText        pass;
    private String          username = "";
    private String          password = "";
    private ProgressDialog  progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login       = (Button) findViewById(R.id.btnlogin);
        register    = (Button) findViewById(R.id.btnregister);
        name        = (EditText) findViewById(R.id.edtusername);
        pass        = (EditText) findViewById(R.id.edtpassword);
        progress    = new ProgressDialog(LoginActivity.this);

        login.setOnClickListener(this);
        register.setOnClickListener(this);

    }
    //method to check internet connection
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

    //method to send login request
            public void SendJsonRequest() {
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
                            HttpPost httpPost = new HttpPost(Config.URL_LOGIN);
                            List<NameValuePair> nameValuePair = new ArrayList<>();
                            nameValuePair.add(new BasicNameValuePair("username", username));
                            nameValuePair.add(new BasicNameValuePair("password", password));

                            try {
                                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));

                            } catch (UnsupportedEncodingException e) {
                                return e + "";
                            }
                            try {
                                HttpResponse response = httpClient.execute(httpPost);
                                //Converting Response To JsonString
                                return ConvertResponse_TO_JSON.entityToString(response.getEntity());
                            } catch (ClientProtocolException e) {
                                // Log exception
                                e.printStackTrace();
                            } catch (IOException e) {
                                // Log exception
                                e.printStackTrace();
                            }
                            return "Bad NetWork";
                        } else {
                            return "Check Your Connection";

                        }
                    }

                    @Override
                    protected void onPostExecute(String JsonString) {
                     JSONObject jsonobj = null;
                        progress.dismiss();
                        try {

                            jsonobj = new JSONObject(JsonString);
                            //Parsing JSON and Checking the error_code (username ot password are correct or not)
                            if (jsonobj.getString("error").equals("0")) {
                                Config.U_ID = jsonobj.getString("user_id");
                                Intent intent = new Intent(LoginActivity.this, DashBoardActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, "Invalid User", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            //toast(e + " ");
                        }
                    }
                }.execute();
            }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btnlogin:
                username = name.getText().toString();
                password = pass.getText().toString();
                if(TextUtils.isEmpty(username))
                {
                    name.setError("Enter Name");
                }
            else if(TextUtils.isEmpty(password)){
                pass.setError("Enter Password");
               }
                else {
                    SendJsonRequest();
                }
                break;
            case R.id.btnregister:
                Intent it = new Intent(LoginActivity.this,RegistrationActivity.class);
                startActivity(it);
                break;

        }
    }
}//LoginActivity





