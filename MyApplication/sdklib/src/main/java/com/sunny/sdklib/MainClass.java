package com.sunny.sdklib;

import android.app.Fragment;
import android.os.Bundle;
import com.unity3d.player.UnityPlayer;

public class MainClass extends Fragment {

    private static final String TAG = "Main";
    private static MainClass Instance = null;
    private String gameObjectName;

    public static MainClass GetInstance(String gameObjectName){
        if (Instance == null){
            Instance = new MainClass();
            Instance.gameObjectName = gameObjectName;
            UnityPlayer.currentActivity.getFragmentManager().beginTransaction().add(Instance, TAG).commit();
        }
        return Instance;
    }
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);  // This is important to avoid loss of instance when rotating screen.
    }
    // Example1:CallBack function(call unity methods in Android)
    public void HelloWorld()
    {
        UnityPlayer.UnitySendMessage(gameObjectName,"PluginCallBack","Hello World!");
    }
    // Example2:Android method called in Unity
    public int AndroidAdd(int a, int b)
    {
        return a + b;
    }
}
