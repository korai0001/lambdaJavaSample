package com.fic.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.amazonaws.services.cognitoidp.model.AdminInitiateAuthResult;
import com.amazonaws.services.cognitoidp.model.AdminRespondToAuthChallengeResult;
import com.amazonaws.services.cognitoidp.model.AuthenticationResultType;
import com.amazonaws.services.cognitoidp.model.GlobalSignOutResult;

import com.amazonaws.services.cognitoidp.model.InternalErrorException;
import com.amazonaws.services.cognitoidp.model.NotAuthorizedException;
import com.amazonaws.services.cognitoidp.model.TooManyRequestsException;
import com.amazonaws.services.cognitoidp.model.UserNotFoundException;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;

import com.fic.model.CognitoResponseModel;
import java.util.HashMap;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ AuthenticationService.class,AWSCognitoIdentityProviderClientBuilder.class})
public class AuthenticationServiceTest {

    @Test
    //正常系１
    public void loginServiceTest1() throws Exception {
        
        //cognitoのmock化とDummyの投入
        AdminInitiateAuthResult adminInitiateAuthResult = new AdminInitiateAuthResult();
        adminInitiateAuthResult.setChallengeName("testChallenge");
        HashMap<String, String> testData = new HashMap<String, String>();
        testData.put("USER_ID_FOR_SRP", "testSrp");
        adminInitiateAuthResult.setChallengeParameters(testData);
        adminInitiateAuthResult.setSession("testSession");
        AuthenticationResultType testResultType = new AuthenticationResultType();
        testResultType.setAccessToken("testAccessToken");
        testResultType.setRefreshToken("testReflashToken");
        adminInitiateAuthResult.setAuthenticationResult(testResultType);

        //dummyの実行メソッドの設定
        AWSCognitoIdentityProvider dummy = Mockito.mock(AWSCognitoIdentityProvider.class);
        when(dummy.adminInitiateAuth(any())).thenReturn(adminInitiateAuthResult);

        //private methodのmock化

        AuthenticationService authenticationService = spy(new AuthenticationService());
        whenNew(AuthenticationService.class).withNoArguments().thenReturn(authenticationService);

        doNothing().when(authenticationService, "getCognitoIdenityProvider");
        Whitebox.setInternalState(authenticationService, "userPoolId", "test");
        Whitebox.setInternalState(authenticationService, "clientId", "test");
        Whitebox.setInternalState(authenticationService, "cognitoIdentityProvider", dummy);

        //responseパラメータ設定
        APIGatewayProxyRequestEvent event = new APIGatewayProxyRequestEvent();
        event.setBody("{\"userId\":\"00000001\",\"password\":\"testpass\"}");

        //テスト実行
        CognitoResponseModel response = authenticationService.loginService(event);

        assertEquals(200, response.getResponse().getStatusCode().intValue());
    }
    
    @Test
    //異常系(認証失敗491)
    public void loginServiceTest2() throws Exception {

        AWSCognitoIdentityProvider dummy =  Mockito.mock(AWSCognitoIdentityProvider.class);
        when(dummy.adminInitiateAuth(any())).thenThrow(new NotAuthorizedException("error"));

        AuthenticationService authenticationService = spy(new AuthenticationService());
        whenNew(AuthenticationService.class).withNoArguments().thenReturn(authenticationService);

        doNothing().when(authenticationService, "getCognitoIdenityProvider");
        Whitebox.setInternalState(authenticationService, "userPoolId", "test");
        Whitebox.setInternalState(authenticationService, "clientId", "test");
        Whitebox.setInternalState(authenticationService, "cognitoIdentityProvider", dummy);

        //responseパラメータ設定
        APIGatewayProxyRequestEvent event = new APIGatewayProxyRequestEvent();
        event.setBody("{\"userId\":\"00000001\",\"password\":\"testpass\"}");

        CognitoResponseModel response = authenticationService.loginService(event);

        assertEquals(491, response.getResponse().getStatusCode().intValue());

    }

    @Test
    //異常系(406)
    public void loginServiceTest3() throws Exception {

        AWSCognitoIdentityProvider dummy =  Mockito.mock(AWSCognitoIdentityProvider.class);
        when(dummy.adminInitiateAuth(any())).thenThrow(new UserNotFoundException("error"));

        AuthenticationService authenticationService = spy(new AuthenticationService());
        whenNew(AuthenticationService.class).withNoArguments().thenReturn(authenticationService);

        doNothing().when(authenticationService, "getCognitoIdenityProvider");
        Whitebox.setInternalState(authenticationService, "userPoolId", "test");
        Whitebox.setInternalState(authenticationService, "clientId", "test");
        Whitebox.setInternalState(authenticationService, "cognitoIdentityProvider", dummy);

        //responseパラメータ設定
        APIGatewayProxyRequestEvent event = new APIGatewayProxyRequestEvent();
        event.setBody("{\"userId\":\"00000001\",\"password\":\"testpass\"}");

        CognitoResponseModel response = authenticationService.loginService(event);

        assertEquals(406, response.getResponse().getStatusCode().intValue());

    }

    @Test
    //異常系(500)
    public void loginServiceTest4()  throws Exception {

        AWSCognitoIdentityProvider dummy =  Mockito.mock(AWSCognitoIdentityProvider.class);
        when(dummy.adminInitiateAuth(any())).thenThrow(new InternalErrorException("error"));

        AuthenticationService authenticationService = spy(new AuthenticationService());
        whenNew(AuthenticationService.class).withNoArguments()
        .thenReturn(authenticationService);

        doNothing().when(authenticationService, "getCognitoIdenityProvider");
        Whitebox.setInternalState(authenticationService, "userPoolId", "test");
        Whitebox.setInternalState(authenticationService, "clientId", "test");
        Whitebox.setInternalState(authenticationService, "cognitoIdentityProvider", dummy);
        //responseパラメータ設定
        APIGatewayProxyRequestEvent event = new APIGatewayProxyRequestEvent();
        event.setBody("{\"userId\":\"00000001\",\"password\":\"testpass\"}");

        CognitoResponseModel response = authenticationService.loginService(event);

        assertEquals(500, response.getResponse().getStatusCode().intValue());

    }

    @Test
    //異常系(TooManyRequest)
    public void loginServiceTest5() throws Exception {

        AWSCognitoIdentityProvider dummy =  Mockito.mock(AWSCognitoIdentityProvider.class);
        when(dummy.adminInitiateAuth(any())).thenThrow(new TooManyRequestsException("error"));

        AuthenticationService authenticationService = spy(new AuthenticationService());
        whenNew(AuthenticationService.class).withNoArguments()
        .thenReturn(authenticationService);

        doNothing().when(authenticationService, "getCognitoIdenityProvider");
        Whitebox.setInternalState(authenticationService, "userPoolId", "test");
        Whitebox.setInternalState(authenticationService, "clientId", "test");
        Whitebox.setInternalState(authenticationService, "cognitoIdentityProvider", dummy);
        boolean assertType = false;
        //responseパラメータ設定
        APIGatewayProxyRequestEvent event = new APIGatewayProxyRequestEvent();
        event.setBody("{\"userId\":\"00000001\",\"password\":\"testpass\"}");
        try {
            authenticationService.loginService(event);
        } catch (TooManyRequestsException ex) {
            assertType = true;
        }
        assertTrue(assertType);
    }

    @Test
    //正常系１
    public void logoutServiceTest1() throws Exception {
        
        //cognitoのmock化とDummyの投入
        GlobalSignOutResult globalSignOutResult = new GlobalSignOutResult();

        //dummyの実行メソッドの設定
        AWSCognitoIdentityProvider dummy = Mockito.mock(AWSCognitoIdentityProvider.class);
        when(dummy.globalSignOut(any())).thenReturn(globalSignOutResult);

        //private methodのmock化

        AuthenticationService authenticationService = spy(new AuthenticationService());
        whenNew(AuthenticationService.class).withNoArguments().thenReturn(authenticationService);

        doNothing().when(authenticationService, "getCognitoIdenityProvider");
        Whitebox.setInternalState(authenticationService, "userPoolId", "test");
        Whitebox.setInternalState(authenticationService, "clientId", "test");
        Whitebox.setInternalState(authenticationService, "cognitoIdentityProvider", dummy);

        //responseパラメータ設定
        APIGatewayProxyRequestEvent event = new APIGatewayProxyRequestEvent();
        event.setBody("{\"userId\":\"00000001\",\"AccessToken\":\"testToken\"}");

        //テスト実行
        CognitoResponseModel response = authenticationService.logoutService(event);

        assertEquals(200, response.getResponse().getStatusCode().intValue());
    }

    @Test
    //異常系(500)
    public void logoutServiceTest2()  throws Exception {

        AWSCognitoIdentityProvider dummy =  Mockito.mock(AWSCognitoIdentityProvider.class);
        when(dummy.globalSignOut(any())).thenThrow(new InternalErrorException("error"));

        AuthenticationService authenticationService = spy(new AuthenticationService());
        whenNew(AuthenticationService.class).withNoArguments()
        .thenReturn(authenticationService);

        doNothing().when(authenticationService, "getCognitoIdenityProvider");
        Whitebox.setInternalState(authenticationService, "userPoolId", "test");
        Whitebox.setInternalState(authenticationService, "clientId", "test");
        Whitebox.setInternalState(authenticationService, "cognitoIdentityProvider", dummy);
        //responseパラメータ設定
        APIGatewayProxyRequestEvent event = new APIGatewayProxyRequestEvent();
        event.setBody("{\"userId\":\"00000001\",\"AccessToken\":\"testToken\"}");

        CognitoResponseModel response = authenticationService.logoutService(event);

        assertEquals(500, response.getResponse().getStatusCode().intValue());
    }

    @Test
    //正常系１
    public void defaultPwdChangeTest1() throws Exception {
        
        //cognitoのmock化とDummyの投入
        AdminRespondToAuthChallengeResult adminRespondToAuthChallengeResult = 
            new AdminRespondToAuthChallengeResult();

        //dummyの実行メソッドの設定
        AWSCognitoIdentityProvider dummy = Mockito.mock(AWSCognitoIdentityProvider.class);
        when(dummy.adminRespondToAuthChallenge(any()))
            .thenReturn(adminRespondToAuthChallengeResult);

        //private methodのmock化

        AuthenticationService authenticationService = spy(new AuthenticationService());
        whenNew(AuthenticationService.class).withNoArguments().thenReturn(authenticationService);

        doNothing().when(authenticationService, "getCognitoIdenityProvider");
        Whitebox.setInternalState(authenticationService, "userPoolId", "test");
        Whitebox.setInternalState(authenticationService, "clientId", "test");
        Whitebox.setInternalState(authenticationService, "cognitoIdentityProvider", dummy);

        //responseパラメータ設定
        APIGatewayProxyRequestEvent event = new APIGatewayProxyRequestEvent();
        event.setBody(
            "{\"userId\":\"00000001\",\"Session\":\"testSession\",\"newPassword\":\"testPass\"}");

        //テスト実行
        CognitoResponseModel response = authenticationService.defaultPwdChange(event);

        assertEquals(200, response.getResponse().getStatusCode().intValue());
    }

    @Test
    //異常系１
    public void defaultPwdChangeTest2() throws Exception {
        
        //dummyの実行メソッドの設定
        AWSCognitoIdentityProvider dummy =  Mockito.mock(AWSCognitoIdentityProvider.class);
        when(dummy.adminRespondToAuthChallenge(any()))
            .thenThrow(new NotAuthorizedException("error"));

        //private methodのmock化

        AuthenticationService authenticationService = spy(new AuthenticationService());
        whenNew(AuthenticationService.class).withNoArguments().thenReturn(authenticationService);

        doNothing().when(authenticationService, "getCognitoIdenityProvider");
        Whitebox.setInternalState(authenticationService, "userPoolId", "test");
        Whitebox.setInternalState(authenticationService, "clientId", "test");
        Whitebox.setInternalState(authenticationService, "cognitoIdentityProvider", dummy);

        //responseパラメータ設定
        APIGatewayProxyRequestEvent event = new APIGatewayProxyRequestEvent();
        event.setBody(
            "{\"userId\":\"00000001\",\"Session\":\"testSession\",\"newPassword\":\"testPass\"}");

        //テスト実行
        CognitoResponseModel response = authenticationService.defaultPwdChange(event);

        assertEquals(491, response.getResponse().getStatusCode().intValue());
    }

    @Test
    //異常系１
    public void defaultPwdChangeTest3() throws Exception {
        
        //dummyの実行メソッドの設定
        AWSCognitoIdentityProvider dummy =  Mockito.mock(AWSCognitoIdentityProvider.class);
        when(dummy.adminRespondToAuthChallenge(any()))
            .thenThrow(new InternalErrorException("error"));

        //private methodのmock化

        AuthenticationService authenticationService = spy(new AuthenticationService());
        whenNew(AuthenticationService.class).withNoArguments().thenReturn(authenticationService);

        doNothing().when(authenticationService, "getCognitoIdenityProvider");
        Whitebox.setInternalState(authenticationService, "userPoolId", "test");
        Whitebox.setInternalState(authenticationService, "clientId", "test");
        Whitebox.setInternalState(authenticationService, "cognitoIdentityProvider", dummy);

        //responseパラメータ設定
        APIGatewayProxyRequestEvent event = new APIGatewayProxyRequestEvent();
        event.setBody(
            "{\"userId\":\"00000001\",\"Session\":\"testSession\",\"newPassword\":\"testPass\"}");

        //テスト実行
        CognitoResponseModel response = authenticationService.defaultPwdChange(event);

        assertEquals(500, response.getResponse().getStatusCode().intValue());
    }

}
