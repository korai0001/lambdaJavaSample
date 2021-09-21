package com.fic.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.util.HashMap;

import com.amazonaws.services.cognitoidp.model.AdminInitiateAuthResult;
import com.amazonaws.services.cognitoidp.model.AdminRespondToAuthChallengeResult;
import com.amazonaws.services.cognitoidp.model.AuthenticationResultType;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.services.cognitoidp.model.TooManyRequestsException;

import com.fic.model.CognitoResponseModel;
import com.fic.service.AuthenticationService;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ AuthenticationController.class})
public class AuthenticationControllerTest {

    @Test
    //ログインコントロール正常系
    public void loginTest1() throws Exception {
        
        AdminInitiateAuthResult result = new AdminInitiateAuthResult();

        result.setChallengeName("testChallenge");
        HashMap<String, String> testData = new HashMap<String, String>();
        testData.put("USER_ID_FOR_SRP", "testSrp");
        result.setChallengeParameters(testData);
        result.setSession("testSession");
        AuthenticationResultType testResultType = new AuthenticationResultType();
        testResultType.setAccessToken("testAccessToken");
        testResultType.setRefreshToken("testReflashToken");
        result.setAuthenticationResult(testResultType);

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        response.setStatusCode(200);

        CognitoResponseModel dummyModel = new CognitoResponseModel();
        dummyModel.setLoginResult(result);
        dummyModel.setResponse(response);

        AuthenticationService service = Mockito.mock(AuthenticationService.class);
        whenNew(AuthenticationService.class).withNoArguments().thenReturn(service);
        when(service.loginService(any())).thenReturn(dummyModel);

        APIGatewayProxyRequestEvent dummy = new APIGatewayProxyRequestEvent();

        AuthenticationController controller = new AuthenticationController();

        //test実行
        APIGatewayProxyResponseEvent resultData = controller.login(dummy);

        assertEquals(200, resultData.getStatusCode().intValue());

    }

    @Test
    //ログインコントロール正常系（初回ログイン）
    public void loginTest2() throws Exception {
        
        AdminInitiateAuthResult result = new AdminInitiateAuthResult();

        result.setChallengeName("NEW_PASSWORD_REQUIRED");
        HashMap<String, String> testData = new HashMap<String, String>();
        testData.put("USER_ID_FOR_SRP", "testSrp");
        result.setChallengeParameters(testData);
        result.setSession("testSession");
        AuthenticationResultType testResultType = new AuthenticationResultType();
        testResultType.setAccessToken("testAccessToken");
        testResultType.setRefreshToken("testReflashToken");
        result.setAuthenticationResult(testResultType);

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        response.setStatusCode(300);

        CognitoResponseModel dummyModel = new CognitoResponseModel();
        dummyModel.setLoginResult(result);
        dummyModel.setResponse(response);

        AuthenticationService service = Mockito.mock(AuthenticationService.class);
        whenNew(AuthenticationService.class).withNoArguments().thenReturn(service);
        when(service.loginService(any())).thenReturn(dummyModel);

        APIGatewayProxyRequestEvent dummy = new APIGatewayProxyRequestEvent();

        AuthenticationController controller = new AuthenticationController();

        //test実行
        APIGatewayProxyResponseEvent resultData = controller.login(dummy);

        assertEquals(300, resultData.getStatusCode().intValue());

    }

    @Test
    //ログインコントロール異常系（TooManyRequest）
    public void loginTest3() throws Exception {
        
        AdminInitiateAuthResult result = new AdminInitiateAuthResult();

        result.setChallengeName("testChallenge");
        HashMap<String, String> testData = new HashMap<String, String>();
        testData.put("USER_ID_FOR_SRP", "testSrp");
        result.setChallengeParameters(testData);
        result.setSession("testSession");
        AuthenticationResultType testResultType = new AuthenticationResultType();
        testResultType.setAccessToken("testAccessToken");
        testResultType.setRefreshToken("testReflashToken");
        result.setAuthenticationResult(testResultType);

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        response.setStatusCode(200);

        CognitoResponseModel dummyModel = new CognitoResponseModel();
        dummyModel.setErrorMessage("getErrorMessage");
        dummyModel.setLoginResult(result);
        dummyModel.setResponse(response);

        AuthenticationService service = Mockito.mock(AuthenticationService.class);
        whenNew(AuthenticationService.class).withNoArguments().thenReturn(service);
        when(service.loginService(any())).thenThrow(new TooManyRequestsException("error"));

        APIGatewayProxyRequestEvent dummy = new APIGatewayProxyRequestEvent();

        AuthenticationController controller = new AuthenticationController();

        //test実行
        APIGatewayProxyResponseEvent resultData = controller.login(dummy);

        assertEquals(429, resultData.getStatusCode().intValue());

    }

    @Test
    //ログインコントロール正常系
    public void logoutTest1() throws Exception {
        
        AdminInitiateAuthResult result = new AdminInitiateAuthResult();

        result.setChallengeName("testChallenge");
        HashMap<String, String> testData = new HashMap<String, String>();
        testData.put("USER_ID_FOR_SRP", "testSrp");
        result.setChallengeParameters(testData);
        result.setSession("testSession");
        AuthenticationResultType testResultType = new AuthenticationResultType();
        testResultType.setAccessToken("testAccessToken");
        testResultType.setRefreshToken("testReflashToken");
        result.setAuthenticationResult(testResultType);
        
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        response.setStatusCode(200);

        CognitoResponseModel dummyModel = new CognitoResponseModel();
        dummyModel.setLoginResult(result);
        dummyModel.setResponse(response);

        AuthenticationService service = Mockito.mock(AuthenticationService.class);
        whenNew(AuthenticationService.class).withNoArguments().thenReturn(service);
        when(service.logoutService(any())).thenReturn(dummyModel);

        APIGatewayProxyRequestEvent dummy = new APIGatewayProxyRequestEvent();

        AuthenticationController controller = new AuthenticationController();

        //test実行
        APIGatewayProxyResponseEvent resultData = controller.logout(dummy);

        assertEquals(200, resultData.getStatusCode().intValue());

    }

    @Test
    //パスワード変更正常系
    public void changePasswordTest1() throws Exception {
        
        AdminRespondToAuthChallengeResult result = new AdminRespondToAuthChallengeResult();

        result.setChallengeName("testChallenge");
        HashMap<String, String> testData = new HashMap<String, String>();
        testData.put("USER_ID_FOR_SRP", "testSrp");
        result.setChallengeParameters(testData);
        result.setSession("testSession");
        AuthenticationResultType testResultType = new AuthenticationResultType();
        testResultType.setAccessToken("testAccessToken");
        testResultType.setRefreshToken("testReflashToken");
        result.setAuthenticationResult(testResultType);

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        response.setStatusCode(200);

        CognitoResponseModel dummyModel = new CognitoResponseModel();
        dummyModel.setChangePasswordResult(result);
        dummyModel.setResponse(response);

        AuthenticationService service = Mockito.mock(AuthenticationService.class);
        whenNew(AuthenticationService.class).withNoArguments().thenReturn(service);
        when(service.defaultPwdChange(any())).thenReturn(dummyModel);

        APIGatewayProxyRequestEvent dummy = new APIGatewayProxyRequestEvent();

        AuthenticationController controller = new AuthenticationController();

        //test実行
        APIGatewayProxyResponseEvent resultData = controller.changePassword(dummy);

        assertEquals(200, resultData.getStatusCode().intValue());

    }

    @Test
    //パスワード変更異常系（システムエラー）
    public void changePasswordTest2() throws Exception {
        
        AdminRespondToAuthChallengeResult result = new AdminRespondToAuthChallengeResult();

        result.setChallengeName("testChallenge");
        HashMap<String, String> testData = new HashMap<String, String>();
        testData.put("USER_ID_FOR_SRP", "testSrp");
        result.setChallengeParameters(testData);
        result.setSession("testSession");
        AuthenticationResultType testResultType = new AuthenticationResultType();
        testResultType.setAccessToken("testAccessToken");
        testResultType.setRefreshToken("testReflashToken");
        result.setAuthenticationResult(testResultType);

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        response.setStatusCode(500);

        CognitoResponseModel dummyModel = new CognitoResponseModel();
        dummyModel.setErrorMessage("getErrorMessage");
        dummyModel.setChangePasswordResult(result);
        dummyModel.setResponse(response);

        AuthenticationService service = Mockito.mock(AuthenticationService.class);
        whenNew(AuthenticationService.class).withNoArguments().thenReturn(service);
        when(service.defaultPwdChange(any())).thenReturn(dummyModel);

        APIGatewayProxyRequestEvent dummy = new APIGatewayProxyRequestEvent();

        AuthenticationController controller = new AuthenticationController();

        //test実行
        APIGatewayProxyResponseEvent resultData = controller.changePassword(dummy);

        assertEquals(500, resultData.getStatusCode().intValue());

    }
}
