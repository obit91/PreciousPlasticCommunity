package com.example.android.preciousplastic.activities;

import com.example.android.preciousplastic.R;

import java.util.Map;
import java.util.TreeMap;

public class MapConstants {

    // logging tag
    public static final String TAG = "MAP_ACTIVITY";

    // keys of pins on map
    public final class MapPinKeys {
        final public static String ID = "ID";
        final public static String NAME = "name";
        final public static String LAT = "lat";
        final public static String LNG = "lng";
        final public static String DESC = "description";
        final public static String SITE = "website";
        final public static String IMGS = "imgs";
        final public static String STATUS = "status";
        final public static String CREATED = "created_date";
        final public static String MODIFIED = "modified_date";
        final public static String USERNAME = "username";
        final public static String FILTERS = "filters";
        final public static String FILTERS_STARTED = "STARTED";    // Want to get started
        final public static String FILTERS_WORKSPACE = "WORKSHOP";  // Workspace
        final public static String FILTERS_MACHINE = "MACHINE";    // Machine Builder
    }

    // grid density binning
    public final static int DENSITY_X = 10;
    public final static int DENSITY_Y = 10;

    // pin can be drawn as single, or as a group icon
    enum PinType {SINGLE, GROUP}
    public enum PinFilter {STARTED, WORKSPACE, MACHINE, HAZARDS}

    // map between pinFilter values and filters layout (resources) consts
    public static Map<PinFilter, Integer> filterConstsMap = createFiltersMap();
    public static Map<PinFilter, Integer> createFiltersMap() {
        TreeMap<PinFilter, Integer> map = new TreeMap<>();
        map.put(PinFilter.WORKSPACE, R.id.filter_workspace_checkbox);
        map.put(PinFilter.MACHINE, R.id.filter_machine_checkbox);
        map.put(PinFilter.STARTED, R.id.filter_started_checkbox);
        map.put(PinFilter.HAZARDS, R.id.filter_hazard_checkbox);
        return map;
    }
}
