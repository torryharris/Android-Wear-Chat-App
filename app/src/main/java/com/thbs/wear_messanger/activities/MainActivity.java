package com.thbs.wear_messanger.activities;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.support.wearable.view.WearableRecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.thbs.wear_messanger.R;
import com.thbs.wear_messanger.adapters.ChatAdapter;
import com.thbs.wear_messanger.models.Chat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MainActivity extends WearableActivity implements TextToSpeech.OnInitListener, View.OnClickListener {

    private BoxInsetLayout mContainerView;
    private WearableRecyclerView mRecyclerView;
    private ImageButton micImageButton;
    private Context mContext;

    private final int REQ_CODE_SPEECH_INPUT = 100;

    private RecyclerView.LayoutManager layoutManager;
    private ChatAdapter chatAdapter;
    private List<Chat> chatArrayList;
    private TextToSpeech textToSpeech;
    private String mReplyMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setAmbientEnabled();

        mContext = this;

        mContainerView = findViewById(R.id.container);
        mRecyclerView = findViewById(R.id.recyclerView);
        micImageButton = findViewById(R.id.micButton);
        micImageButton.setOnClickListener(this);

        setUpRecyclerView();
        setRecyclerViewAdapter();

    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
    }

    @Override
    public void onExitAmbient() {
        super.onExitAmbient();
    }

    // Setting up RecyclerView and initialise arraylist
    private void setUpRecyclerView() {
        chatArrayList = new ArrayList<>();
        textToSpeech = new TextToSpeech(this, this);
        mRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

    }

    // Setting RecyclerView adapter
    private void setRecyclerViewAdapter() {

        chatAdapter = new ChatAdapter(this, chatArrayList);
        mRecyclerView.setAdapter(chatAdapter);
        mRecyclerView.scrollToPosition(chatArrayList.size() - 1);

        promptSpeechInput();

    }

    // This callback is invoked when the Speech Recognizer returns.
    // This is where you process the intent and extract the speech text from the intent.
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    sendMessage(result.get(0));
                }
                break;
            }

        }
    }

    @Override
    public void onClick(View view) {
        promptSpeechInput();
    }

    //Init method will initialises speech listener
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = textToSpeech.setLanguage(Locale.US);
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                //Language not supported
            } else {

                textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                    @Override
                    public void onDone(String utteranceId) {

                        textToSpeech.stop();
                        if (utteranceId.equals("utteranceId")) {

                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    promptSpeechInput();
                                }
                            });
                        }
                    }

                    @Override
                    public void onError(String utteranceId) {
                    }

                    @Override
                    public void onStart(String utteranceId) {
                    }
                });

                micImageButton.setEnabled(true);

                speakOut();
            }

        } else {
            //Initialization Failed!
        }
    }

    @Override
    public void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }

    // To send message to out going message list
    private void sendMessage(final String messageText) {

        if (messageText.trim().length() == 0)
            return;

        if (!messageText.isEmpty()) {
            if (isNetworkAvailable()) {
                micImageButton.setVisibility(View.VISIBLE);

                Date dateNow = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat(getString(R.string.yyyyMMdd), Locale.ENGLISH);
                SimpleDateFormat timeFormat = new SimpleDateFormat(getString(R.string.HHmm), Locale.ENGLISH);
                String date = dateFormat.format(dateNow);
                String time = timeFormat.format(dateNow);

                final Chat chat = new Chat();
                chat.setSender(true);
                chat.setMessage(messageText);
                chat.setDate(date);
                chat.setTime(time);

                if (chatArrayList.size() != 0) {
                    if (chatArrayList.get(chatArrayList.size() - 1).getDate().equalsIgnoreCase(date)) {
                        chat.setDateSame(true);
                    } else {
                        chat.setDateSame(false);
                    }
                } else {
                    chat.setDateSame(false);
                }

                chatArrayList.add(chat);
                if (chatAdapter != null) {
                    chatAdapter.notifyDataSetChanged();
                    mRecyclerView.scrollToPosition(chatArrayList.size() - 1);
                }
            }
        }

        UpdateChatMessage(messageText);

    } // End of sendMessage method

    // Method : will update the reply message on UI
    //This is the method where reply message can be shown (from db or api)
    //for this demo it will show same send message has reply message.
    private void UpdateChatMessage(String messageText) {
        if (messageText != null) {

            mReplyMessage = messageText;

            Chat chat = new Chat();
            chat.setSender(false);
            chat.setMessage(mReplyMessage);
            chatArrayList.add(chat);


            if (chatAdapter != null)
                chatAdapter.notifyDataSetChanged();

            speakOut();

            Date dateNow = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat(getString(R.string.yyyyMMdd), Locale.ENGLISH);
            SimpleDateFormat timeFormat = new SimpleDateFormat(getString(R.string.HHmm), Locale.ENGLISH);
            String date = dateFormat.format(dateNow);
            String time = timeFormat.format(dateNow);
            chat.setDate(date);
            chat.setTime(time);
            chat.setDateSame(true);

        } else {
            //No input message has been received
        }
    }

    /**
     * Showing google speech input dialog
     */
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    // To speakOut the reply message.
    private void speakOut() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textToSpeech.speak(mReplyMessage, TextToSpeech.QUEUE_FLUSH, null, getString(R.string.utteranceId));
        } else {
            HashMap<String, String> map = new HashMap<>();
            map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, getString(R.string.UniqueID));
            textToSpeech.speak(mReplyMessage, TextToSpeech.QUEUE_FLUSH, map);
        }
    }

    // To check internet connectivity.
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}

