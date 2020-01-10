package com.ulan.az.usluga.helpers;

import android.location.Location;

import com.ulan.az.usluga.Category.Category;
import com.ulan.az.usluga.service.Service;

import java.util.ArrayList;

/**
 * Created by User on 04.08.2018.
 */

public class Shared {

    public static int category_id;
    public static boolean is_search = false;
    public static String search_text;
    public static double lat_search = 0;
    public static double lon_search = 0;
    public static int category_id_order;
    public static String mobile;
    public static Location location;
    public static ArrayList<Service> orderCategories;
    public static ArrayList<Service> serviceCategories;
    public static ArrayList<Category> forumCategories;
    public static ArrayList<Category> forumCategories_search;
    public static String key = "key=AAAAB0s4ndU:APA91bERzAa-JCTPzUVOqMEGCAUczKNSHWiQpW6wk_F4-FzQGlts3R8MZ8L8IotQWFynN8MaDNdQhTVuB0od7DUGs-3Ymjh-jkK6ttZRA-Eynq-FGO_VkheonfkiMdguM6q6gsxony4D";




}
