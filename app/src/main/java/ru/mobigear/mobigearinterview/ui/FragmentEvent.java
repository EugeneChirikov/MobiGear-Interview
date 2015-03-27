package ru.mobigear.mobigearinterview.ui;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONObject;

import ru.mobigear.mobigearinterview.R;
import ru.mobigear.mobigearinterview.data.DataContract;
import ru.mobigear.mobigearinterview.network.AttendEventRequest;
import ru.mobigear.mobigearinterview.network.ResponseListener;
import ru.mobigear.mobigearinterview.network.RunnableWithToken;
import ru.mobigear.mobigearinterview.network.VolleyHelper;
import ru.mobigear.mobigearinterview.utils.Constants;
import ru.mobigear.mobigearinterview.utils.Utils;

/**
 * Created by eugene on 3/25/15.
 */
public class FragmentEvent extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = FragmentEvent.class.getSimpleName();
    private Uri uri = DataContract.getContentUri(DataContract.PATH_EVENT);
    private String selection = DataContract.Event.EVENT_ID + " = ? ";
    private TextView title;
    private TextView body;
    private TextView date;
    private NetworkImageView image;
    private Button attendButton;
    private ViewSwitcher attendViewSwitcher;
    private static final int ATTEND_BUTTON = 0;
    private static final int ATTEND_LABEL = 1;

    private static final int LOADER_ID = 0;
    public static final String[] eventProjection = {
            DataContract.Event._ID,
            DataContract.Event.DATE,
            DataContract.Event.TITLE,
            DataContract.Event.IMAGE_URL,
            DataContract.Event.BODY,
            DataContract.Event.IS_USER_REGISTERED
    };
    public static final int COLUMN_DATE = 1;
    public static final int COLUMN_TITLE = 2;
    public static final int COLUMN_IMAGE_URL = 3;
    public static final int COLUMN_BODY = 4;
    public static final int COLUMN_IS_USER_REGISTERED = 5;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    public static FragmentEvent newInstance(long eventId) {
        FragmentEvent f = new FragmentEvent();
        Bundle args = new Bundle();
        args.putLong(Constants.EVENT_ID_KEY, eventId);
        f.setArguments(args);
        return f;
    }

    public long getEventId() {
        return getArguments().getLong(Constants.EVENT_ID_KEY, -1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event, container, false);
        title = (TextView) view.findViewById(R.id.event_title);
        image = (NetworkImageView) view.findViewById(R.id.event_image);
        date = (TextView) view.findViewById(R.id.event_date);
        body = (TextView) view.findViewById(R.id.event_body);
        body.setMovementMethod(LinkMovementMethod.getInstance());
        attendButton = (Button) view.findViewById(R.id.attend_button);
        attendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestAttend();
            }
        });
        attendViewSwitcher = (ViewSwitcher) view.findViewById(R.id.attend_switcher);
        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        VolleyHelper.getRequestQueue(getActivity()).cancelAll(TAG);
        Utils.dismissProgressDialog(getFragmentManager(), TAG);
    }

    private void requestAttend() {
        Utils.doIfTokenObtained(getActivity(), new RunnableWithToken() {
            @Override
            public void run(String token) {
                AttendEventRequest attendEventRequest = new AttendEventRequest(getEventId());
                attendEventRequest.setToken(token);
                VolleyHelper.makeJSONObjectRequest(getActivity(), getFragmentManager(), attendEventRequest, TAG, new ResponseListener() {
                    @Override
                    public void onError(int errorCode) {
                        handleAttendEventError(errorCode);
                    }

                    @Override
                    public void onSuccess(JSONObject response) {
                        updateEventAttendanceInDB();
                    }
                });
            }
        });
    }

    private void updateEventAttendanceInDB() {
        ContentResolver contentResolver = getActivity().getContentResolver();
        String[] selectionArgs = {String.valueOf(getEventId())};
        ContentValues values = new ContentValues();
        values.put(DataContract.Event.IS_USER_REGISTERED, 1);
        contentResolver.update(uri, values, selection, selectionArgs);
    }

    private void handleAttendEventError(int errorCode) {
        Toast.makeText(getActivity(), "Записаться не удалось", Toast.LENGTH_SHORT).show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] selectionArgs = {String.valueOf(getEventId())};
        return new CursorLoader(
                getActivity(),
                uri,
                eventProjection,
                selection,
                selectionArgs,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.moveToFirst())
        {
            title.setText(data.getString(COLUMN_TITLE));
            body.setText(Html.fromHtml(data.getString(COLUMN_BODY)));
            image.setImageUrl(data.getString(COLUMN_IMAGE_URL), VolleyHelper.getImageLoader(getActivity()));
            Utils.setDateTime(date, data.getString(COLUMN_DATE));
            boolean isUserRegisteredToAttend = data.getInt(COLUMN_IS_USER_REGISTERED) == 1;
            attendViewSwitcher.setDisplayedChild(isUserRegisteredToAttend ? ATTEND_LABEL : ATTEND_BUTTON);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        title.setText("");
        body.setText("");
        date.setText("");
        attendViewSwitcher.setDisplayedChild(ATTEND_BUTTON);
    }
}
