package com.example.pocketcasts.util;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

public class EncodedStringRequest extends StringRequest {


    public EncodedStringRequest(int method, String url,
                                Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
    }


    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        String parsed = null;
        parsed = new String(response.data,
                StandardCharsets.UTF_8);
        return Response.success(parsed,
                HttpHeaderParser.parseCacheHeaders(response));
    }
}
