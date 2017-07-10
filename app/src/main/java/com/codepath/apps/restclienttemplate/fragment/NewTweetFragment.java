package com.codepath.apps.restclienttemplate.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.rest.RestApplication;
import com.codepath.apps.restclienttemplate.rest.RestClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

/**
 * Created by Thieusike on 7/2/2017.
 */

public class NewTweetFragment extends DialogFragment implements View.OnClickListener {


    @BindView(R.id.btnCloseNewTweet)
    ImageView btnCloseNewTweet;
    @BindView(R.id.lblUsername)
    TextView lblUsername;
    @BindView(R.id.lblAt)
    TextView lblAt;
    @BindView(R.id.ivNewTweetUserAvt)
    ImageView ivNewTweetUserAvt;
    @BindView(R.id.edtNewTweet)
    EditText edtNewTweetBody;
    @BindView(R.id.btnNewTweet)
    Button btnNewTweet;
    @BindView(R.id.tvCharCount)
    TextView tvCharCount;


    public NewTweetFragment() {}

    public interface NewTweetFragmentListener {
        void onFinishNewTweetFragmentDialog(String body, String time);
    }

    public void sendBackResult(String body, String time) {

        // Return input text back to activity through the implemented listener
        NewTweetFragmentListener listener = (NewTweetFragmentListener) getActivity();
        listener.onFinishNewTweetFragmentDialog(body, time);
        // Close the dialog and return back to the parent activity
        dismiss();

    }




    public static NewTweetFragment newInstance(String username, String at ,String useravt) {
        NewTweetFragment frag = new NewTweetFragment();
        Bundle args = new Bundle();
        args.putString("username", username);
        args.putString("at", at);
        args.putString("useravt", useravt);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_new_tweet, container);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        edtNewTweetBody = (EditText) view.findViewById(R.id.edtNewTweet);
        // Fetch arguments from bundle and set title
        String username = getArguments().getString("username");
        String at = getArguments().getString("at");
        String uservt = getArguments().getString("useravt");

        // Show soft keyboard automatically and request focus to field
        edtNewTweetBody.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        ButterKnife.bind(this, view);

        lblUsername.setText(username);
        lblAt.setText(at);
        loadImageToView(uservt, ivNewTweetUserAvt);

        btnNewTweet.setOnClickListener(this);
        btnCloseNewTweet.setOnClickListener(this);
        edtNewTweetBody.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
               tvCharCount.setText((140 -  s.length()) + "");;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void loadImageToView(String imageUrl, ImageView view){
        Picasso.with(getActivity())
                .load(imageUrl)
                .fit()
                .placeholder(R.drawable.loading)
                .into(view);

    }

    private void postNewTweet(String body){
        RestClient client = RestApplication.getRestClient();
        client.postTweet(body, new JsonHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] header, JSONObject jsonObject){
                try {
                    Toast.makeText(getActivity(), "Post new Status", Toast.LENGTH_SHORT).show();
                    sendBackResult(jsonObject.getString("text"),jsonObject.getString("created_at"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            public void onFailure(int statusCode, Header[] headers, Throwable t , JSONObject jsonObject){
                Log.d("Error", t.toString());
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnCloseNewTweet:
                this.dismiss();
                break;
            case R.id.btnNewTweet:
                postNewTweet(edtNewTweetBody.getText().toString());

        }
    }


}
