package com.junjingit.propery;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

public class NotifyDetailActivity extends AppCompatActivity
{
    
    private WebView mWebView;
    
    private TextView mWaiting;
    
    private Toolbar mToolbar;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify_detail);
        
        initView();
    }
    
    private void initView()
    {
        mWebView = (WebView) findViewById(R.id.notify_detail_webview);
        mToolbar = (Toolbar) findViewById(R.id.notify_toolbar);
        
        mToolbar.setNavigationIcon(R.drawable.avoscloud_search_actionbar_back);
        
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                finish();
            }
        });
        
        mWebView.loadUrl("http://www.baidu.com");
        mWaiting = (TextView) findViewById(R.id.waiting);
        
        mWebView.setWebViewClient(new WebViewClient()
        {
            
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
                //  重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
                view.loadUrl(url);
                return true;
            }
            
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon)
            {
                super.onPageStarted(view, url, favicon);
                
                //                mLoadFailed.setVisibility(View.GONE);
                mWaiting.setVisibility(View.VISIBLE);
            }
            
            @Override
            public void onPageFinished(WebView view, String url)
            {
                super.onPageFinished(view, url);
                
                //                mJumpBeanText.stopJumping();
                mWaiting.setVisibility(View.GONE);
            }
        });
    }
    
}
