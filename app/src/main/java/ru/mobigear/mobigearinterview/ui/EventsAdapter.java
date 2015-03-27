package ru.mobigear.mobigearinterview.ui;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import ru.mobigear.mobigearinterview.R;
import ru.mobigear.mobigearinterview.network.VolleyHelper;
import ru.mobigear.mobigearinterview.utils.Utils;

/**
 * Created by eugene on 3/25/15.
 */
public class EventsAdapter extends CursorAdapter {
    public EventsAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    public EventsAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_event, parent, false);
        ViewHolder vh = new ViewHolder(view);
        view.setTag(vh);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder vh = (ViewHolder) view.getTag();
        String title = cursor.getString(FragmentEventsList.COLUMN_TITLE);
        String date = cursor.getString(FragmentEventsList.COLUMN_DATE);
        String imageUrl = cursor.getString(FragmentEventsList.COLUMN_IMAGE_URL);
        vh.title.setText(title);
        vh.title.setLines(2);
        ImageLoader imageLoader = VolleyHelper.getImageLoader(context);
        vh.image.setImageUrl(imageUrl, imageLoader);
        Utils.setDateTime(vh.date, date);
    }

    private class ViewHolder
    {
        TextView title;
        NetworkImageView image;
        TextView date;
        public ViewHolder(View view) {
            title = (TextView) view.findViewById(R.id.event_title);
            image = (NetworkImageView) view.findViewById(R.id.event_image);
            date = (TextView) view.findViewById(R.id.event_date);
        }
    }
}
