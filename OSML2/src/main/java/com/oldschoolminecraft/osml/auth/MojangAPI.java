package com.oldschoolminecraft.osml.auth;

import org.json.JSONObject;

import com.oldschoolminecraft.osml.util.JSONWebResponse;
import com.oldschoolminecraft.osml.util.Util;

public class MojangAPI
{
    public static JSONWebResponse authenticate(String username, String password, String clientToken)
    {
        JSONObject req = new JSONObject();
        
        JSONObject agent = new JSONObject();
        agent.put("name", "Minecraft");
        agent.put("version", 1);
        req.put("agent", agent);
        
        req.put("username", username);
        req.put("password", password);
        req.put("clientToken", clientToken);
        req.put("requestUser", true);
        
        return request("authenticate", req);
    }
    
    public static JSONWebResponse refresh(String accessToken, String clientToken)
    {
        JSONObject req = new JSONObject();
        req.put("accessToken", accessToken);
        req.put("clientToken", clientToken);
        return request("refresh", req);
    }
    
    public static JSONWebResponse validate(String accessToken, String clientToken)
    {
        JSONObject req = new JSONObject();
        req.put("accessToken", accessToken);
        req.put("clientToken", clientToken);
        return request("validate", req);
    }
    
    public static JSONWebResponse signout(String username, String password)
    {
        JSONObject req = new JSONObject();
        req.put("username", username);
        req.put("password", password);
        return request("signout", req);
    }
    
    public static JSONWebResponse invalidate(String accessToken, String clientToken)
    {
        JSONObject req = new JSONObject();
        req.put("accessToken", accessToken);
        req.put("clientToken", clientToken);
        return request("invalidate", req);
    }
    
    private static JSONWebResponse request(String endpoint, JSONObject req)
    {
        return Util.postJSON("https://authserver.mojang.com/" + endpoint, req);
    }
}
