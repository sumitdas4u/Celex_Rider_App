package com.celex.rider.AllFragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.celex.rider.R;

public class Web_View extends Fragment {
    View view;
    Context context;

    ProgressBar progress_bar;
    WebView webView;
    String url="www.google.com";
    String title;
    TextView title_txt;
    public Web_View() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_web_view, container, false);
        context=getContext();

        Bundle bundle=getArguments();
        if(bundle!=null){
            url=bundle.getString("url");
            title=bundle.getString("title");
        }


        view.findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        title_txt=view.findViewById(R.id.title_txt);
        title_txt.setText(title);

        webView=view.findViewById(R.id.webview);
        progress_bar=view.findViewById(R.id.progress_bar);
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int progress) {
                if(progress>=80){
                    progress_bar.setVisibility(View.GONE);
                }
            }
        });


        webView.getSettings().setJavaScriptEnabled(true);

        webView.loadUrl(url);


        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return false;
            }
        });

        return view;
    }
}