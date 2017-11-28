package com.example.blackwidow;

/**
 * Created by Ryan S on 11/28/2017.
 */

public interface INetworkPostCallback {
    void NetworkPostCallback(NetworkHelper.POST_TYPE x, String jsonResponse);
}
