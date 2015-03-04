package edu.asu.bscs.csiebler.waypointdatabase;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedHashMap;

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
 * Purpose:  Demonstrate an adapter for an expandable list view
 *
 * @author Tim Lindquist Tim.Lindquist@asu.edu
 *         Software Engineering, CIDSE, IAFSE, Arizona State University Polytechnic
 * @version February 07, 2015
 */
public class ExpandableStuffAdapter extends BaseExpandableListAdapter implements View.OnTouchListener, ExpandableListView.OnGroupExpandListener, ExpandableListView.OnGroupCollapseListener, ExpandableListInterface {

    // Declare the Activity
    private MainActivity parent;

    // Linked hash map ensures consistent order for iteration and toarray.
    private LinkedHashMap<String,String[]> model;

    /**
     * @param parent
     */
    public ExpandableStuffAdapter(MainActivity parent) {
        Log.d(this.getClass().getSimpleName(), "in constructor so creating new model");

        this.parent = parent;
        this.model = new LinkedHashMap<>();

        parent.elview.setOnGroupExpandListener(this);
        parent.elview.setOnGroupCollapseListener(this);

        setModelFromDB();
    }

    /**
     * @param groupPosition
     * @param childPosition
     * @return
     */
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        String[] stuffTitles = model.keySet().toArray(new String[]{});
        return model.get(stuffTitles[groupPosition])[childPosition];
    }

    /**
     * @param groupPosition
     * @param childPosition
     * @return
     */
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    /**
     * @param groupPosition
     * @param childPosition
     * @param isLastChild
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final String childText = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            Log.d(this.getClass().getSimpleName(), "in getChildView null so creating new view");
            LayoutInflater inflater = (LayoutInflater) this.parent.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item, null);
        }

        TextView txtListChild = (TextView) convertView.findViewById(R.id.lblListItem);
        convertView.setOnTouchListener(this);
        txtListChild.setText(childText);

        return convertView;
    }

    /**
     * @param groupPosition
     * @return
     */
    @Override
    public int getChildrenCount(int groupPosition) {
        String[] stuffTitles = model.keySet().toArray(new String[]{});
        return model.get(stuffTitles[groupPosition]).length;
    }

    /**
     * @param groupPosition
     * @return
     */
    @Override
    public Object getGroup(int groupPosition) {
        String[] stuffTitles = model.keySet().toArray(new String[]{});
        return stuffTitles[groupPosition];
    }

    /**
     * @return
     */
    @Override
    public int getGroupCount() {
        String[] stuffTitles = model.keySet().toArray(new String[]{});
        return stuffTitles.length;
    }

    /**
     * @param groupPosition
     * @return
     */
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    /**
     * @param groupPosition
     * @param isExpanded
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);

        if (convertView == null) {
            Log.d(this.getClass().getSimpleName(), "in getGroupView null so creating new view");
            LayoutInflater inflater = (LayoutInflater) this.parent.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_group, null);
        }

        TextView lblListHeader = (TextView) convertView.findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        return convertView;
    }

    /**
     * @return
     */
    @Override
    public boolean hasStableIds() {
        return false;
    }

    /**
     * @param groupPosition
     * @param childPosition
     * @return
     */
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        String[] stuffTitles = model.keySet().toArray(new String[]{});
        return true;
    }

    /**
     * when the user touches an item, onTouch is called for action down and again for action up we
     * only want to do something on one of those actions. event tells us which action.
     *
     * @param v
     * @param event
     * @return
     */
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            Log.d(this.getClass().getSimpleName(), "in onTouch called for view of type: " + v.getClass().toString());

            // onTouch is passed the textview's parent view, a linearlayout - what we set the
            // event on. Look at its children to find the textview
            if (v instanceof android.widget.LinearLayout) {
                LinearLayout layView = (android.widget.LinearLayout) v;

                for (int i = 0; i <= layView.getChildCount(); i++) {
                    if (layView.getChildAt(i) instanceof TextView) {
                        TextView tmp = ((TextView) layView.getChildAt(i));
                        tmp.setBackgroundColor(Color.GRAY);
                        Log.d(this.getClass().getSimpleName(), "TextView " + ((TextView) layView.getChildAt(i)).getText() + " selected.");

                        // Load the Waypoint information from the SQLite DB
                        try {
                            WaypointDB db = new WaypointDB(parent);

                            Log.d(getClass().getSimpleName(), "STARTING: Database Connection");

                            db.copyDB();
                            SQLiteDatabase waypointDB = db.openDB();
                            waypointDB.beginTransaction();

                            Log.d(getClass().getSimpleName(), "PERFORM: Select Waypoint Query");

                            Cursor cur = waypointDB.rawQuery("SELECT name, address, category, elevation, latitude, longitude category FROM waypoint WHERE name LIKE ?;", new String[]{
                                    (String) tmp.getText()
                            });

                            Waypoint waypoint = new Waypoint(cur);

                            cur.close();
                            waypointDB.endTransaction();
                            waypointDB.close();
                            db.close();

                            Log.d(getClass().getSimpleName(), "COMPLETE: Select Waypoint Query");

                            Intent intent = new Intent(parent, FormActivity.class);
                            intent.putExtra(parent.getResources().getString(R.string.message_waypoint), waypoint);
                            parent.startActivity(intent);
                            parent.finish();
                        } catch (Exception ex) {
                            Log.w(getClass().getSimpleName(), "Exception creating adapter: " + ex.getMessage());
                        }
                    }
                }
            }

            // Code below never executes. onTouch is called for textview's linearlayout parent
            if (v instanceof TextView) {
                Log.d(this.getClass().getSimpleName(), "in onTouch called for: " + ((TextView) v).getText());
            }
        }

        return true;
    }

    /**
     * @param groupPosition
     */
    public void onGroupExpand(int groupPosition) {
        Log.d(this.getClass().getSimpleName(), "in onGroupExpand called for: " + model.keySet().toArray(new String[]{})[groupPosition]);

        for (int i = 0; i < this.getGroupCount(); i++) {
            if (i != groupPosition) {
                parent.elview.collapseGroup(i);
            }
        }
    }

    /**
     * @param groupPosition
     */
    public void onGroupCollapse(int groupPosition) {
        Log.d(this.getClass().getSimpleName(), "in onGroupCollapse called for: " + model.keySet().toArray(new String[]{})[groupPosition]);
    }

    /**
     *
     */
    @Override
    public void refreshList() {
        notifyDataSetChanged();
    }

    /**
     * @param waypoint
     */
    @Override
    public void requestForm(Waypoint waypoint) {
        Intent intent = new Intent(parent, FormActivity.class);
        intent.putExtra(parent.getResources().getString(R.string.message_waypoint), waypoint);
        parent.startActivity(intent);
    }

    /**
     * @return
     */
    @Override
    public Resources getCurrentResources() {
        return parent.getResources();
    }

    private void setModelFromDB() {
        try {
            model.clear();
            WaypointDB db = new WaypointDB(parent);

            Log.d(getClass().getSimpleName(), "STARTING: Database Connection");

            db.copyDB();
            SQLiteDatabase waypointDB = db.openDB();
            waypointDB.beginTransaction();

            Log.d(getClass().getSimpleName(), "PERFORM: Select Category Query");

            Cursor categories = waypointDB.rawQuery("SELECT DISTINCT category FROM waypoint;", new String[]{});

            Log.d(getClass().getSimpleName(), "COMPLETE: Select Category Query");

            while (categories.moveToNext()) {
                ArrayList<String> namesArr = new ArrayList<>();

                Waypoint.Category category = Waypoint.Category.valueOf(categories.getString(0));

                Log.d(getClass().getSimpleName(), "PERFORM: Select Waypoint Query");

                Cursor names = waypointDB.rawQuery("SELECT name FROM waypoint WHERE category LIKE ?;", new String[] {
                        category.name()
                });

                Log.d(getClass().getSimpleName(), "COMPLETE: Select Waypoint Query");

                while (names.moveToNext()) {
                    namesArr.add(names.getString(0));
                }

                model.put(category.toString(), namesArr.toArray(new String[]{}));

                names.close();
            }

            categories.close();
            waypointDB.endTransaction();
            waypointDB.close();
            db.close();
        } catch (Exception ex) {
            Log.w(getClass().getSimpleName(), "Exception creating adapter: " + ex.getMessage());
        }
    }

}
