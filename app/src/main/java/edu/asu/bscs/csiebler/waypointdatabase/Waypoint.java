package edu.asu.bscs.csiebler.waypointdatabase;

import android.database.Cursor;

import org.json.JSONObject;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

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
public class Waypoint implements Serializable {

    /**
     * Categories for the Waypoints
     */
    public enum Category {
        TRAVEL,
        SCHOOL,
        TOURIST_TRAP,
        AUTO_DESTINATION,
        HIKING,
        RESTAURANT,
        FRIENDS,
        OTHER;

        @Override
        public String toString() {
            switch (this) {
                case TRAVEL:
                    return "Travel";
                case SCHOOL:
                    return "School";
                case TOURIST_TRAP:
                    return "Tourist Trap";
                case AUTO_DESTINATION:
                    return "Auto-Destination";
                case HIKING:
                    return "Hiking";
                case RESTAURANT:
                    return "Restaurant";
                case FRIENDS:
                    return "Friends";
                case OTHER:
                    return "Other";
                default:
                    return "";
            }
        }
    };

    private double latitude; // Value between -90 (south pole) and +90 (north pole)
    private double longitude; // Value between -180 and + 180. Longitude of -180 is the international date line
    private double elevation; // Height above sea level
    private String name; // Describes the Waypoint
    private String address; // Street address for a Waypoint
    private Category category; // Classification for a Waypoint

    /**
     * Constructor
     *
     * @param name Name of the Waypoint
     * @param address Address of the Waypoint
     * @param latitude Latitude of the Waypoint
     * @param longitude Longitude of the Waypoint
     * @param elevation Elevation of the Waypoint
     * @param category Category of the Waypoint
     */
    public Waypoint(String name, String address, double latitude, double longitude, double elevation, Category category) {
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.elevation = elevation;
        this.category = category;
    }

    /**
     * Constructor
     *
     * @param cursor
     */
    public Waypoint(Cursor cursor) {
        this.name = cursor.getString(0);
        this.address = cursor.getString(1);
        this.category = Category.valueOf(cursor.getString(2));
        this.elevation = cursor.getDouble(3);
        this.latitude = cursor.getDouble(4);
        this.longitude = cursor.getDouble(5);
    }

    /**
     * Constructor
     *
     * @param jsonObj
     */
    public Waypoint(JSONObject jsonObj) {
        try{
            latitude = jsonObj.getDouble("lat");
            longitude = jsonObj.getDouble("lon");
            elevation = jsonObj.getDouble("ele");
            name = jsonObj.getString("name");
            address = jsonObj.getString("address");
            category = Category.valueOf(jsonObj.getString("category"));
        } catch (Exception ex){
            System.out.println("Exception constructing waypoint from JSON: " + ex.getMessage());
        }
    }

    /**
     * Constructor
     *
     * @param jsonString
     */
    public Waypoint(String jsonString) {
        try{
            System.out.println("constructor from json received: "+jsonString);
            JSONObject obj = new JSONObject(jsonString);
            latitude = obj.getDouble("lat");
            longitude = obj.getDouble("lon");
            elevation = obj.getDouble("ele");
            name = obj.getString("name");
            address = obj.getString("address");
            category = Category.valueOf(obj.getString("category"));
            System.out.println("constructed " + this.toJsonString() + " from json");
        } catch (Exception ex) {
            System.out.println("Exception constructing waypoint from string: " + ex.getMessage());
        }
    }

    /**
     *
     * @return
     */
    public String toJsonString() {
        String ret = "{}";

        try{
            JSONObject obj = new JSONObject();
            obj.put("lat",new Double(latitude));
            obj.put("lon",new Double(longitude));
            obj.put("ele",new Double(elevation));
            obj.put("name",name);
            obj.put("address",address);
            obj.put("category",category.name());
            ret = obj.toString(0);
        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());
        }

        return ret;
    }

    /**
     *
     * @return
     */
    public JSONObject toJson(){
        JSONObject obj = new JSONObject();
        try{
            obj.put("lat",new Double(latitude));
            obj.put("lon",new Double(longitude));
            obj.put("ele",new Double(elevation));
            obj.put("name",name);
            obj.put("address",address);
            obj.put("category",category.name());
        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());
        }

        return obj;
    }

    /**
     *
     * @return
     */
    public Map<String,Object> toMap(){
        Map<String,Object> obj = new HashMap<>();

        try{
            obj.put("lat",new Double(latitude));
            obj.put("lon",new Double(longitude));
            obj.put("ele",new Double(elevation));
            obj.put("name",name);
            obj.put("address",address);
            obj.put("category",category.name());
        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());
        }

        return obj;
    }

    /**
     * toString method.
     *
     * @return name (address) - {latitude,longitude} - [category]
     */
    @Override
    public String toString() {
        DecimalFormat df = new DecimalFormat("##0.000");
        return name + " - (" + address + ") - {" + df.format(latitude) + "," + df.format(longitude) + "} @ " + elevation + " - [" + category + "]";
    }

    /**
     * Get latitude.
     *
     * @return value
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Set latitude.
     *
     * @param latitude
     * @return current object
     */
    public Waypoint setLatitude(int latitude) {
        this.latitude = latitude;
        return this;
    }

    /**
     * Get longitude.
     *
     * @return
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Set longitude.
     *
     * @param longitude
     * @return current object
     */
    public Waypoint setLongitude(int longitude) {
        this.longitude = longitude;
        return this;
    }

    /**
     * Get elevation.
     *
     * @return
     */
    public double getElevation() {
        return elevation;
    }

    /**
     * Set elevation.
     *
     * @param elevation
     * @return current object
     */
    public Waypoint setElevation(double elevation) {
        this.elevation = elevation;
        return this;
    }

    /**
     * Get name.
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Set name.
     *
     * @param name
     * @return current object
     */
    public Waypoint setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Get address.
     *
     * @return
     */
    public String getAddress() {
        return address;
    }

    /**
     * Set address.
     *
     * @param address
     * @return current object
     */
    public Waypoint setAddress(String address) {
        this.address = address;
        return this;
    }

    /**
     * Get category
     *
     * @return
     */
    public Category getCategory() {
        return category;
    }

    /**
     * Set category
     *
     * @param category
     * @return current object
     */
    public Waypoint setCategory(Category category) {
        this.category = category;
        return this;
    }

}
