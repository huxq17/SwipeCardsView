package com.huxq17.example.mzitu;


import androidx.annotation.NonNull;

import com.huxq17.download.PumpFactory;
import com.huxq17.download.core.DownloadInfo;
import com.huxq17.download.core.service.IDownloadManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

public class RxPump {
    /**
     * Delete a download info by Tag
     *
     * @param tag tag
     */
    public static Observable<Boolean> deleteByTag(final String tag) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) {
                PumpFactory.getService(IDownloadManager.class).deleteByTag(tag);
                e.onNext(true);
                e.onComplete();
            }
        });
    }

    /**
     * Delete a download info by unique download id. this method may delete a group of tasks.
     *
     * @param id unique download id,default is download url.
     */
    public static Observable<Boolean> deleteById(@NonNull final String id) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) {
                PumpFactory.getService(IDownloadManager.class).deleteById(id);
                e.onNext(true);
                e.onComplete();
            }
        });
    }

    /**
     * Get a list of all download list.
     *
     * @return
     */
    public static Observable<List<DownloadInfo>> getAllDownloadList() {
        return Observable.create(new ObservableOnSubscribe<List<DownloadInfo>>() {
            @Override
            public void subscribe(ObservableEmitter<List<DownloadInfo>> e) {
                e.onNext(PumpFactory.getService(IDownloadManager.class).getAllDownloadList());
                e.onComplete();
            }
        });
    }

    public static Observable<List<DownloadInfo>> getDownloadingList() {
        return Observable.create(new ObservableOnSubscribe<List<DownloadInfo>>() {
            @Override
            public void subscribe(ObservableEmitter<List<DownloadInfo>> e) {
                e.onNext(PumpFactory.getService(IDownloadManager.class).getDownloadingList());
                e.onComplete();
            }
        });
    }

    public static Observable<List<DownloadInfo>> getDownloadedList() {
        return Observable.create(new ObservableOnSubscribe<List<DownloadInfo>>() {
            @Override
            public void subscribe(ObservableEmitter<List<DownloadInfo>> e) {
                e.onNext(PumpFactory.getService(IDownloadManager.class).getDownloadedList());
                e.onComplete();
            }
        });
    }

    /**
     * Get download list filter by tag.
     *
     * @param tag tag
     * @return
     */
    public static Observable<List<DownloadInfo>> getDownloadListByTag(final String tag) {
        return Observable.create(new ObservableOnSubscribe<List<DownloadInfo>>() {
            @Override
            public void subscribe(ObservableEmitter<List<DownloadInfo>> e) {
                e.onNext(PumpFactory.getService(IDownloadManager.class).getDownloadListByTag(tag));
                e.onComplete();
            }
        });
    }

    /**
     * Get downloadInfo by unique download id.
     *
     * @param id unique download id,default is download url.
     * @return
     */
    public static Observable<DownloadInfo> getDownloadInfoById(@NonNull final String id) {
        return Observable.create(new ObservableOnSubscribe<DownloadInfo>() {
            @Override
            public void subscribe(ObservableEmitter<DownloadInfo> e) {
                DownloadInfo downloadInfo = PumpFactory.getService(IDownloadManager.class).getDownloadInfoById(id);
                if(downloadInfo!=null){
                    e.onNext(downloadInfo);
                    e.onComplete();
                }else{
                    e.onError(new Exception(id+"'s downloadInfo is null."));
                }
            }
        });
    }

    /**
     * Check url whether download success
     *
     * @param id unique download id,default is download url.
     * @return true If Pump has downloaded
     */
    public static Observable<Boolean> hasDownloadSucceed(@NonNull final String id) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) {
                e.onNext(PumpFactory.getService(IDownloadManager.class).hasDownloadSucceed(id));
                e.onComplete();
            }
        });
    }

    /**
     * If url had download successful,return the local file
     *
     * @param id unique download id,default is download url.
     * @return the file has downloaded.
     */
    public static Observable<File> getFileIfSucceed(@NonNull final String id) {
        return Observable.create(new ObservableOnSubscribe<File>() {
            @Override
            public void subscribe(ObservableEmitter<File> e) {
                File file = PumpFactory.getService(IDownloadManager.class).getFileIfSucceed(id);
                if(file!=null){
                    e.onNext(file);
                    e.onComplete();
                }else{
                    e.onError(new FileNotFoundException(id+" have not download successful yet."));
                }

            }
        });
    }

}
