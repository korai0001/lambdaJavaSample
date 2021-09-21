package com.fic.model;

import com.amazonaws.services.cognitoidp.model.AdminInitiateAuthResult;
import com.amazonaws.services.cognitoidp.model.AdminRespondToAuthChallengeResult;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

/**
 * Response from Cognito is mapped to this model class.
 * @author TCS
 */
public class CognitoResponseModel {

    /**
     * response model.
     */
    private APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();

    /**
     * cognito reslut model(login).
     */
    private AdminInitiateAuthResult loginResult = new AdminInitiateAuthResult();

    /**
     * cognito reslut model(change_password).
     */
    AdminRespondToAuthChallengeResult changePasswordResult = 
        new AdminRespondToAuthChallengeResult();

    /**
     * exception string.
     */
    private String errorMessage;

    /**
     * response.
     * @return responseModel
     */
    public APIGatewayProxyResponseEvent getResponse() {
        return response;
    }

    /**
     * response.
     * @param response responseModel
     */
    public void setResponse(APIGatewayProxyResponseEvent response) {
        this.response = response;
    }

    /**
     * loginresult getter.
     * @return cognitoResultModel
     */
    public AdminInitiateAuthResult getLoginResult() {
        return loginResult;
    }

    /**
     * loginresult setter.
     * @param result cognitoResultModel.
     */
    public void setLoginResult(AdminInitiateAuthResult result) {
        this.loginResult = result;
    }


    /**
     * changePasswordResult getter.
     * @return cognitoResultModel
     */
    public AdminRespondToAuthChallengeResult getChangePasswordResult() {
        return changePasswordResult;
    }

    /**
     * changePasswordResult setter.
     * @param result cognitoResultModel.
     */
    public void setChangePasswordResult(AdminRespondToAuthChallengeResult result) {
        this.changePasswordResult = result;
    }

    /**
     * result.
     * @return cognitoResultModel
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * result.
     * @param errorMessage exceptionMessage.
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

}
