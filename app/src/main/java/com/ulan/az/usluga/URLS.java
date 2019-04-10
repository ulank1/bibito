package com.ulan.az.usluga;

/**
 * Created by User on 31.07.2018.
 */

public class URLS {
    public static String root = "http://145.239.33.4:5555/";

    public static String users = root+"api/v1/users/?format=json&limit=0";
    public static String users_put = root+"api/v1/users/";
    public static String services = root+"api/v1/service/?format=json&limit=0";
    public static String services_delete = root+"api/v1/service/";
    public static String order = root+"api/v1/order/?format=json&limit=0";
    public static String order_delete = root+"api/v1/order/";
    public static String category = root+"api/v1/category/?format=json&limit=0";
    public static String category_service = root+"api/v1/category_service/?format=json&limit=0";
    public static String sub_category = root+"api/v1/subcategory/?format=json&limit=0";
    public static String sub_category_service = root+"api/v1/subcategory_service/?format=json&limit=0";
    public static String forum_sub_category = root+"api/v1/forumsubcategory/?format=json&limit=0";
    public static String forum_category = root+"api/v1/forumcategory/?format=json&limit=0";
    public static String forum = root+"api/v1/forum1/?format=json&limit=0";
    public static String forum_put = root+"api/v1/forum1/";
    public static String confirmation = root+"api/v1/confirmation/?format=json&limit=0";
    public static String confirmation_delete = root+"api/v1/confirmation/";
    public static String confirm = root+"api/v1/confirm/?format=json&limit=0";
    public static String confirm_delete = root+"api/v1/confirm/";
    public static String confirm_service = root+"api/v1/confirm_service/?format=json&limit=0";
    public static String confirm_delete_service = root+"api/v1/confirm_service/";
    public static String comment = root+"api/v1/comment/?format=json&limit=0";
    public static String comment_service = root+"api/v1/comment_service/?format=json&limit=0";
    public static String comment_order = root+"api/v1/comment_order/?format=json&limit=0";

    public static String like_order = root+"api/v1/like_order/?format=json&limit=0";
    public static String like_order_put = root+"api/v1/like_order/";
    public static String like_service = root+"api/v1/like_service/?format=json&limit=0";
    public static String like_service_put = root+"api/v1/like_service/";
    public static String like_forum = root+"api/v1/like_forum/?format=json&limit=0";
    public static String like_forum_put = root+"api/v1/like_forum/";


}
