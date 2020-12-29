package ddwu.mobile.finalproject.ma02_20181022;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class NetworkManager {

    private Context context;

    public NetworkManager(Context context){ this.context = context;}

    public String downloadContents(String address) {
        HttpURLConnection conn = null;
        InputStream stream = null;
        String result = null;

        if (!isOnline()) return null;

        try {
            URL url = new URL(address);
            conn = (HttpURLConnection)url.openConnection();
            stream = getNetworkConnection(conn);
            result = readStreamToString(stream);
            if (stream != null) stream.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) conn.disconnect();
        }
        return result;
    }

    /* 주소를 전달받아 bitmap 다운로드 후 반환 */
    public Bitmap downloadImage(String address) {
        HttpURLConnection conn = null;
        InputStream stream = null;
        Bitmap result = null;

        try {
            URL url = new URL(address);
            conn = (HttpURLConnection)url.openConnection();
            stream = getNetworkConnection(conn);
            result = readStreamToBitmap(stream);
            if (stream != null) stream.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) conn.disconnect();
        }

        return result;
    }

    /* InputStream을 전달받아 비트맵으로 변환 후 반환 */
    private Bitmap readStreamToBitmap(InputStream stream) {
        return BitmapFactory.decodeStream(stream);
    }
    /* InputStream을 전달받아 문자열로 변환 후 반환 */
    private String readStreamToString(InputStream stream){
        StringBuilder result = new StringBuilder();

        try {
            InputStreamReader inputStreamReader = new InputStreamReader(stream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String readLine = bufferedReader.readLine();

            while (readLine != null) {
                result.append(readLine + "\n");
                readLine = bufferedReader.readLine();
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    /* 네트워크 환경 조사 */
    private boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private InputStream getNetworkConnection(HttpURLConnection conn) throws Exception {
        // 클라이언트 아이디 및 시크릿 그리고 요청 URL 선언
        conn.setReadTimeout(3000);
        conn.setConnectTimeout(3000);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        if (conn.getResponseCode() != HttpsURLConnection.HTTP_OK) {
            throw new IOException("HTTP error code: " + conn.getResponseCode());
        }

        return conn.getInputStream();
    }
}
