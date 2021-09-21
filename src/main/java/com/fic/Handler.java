package com.fic;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import com.fic.common.CommonConst;
import com.fic.controller.AuthenticationController;
import com.fic.controller.DataBaseController;

import org.json.JSONObject;

// Handler value: example.Handler
public class Handler implements 
    RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    @Override
     public APIGatewayProxyResponseEvent
        handleRequest(APIGatewayProxyRequestEvent event, Context context) {

        // responseData make*******
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        AuthenticationController controller = new AuthenticationController();
        DataBaseController dataBaseController = new DataBaseController();

        // login
        if (event.getPath().equals(CommonConst.LOGIN)) {
            response = controller.login(event);
        }
        // logout
        if (event.getPath().equals(CommonConst.LOGOUT)) {
            response = controller.logout(event);
        }
        // changePassword
        if (event.getPath().equals(CommonConst.CHANGE_PASSWORD)) {
            response = controller.changePassword(event);
        }
        // goodsRegist
        if (event.getPath().equals(CommonConst.GOODS_REGISTER)) {
            response = dataBaseController.goodsRegister(event);
        }
        // goodsDelete
        if (event.getPath().equals(CommonConst.GOODS_DELETE)) {
            response = dataBaseController.goodsDelete(event);

        }

        // log出力
        LambdaLogger logger = context.getLogger();
        JSONObject output = outputLog(event, response);
        logger.log(output.toString());

        return response;
    }

    /**
     * ログの出力.
     */
    private JSONObject outputLog(
        APIGatewayProxyRequestEvent request, APIGatewayProxyResponseEvent response) {

        JSONObject outputLog = new JSONObject();
        JSONObject requestObject = new JSONObject(request.getBody());
        JSONObject responseObject = new JSONObject(response.getBody());

        if (response.getStatusCode() == CommonConst.HTTP_STATUS_CODE_200
                || response.getStatusCode() == CommonConst.HTTP_STATUS_CODE_300) {

            outputLog.put("apiname", request.getPath());
            outputLog.put("userid", requestObject.getString("userId"));
            outputLog.put("requestparam", requestObject);
            outputLog.put("responsecode", response.getStatusCode());
            outputLog.put("responseparam", responseObject);
        } else {
            outputLog.put("apiname", request.getPath());
            outputLog.put("userid", requestObject.getString("userId"));
            outputLog.put("requestparam", requestObject);
            outputLog.put("responsecode", response.getStatusCode());
            outputLog.put("stacTrace", responseObject.getString("stacTrace"));
        }
        return outputLog;

    }

}
