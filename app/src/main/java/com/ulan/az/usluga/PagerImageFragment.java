package com.ulan.az.usluga;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;


/**
 * A simple {@link Fragment} subclass.
 */
public class PagerImageFragment extends Fragment {


    static final String ARGUMENT_PAGE_NUMBER = "arg_page_number";

    int pageNumber;
    int backColor;
    String url;

    public static PagerImageFragment newInstance(int page, String url) {
        PagerImageFragment pageFragment = new PagerImageFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(ARGUMENT_PAGE_NUMBER, page);
        arguments.putString("url", url);
        pageFragment.setArguments(arguments);
        return pageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageNumber = getArguments().getInt(ARGUMENT_PAGE_NUMBER);
        url = getArguments().getString("url");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pager_image, null);

        ImageView imageView = view.findViewById(R.id.image);
        Log.e("IMAGE",url);

        Glide.with(this).load("http://145.239.33.4:5555"+url).placeholder(R.drawable.placeholder_image).into(imageView);

        return view;
    }

}
