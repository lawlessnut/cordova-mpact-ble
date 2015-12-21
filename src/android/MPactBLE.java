package com.mpact.ble;

/**
 * Created by pchm87 on 10/27/2015.
 */


import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.RemoteException;
import android.util.Log;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.zebra.mpact.MainActivity;
import com.zebra.mpact.mpactclient.MPactBeaconType;
import com.zebra.mpact.mpactclient.MPactCategoryValue;
import com.zebra.mpact.mpactclient.MPactClient;
import com.zebra.mpact.mpactclient.MPactClientConsumer;
import com.zebra.mpact.mpactclient.MPactClientNotifier;
import com.zebra.mpact.mpactclient.MPactProximity;
import com.zebra.mpact.mpactclient.MPactServerInfo;
import com.zebra.mpact.mpactclient.MPactService;
import com.zebra.mpact.mpactclient.MPactTag;


import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.zebra.mpact.mpactclient.MPactClient.*;

public class MPactBLE extends CordovaPlugin implements MPactClientConsumer, MPactClientNotifier {

    public static final String TAG = "MPACT_CLIENT";
    private CallbackContext mScanCallbackContext = null;
    private Context mContext;
    private JSONObject bleJSONObj ;
    MPactClient mPactClient;
    MPactClientConsumer consumer;
    ServiceConnection mPactServerConnection;
    private static CookieManager cookieManager;


    @Override
    public void initialize(CordovaInterface cordovaInterface, CordovaWebView webView){
        Log.v(TAG, "INITIALIZE CORDOVA");
        super.initialize(cordovaInterface, webView);
//        MPactBLEConsumer mPactBLEConsumer = new MPactBLEConsumer(cordovaInterface);
        mContext = webView.getContext();

       /* mPactClient = MPactClient.getInstanceForApplication(cordovaInterface.getActivity().getApplicationContext());

        mPactClient.bind(mPactBLEConsumer);*/
        mPactClient = MPactClient.getInstanceForApplication(cordovaInterface.getActivity().getApplicationContext());
        mPactClient = new MPactClient(mContext);
        mPactClient.putConsumersHas(this);

        Intent intent = new Intent(cordovaInterface.getActivity().getApplicationContext(), MPactService.class);

        this.cordova.getActivity().bindService(intent, mPactClient.MPactServiceConnection, Context.BIND_AUTO_CREATE);
        cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);
    }
    @Override
    public void onStart() {
        Log.v(TAG, "START ACTIVITY VISIBLE TO THE USER ");
    }

    @Override
    public boolean execute(String action,  CordovaArgs args, CallbackContext callbackContext) throws JSONException {
        Log.v(TAG, "Execute PLUGIN ACTION " + action);
        if("deviceready".equals(action)){
            Log.v(TAG, action + " Executed");
            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.v(TAG, "Cordova un on UI thread is executed");
                }
            });
            return true;
        }else if (action.equals("greet")) {

            String name = args.getString(0);
            String message = "Hello, " + name;
            callbackContext.success(message);

            return true;

        }else if("connectServer".equals(action)){
            Log.v(TAG, action + " ACTION");
            final JSONObject jsonObject = args.getJSONObject(0);
            determineState(jsonObject, callbackContext);
            callbackContext.success();
            return true;
        }else if("stopScan".equals(action)){
            Log.v(TAG, "STOP BLE SCAN ACTION");
            return true;
        }else if("startScan".equals(action)){

            PluginResult pluginResult = new PluginResult(PluginResult.Status.OK,bleJSONObj);
            pluginResult.setKeepCallback(true);
//            this.mScanCallbackContext.sendPluginResult(pluginResult);
            callbackContext.sendPluginResult(pluginResult);
            return true;
        }else if("services".equals(action)){
            Log.v(TAG, "SERVICE CALLED");

            return true;
        } else {

            return false;

        }
    }

    private void determineState(final JSONObject jsonObject, final CallbackContext callbackContext) throws JSONException {


        String serverIp = jsonObject.optString("ip");
        String userName = jsonObject.optString("username");
        String password = jsonObject.optString("password");
        String port = jsonObject.optString("port");



        Log.v(TAG, "determine function getting execute");

        MPactServerInfo mPactServerInfo = new MPactServerInfo();

        mPactClient.setClientName("vipul");

        mPactServerInfo.setHost(serverIp);
        mPactServerInfo.setPort(Integer.parseInt(port));
        mPactServerInfo.setLoginID(userName);
        mPactServerInfo.setPassword(password);
        mPactServerInfo.setUseHTTPS(false);
        mPactServerInfo.setAuthenticate(false);

        mPactClient.setServer(mPactServerInfo);
        mPactClient.setiBeaconUUID("FE913213-B311-4A42-8C16-47FAEAC938DB");

        try{
            mPactClient.Start();
        }catch (RemoteException e){
            e.printStackTrace();
        }
        this.mScanCallbackContext = callbackContext;
    }


    @Override
    public void didDetermineClosestTag(MPactTag mpactTag) {
        Log.v(TAG,"didDetermineClosestTag IS GETTING CALLED");
        Log.v(TAG,mpactTag.getTagID());
        Log.v(TAG,String.valueOf(mpactTag.getRssi()));
        Log.v(TAG,String.valueOf(mpactTag.getBatteryLife()));

        try{
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("rssi",mpactTag.getRssi());
            jsonObject.put("tag", mpactTag.getTagID());
            jsonObject.put("tsBL", mpactTag.getBatteryLife());

            bleJSONObj = jsonObject;

            keepCallBack(mScanCallbackContext,jsonObject);
        }catch(JSONException e){
            mScanCallbackContext.error(e.toString());
        }


    }

    private void keepCallBack(CallbackContext mScanCallbackContext, JSONObject jsonObject) {
        PluginResult pluginResult = new PluginResult(PluginResult.Status.OK,jsonObject);
        pluginResult.setKeepCallback(true);
        mScanCallbackContext.sendPluginResult(pluginResult);

    }

    @Override
    public void didDetermineState(int state) {
        Log.v(TAG,"DID DETERMINE METHOD IS GETTING CALLED");
        String displayString = "Unknown Region";
        switch (state) {
            case INSIDE:
                displayString = "Inside Region";
                break;
            case OUTSIDE:
                displayString = "Outside Region";
                break;
            default:
                break;
        }
        if(mScanCallbackContext != null){
            Log.d(TAG,"mScanCallBackContext is getting called");
            try{
                Log.d(TAG,"mScanCallBackContext is getting called "+displayString);
                JSONObject parameter = new JSONObject();
                parameter.put("region",displayString);

            }catch (JSONException e){

                Log.e(TAG,e.toString());
            }
        }
    }


    public void didDetermineState(int state, Integer major, Integer minor) {
        Log.v(TAG, "didDetermineState method getting called");
    }


    public void didDetermineState(int state, MPactCategoryValue categoryValue) {
        Log.v(TAG, "didDetermineState method getting called");
    }


    public void didChangeIBeaconUUID(String uuid) {
        Log.v(TAG, "didChangeIBeaconUUID method getting called" + uuid);
    }


    public void didChangeBeaconType(MPactBeaconType beaconType) {
        Log.v(TAG, "DIDCHANGEBEACONTYPE method getting called");

    }


    public void didChangeProximityRange(MPactProximity proximityRange) {
        Log.v(TAG, "didChangeProximityRange method getting called");

    }

    @Override
    public void onUserDeviceNotSupported() {
        Log.v(TAG, "onUserDeviceNotSupported method getting called");

    }

    @Override
    public void onBLEServiceCrashed() {
        Log.v(TAG, "onBLEServiceCrashed method getting called");
    }


    @Override
    public void onMPactClientServiceConnect() {
        Log.v(TAG,"=============================================");
        mPactClient.setNotifier(this);
    }

    @Override
    public Context getApplicationContext() {
        return null;
    }

    @Override
    public void unbindService(ServiceConnection connection) {

    }

    @Override
    public boolean bindService(Intent intent, ServiceConnection connection, int mode) {
        return false;
    }
}
