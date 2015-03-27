package ru.mobigear.mobigearinterview.ui;

import android.app.Activity;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;

import org.json.JSONObject;

import ru.mobigear.mobigearinterview.R;
import ru.mobigear.mobigearinterview.data.DataContract;
import ru.mobigear.mobigearinterview.network.EditProfileRequest;
import ru.mobigear.mobigearinterview.network.ResponseListener;
import ru.mobigear.mobigearinterview.network.RunnableWithToken;
import ru.mobigear.mobigearinterview.network.VolleyHelper;
import ru.mobigear.mobigearinterview.utils.Utils;

/**
 * Created by eugene on 3/25/15.
 */
public class FragmentProfile extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = FragmentProfile.class.getSimpleName();
    private EditText fioEdit;
    private EditText emailEdit;
    private EditText phoneEdit;
    private Button actionButton;
    private Button changeAvatar;
    private ImageView avatarImage;
    private ProfileObserver profileObserver;
    private boolean isDataEditable = false;
    private String avatarBase64 = null;
    private String avatarUrl;

    private static final int PICK_IMAGE_REQUEST = 100;

    private static final int LOADER_ID = 0;
    public static final String[] profileProjection = {
            DataContract.Profile._ID,
            DataContract.Profile.FIO,
            DataContract.Profile.PHONE,
            DataContract.Profile.EMAIL,
            DataContract.Profile.AVATAR_URL
    };
    public static final int COLUMN_FIO = 1;
    public static final int COLUMN_PHONE = 2;
    public static final int COLUMN_EMAIL = 3;
    public static final int COLUMN_AVATAR_URL = 4;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        fioEdit = (EditText) rootView.findViewById(R.id.fio);
        emailEdit = (EditText) rootView.findViewById(R.id.email);
        phoneEdit = (EditText) rootView.findViewById(R.id.phone);
        avatarImage = (ImageView) rootView.findViewById(R.id.avatar);
        actionButton = (Button) rootView.findViewById(R.id.button_action);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isDataEditable) {
                    String fio = fioEdit.getText().toString();
                    String email = emailEdit.getText().toString();
                    String phone = phoneEdit.getText().toString();
                    if (Utils.isInputValid(new String[]{email, fio, phone}) && avatarBase64 != null) {
                        EditProfileRequest editProfileRequest = new EditProfileRequest();
                        editProfileRequest.setFio(fio);
                        editProfileRequest.setEmail(email);
                        editProfileRequest.setPhone(phone);
                        editProfileRequest.setAvatar(avatarBase64);
                        requestProfile(editProfileRequest);
                    }
                    else
                        Toast.makeText(getActivity(), getString(R.string.input_invalid_message), Toast.LENGTH_SHORT).show();
                }
                setEditMode(!isDataEditable);
            }
        });
        changeAvatar = (Button) rootView.findViewById(R.id.button_change_avatar);
        changeAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = getParentFragment();
                if (fragment == null)
                    fragment = FragmentProfile.this;
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                fragment.startActivityForResult(Intent.createChooser(intent, "Выберите аватар"), PICK_IMAGE_REQUEST);
            }
        });
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        profileObserver = new ProfileObserver(new Handler());
    }

    private class ProfileObserver extends ContentObserver {
        public ProfileObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            getLoaderManager().restartLoader(LOADER_ID, null, FragmentProfile.this);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == PICK_IMAGE_REQUEST) {
            Uri selectedImageUri = data.getData();
            String imagePath = imagePathFrom(selectedImageUri);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);
            avatarBase64 = Utils.bitmapToBase64String(bitmap);
            avatarImage.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        VolleyHelper.getRequestQueue(getActivity()).cancelAll(TAG);
        Utils.dismissProgressDialog(getFragmentManager(), TAG);
    }

    private String imagePathFrom(Uri uri) {
        Cursor cursor = getActivity().getContentResolver().query(uri,
                new String[]{android.provider.MediaStore.Images.ImageColumns.DATA}, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                return cursor.getString(0);
            }
            cursor.close();
        }
        return null;
    }

    private void requestProfile(final EditProfileRequest editProfileRequest) {
        Utils.doIfTokenObtained(getActivity(), new RunnableWithToken() {
            @Override
            public void run(String token) {
                editProfileRequest.setToken(token);
                VolleyHelper.makeJSONObjectRequest(getActivity(), getFragmentManager(), editProfileRequest, TAG, new ResponseListener() {
                    @Override
                    public void onError(int errorCode) {
                        handleError(errorCode);
                    }

                    @Override
                    public void onSuccess(JSONObject response) {
                        editProfileRequest.handleResponse(getActivity(), response);
                        VolleyHelper.getRequestQueue(getActivity()).getCache().invalidate(avatarUrl, true);
                    }
                });
            }
        });
    }

    private void handleError(int errorCode) {
        Toast.makeText(getActivity(), "Сохранить не удалось", Toast.LENGTH_SHORT).show(); // TODO show more precise error
    }

    @Override
    public void onResume() {
        super.onResume();
        setEditMode(isDataEditable);
        Uri uri = DataContract.getContentUri(DataContract.PATH_PROFILE);
        getActivity().getContentResolver().registerContentObserver(uri, true, profileObserver);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().getContentResolver().unregisterContentObserver(profileObserver);
    }

    private void setEditMode(boolean isEditable) {
        changeAvatar.setVisibility(isEditable ? View.VISIBLE : View.INVISIBLE);
        fioEdit.setFocusable(isEditable);
        fioEdit.setFocusableInTouchMode(isEditable);
        emailEdit.setFocusable(isEditable);
        emailEdit.setFocusableInTouchMode(isEditable);
        phoneEdit.setFocusable(isEditable);
        phoneEdit.setFocusableInTouchMode(isEditable);
        actionButton.setText(isEditable ? "Сохранить" : "Редактировать");
        isDataEditable = isEditable;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = DataContract.getContentUri(DataContract.PATH_PROFILE);
        return new CursorLoader(
                getActivity(),
                uri,
                profileProjection,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (!data.moveToFirst()) {
            updateViews("", "", "", "");
            return;
        }
        avatarUrl = data.getString(COLUMN_AVATAR_URL);
        updateViews(
                data.getString(COLUMN_FIO),
                data.getString(COLUMN_EMAIL),
                data.getString(COLUMN_PHONE),
                avatarUrl);
    }

    private void updateViews(String fio, String email, String phone, final String avatarUrl) {
        fioEdit.setText(fio);
        emailEdit.setText(email);
        phoneEdit.setText(phone);
        VolleyHelper.getImageLoader(getActivity()).get(avatarUrl, ImageLoader.getImageListener(avatarImage,
                R.drawable.placeholder, R.drawable.placeholder));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        updateViews("", "", "", "");
    }
}
