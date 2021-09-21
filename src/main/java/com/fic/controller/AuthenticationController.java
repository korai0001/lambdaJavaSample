package com.fic.controller;

import com.amazonaws.services.cognitoidp.model.AdminInitiateAuthResult;
import com.amazonaws.services.cognitoidp.model.AdminRespondToAuthChallengeResult;
import com.amazonaws.services.cognitoidp.model.TooManyRequestsException;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fic.common.CommonConst;
import com.fic.common.CommonUtils;
import com.fic.model.CognitoResponseModel;
import com.fic.service.AuthenticationService;

import org.json.JSONObject;

public class AuthenticationController {
    
    /** 
    * login controller.
    * @param event APIGatewayProxyRequestEvent
    * @return response
    */
    public APIGatewayProxyResponseEvent login(APIGatewayProxyRequestEvent event) {

        CognitoResponseModel model = new CognitoResponseModel();
        AuthenticationService service = new AuthenticationService();
        int maxRetryCount = 5;
        int loopCount = 0;
        //cognito呼び出し、TooManyRequest用にリトライ処理

        for (loopCount = 0; loopCount < maxRetryCount; loopCount++) {
            try {
                model = service.loginService(event);
                break;
            } catch (TooManyRequestsException ex) {
                ex.printStackTrace();
            }
        }
        APIGatewayProxyResponseEvent response = model.getResponse();

        //最大リトライ回数エラー
        if (loopCount >= maxRetryCount) {
            response.setStatusCode(CommonConst.HTTP_STATUS_CODE_429);
        }
        //初回ログイン時

        //JSONObject responseDetailsJson = new JSONObject(response.getBody());
        AdminInitiateAuthResult result = model.getLoginResult();
        String changeParam = result.getChallengeName();
        if (changeParam != null && changeParam.equals("NEW_PASSWORD_REQUIRED")) {
            response.setStatusCode(CommonConst.HTTP_STATUS_CODE_300);
        }
        //response情報の生成
        response = CommonUtils.setResponseData(response);
        model.setResponse(response);
        if (response.getStatusCode() == CommonConst.HTTP_STATUS_CODE_429) {
            model.setErrorMessage("TooManyRequestsException");
        }
        response.setBody(makeBodylogin(model,response.getStatusCode()));

        return response;
    }

    /** 
    * login controller.
    * @param event APIGatewayProxyRequestEvent
    * @return response
    */
    public APIGatewayProxyResponseEvent logout(APIGatewayProxyRequestEvent event) {

        CognitoResponseModel model = new CognitoResponseModel();
        AuthenticationService service = new AuthenticationService();

        model = service.logoutService(event);

        APIGatewayProxyResponseEvent response = model.getResponse();

        response = CommonUtils.setResponseData(response);
        model.setResponse(response);
        response.setBody("{}");

        return response;
    }

    /** 
    * login controller.
    * @param event APIGatewayProxyRequestEvent
    * @return response
    */
    public APIGatewayProxyResponseEvent changePassword(APIGatewayProxyRequestEvent event) {

        CognitoResponseModel model = new CognitoResponseModel();
        AuthenticationService service = new AuthenticationService();

        model = service.defaultPwdChange(event);

        APIGatewayProxyResponseEvent response = model.getResponse();

        response = CommonUtils.setResponseData(response);
        model.setResponse(response);
        response.setBody(makeBodyChangePassword(model,response.getStatusCode()));

        return response;
    }

    /**
     * login時の戻り値作成.
     * @return responseBody BODY値
     */
    private static String makeBodylogin(CognitoResponseModel model, int status) {

        AdminInitiateAuthResult result = model.getLoginResult();
        JSONObject responseDetailsJson = new JSONObject();

        //System.out.println(result.getAuthenticationResult());
        if (status == CommonConst.HTTP_STATUS_CODE_200) {

            //tokenは直接取らないと表示できないので、詰め替える
            responseDetailsJson.put(
                "AccessToken",result.getAuthenticationResult().getAccessToken());
            
        } else if (status == CommonConst.HTTP_STATUS_CODE_300) {
            responseDetailsJson.put("Session",result.getSession());

        }

        //ログ出力用にbodyに追記する
        if (model.getErrorMessage() != null) {
            responseDetailsJson.put("stacTrace",model.getErrorMessage());
        }

        String responseBody = responseDetailsJson.toString();
        return responseBody;
        
    }

    /**
     * PasswordChange時の戻り値作成.
     * @return responseBody BODY値
     */
    private static String makeBodyChangePassword(CognitoResponseModel model, int status) {

        AdminRespondToAuthChallengeResult result = model.getChangePasswordResult();
        JSONObject responseDetailsJson = new JSONObject();

        if (status == CommonConst.HTTP_STATUS_CODE_200) {

            //tokenは直接取らないと表示できないので、詰め替える
            responseDetailsJson.put(
                "AccessToken",result.getAuthenticationResult().getAccessToken());
        }

        //ログ出力用にbodyに追記する
        if (model.getErrorMessage() != null) {
            responseDetailsJson.put("stacTrace",model.getErrorMessage());
        }

        String responseBody = responseDetailsJson.toString();
        return responseBody;
        
    }

}
