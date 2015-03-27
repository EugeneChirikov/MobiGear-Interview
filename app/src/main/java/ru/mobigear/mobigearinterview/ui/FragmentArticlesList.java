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


public class FragmentArticlesList extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private OnFragmentsIntaractionListener mListener;
    private ArticlesAdapter articlesAdapter;

    private static final int LOADER_ID = 0;
    public static final String[] listItemProjection = {
            DataContract.Article._ID,
            DataContract.Article.ARTICLE_ID,
            DataContract.Article.TITLE,
            DataContract.Article.IMAGE_URL
    };
    public static final int COLUMN_ARTICLE_ID = 1;
    public static final int COLUMN_TITLE = 2;
    public static final int COLUMN_IMAGE_URL = 3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        articlesAdapter = new ArticlesAdapter(getActivity(), null, 0);
        setListAdapter(articlesAdapter);
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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID, null, this);
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
            long articleId = cursor.getLong(COLUMN_ARTICLE_ID);
            Bundle extras = new Bundle();
            extras.putLong(Constants.ARTICLE_ID_KEY, articleId);
            mListener.articleChosen(articleId);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = DataContract.getContentUri(DataContract.PATH_ARTICLE);
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
        articlesAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        articlesAdapter.swapCursor(null);
    }

}
