package com.decoration.appshore;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;

import com.decoration.appshore.custom.CustomActivity;
import com.decoration.appshore.utils.Session;
import com.decoration.appshore.utils.Utils;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

public class register extends CustomActivity {

    /** The username EditText. */
    private EditText user;

    /** The password EditText. */
    private EditText pwd;

    /** The email EditText. */
    private EditText email;

    private static final String serverhost = "https://127.0.0.1:10050";

    private static final String fileSaveToken = "appShore_Token";

    /* (non-Javadoc)
     * @see com.chatt.custom.CustomActivity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        setTouchNClick(R.id.btnReg);

        user = (EditText) findViewById(R.id.user);
        pwd = (EditText) findViewById(R.id.pwd);
        email = (EditText) findViewById(R.id.email);
    }

    /* (non-Javadoc)
     * @see com.chatt.custom.CustomActivity#onClick(android.view.View)
     */
    @Override
    public void onClick(View v)
    {
        super.onClick(v);

        String u = user.getText().toString();
        String p = pwd.getText().toString();
        String e = email.getText().toString();
        if (u.length() == 0 || p.length() == 0 || e.length() == 0)
        {
            Utils.showDialog(this, R.string.err_fields_empty);
            return;
        }
        final ProgressDialog dia = ProgressDialog.show(this, null,
                getString(R.string.alert_wait));

        try {
            connectServer(u, p, e);
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (KeyManagementException e1) {
            e1.printStackTrace();
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        }

        try {
            Boolean connectRes = connectServer(u, p, e);
            dia.dismiss();
            if (connectRes) {
                startActivity(new Intent(register.this, MainActivity.class));
                setResult(RESULT_OK);
                finish();
            } else {
                Utils.showDialog(
                        register.this,
                        getString(R.string.err_singup) + " register fail!");
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (KeyManagementException e1) {
            e1.printStackTrace();
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        }
    }

    private String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException
    {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (NameValuePair pair : params)
        {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    private InputStream getInputStream(String urlStr, String user, String password, String email){
        InputStream resultString = null;
        try {
            URL url;
            Session session = new Session(getApplicationContext());
            url = new URL(urlStr);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

            // Create the SSL connection
            SSLContext sc;
            sc = SSLContext.getInstance("TLS");
            sc.init(null, null, new java.security.SecureRandom());
            conn.setSSLSocketFactory(sc.getSocketFactory());

            // set Timeout and method
            conn.setReadTimeout(7000);
            conn.setConnectTimeout(7000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            // set request head
//            conn.addRequestProperty("Accept-Language", "en-US,en;q=0.5");
            String sessionId = session.getValue("session");
            if (sessionId != null && !sessionId.isEmpty())
                conn.setRequestProperty("Cookie", "session=" + sessionId);

            // add post data

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("username", user));
            params.add(new BasicNameValuePair("password", password));
            params.add(new BasicNameValuePair("email", email));

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(getQuery(params));
            writer.flush();
            writer.close();
            os.close();

            conn.connect();
            int code = conn.getResponseCode();
            if (code == 200){
                resultString = conn.getInputStream();
                List<String> cookies = conn.getHeaderFields().get("Cookie");
                if(cookies != null){
                    for (String cookie : cookies){
                        if (cookie.contains("session")){
                            String[] sessions = cookie.split("=");
                            session.setKeyValue("session", sessions[1]);
                        }
                    }
                }
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultString;
    }

    public Boolean connectServer(String username, String password, String email) throws IOException, KeyManagementException, NoSuchAlgorithmException {
        InputStream is = getInputStream(serverhost, username, password, email);
        if (is == null)
            return false;
        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        StringBuilder stringBuilder = new StringBuilder();

        String bufferedStrChunk;

        while ((bufferedStrChunk = in.readLine()) != null) {
            stringBuilder.append(bufferedStrChunk);
        }
        try {
            JSONObject jObject = new JSONObject(stringBuilder.toString());
            String res = jObject.getString("Login");
            if (res.equals("success")) {
                String token = jObject.getString("Token");
                System.out.println(token);
                FileOutputStream fos = openFileOutput(fileSaveToken, Context.MODE_PRIVATE);
                fos.write(token.getBytes());
                fos.close();
                return true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }
}
