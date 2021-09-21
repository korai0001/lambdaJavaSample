package com.fic.controller;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fic.common.CommonUtils;
import com.fic.model.CognitoResponseModel;
import com.fic.service.DatabaseService;

import org.json.JSONObject;


public class DataBaseController {



    APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();

    /** 
    * goodsregister controller.
    * @param event APIGatewayProxyRequestEvent
    * @return response
    */
    public APIGatewayProxyResponseEvent goodsRegister(APIGatewayProxyRequestEvent event) {

        CognitoResponseModel model = new CognitoResponseModel();
        DatabaseService service = new DatabaseService();
        JSONObject responseDetailsJson = new JSONObject();

        model = service.goodsregisterService(event);

        APIGatewayProxyResponseEvent response = model.getResponse();

        response = CommonUtils.setResponseData(response);
        model.setResponse(response);

        if (model.getErrorMessage() != null) {
            responseDetailsJson.put("stacTrace",model.getErrorMessage());
            response.setBody(responseDetailsJson.toString());
        } else {
            response.setBody("{}");
        }

        return response;
    }

    /** 
    * goodsdelete controller.
    * @param event APIGatewayProxyRequestEvent
    * @return response
    */
    public APIGatewayProxyResponseEvent goodsDelete(APIGatewayProxyRequestEvent event) {

        CognitoResponseModel model = new CognitoResponseModel();
        DatabaseService service = new DatabaseService();
        JSONObject responseDetailsJson = new JSONObject();

        model = service.goodsdeleteService(event);

        APIGatewayProxyResponseEvent response = model.getResponse();

        response = CommonUtils.setResponseData(response);
        model.setResponse(response);
        if (model.getErrorMessage() != null) {
            responseDetailsJson.put("stacTrace",model.getErrorMessage());
            response.setBody(responseDetailsJson.toString());
        } else {
            response.setBody("{}");
        }
        return response;
    }





    
}
