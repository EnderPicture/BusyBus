package com.enderpicture.android.busybus;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.widget.DialogTitle;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class AsyncArchiveFetch extends AsyncTask<AsyncArchiveFetch.FetchObject, Integer, AsyncArchiveFetch.FetchObject> {

    FetchObject fetchObject;

    @Override
    protected FetchObject doInBackground(FetchObject... fetchObjects) {
        if (fetchObjects.length > 0) {

            long millis = System.currentTimeMillis();

            fetchObject = fetchObjects[0];
            try {
                // start connection
                URL url = new URL("http://ns.translink.ca/gtfs/google_transit.zip");
                URLConnection conn = url.openConnection();
                int contentLength = conn.getContentLength();

                DataInputStream stream = new DataInputStream(url.openStream());



                if (isCancelled()) return null;

                // download
                byte[] buffer = new byte[contentLength];
                stream.readFully(buffer);
                stream.close();

                if (isCancelled()) return null;
                publishProgress(10);

                // create new file
                File dir = fetchObject.mContext.getExternalCacheDir();
                File newFolder = new File(dir.getPath() + "/temp");
                newFolder.mkdir();
                File newZip = new File(newFolder.getPath() + "/archived_data.zip");

                // write data to file
                DataOutputStream fos = new DataOutputStream(new FileOutputStream(newZip));
                fos.write(buffer);
                fos.flush();
                fos.close();

                // unzip
                byte[] zipDecompressionBuffer = new byte[1024];
                ZipInputStream zis = new ZipInputStream(new FileInputStream(newZip.getPath()));
                ZipEntry zipEntry = zis.getNextEntry();
                while (zipEntry != null) {
                    String fileName = zipEntry.getName();
                    File newExtraction = new File(newFolder.getPath() + "/" + fileName);

                    FileOutputStream extractionFos = new FileOutputStream(newExtraction);
                    int len;
                    while ((len = zis.read(zipDecompressionBuffer)) > 0) {
                        extractionFos.write(zipDecompressionBuffer, 0, len);
                    }
                    extractionFos.close();
                    zipEntry = zis.getNextEntry();
                }

                // delete the zip file, already got all .txt files
                newZip.delete();

                if (isCancelled()) return null;
                publishProgress(20);

                SQLiteGTFSHelper gtfsHelper = new SQLiteGTFSHelper(fetchObject.mContext);
                SQLiteDatabase database = gtfsHelper.getWritableDatabase();
                gtfsHelper.clear(database);

                String[] list = newFolder.list();

                // speed up sql for it doing pointless things
//                database.execSQL("PRAGMA synchronous = OFF;");
//                database.execSQL("PRAGMA journal_mode = MEMORY;");
                database.beginTransaction();
                try {

                    for (int i = 0; i < list.length; i++) {
                        // get just the file name without .txt

                        String fileName = list[i].replace(".txt", "");

                        // grab the file
                        File file = new File(newFolder.getPath() + "/" + list[i]);

                        // only use one file for now, no need for all the other files
//                        if (fileName.equals(SQLiteGTFSHelper.AGENCY) ||
//                                fileName.equals(SQLiteGTFSHelper.CALENDAR) ||
//                                fileName.equals(SQLiteGTFSHelper.CALENDAR_DATES) ||
//                                fileName.equals(SQLiteGTFSHelper.FEED_INFO) ||
//                                fileName.equals(SQLiteGTFSHelper.ROUTES) ||
//                                fileName.equals(SQLiteGTFSHelper.SHAPES) ||
//                                fileName.equals(SQLiteGTFSHelper.STOP_TIMES) ||
//                                fileName.equals(SQLiteGTFSHelper.STOPS) ||
//                                fileName.equals(SQLiteGTFSHelper.TRANSFERS) ||
//                                fileName.equals(SQLiteGTFSHelper.TRIPS)) {
                        if (fileName.equals(SQLiteGTFSHelper.STOPS)) {


                            // start reading the file
                            BufferedReader br = new BufferedReader(new FileReader(file));

                            String line;

                            // remove the first line
                            if ((br.readLine()) != null) {
                                while ((line = br.readLine()) != null) {
                                    if (isCancelled()) return null;
                                    line+=" ";
                                    String[] values = line.split(",");

                                    ContentValues contentValues = new ContentValues();

                                    String[] columns = SQLiteGTFSHelper.COL.get(fileName);

                                    if (values.length == columns.length) {
                                        for (int j = 0; j < columns.length; j++) {

                                            if (columns[j].contains("lat") || columns[j].contains("lon")) {
                                                contentValues.put(columns[j], Double.parseDouble(values[j]));
                                            } else {
                                                contentValues.put(columns[j], values[j]);
                                            }
                                        }
                                        database.insert(fileName, null, contentValues);
                                    }
                                }
                            }
                        }

                        file.delete();

                        publishProgress(20+((int)(((double)(i+1)/list.length)*70)));
                    }

                    database.setTransactionSuccessful();
                } finally {
                    database.endTransaction();
                    database.close();
//                    database.execSQL("PRAGMA synchronous = NORMAL;");
//                    database.execSQL("PRAGMA journal_mode = WAL;");
                }


                // delete everything
                newFolder.delete();
                publishProgress(100);
                return fetchObjects[0];

            } catch (Exception e) {
                e.printStackTrace();
            }


        }


        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);

        fetchObject.fetchUpdateInterface.fetchUpdate(values[0]);
    }

    @Override
    protected void onPostExecute(FetchObject fetchObject) {
        super.onPostExecute(fetchObject);
    }

    public static class FetchObject {
        Context mContext;
        FetchUpdateInterface fetchUpdateInterface;

        public FetchObject(Context context, FetchUpdateInterface _fetchUpdateInterface) {
            mContext = context;
            fetchUpdateInterface = _fetchUpdateInterface;
        }
    }

    public interface FetchUpdateInterface {
        /**
         * upates the ui
         *
         * @param value from 0 -> 100
         */
        void fetchUpdate(int value);
    }
}
