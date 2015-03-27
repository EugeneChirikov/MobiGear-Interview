package ru.mobigear.mobigearinterview.ui;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ListView;

import ru.mobigear.mobigearinterview.data.DataContract;
import ru.mobigear.mobigearinterview.utils.Constants;


public class FragmentEventsList extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private OnFragmentsIntaractionListener mListener;
    private EventsAdapter eventsAdapter;

    private static final int LOADER_ID = 0;
    public static final String[] listItemProjection = {
            DataContract.Event._ID,
            DataContract.Event.EVENT_ID,
            DataContract.Event.TITLE,
            DataContract.Event.IMAGE_URL,
            DataContract.Event.DATE
    };
    public static final int COLUMN_EVENT_ID = 1;
    public static final int COLUMN_TITLE = 2;
    public static final int COLUMN_IMAGE_URL = 3;
    public static final int COLUMN_DATE = 4;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventsAdapter = new EventsAdapter(getActivity(), null, 0);
        setListAdapter(eventsAdapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentsIntaractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        if (null != mListener) {
            CursorAdapter adapter = (CursorAdapter) l.getAdapter();
            if (adapter == null)
                return;
            Cursor cursor = adapter.getCursor();
            if (cursor == null || !cursor.moveToPosition(position))
                return;
            long eventId = cursor.getLong(COLUMN_EVENT_ID);
            Bundle extras = new Bundle();
            extras.putLong(Constants.EVENT_ID_KEY, eventId);
            mListener.eventChosen(eventId);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = DataContract.getContentUri(DataContract.PATH_EVENT);
        return new CursorLoader(
                getActivity(),
                uri,
                listItemProjection,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        eventsAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        eventsAdapter.swapCursor(null);
    }
}
