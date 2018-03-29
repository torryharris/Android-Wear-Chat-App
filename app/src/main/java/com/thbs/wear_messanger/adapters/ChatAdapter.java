package com.thbs.wear_messanger.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thbs.wear_messanger.R;
import com.thbs.wear_messanger.models.Chat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by divya_ravikumar on 12/13/2017.
 */

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static String TAG = ChatAdapter.class.getSimpleName();

    private Context context;
    private List<Chat> chatArrayList;
    private int SELF = 100;

    public ChatAdapter(Context context, List<Chat> chatArrayList) {
        this.context = context;
        this.chatArrayList = chatArrayList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;

        // view type is to identify where to render the chat chat
        // left or right
        if (viewType == SELF) {
            // self chat
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_chat_item_sender, parent, false);
        } else {
            // others chat
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_chat_item_receiver, parent, false);
        }

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Chat chat = chatArrayList.get(position);

        String chatMessage = chat.getMessage() + "   ";

        if (chat.isSender()) {
            String fullText = chatMessage + "  " + chat.getTime();
            SpannableString ss1 = new SpannableString(fullText);
            ss1.setSpan(new RelativeSizeSpan(0.8f), chatMessage.length(), fullText.length(), 0); // set size
            ss1.setSpan(new ForegroundColorSpan(context.getResources().getColor(android.R.color.darker_gray)), chatMessage.length(), fullText.length(), 0);// set color
            ((ViewHolder) holder).message.setText(ss1);

            if (chat.isDateSame()) {
                ((ViewHolder) holder).date.setVisibility(View.GONE);
            } else {

                if (getToday().equalsIgnoreCase(chat.getDate())) {
                    ((ViewHolder) holder).date.setText(context.getString(R.string.today));
                } else if (getYesterday().equalsIgnoreCase(chat.getDate())) {
                    ((ViewHolder) holder).date.setText(context.getString(R.string.yesterday));
                } else {
                    ((ViewHolder) holder).date.setText(getDate(chat.getDate()));
                }

                ((ViewHolder) holder).date.setVisibility(View.VISIBLE);
            }
        } else {
            ((ViewHolder) holder).date.setVisibility(View.GONE);
            String fullText = chatMessage + "  " + chat.getTime();
            SpannableString ss1 = new SpannableString(fullText);
            ss1.setSpan(new RelativeSizeSpan(0.8f), chatMessage.length(), fullText.length(), 0); // set size
            ss1.setSpan(new ForegroundColorSpan(Color.WHITE), chatMessage.length(), fullText.length(), 0);// set color
            ((ViewHolder) holder).message.setText(ss1);
        }


    }

    @Override
    public int getItemCount() {
        return chatArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        Chat chat = chatArrayList.get(position);
        if (chat.isSender()) {
            return SELF;
        }

        return position;
    }

    // To get new date.
    private String getDate(String date) {

        String dateStr = " ";
        SimpleDateFormat format = new SimpleDateFormat(context.getString(R.string.yyyyMMdd), Locale.ENGLISH);
        try {
            Date date1 = format.parse(date);
            SimpleDateFormat dateFormat = new SimpleDateFormat(context.getString(R.string.ddMMMyyyy), Locale.ENGLISH);
            dateStr = dateFormat.format(date1);

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return dateStr;
    }

    // To get previous day of the current date
    private String getYesterday() {

        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        SimpleDateFormat dateFormat = new SimpleDateFormat(context.getString(R.string.yyyyMMdd), Locale.ENGLISH);
        return dateFormat.format(cal.getTime());
    }

    // To get current day
    private String getToday() {

        Date dateNow = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat(context.getString(R.string.yyyyMMdd), Locale.ENGLISH);
        return dateFormat.format(dateNow);
    }

    // Customised view-holder class
    private class ViewHolder extends RecyclerView.ViewHolder {
        TextView message;
        TextView date;

        private ViewHolder(View view) {
            super(view);
            message = itemView.findViewById(R.id.chat);
            date = itemView.findViewById(R.id.tv_date);
        }
    }
}

