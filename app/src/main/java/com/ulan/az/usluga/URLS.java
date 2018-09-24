package com.ulan.az.usluga;

/**
 * Created by User on 31.07.2018.
 */

public class URLS {
    public static String root = "http://145.239.33.4:5555/";

    public static String users = root+"api/v1/users/?format=json";
    public static String users_put = root+"api/v1/users/";
    public static String services = root+"api/v1/service/?format=json";
    public static String order = root+"api/v1/order/?format=json";
    public static String order_delete = root+"api/v1/order/";
    public static String category = root+"api/v1/category/?format=json";
    public static String sub_category = root+"api/v1/subcategory/?format=json";
    public static String forum_sub_category = root+"api/v1/forumsubcategory/?format=json";
    public static String forum_category = root+"api/v1/forumcategory/?format=json";
    public static String forum = root+"api/v1/forum1/?format=json";
    public static String forum_put = root+"api/v1/forum1/";
    public static String confirmation = root+"api/v1/confirmation/?format=json";
    public static String confirmation_delete = root+"api/v1/confirmation/";
    public static String confirm = root+"api/v1/confirm/?format=json";
    public static String confirm_delete = root+"api/v1/confirm/";
    public static String comment = root+"api/v1/comment/?format=json";

}
