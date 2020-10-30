package com.oldschoolminecraft.osml.auth;

import org.json.JSONObject;

import com.oldschoolminecraft.osml.util.JSONWebResponse;
import com.oldschoolminecraft.osml.util.Util;

public class HydraAPI
{
    public static JSONWebResponse authenticateUsername(String username, String password)
    {
        JSONObject req = new JSONObject();
        req.put("username", username);
        req.put("password", password);
        return authenticate(req);
    }
    
    public static JSONWebResponse authenticateEmail(String email, String password)
    {
        JSONObject req = new JSONObject();
        req.put("email", email);
        req.put("password", password);
        return authenticate(req);
    }
    
    public static JSONWebResponse authenticate(JSONObject req)
    {
        return request("authenticate", req);
    }
    
    public static JSONWebResponse validate(String uuid, String token)
    {
        JSONObject req = new JSONObject();
        req.put("uuid", uuid);
        req.put("token", token);
        return request("validate", req);
    }
    
    public static JSONWebResponse invalidate(String token)
    {
        JSONObject req = new JSONObject();
        req.put("token", token);
        return request("invalidate", req);
    }
    
    public static JSONWebResponse security(JSONObject req)
    {
        return request("security", req);
    }
    
    private static JSONWebResponse request(String endpoint, JSONObject req)
    {
        return Util.postJSON("https://www.oldschoolminecraft.com/api/hydra/v1/" + endpoint, req);
    }
}
