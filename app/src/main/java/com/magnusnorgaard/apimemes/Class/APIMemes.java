package com.magnusnorgaard.apimemes.Class;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONObject;

public class APIMemes {

    private final Context context;
    public static final String URL = "https://api-memes-spanish.herokuapp.com";
    public static final String URL2 = "http://api-memes-spanish.herokuapp.com";
    public static final String ImagesListConstant = "/files/jpg/";
    public static final String VideosListConstant = "/files/mp4/";
    public static final String ImagesRandomConstant = "/files/random/jpg";
    public static final String VideosRandomConstant = "/files/random/mp4";

    // Constructor
    public APIMemes(Context context){
        this.context = context;
    }

    // Check if server is active
    private int intentsToReconnection = 3;
    public interface addOnLGetConnection{
        void OnActive(String response);
        void OnNotActive(String error);
    }
    private addOnLGetConnection addOnLGetConnection;
    public void addOnLGetConnection(addOnLGetConnection listener){
        this.addOnLGetConnection = listener;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (addOnLGetConnection!=null){
                    addOnLGetConnection.OnActive(response);
                    intentsToReconnection = 3;
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError e) {
                if (intentsToReconnection >= 3){
                    addOnLGetConnection(addOnLGetConnection);
                    intentsToReconnection--;
                } else {
                    if (addOnLGetConnection!=null){
                        addOnLGetConnection.OnNotActive(e.toString());
                        intentsToReconnection = 3;
                    }
                }
            }
        });
        Volley.newRequestQueue(context).add(stringRequest);
    }

    // Get images list
    public interface addOnListJpgListener{
        void OnSuccess(JSONObject response, JSONArray listFromImages);
        void OnFailure(String reason);
    }
    private addOnListJpgListener addOnListJpgListener;
    public void addOnListJpgListener(addOnListJpgListener listener){
        this.addOnListJpgListener = listener;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL+ImagesListConstant, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (addOnListJpgListener!=null){
                    try {
                        addOnListJpgListener.OnSuccess(new JSONObject(response), new JSONObject(response).getJSONArray("response"));
                    } catch (Exception e) {
                        addOnListJpgListener.OnFailure(e.toString());
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError e) {
                if (addOnListJpgListener!=null){
                    addOnListJpgListener.OnFailure(e.toString());
                }
            }
        });
        Volley.newRequestQueue(context).add(stringRequest);
    }

    // Get videos list
    public interface addOnListMp4Listener{
        void OnSuccess(JSONObject response, JSONArray listFromVideos);
        void OnFailure(String reason);
    }
    private addOnListMp4Listener addOnListMp4Listener;
    public void addOnListMp4Listener(addOnListMp4Listener listener){
        this.addOnListMp4Listener = listener;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL+VideosListConstant, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (addOnListMp4Listener!=null){
                    try {
                        addOnListMp4Listener.OnSuccess(new JSONObject(response), new JSONObject(response).getJSONArray("response"));
                    } catch (Exception e) {
                        addOnListMp4Listener.OnFailure(e.toString());
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError e) {
                if (addOnListMp4Listener!=null){
                    addOnListMp4Listener.OnFailure(e.toString());
                }
            }
        });
        Volley.newRequestQueue(context).add(stringRequest);
    }

    // Get random image
    public interface addOnRandomJpgListener{
        void OnSuccess(JSONObject response, String url);
        void OnFailure(String reason);
    }
    private addOnRandomJpgListener addOnRandomJpgListener;
    public void addOnRandomJpgListener(addOnRandomJpgListener listener){
        this.addOnRandomJpgListener = listener;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL+ImagesRandomConstant, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (addOnRandomJpgListener!=null){
                    try {
                        addOnRandomJpgListener.OnSuccess(new JSONObject(response), new JSONObject(response).getString("urlresource"));
                    } catch (Exception e) {
                        addOnRandomJpgListener.OnFailure(e.toString());
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError e) {
                   if (addOnRandomJpgListener!=null){
                    addOnRandomJpgListener.OnFailure(e.toString());
                }
            }
        });
        Volley.newRequestQueue(context).add(stringRequest);
    }

    // Get random video
    public interface addOnRandomMp4Listener{
        void OnSuccess(JSONObject response, String url);
        void OnFailure(String reason);
    }
    private addOnRandomMp4Listener addOnRandomMp4Listener;
    public void addOnRandomMp4Listener(addOnRandomMp4Listener listener){
        this.addOnRandomMp4Listener = listener;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL+VideosRandomConstant, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (addOnRandomMp4Listener!=null){
                    try {
                        addOnRandomMp4Listener.OnSuccess(new JSONObject(response), new JSONObject(response).getString("urlresource"));
                    } catch (Exception e) {
                        addOnRandomMp4Listener.OnFailure(e.toString());
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError e) {
                if (addOnRandomMp4Listener!=null){
                    addOnRandomMp4Listener.OnFailure(e.toString());
                }
            }
        });
        Volley.newRequestQueue(context).add(stringRequest);
    }
}