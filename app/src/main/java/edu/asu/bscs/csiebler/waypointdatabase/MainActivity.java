package edu.asu.bscs.csiebler.waypointdatabase;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

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
 * Purpose: main activity in a simple app to demonstrate expandable list view control and adapter.
 *
 * @author Tim Lindquist Tim.Lindquist@asu.edu
 *         Software Engineering, CIDSE, IAFSE, Arizona State University Polytechnic
 * @version February 07, 2015
 */
public class MainActivity extends Activity {

    public ExpandableListView elview;
    public ExpandableStuffAdapter myListAdapter;

    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        elview = (ExpandableListView) findViewById(R.id.lvExp);
        myListAdapter = new ExpandableStuffAdapter(this);
        elview.setAdapter(myListAdapter);
    }

    /**
     * Inflate the menu; this adds items to the action bar if it is present.
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Handle action bar item clicks here. The action bar will automatically handle clicks on the
     * Home/Up button, so long as you specify a parent activity in AndroidManifest.xml.
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * @param v
     */
    public void exportWaypoints(View v) {
        File sdCard = Environment.getExternalStorageDirectory();

        FileWriter fw = null;

        try {
            fw = new FileWriter(sdCard.getAbsolutePath() + "/waypoints.json");
            fw.write(getJsonResults().toString());
        } catch (IOException e) {
            Log.w(getClass().getSimpleName(), e.getMessage());
        } finally {
            try {
                if (fw != null) {
                    fw.flush();
                    fw.close();
                }
            } catch (IOException e) {
                Log.w(getClass().getSimpleName(), e.getMessage());
            }
        }
    }

    /**
     * @param v
     */
    public void newWaypoint(View v) {
        Intent intent = new Intent(this, FormActivity.class);
        startActivity(intent);
    }

    private JSONArray getJsonResults() {
        JSONArray resultSet = new JSONArray();

        try {
            WaypointDB db = new WaypointDB(this);
            db.copyDB();
            SQLiteDatabase waypointDB = db.openDB();
            waypointDB.beginTransaction();

            String searchQuery = "SELECT  * FROM waypoint";
            Cursor cursor = waypointDB.rawQuery(searchQuery, null);

            cursor.moveToFirst();

            while (cursor.isAfterLast() == false) {
                int totalColumn = cursor.getColumnCount();
                JSONObject rowObject = new JSONObject();

                for (int i = 0; i < totalColumn; i++) {
                    if (cursor.getColumnName(i) != null) {
                        try {
                            if (cursor.getString(i) != null) {
                                Log.d("TAG_NAME", cursor.getString(i));
                                rowObject.put(cursor.getColumnName(i), cursor.getString(i));
                            } else {
                                rowObject.put(cursor.getColumnName(i), "");
                            }
                        } catch (Exception e) {
                            Log.d("TAG_NAME", e.getMessage());
                        }
                    }
                }

                resultSet.put(rowObject);
                cursor.moveToNext();
            }

            cursor.close();
            waypointDB.endTransaction();
            waypointDB.close();
            db.close();

            Log.d("TAG_NAME", resultSet.toString());
        } catch (Exception ex) {
            Log.w(getClass().getSimpleName(), "Exception creating adapter: " + ex.getMessage());
        }

        return resultSet;
    }

}
