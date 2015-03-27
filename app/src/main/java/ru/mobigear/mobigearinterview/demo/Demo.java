package ru.mobigear.mobigearinterview.demo;

import android.content.Context;
import android.support.annotation.RawRes;
import android.test.InstrumentationTestCase;

import org.json.JSONObject;

import java.io.InputStream;

import ru.mobigear.mobigearinterview.R;
import ru.mobigear.mobigearinterview.network.AbstractRequest;
import ru.mobigear.mobigearinterview.network.ArticlesRequest;
import ru.mobigear.mobigearinterview.network.EventsRequest;
import ru.mobigear.mobigearinterview.network.ProfileRequest;
import ru.mobigear.mobigearinterview.network.ResponseParser;
import ru.mobigear.mobigearinterview.network.ServerResponse;

/**
 * Created by eugene on 3/27/15.
 */
public class Demo extends InstrumentationTestCase {

    public static void fillWithData(Context context) throws Throwable {
        fillFromFile(context, R.raw.articles, new ArticlesRequest());
        fillFromFile(context, R.raw.events, new EventsRequest());
        fillFromFile(context, R.raw.profile, new ProfileRequest());
    }

    private static void fillFromFile(Context context, @RawRes int fileId, AbstractRequest request) throws Throwable {
        InputStream inputStream = context.getResources().openRawResource(fileId);
        String result = TestUtils.readString(inputStream);
        JSONObject jsonResult = new JSONObject(result);
        ServerResponse serverResponse = ResponseParser.parseResponse(jsonResult);
        if (serverResponse == null)
            return;
        assertTrue(!serverResponse.isError());
        request.handleResponse(context, serverResponse.getData());
    }
}
