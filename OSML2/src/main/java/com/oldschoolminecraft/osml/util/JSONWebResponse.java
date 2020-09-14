package com.oldschoolminecraft.osml.util;

import org.json.JSONObject;

public class JSONWebResponse
{
    public int status;
    public JSONObject data;
    
    public JSONWebResponse(int status, JSONObject data)
    {
        this.status = status;
        this.data = data;
    }
}
