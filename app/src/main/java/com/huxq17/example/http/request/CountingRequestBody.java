package com.huxq17.example.http.request;

import com.andbase.tractor.task.Task;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

/**
 * Decorates an OkHttp request body to count the number of bytes written when writing it. Can
 * decorate any request body, but is most useful for tracking the upload progress of large
 * multipart requests.
 *
 * @author Leo Nikkil
 */
public class CountingRequestBody extends RequestBody {

    protected RequestBody delegate;
    protected Task mTask;
    private int mProcess = -1;

    protected CountingSink countingSink;

    public CountingRequestBody(RequestBody delegate, Task task) {
        this.delegate = delegate;
        this.mTask = task;
    }

    @Override
    public MediaType contentType() {
        return delegate.contentType();
    }

    @Override
    public long contentLength() {
        try {
            return delegate.contentLength();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        BufferedSink bufferedSink;

        countingSink = new CountingSink(sink);
        bufferedSink = Okio.buffer(countingSink);

        delegate.writeTo(bufferedSink);

        bufferedSink.flush();
    }

    protected final class CountingSink extends ForwardingSink {

        private long bytesWritten = 0;

        public CountingSink(Sink delegate) {
            super(delegate);
        }

        @Override
        public void write(Buffer source, long byteCount) throws IOException {
            super.write(source, byteCount);

            bytesWritten += byteCount;
            int process = (int) (1.0 * bytesWritten / contentLength() * 100);
            if (mTask != null&&process!=mProcess) {
                mTask.notifyLoading(process);
                mProcess = process;
            }
            if (process == 100) {
                mTask = null;
            }
        }

    }
}