package edu.asu.bscs.csiebler.waypointdatabase;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;

/**
 * Copyright 2015 Tim Lindquist,
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p/>
 * Purpose: helper for accessing the waypoint database
 *
 * @author Tim Lindquist Tim.Lindquist@asu.edu
 *         Software Engineering, CIDSE, IAFSE, Arizona State University Polytechnic
 * @version February 17, 2015
 */
public class WaypointDB extends SQLiteOpenHelper {
    private static final boolean debugon = false;
    private static final int DATABASE_VERSION = 3;
    private static String dbName = "waypoint";
    private String dbPath;
    private SQLiteDatabase crsDB;
    private final Context context;

    /**
     * @param context
     */
    public WaypointDB(Context context) {
        super(context, dbName, null, DATABASE_VERSION);
        this.context = context;
        dbPath = context.getFilesDir().getPath() + "/";
        Log.d(this.getClass().getSimpleName(), "dbpath: " + dbPath);
    }

    /**
     * @throws IOException
     */
    public void createDB() throws IOException {
        this.getReadableDatabase();

        try {
            copyDB();
        } catch (IOException e) {
            Log.w(this.getClass().getSimpleName(), "createDB Error copying database " + e.getMessage());
        }
    }

    /**
     *
     * @throws IOException
     */
    public void copyDB() throws IOException {
        try {
            if (!checkDB()) {
                // only copy the database if it doesn't already exist in my database directory
                debug("WaypointDB --> copyDB", "checkDB returned false, starting copy");
                InputStream ip = context.getResources().openRawResource(R.raw.waypoint);
                // make sure the database path exists. if not, create it.
                File aFile = new File(dbPath);

                if (!aFile.exists()) {
                    aFile.mkdirs();
                }

                String op = dbPath + dbName + ".db";
                OutputStream output = new FileOutputStream(op);
                byte[] buffer = new byte[1024];
                int length;

                while ((length = ip.read(buffer)) > 0) {
                    output.write(buffer, 0, length);
                }

                output.flush();
                output.close();
                ip.close();
            }
        } catch (IOException e) {
            Log.w("WaypointDB --> copyDB", "IOException: " + e.getMessage());
        }
    }

    /**
     *
     * @return
     * @throws SQLException
     */
    public SQLiteDatabase openDB() throws SQLException {
        String myPath = dbPath + dbName + ".db";
        crsDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
        debug("WaypointDB --> openDB", "opened db at path: " + crsDB.getPath());
        return crsDB;
    }

    /**
     *
     */
    @Override
    public synchronized void close() {
        if (crsDB != null)
            crsDB.close();
        super.close();
    }

    /**
     *
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    /**
     *
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * does the database exist and has it been initialized? This method determines whether
     * the database needs to be copied to the data/data/pkgName/databases directory by
     * checking whether the file exists. If it does it checks to see whether the db is
     * uninitialized or whether it has the waypoint table.
     *
     * @return false if the database file needs to be copied from the assets directory, true
     * otherwise.
     */
    private boolean checkDB() {    //does the database exist and is it initialized?
        SQLiteDatabase checkDB = null;
        boolean ret = false;

        try {
            String path = dbPath + dbName + ".db";
            debug("WaypointDB --> checkDB: path to db is", path);
            File aFile = new File(path);

            if (aFile.exists()) {
                checkDB = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READWRITE);

                if (checkDB != null) {
                    debug("WaypointDB --> checkDB", "opened db at: " + checkDB.getPath());
                    Cursor tabChk = checkDB.rawQuery("SELECT name FROM sqlite_master where type='table' and name='waypoint';", null);
                    boolean crsTabExists = false;

                    if (tabChk == null) {
                        debug("WaypointDB --> checkDB", "check for waypoint table result set is null");
                    } else {
                        tabChk.moveToNext();
                        debug("WaypointDB --> checkDB", "check for waypoint table result set is: " + ((tabChk.isAfterLast() ? "empty" : tabChk.getString(0))));
                        crsTabExists = !tabChk.isAfterLast();
                    }

                    if (crsTabExists) {
                        Cursor c = checkDB.rawQuery("SELECT * FROM waypoint", null);
                        c.moveToFirst();

                        while (!c.isAfterLast()) {
                            String waypointName = c.getString(0);
                            debug("WaypointDB --> checkDB", "Waypoint table has WaypointName: " + waypointName);
                            c.moveToNext();
                        }

                        ret = true;
                    }
                }
            }
        } catch (SQLiteException e) {
            Log.w("WaypointDB->checkDB", e.getMessage());
        }

        if (checkDB != null) {
            checkDB.close();
        }

        return ret;
    }

    private void debug(String hdr, String msg) {
        if (debugon) {
            Log.d(hdr, msg);
        }
    }

}