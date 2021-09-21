package com.fic.common;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import java.util.HashMap;

public class CommonUtils {

    /**
     * response common set function.
     * @param event responseCode
     * @return event 
     */
    public static APIGatewayProxyResponseEvent setResponseData(APIGatewayProxyResponseEvent event) {

        event.setIsBase64Encoded(false);

        //header set (CORS)
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Access-Control-Allow-Origin","*");
        headers.put("Access-Control-Allow-Headers","access-control-allow-origin");
        event.setHeaders(headers);
        
        return event;

    }

    /**
     * This method fetches the environment variables.
     * @param envName String
     * @param defData String
     * @return String
     */
    public static String getEnvData(String envName, String defData) {

        String retData = System.getenv(envName);
        if (retData == null) {
            return defData;
        }
        return retData;
    }

}
