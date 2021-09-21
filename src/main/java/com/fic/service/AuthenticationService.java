package com.fic.service;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.regions.Regions;

//cognito関係
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.amazonaws.services.cognitoidp.model.AdminInitiateAuthRequest;
import com.amazonaws.services.cognitoidp.model.AdminInitiateAuthResult;
import com.amazonaws.services.cognitoidp.model.AdminRespondToAuthChallengeRequest;
import com.amazonaws.services.cognitoidp.model.AdminRespondToAuthChallengeResult;
import com.amazonaws.services.cognitoidp.model.AuthFlowType;
import com.amazonaws.services.cognitoidp.model.ChallengeNameType;
import com.amazonaws.services.cognitoidp.model.GlobalSignOutRequest;

import com.amazonaws.services.cognitoidp.model.NotAuthorizedException;
import com.amazonaws.services.cognitoidp.model.TooManyRequestsException;
import com.amazonaws.services.cognitoidp.model.UserNotFoundException;

//apigateway関係
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import com.fic.common.CommonConst;
import com.fic.model.CognitoResponseModel;

import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;

public class AuthenticationService {

    private AWSCognitoIdentityProvider cognitoIdentityProvider;

    /**
     * region.
     */
    private Regions region = Regions.US_WEST_2;

    /**
     * userPoolId.
     */
    private String userPoolId = System.getenv(CommonConst.AWS_COGNITO_POOLID.toString());
    /**
     * clientId.
     */
    private String clientId = System.getenv(CommonConst.AWS_COGNITO_CLIENTID.toString());

    /**
     * login service.
     * 
     * @param event requestParameter
     * @return
     */
    public CognitoResponseModel loginService(
        APIGatewayProxyRequestEvent event) throws TooManyRequestsException {

        CognitoResponseModel model = new CognitoResponseModel();
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        // eventから情報取得
        String bodyParamJson = event.getBody();
        JSONObject jsonObj = new JSONObject(bodyParamJson);

        String userId = jsonObj.getString("userId");
        String password = jsonObj.getString("password");
        try {
            this.getCognitoIdenityProvider();
            HashMap<String, String> authParams = new HashMap<String, String>();
            authParams.put(CommonConst.USER_NAME.toString(), userId);
            authParams.put(CommonConst.PASSWORD.toString(), password);

            AdminInitiateAuthRequest adminInitiateAuthRequest = new AdminInitiateAuthRequest();
            adminInitiateAuthRequest.withAuthFlow(
                AuthFlowType.ADMIN_NO_SRP_AUTH).withClientId(clientId)
                    .withUserPoolId(userPoolId).withAuthParameters(authParams);

            // 認証実行
            AdminInitiateAuthResult result = 
                cognitoIdentityProvider.adminInitiateAuth(
                adminInitiateAuthRequest);

            // 結果を格納
            response.setStatusCode(CommonConst.HTTP_STATUS_CODE_200);
            model.setResponse(response);
            model.setLoginResult(result);

            return model;
        } catch (TooManyRequestsException e) {
            throw new TooManyRequestsException("TooManyRequestsException");
        } catch (UserNotFoundException unfEx) {
            model.setErrorMessage(unfEx.toString());
            response.setStatusCode(CommonConst.HTTP_STATUS_CODE_406);
            model.setResponse(response);
            return model;
        } catch (NotAuthorizedException naEx) {
            model.setErrorMessage(naEx.toString());
            response.setStatusCode(CommonConst.HTTP_STATUS_CODE_491);
            model.setResponse(response);
            return model;
        } catch (Exception ex) {
            model.setErrorMessage(ex.toString());
            response.setStatusCode(CommonConst.HTTP_STATUS_CODE_500);
            model.setResponse(response);
            return model;
        }
    }

    /**
     * change password.
     * 
     * @param event requestParameter
     * @return
     */
    public CognitoResponseModel defaultPwdChange(APIGatewayProxyRequestEvent event) {

        CognitoResponseModel model = new CognitoResponseModel();
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        // eventから情報取得
        String bodyParamJson = event.getBody();
        JSONObject jsonObj = new JSONObject(bodyParamJson);

        String userId = jsonObj.getString("userId");
        String session = jsonObj.getString("Session");
        String newPassword = jsonObj.getString("newPassword");
        try {
            this.getCognitoIdenityProvider();

            Map<String, String> challengeParams = new HashMap<String, String>();
            challengeParams.put(CommonConst.USER_NAME, userId);
            challengeParams.put(CommonConst.NEW_PASSWORD, newPassword);

            AdminRespondToAuthChallengeRequest 
                  authChallengeRequest = new AdminRespondToAuthChallengeRequest();
            authChallengeRequest.withUserPoolId(userPoolId).withClientId(clientId)
                    .withChallengeName(ChallengeNameType.NEW_PASSWORD_REQUIRED)
                    .withChallengeResponses(challengeParams)
                    .withSession(session);

            // 処理実行
            AdminRespondToAuthChallengeResult result = cognitoIdentityProvider
                    .adminRespondToAuthChallenge(authChallengeRequest);

            response.setStatusCode(CommonConst.HTTP_STATUS_CODE_200);
            model.setResponse(response);
            model.setChangePasswordResult(result);

            return model;
        } catch (NotAuthorizedException naEx) {
            model.setErrorMessage(naEx.toString());
            response.setStatusCode(CommonConst.HTTP_STATUS_CODE_491);
            model.setResponse(response);
            return model;
        } catch (Exception ex) {
            model.setErrorMessage(ex.toString());
            response.setStatusCode(CommonConst.HTTP_STATUS_CODE_500);
            model.setResponse(response);
            return model;
        }
    }

    /**
     * logout service.
     * 
     * @param event requestParameter
     * @return model response model
     */
    public CognitoResponseModel logoutService(APIGatewayProxyRequestEvent event) {

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        CognitoResponseModel model = new CognitoResponseModel();

        // eventから情報取得
        String bodyParamJson = event.getBody();
        JSONObject jsonObj = new JSONObject(bodyParamJson);

        String accessToken = jsonObj.getString("AccessToken");
        try {

            // Cognito Identity Provider
            this.getCognitoIdenityProvider();
            GlobalSignOutRequest request = new GlobalSignOutRequest();

            request.withAccessToken(accessToken);
            cognitoIdentityProvider.globalSignOut(request);

            response.setStatusCode(CommonConst.HTTP_STATUS_CODE_200);
            model.setResponse(response);

            return model;

        } catch (Exception ex) {
            model.setErrorMessage(ex.toString());
            response.setStatusCode(CommonConst.HTTP_STATUS_CODE_500);
            model.setResponse(response);
            return model;
        }
    }

    /**
     * This method provides the user access key and secret key to access Cognito.
     */
    private void getCognitoIdenityProvider() {

        ClientConfiguration clientConfig = new ClientConfiguration();
        clientConfig.setProtocol(Protocol.HTTPS);
        clientConfig.setProxyHost(System.getenv("HTTP_PROXY"));
        clientConfig.setProxyPort(Integer.parseInt(System.getenv("PROXY_PORT")));
        AWSCredentialsProvider credentialsProvider = new EnvironmentVariableCredentialsProvider();
        cognitoIdentityProvider = AWSCognitoIdentityProviderClientBuilder.standard()
                .withCredentials(credentialsProvider).withRegion(region).build();
    }
}
