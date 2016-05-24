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
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import java.util.ArrayList;
import java.util.List;

import weavebytes.com.futureerp.utils.Config;
import weavebytes.com.futureerp.utils.ConvertResponse_TO_JSON;
import weavebytes.com.futureerp.R;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {

    Button      register;
    EditText    edtname;
    EditText    edtpass;
    EditText    edtmail;
    String      username = "";
    String      password = "";
    String      email = "";
    private ProgressDialog progress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        register = (Button) findViewById(R.id.btnregister);
        edtname  = (EditText) findViewById(R.id.edtusername);
        edtpass  = (EditText) findViewById(R.id.edtpassword);
        edtmail  = (EditText) findViewById(R.id.edtemail);
        progress = new ProgressDialog(RegistrationActivity.this);

        register.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.btnregister:
                username = edtname.getText().toString();
                password = edtpass.getText().toString();
                email    = edtmail.getText().toString();
                if (TextUtils.isEmpty(username)){

                    edtname.setError(username);
                } else if(TextUtils.isEmpty(password)){
                    edtpass.setError("Enter Password");
                }else if(TextUtils.isEmpty(email)){
                    edtmail.setError("Enter Password");
                }
                else
                SendRegRequest();
        }
    }

    //method to send request to register new user
    public void SendRegRequest(){

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

                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost     = new HttpPost(Config.URL_REGISTER);

                    List<NameValuePair> nameValuePair = new ArrayList<>();
                    nameValuePair.add(new BasicNameValuePair("email", email));
                    nameValuePair.add(new BasicNameValuePair("username", username));
                    nameValuePair.add(new BasicNameValuePair("password", password));

                    try {
                        httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));

                    } catch (UnsupportedEncodingException e) {
                        return e + "";
                    }
                    try {
                        HttpResponse response = httpClient.execute(httpPost);
                        Log.d("Http Post Response:", response.toString());
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
            }

            @Override
            protected void onPostExecute(String JsonString) {
                Toast.makeText(RegistrationActivity.this, JsonString, Toast.LENGTH_SHORT).show();
                JSONObject jsonobj = null;
                progress.dismiss();
                try {
                    jsonobj = new JSONObject(JsonString);
                    //Parsing JSON and Checking the error_code (username ot password are correct or not)

                    if (jsonobj.getString("error").equals("0")) {

                        Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(RegistrationActivity.this, "Failed to register", Toast.LENGTH_SHORT).show();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                   }
            }
        }.execute();
    }
}//RegistrationActivity
