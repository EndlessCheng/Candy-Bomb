package com.endless.android.candybomb;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HighScoreFetch {
    private static final String TAG = HighScoreFetch.class.getSimpleName();
    private static final String ENDPOINT = "http://endlesscheng.sinaapp.com/key/";
    private static final String API_KEY = ""; // * your api key
    private static final String METHOD_GET_HIGH_SCORE = "get_candy_bomb_high_score";

    private byte[] getUrlBytes(String urlSpec) throws IOException {
//        Log.i(TAG, urlSpec);

        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return null;
            }

            int bytesRead;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    private String getUrl(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    public String FetchHighScore() {
        try {
            String url = ENDPOINT + API_KEY + "/" + METHOD_GET_HIGH_SCORE;
            String result = getUrl(url);
//            Log.i(TAG, result);
            return result;
        } catch (IOException e) {

        }
        return null;
    }
}
