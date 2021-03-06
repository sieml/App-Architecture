package com.frodo.app.android.simple;

import com.frodo.app.android.core.task.AndroidFetchNetworkDataTask;
import com.frodo.app.framework.exception.HttpException;

import java.io.File;

import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.mime.TypedFile;
import rx.Subscriber;

/**
 * Created by frodo on 2015/8/15.
 */
public class UploadFileToServerTask extends AndroidFetchNetworkDataTask<UploadFileToServerTask.FileWebService, UploadFileToServerTask.FileUploadedResponse> {
    private File file;

    protected UploadFileToServerTask(FileWebService service, File file, Subscriber<FileUploadedResponse> subscriber) {
        super(service, subscriber);
        this.file = file;
    }

    @Override
    public FileUploadedResponse doBackgroundCall() throws HttpException {
        return getService().upload(new TypedFile("text", file));
    }

    @Override
    public void onSuccess(FileUploadedResponse result) {
    }

    public interface FileWebService {
        @Multipart
        @POST("/files")
        FileUploadedResponse upload(@Part("fileContent") TypedFile file);
    }

    public class FileUploadedResponse {
    }
}
