package com.wanjian.vase;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.wanjian.proxy.Vase;
import com.wanjian.proxy.event.Message;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    ListView mListView;
    List<Message> mDatas = new ArrayList<>();
    BaseAdapter mAdapter;
    ViewGroup detailView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListView = (ListView) findViewById(R.id.listview);
        findViewById(R.id.but).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vase.init(null);
            }
        });
        mAdapter = createAdapter();
        mListView.setAdapter(mAdapter);
        detailView = (ViewGroup) LayoutInflater.from(getApplicationContext()).inflate(R.layout.detail, (ViewGroup) getWindow().getDecorView(), false);
        detailView.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ViewGroup) detailView.getParent()).removeView(detailView);
            }
        });
        EventBus.getDefault().register(this);


    }

    private BaseAdapter createAdapter() {
        return new BaseAdapter() {
            @Override
            public int getCount() {
                return mDatas.size();
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.item, mListView, false);
                    convertView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Message message = ((Message) v.getTag());
                            ((TextView) detailView.findViewById(R.id.reqStateLine)).setText(message.reqStateLine);

                            TextView headers = ((TextView) detailView.findViewById(R.id.reqHeaders));
                            headers.setText(null);
                            for (Map.Entry<String, String> entry : message.reqHeaders.entrySet()) {
                                headers.append(entry.getKey());
                                headers.append(" : ");
                                headers.append(entry.getValue());
                                headers.append("\n\n");
                            }
                            if (message.reqBody != null)
                                ((TextView) detailView.findViewById(R.id.reqBody)).setText(new String(message.reqBody));

                            ((TextView) detailView.findViewById(R.id.respStateLine)).setText(message.respStateLine);
                            headers = ((TextView) detailView.findViewById(R.id.respHeaders));
                            headers.setText(null);
                            for (Map.Entry<String, String> entry : message.respHeaders.entrySet()) {
                                headers.append(entry.getKey());
                                headers.append(" : ");
                                headers.append(entry.getValue());
                                headers.append("\n\n");
                            }
                            ((ImageView) detailView.findViewById(R.id.img)).setImageBitmap(null);
                            String contentType = message.respHeaders.get("Content-Type");
                            if (contentType != null && contentType.toLowerCase().contains("image")) {
                                Bitmap bitmap = BitmapFactory.decodeByteArray(message.respBody, 0, message.respBody.length);
                                if (bitmap != null) {
                                    ((ImageView) detailView.findViewById(R.id.img)).setImageBitmap(bitmap);
                                    ((TextView) detailView.findViewById(R.id.respBody)).setText(bitmap.getWidth() + "*" + bitmap.getHeight());
                                }
                            } else {
                                if (message.respBody != null) {
                                    ((TextView) detailView.findViewById(R.id.respBody)).setText(new String(message.respBody));
                                }
                            }

                            ((ViewGroup) getWindow().getDecorView()).addView(detailView);
                        }
                    });
                }

                Message message = mDatas.get(position);
                ((TextView) ((ViewGroup) convertView).getChildAt(0)).setText(message.reqHeaders.host());
                convertView.setTag(message);
                return convertView;
            }
        };
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Message request) {
        System.out.println("rec " + request.reqHeaders.host());
        mDatas.add(0, request);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void finish() {

    }
}
