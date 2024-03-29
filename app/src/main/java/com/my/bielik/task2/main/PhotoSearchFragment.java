package com.my.bielik.task2.main;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.jakewharton.rxbinding3.widget.RxTextView;
import com.jakewharton.rxbinding3.widget.TextViewTextChangeEvent;
import com.my.bielik.task2.R;
import com.my.bielik.task2.database.entity.Photo;
import com.my.bielik.task2.photoview.PhotoViewFragment;
import com.my.bielik.task2.thread.AddressToTitleConvertRunnable;
import com.my.bielik.task2.thread.PhotoSearchRunnable;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static android.content.Context.MODE_PRIVATE;
import static com.my.bielik.task2.app.MyApplication.APP_PREFERENCES;
import static com.my.bielik.task2.main.MainActivity.LAST_SEARCH_VALUE;
import static com.my.bielik.task2.thread.PhotoSearchRunnable.SEARCH_PHOTOS_WITH_GEO_COORDINATES;
import static com.my.bielik.task2.thread.PhotoSearchRunnable.SEARCH_PHOTOS_WITH_TEXT;
import static com.my.bielik.task2.user.LoginActivity.LATITUDE_EXTRA;
import static com.my.bielik.task2.user.LoginActivity.LONGITUDE_EXTRA;

public class PhotoSearchFragment extends Fragment {

    private TextInputEditText inputRequest;
    private RecyclerView rvPhotos;
    private Button btnSearch;

    private PhotoAdapter adapter;
    private LinearLayoutManager layoutManager;
    private SharedPreferences preferences;

    private PhotoSearchRunnable runnable;

    private boolean isLoading;
    private String geoPhotoTitle;

    private OnPhotoSelectedListener photoSelectedListener;

    private boolean twoPane;

    private Disposable requestTextDisposable;

    public PhotoSearchFragment() {
    }

    static PhotoSearchFragment newInstance() {
        return new PhotoSearchFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo_search, container, false);
        inputRequest = view.findViewById(R.id.et_request);
        rvPhotos = view.findViewById(R.id.rv_photos);
        btnSearch = view.findViewById(R.id.button_search);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runnable.setText(inputRequest.getText().toString().trim());
                getPhotos(SEARCH_PHOTOS_WITH_TEXT);
            }
        });

        preferences = getActivity().getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        if (preferences.contains(LAST_SEARCH_VALUE)) {
            inputRequest.setText(preferences.getString(LAST_SEARCH_VALUE, ""));
        }

        setRecyclerView();
        setPhotoSearchRunnable();

        if (getArguments() != null) {
            getPhotosWithGeo(getArguments().getDouble(LATITUDE_EXTRA), getArguments().getDouble(LONGITUDE_EXTRA));
        } else {
            setRequestTextObservable();
        }

        if (view.findViewById(R.id.fl_photo_view) != null) {
            twoPane = true;
        }

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnPhotoSelectedListener) {
            photoSelectedListener = (OnPhotoSelectedListener) context;
        } else {
            throw new ClassCastException(context.toString());
        }
    }

    private void setRecyclerView() {
        layoutManager = new LinearLayoutManager(getActivity());
        rvPhotos.setLayoutManager(layoutManager);

        final PhotoAdapter.OnItemClickListener listener = new PhotoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (twoPane) {
                    getFragmentManager().beginTransaction().replace(R.id.fl_photo_view,
                            PhotoViewFragment.newInstance(adapter.getDataSet().get(position).getTitle(),
                                    adapter.getDataSet().get(position).getUrl(),
                                    ((MainActivity) getActivity()).getUserId(),
                                    adapter.getDataSet().get(position).getFlickrPhotoId())).commit();
                } else {
                    photoSelectedListener.onPhotoSelected(adapter.getDataSet().get(position).getTitle(),
                            adapter.getDataSet().get(position).getUrl(),
                            adapter.getDataSet().get(position).getFlickrPhotoId());
                }

            }
        };
        adapter = new PhotoAdapter(listener);
        rvPhotos.setAdapter(adapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                removeItem(viewHolder.getAdapterPosition());
            }
        }).attachToRecyclerView(rvPhotos);

        rvPhotos.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();

                if (!isLoading) {
                    if (firstVisibleItem >= totalItemCount * 0.8) {
                        isLoading = true;
                        loadMorePhotos();
                    }
                }
            }
        });
    }

    private void setRequestTextObservable() {
        requestTextDisposable = RxTextView.textChangeEvents(inputRequest)
                .debounce(400, TimeUnit.MILLISECONDS)
                .map(new Function<TextViewTextChangeEvent, String>() {
                    @Override
                    public String apply(TextViewTextChangeEvent t) {
                        return t.getText().toString();
                    }
                })
                .filter(new Predicate<String>() {
                    @Override
                    public boolean test(String s) {
                        return s.length() > 3;
                    }
                })
                .subscribeOn(Schedulers.io()) // Or Schedulers.newThread()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) {
                        runnable.setText(s);
                        getPhotos(SEARCH_PHOTOS_WITH_TEXT);
                    }
                });

    }

    private void removeItem(int position) {
        adapter.removeDataItem(position);
        adapter.notifyItemRemoved(position);
    }

    private void loadMorePhotos() {
        if (!runnable.updatePage()) {
            Toast.makeText(getActivity(), getString(R.string.toast_no_more_photos), Toast.LENGTH_SHORT).show();
            return;
        }
        ((MainActivity) getActivity()).getProcessResponseThread().getHandler().post(runnable);
    }

    private void finishLoading() {
        isLoading = false;
    }

    private void setPhotoSearchRunnable() {
        runnable = new PhotoSearchRunnable(new PhotoSearchRunnable.PhotosFoundCallback() {
            @Override
            public void onPhotosFound(final List<Photo> photos, final boolean isUpdating, final int searchType) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if (!isUpdating) {
                            adapter.clearDataSet();
                        }
                        Photo photo;
                        for (int i = 0; i < photos.size(); i++) {
                            photo = photos.get(i);

                            if (searchType == SEARCH_PHOTOS_WITH_GEO_COORDINATES) {
                                photo.setTitle(geoPhotoTitle);
                            }
                            adapter.updateDataSet(photo);
                        }

                        adapter.notifyDataSetChanged();
                        finishLoading();

                        if (adapter.getDataSet().size() == 0) {
                            Toast.makeText(getActivity(), getString(R.string.no_photos), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }

    private void getPhotos(int searchType) {
        runnable.setSearchType(searchType);
        runnable.resetPage();
        ((MainActivity) getActivity()).getProcessResponseThread().getHandler().post(runnable);
    }

    private void getPhotosWithGeo(double latitude, double longitude) {
        runnable.setGeoCoordinates(latitude, longitude);

        ((MainActivity) getActivity()).getProcessResponseThread().getHandler()
                .post(new AddressToTitleConvertRunnable((MainActivity) getActivity(), latitude, longitude,
                        new AddressToTitleConvertRunnable.OnConvertingFinishedCallback() {
                            @Override
                            public void onConvertingFinished(String text) {
                                geoPhotoTitle = text;
                            }
                        }));
        getPhotos(SEARCH_PHOTOS_WITH_GEO_COORDINATES);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(LAST_SEARCH_VALUE, inputRequest.getText().toString());
        editor.apply();

    }
}
