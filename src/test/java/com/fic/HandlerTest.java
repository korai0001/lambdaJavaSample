package com.fic;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fic.controller.AuthenticationController;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Handler.class})
public class HandlerTest {

    @Test
    //ログイン正常系
    public void handleRequestTest1() throws Exception {

        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent();
        request.setPath("/login");
        request.setBody("{\"userId\":\"00000001\",\"password\":\"testpass\"}");

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        response.setStatusCode(200);
        response.setBody("{\"responseTest\":\"OK\"}");

        AuthenticationController controller = Mockito.mock(AuthenticationController.class);
        whenNew(AuthenticationController.class).withNoArguments().thenReturn(controller);
        when(controller.login(any())).thenReturn(response);

        Context context = Mockito.mock(Context.class);
        LambdaLogger loggerMock = Mockito.mock(LambdaLogger.class);

        when(context.getLogger()).thenReturn(loggerMock);

        Handler handler = new Handler();
        response = handler.handleRequest(request, context);

        assertEquals(200, response.getStatusCode().intValue());

    }

    @Test
    //ログアウト正常系
    public void handleRequestTest2() throws Exception {

        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent();
        request.setPath("/logout");
        request.setBody("{\"userId\":\"00000001\",\"password\":\"testpass\"}");

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        response.setStatusCode(200);
        response.setBody("{\"responseTest\":\"OK\"}");

        AuthenticationController controller = Mockito.mock(AuthenticationController.class);
        whenNew(AuthenticationController.class).withNoArguments().thenReturn(controller);
        when(controller.logout(any())).thenReturn(response);

        Context context = Mockito.mock(Context.class);
        LambdaLogger loggerMock = Mockito.mock(LambdaLogger.class);

        when(context.getLogger()).thenReturn(loggerMock);

        Handler handler = new Handler();
        response = handler.handleRequest(request, context);

        assertEquals(200, response.getStatusCode().intValue());

    }

    @Test
    //パスワード変更正常系
    public void handleRequestTest3() throws Exception {

        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent();
        request.setPath("/changepwd");
        request.setBody("{\"userId\":\"00000001\",\"password\":\"testpass\"}");

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        response.setStatusCode(200);
        response.setBody("{\"responseTest\":\"OK\"}");

        AuthenticationController controller = Mockito.mock(AuthenticationController.class);
        whenNew(AuthenticationController.class).withNoArguments().thenReturn(controller);
        when(controller.changePassword(any())).thenReturn(response);

        Context context = Mockito.mock(Context.class);
        LambdaLogger loggerMock = Mockito.mock(LambdaLogger.class);

        when(context.getLogger()).thenReturn(loggerMock);

        Handler handler = new Handler();
        response = handler.handleRequest(request, context);

        assertEquals(200, response.getStatusCode().intValue());

    }

    @Test
    //ログイン正常系(response300)
    public void handleRequestTest4() throws Exception {

        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent();
        request.setPath("/login");
        request.setBody("{\"userId\":\"00000001\",\"password\":\"testpass\"}");

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        response.setStatusCode(300);
        response.setBody("{\"responseTest\":\"OK\"}");

        AuthenticationController controller = Mockito.mock(AuthenticationController.class);
        whenNew(AuthenticationController.class).withNoArguments().thenReturn(controller);
        when(controller.login(any())).thenReturn(response);

        Context context = Mockito.mock(Context.class);
        LambdaLogger loggerMock = Mockito.mock(LambdaLogger.class);

        when(context.getLogger()).thenReturn(loggerMock);

        Handler handler = new Handler();
        response = handler.handleRequest(request, context);

        assertEquals(300, response.getStatusCode().intValue());

    }

    @Test
    //ログイン異常系
    public void handleRequestTest5() throws Exception {

        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent();
        request.setPath("/login");
        request.setBody("{\"userId\":\"00000001\",\"password\":\"testpass\"}");

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        response.setStatusCode(500);
        response.setBody("{\"stacTrace\":\"NG\"}");

        AuthenticationController controller = Mockito.mock(AuthenticationController.class);
        whenNew(AuthenticationController.class).withNoArguments().thenReturn(controller);
        when(controller.login(any())).thenReturn(response);

        Context context = Mockito.mock(Context.class);
        LambdaLogger loggerMock = Mockito.mock(LambdaLogger.class);

        when(context.getLogger()).thenReturn(loggerMock);

        Handler handler = new Handler();
        response = handler.handleRequest(request, context);

        assertEquals(500, response.getStatusCode().intValue());

    }
}
