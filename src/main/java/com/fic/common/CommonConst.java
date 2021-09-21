package com.fic.common;

public class CommonConst {

    // PATH
    public static final String LOGIN = "/login";
    public static final String CHANGE_PASSWORD = "/changepwd";
    public static final String LOGOUT = "/logout";
    public static final String GOODS_REGISTER = "/goods-register";
    public static final String GOODS_DELETE = "/goods-delete";
    // Constants for Dev Env
    
    public static final String DEV_ENVIRONMENT = "DEV_ENV";
    public static final String DEV_ENVIRONMENT_ACTIVE = "1";

    // Constants for AWS Cognito
    public static final String AWS_COGNITO_POOLID = "COGNITO_POOLID";
    public static final String AWS_COGNITO_CLIENTID = "COGNITO_CLIENTID";

    // cognito param
    public static final String USER_NAME = "USERNAME";
    public static final String PASSWORD = "PASSWORD";
    public static final String NEW_PASSWORD = "NEW_PASSWORD";
    public static final String USER_ID_FOR_SRP = "USER_ID_FOR_SRP";
    public static final int LOOP_CNT = 10;

    public static final String AWS_HTTPS_PROXY = "HTTPS_PROXY";
    public static final String AWS_HTTP_PROXY = "HTTP_PROXY";

    // Constants for HTTP Status Code
    public static final int HTTP_STATUS_CODE_200 = 200;
    public static final int HTTP_STATUS_CODE_491 = 491;
    public static final int HTTP_STATUS_CODE_500 = 500;
    public static final int HTTP_STATUS_CODE_406 = 406;
    public static final int HTTP_STATUS_CODE_404 = 404;
    public static final int HTTP_STATUS_CODE_401 = 401;
    public static final int HTTP_STATUS_CODE_403 = 403;
    public static final int HTTP_STATUS_CODE_415 = 415;
    public static final int HTTP_STATUS_CODE_429 = 429;
    public static final int HTTP_STATUS_CODE_300 = 300;

    // Database credentials
    public static final String RDS_DB_NAME = "practice";
    public static final String RDS_USERNAME = "admin";
    public static final String RDS_PASSWORD = "practice";
    public static final String RDS_HOSTNAME = 
        "ilp-training.cluster-cwpdgltmnj6j.us-west-2.rds.amazonaws.com";
    public static final String RDS_PORT = "3306";

}
