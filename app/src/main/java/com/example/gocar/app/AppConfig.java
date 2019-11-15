package com.example.gocar.app;

public class AppConfig {

    //In this class we declare the login and registration urls. While testing you need to replace the ip address with your localhost pc ip.

    // Server user login url
    public static String URL_LOGIN = "http://192.168.1.3/android_login_api/login.php";

    // Server user register url
    public static String URL_REGISTER = "http://192.168.1.3/android_login_api/register.php";

    //vehicle URL
    public static String URL_VEHICLE = "http://192.168.1.3/android_login_api/vehicle.php";

    //reviews URL
    public static String URL_REVIEW_POST = "http://192.168.1.3/android_login_api/reviewPost.php";

    //reviews get URL
    public static String URL_REVIEW_GET = "http://192.168.1.3/android_login_api/reviewGet.php";

}
