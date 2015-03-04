package edu.asu.bscs.csiebler.waypointdatabase;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.math.BigDecimal;

/**
 * Copyright 2015 Cory Siebler
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * @author Cory Siebler csiebler@asu.edu
 * @version Jan 27, 2015
 */
public class FormActivity extends Activity implements FormInterface {

    // Define the Latitude & Longitude Max/Min with Precision
    private static final double LONG_MIN = -180.000000001;
    private static final double LONG_MAX = 180.000000001;
    private static final double LAT_MIN = -90.000000001;
    private static final double LAT_MAX = 90.000000001;
    private static final int PRECISION = 8;

    private Waypoint waypoint;

    /**
     * Called once the activity is created. Parses the activity_main.xml file to create the view
     * for the activity.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        // Grab the Intent
        Intent intent = getIntent();

        // Load the Enum values to the Spinner
        Spinner mySpinner = (Spinner) findViewById(R.id.spinner_category);
        mySpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Waypoint.Category.values()));

        // Retrieve the Waypoint from the previous Intent
        waypoint = (Waypoint) intent.getSerializableExtra(getResources().getString(R.string.message_waypoint));

        if (waypoint != null) {
            Log.d(getClass().getSimpleName(), "Loading Waypoint...");

            // Find the View objects
            EditText nameField = (EditText) findViewById(R.id.field_name);
            EditText latField = (EditText) findViewById(R.id.field_lat);
            EditText longField = (EditText) findViewById(R.id.field_long);
            EditText elevationField = (EditText) findViewById(R.id.field_elevation);
            EditText addressField = (EditText) findViewById(R.id.field_address);
            Spinner categorySpinner = (Spinner) findViewById(R.id.spinner_category);
            TextView waypointText = (TextView) findViewById(R.id.text_waypoint);

            nameField.setText(waypoint.getName());
            addressField.setText(waypoint.getAddress());
            latField.setText(String.valueOf(waypoint.getLatitude()));
            longField.setText(String.valueOf(waypoint.getLongitude()));
            elevationField.setText(String.valueOf(waypoint.getElevation()));

            categorySpinner.setSelection(waypoint.getCategory().ordinal());

            waypointText.setText(waypoint.toString());

            Log.d(getClass().getSimpleName(), "SUCCESS: Waypoint Loaded");
        }
    }

    /**
     * Parses the menu_main.xml file to create the options menu.
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Handles the option selection.
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Called when the Submit button is selected
     *
     * @param v current view
     */
    public void submitWaypoint(View v) {
        // Find the View objects
        EditText nameField = (EditText) findViewById(R.id.field_name);
        EditText latField = (EditText) findViewById(R.id.field_lat);
        EditText longField = (EditText) findViewById(R.id.field_long);
        EditText elevationField = (EditText) findViewById(R.id.field_elevation);
        EditText addressField = (EditText) findViewById(R.id.field_address);
        Spinner categorySpinner = (Spinner) findViewById(R.id.spinner_category);
        TextView waypointText = (TextView) findViewById(R.id.text_waypoint);

        // Retrieve the Waypoint fields
        String name = nameField.getText().toString();
        String address = addressField.getText().toString();
        double latitude = Double.parseDouble(latField.getText().toString());
        double longitude = Double.parseDouble(longField.getText().toString());
        double elevation = Double.parseDouble(elevationField.getText().toString());
        Waypoint.Category category = (Waypoint.Category) categorySpinner.getSelectedItem();

        // Truncate the Double values to PRECISION decimal places
        double truncatedLatitude = new BigDecimal(latitude).setScale(PRECISION, BigDecimal.ROUND_HALF_UP).doubleValue();
        double truncatedLongitude = new BigDecimal(longitude).setScale(PRECISION, BigDecimal.ROUND_HALF_UP).doubleValue();
        double truncatedElevation = new BigDecimal(elevation).setScale(PRECISION, BigDecimal.ROUND_HALF_UP).doubleValue();

        // Validate the Name field
        if (name.isEmpty()) {
            waypointText.setText("ERROR: Please enter a name");
            return;
        }

        // Validate the Address field
        if (address.isEmpty()) {
            waypointText.setText("ERROR: Please enter an address");
            return;
        }

        // Validate the Latitude field
        if (!(LAT_MIN < truncatedLatitude && truncatedLatitude < LAT_MAX)) {
            waypointText.setText("ERROR: Invalid Latitude value given");
            return;
        }

        // Validate the Longitude field
        if (!(LONG_MIN < truncatedLongitude && truncatedLongitude < LONG_MAX)) {
            waypointText.setText("ERROR: Invalid Longitude value given");
            return;
        }

        // Validate the Elevation field
        if (elevationField.getText().toString().isEmpty()) {
            waypointText.setText("ERROR: Invalid Elevation value given");
            return;
        }

        // Initialize the new Waypoint
        waypoint = new Waypoint(name, address, truncatedLatitude, truncatedLongitude, truncatedElevation, category);

        try {
            WaypointDB db = new WaypointDB(this);

            Log.d(getClass().getSimpleName(), "STARTING: Database Connection");

            db.copyDB();
            SQLiteDatabase waypointDB = db.openDB();
            waypointDB.beginTransaction();

            Log.d(getClass().getSimpleName(), "PERFORMING: Waypoint Insert");

            Cursor cur = waypointDB.rawQuery("INSERT INTO waypoint VALUES (?,?,?,?,?,?)", new String[] {
                    waypoint.getName(),
                    waypoint.getAddress(),
                    waypoint.getCategory().name(),
                    Double.toString(waypoint.getElevation()),
                    Double.toString(waypoint.getLatitude()),
                    Double.toString(waypoint.getLongitude())
            });

            cur.close();
            waypointDB.endTransaction();
            waypointDB.close();
            db.close();

            Log.d(getClass().getSimpleName(), "COMPLETE: Waypoint Insert");
        } catch (Exception ex) {
            Log.w(getClass().getSimpleName(), "Exception creating adapter: " + ex.getMessage());
        }

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     *
     * @param v
     */
    public void removeWaypoint(View v) {
        try {
            WaypointDB db = new WaypointDB(this);

            Log.d(getClass().getSimpleName(), "STARTING: Database Connection");

            db.copyDB();
            SQLiteDatabase waypointDB = db.openDB();
            waypointDB.beginTransaction();

            Log.d(getClass().getSimpleName(), "PERFORMING: Waypoint Delete");

            Cursor cur = waypointDB.rawQuery("DELETE FROM waypoint WHERE name LIKE ?", new String[] {
                    waypoint.getName()
            });

            cur.close();
            waypointDB.endTransaction();
            waypointDB.close();
            db.close();

            Log.d(getClass().getSimpleName(), "COMPLETE: Waypoint Delete");
        } catch (Exception ex) {
            Log.w(getClass().getSimpleName(), "Exception creating adapter: " + ex.getMessage());
        }

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     *
     * @return
     */
    @Override
    public Resources getCurrentResources() {
        return getResources();
    }

}
