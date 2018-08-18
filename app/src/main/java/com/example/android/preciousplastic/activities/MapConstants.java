package com.example.android.preciousplastic.activities;

import com.example.android.preciousplastic.R;

import java.util.Map;
import java.util.TreeMap;

public class MapConstants {

    // url addresses
    public final static String BASE_URL = "https://davehakkens.nl";
    public final static String MAP_PINS_SUFFIX = "/wp-json/map/v1/pins";

    // logging tag
    public static final String TAG = "MAP_ACTIVITY";

    // keys of pins on map
    public final class MapPinKeys {
        final static String ID = "ID";
        final static String NAME = "name";
        final static String LAT = "lat";
        final static String LNG = "lng";
        final static String DESC = "description";
        final static String SITE = "website";
        final static String IMGS = "imgs";
        final static String STATUS = "status";
        final static String CREATED = "created_date";
        final static String MODIFIED = "modified_date";
        final static String USERNAME = "username";
        final static String FILTERS = "filters";
        final static String FILTERS_STARTED = "STARTED";    // Want to get started
        final static String FILTERS_WORKSPACE = "WORKSPACE";  // Workspace
        final static String FILTERS_MACHINE = "MACHINE";    // Machine Builder
    }

    // grid density binning
    public final static int DENSITY_X = 10;
    public final static int DENSITY_Y = 10;

    // pin can be drawn as single, or as a group icon
    enum PinType {SINGLE, GROUP}
    enum PinFilter {STARTED, WORKSPACE, MACHINE, HAZARDS}

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
