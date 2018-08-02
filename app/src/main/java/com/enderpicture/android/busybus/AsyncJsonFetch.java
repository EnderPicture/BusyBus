package com.enderpicture.android.busybus;

import android.os.AsyncTask;
import android.widget.Toast;
import android.widget.Toolbar;

import org.json.JSONArray;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AsyncJsonFetch extends AsyncTask<AsyncJsonFetch.FetchObject, Double, AsyncJsonFetch.FetchObject> {


    @Override
    protected FetchObject doInBackground(FetchObject... fetchObjects) {

        if (fetchObjects.length > 0) {
            FetchObject fetchObject = fetchObjects[0];

            try {
                URL url = new URL(fetchObject.mURL);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.addRequestProperty("content-type", "application/JSON");

                InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());


                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                String inputLine;
                String totalOutput = "";
                while ((inputLine = bufferedReader.readLine()) != null)
                    totalOutput += inputLine;

                inputStream.close();
                bufferedReader.close();

                JSONArray jsonArray = new JSONArray(totalOutput);
                fetchObject.mJSONArray = jsonArray;
                fetchObject.mState = FetchObject.COMPLETED;

                return fetchObject;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Double... progress) {
//            setProgressPercent(progress[0]);
    }

    @Override
    protected void onPostExecute(FetchObject fetchObject) {
        if (fetchObject != null)
            fetchObject.mFetchInterface.afterAsyncJsonFetch(fetchObject);

        super.onPostExecute(fetchObject);
    }

    public static class FetchObject {
        public static final int FETCH = 0;
        public static final int COMPLETED = 1;
        public static final int ERROR = -1;

        protected int mTaskCode;
        protected String mURL;
        protected int mState;
        protected FetchInterface mFetchInterface;

        protected JSONArray mJSONArray = null;

        public FetchObject(String url, int taskCode, FetchInterface fetchInterface) {
            mURL = url;
            mTaskCode = taskCode;
            mFetchInterface = fetchInterface;

            mState = FETCH;
        }

        public void setJsonArray(JSONArray jsonArray) {
            if (jsonArray != null) {
                mJSONArray = jsonArray;
                mState = COMPLETED;
            } else {
                mState = ERROR;
            }
        }

        public String getmURL() {
            return mURL;
        }

        public int getmState() {
            return mState;
        }

        public JSONArray getmJSONArray() {
            return mJSONArray;
        }
    }

    public interface FetchInterface {
        void afterAsyncJsonFetch(FetchObject fetchObject);
    }
}
