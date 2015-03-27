package ru.mobigear.mobigearinterview.ui;

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
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import ru.mobigear.mobigearinterview.R;
import ru.mobigear.mobigearinterview.data.DataContract;
import ru.mobigear.mobigearinterview.network.VolleyHelper;
import ru.mobigear.mobigearinterview.utils.Constants;

/**
 * Created by eugene on 3/25/15.
 */
public class FragmentArticle extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private TextView title;
    private TextView body;
    private NetworkImageView image;

    private static final int LOADER_ID = 0;
    public static final String[] articleProjection = {
            DataContract.Article._ID,
            DataContract.Article.TITLE,
            DataContract.Article.IMAGE_URL,
            DataContract.Article.BODY
    };
    public static final int COLUMN_TITLE = 1;
    public static final int COLUMN_IMAGE_URL = 2;
    public static final int COLUMN_BODY = 3;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    public static FragmentArticle newInstance(long articleId) {
        FragmentArticle f = new FragmentArticle();
        Bundle args = new Bundle();
        args.putLong(Constants.ARTICLE_ID_KEY, articleId);
        f.setArguments(args);
        return f;
    }

    public long getArticleId() {
        return getArguments().getLong(Constants.ARTICLE_ID_KEY, -1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_article, container, false);
        title = (TextView) view.findViewById(R.id.article_title);
        image = (NetworkImageView) view.findViewById(R.id.article_image);
        image.setDefaultImageResId(R.drawable.placeholder);
        image.setErrorImageResId(R.drawable.placeholder);
        body = (TextView) view.findViewById(R.id.article_body);
        body.setMovementMethod(LinkMovementMethod.getInstance());
        return view;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = DataContract.getContentUri(DataContract.PATH_ARTICLE);
        String selection = DataContract.Article.ARTICLE_ID + " = ? ";
        String[] selectionArgs = {String.valueOf(getArticleId())};
        return new CursorLoader(
                getActivity(),
                uri,
                articleProjection,
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
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        title.setText("");
        body.setText("");
    }
}