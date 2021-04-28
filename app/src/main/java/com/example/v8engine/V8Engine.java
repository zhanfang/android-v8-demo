package com.example.v8engine;

import android.webkit.ValueCallback;

import com.example.v8engine.thread.V8ThreadPolicy;

public class V8Engine implements IJSRuntime {

    private V8ThreadPolicy v8ThreadPolicy;

    /**
     * 构造函数
     */
    public V8Engine() {
        v8ThreadPolicy = new V8ThreadPolicy(this);
    }

    /**
     * 启动 v8 线程
     */
    public synchronized void startEngine() {
        if (v8ThreadPolicy != null) {
            v8ThreadPolicy.startV8Thread();
        }
    }

    /**
     * 启动引擎，执行 JS Bundle 文件
     */
    public void startEngineInternal() {
        V8.initV8();
    }

    public void requireJSFile(final String filePath) {
        runOnJSThread(new Runnable() {
            @Override
            public void run() {
                V8.require(filePath);
            }
        });
    }

    @Override
    public void runOnJSThread(Runnable runnable) {
        if(v8ThreadPolicy != null) {
            v8ThreadPolicy.runDelegateRunnable(runnable);
        }
    }

    @Override
    public void postOnJSThread(Runnable runnable) {
        if(v8ThreadPolicy != null) {
            v8ThreadPolicy.runDelegateRunnable(runnable);
        }
    }

    @Override
    public void postOnJSThread(Runnable runnable, long delayMillis) {
        if(v8ThreadPolicy != null) {
            v8ThreadPolicy.runDelegateRunnable(runnable, delayMillis);
        }
    }

    /**
     * 在 js 线程中执行 js，执行完毕调用回调
     * @param js
     * @param resultCb
     */
    public void evalJavascript(final String js, final ValueCallback<String> resultCb) {
        runOnJSThread(new Runnable() {
            @Override
            public void run() {
                evalJavascriptImpl(js, resultCb);
            }
        });
    }

    private void evalJavascriptImpl(final String js, final ValueCallback<String> resultCb) {
        String res = V8.runScript(js);
        if (resultCb != null) {
            resultCb.onReceiveValue(res);
        }
    }
}
