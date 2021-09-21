package com.fic.service;

//apigateway関係
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import com.fic.common.CommonConst;
import com.fic.model.CognitoResponseModel;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.json.JSONObject;

public class DatabaseService {

    private static Connection getConnection() {
        if (CommonConst.RDS_HOSTNAME != null) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                String dbName = CommonConst.RDS_DB_NAME;
                String userName = CommonConst.RDS_USERNAME;
                String password = CommonConst.RDS_PASSWORD;
                String hostname = CommonConst.RDS_HOSTNAME;
                String port = CommonConst.RDS_PORT;
                String jdbcUrl = "jdbc:mysql://" + hostname 
                      + ":" + port + "/" + dbName + "?user=" + userName
                        + "&password=" + password;
                Connection con = DriverManager.getConnection(jdbcUrl);
                return con;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    /**
     * goodsregister service.
     *  
     * @param event requestParameter
     * @return model response model
     */
    public CognitoResponseModel goodsregisterService(APIGatewayProxyRequestEvent event) {

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        CognitoResponseModel model = new CognitoResponseModel();

        String bodyParamJson = event.getBody();
        JSONObject jsonObj = new JSONObject(bodyParamJson);

        LocalDateTime nowDateTime = LocalDateTime.now(); 
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        String dispNowDate = nowDateTime.format(formatter);

        // String accessToken = jsonObj.getString("AccessToken");
        
        String sql = "insert into t_production (prod_code, start_date, prod_name, price,"
            + "vender_code, dept_code, class_code, update_id, update_date) "
            + "values(?,?,?,?,?,?,?,?,?)";
        try (Connection con = getConnection(); 
            PreparedStatement st = con.prepareStatement(sql);) {

            // request.withAccessToken(accessToken);

            st.setString(1, jsonObj.getString("prodCode"));
            st.setString(2, jsonObj.getString("startDate"));
            st.setString(3, jsonObj.getString("prodName"));
            st.setInt(4, jsonObj.getInt("price"));
            st.setString(5, jsonObj.getString("venderCode"));
            st.setString(6, jsonObj.getString("deptCode"));
            st.setString(7, jsonObj.getString("classCode"));
            st.setString(8, jsonObj.getString("userId"));
            st.setString(9, dispNowDate);

            st.executeUpdate();

            model.setResponse(response);
            response.setStatusCode(CommonConst.HTTP_STATUS_CODE_200);
            
            return model;

        } catch (Exception e) {
            model.setErrorMessage(e.toString());
            response.setStatusCode(CommonConst.HTTP_STATUS_CODE_500);
            model.setResponse(response);
            return model;
        }
    }


    /**
     * goodsdelete service.
     *  
     * @param event requestParameter
     * @return model response model
     */
    public CognitoResponseModel goodsdeleteService(APIGatewayProxyRequestEvent event) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        CognitoResponseModel model = new CognitoResponseModel();
        String bodyParamJson = event.getBody();
        JSONObject jsonObj = new JSONObject(bodyParamJson);

        // String accessToken = jsonObj.getString("AccessToken");

        String sql = "delete from t_production where prod_code = ? and start_date = ?";
        try (Connection con = getConnection(); 
             PreparedStatement st = con.prepareStatement(sql);) {
            // request.withAccessToken(accessToken);
            
            st.setString(1, jsonObj.getString("prodCode"));
            st.setString(2, jsonObj.getString("startDate"));

            st.executeUpdate();

            model.setResponse(response);
            response.setStatusCode(CommonConst.HTTP_STATUS_CODE_200);

            return model;

        } catch (Exception e) {
            model.setErrorMessage(e.toString());
            response.setStatusCode(CommonConst.HTTP_STATUS_CODE_500);
            model.setResponse(response);
            return model;
        }
    }

}
