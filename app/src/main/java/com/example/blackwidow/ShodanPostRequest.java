package com.example.blackwidow;

import android.content.Context;

/**
 * Created by Ryan on 11/28/2017.
 */

public class ShodanPostRequest {
    public ShodanPostRequest(Context context, String postData, long hostID, IShodanPostCallback postCallback) {
        _postData = postData;
        _hostID = hostID;
        _postContext = context;
        _postCallback = postCallback;
        _postUrl = "https://exploits.shodan.io/api/search";
    }

    private Context _postContext;
    public Context getPostContext() {
        return _postContext;
    }

    private IShodanPostCallback _postCallback;
    public IShodanPostCallback getPostCallback() {
        return _postCallback;
    }

    private String _postUrl = "";
    public String getPostUrl() {
        return _postUrl;
    }

    private String _postData = "";
    public String getPostData() {
        return _postData;
    }

    private long _hostID = -1;
    public long getHostID() {
        return _hostID;
    }
}
