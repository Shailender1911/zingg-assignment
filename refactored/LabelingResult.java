package zingg.labeling;

import zingg.common.client.ZFrame;

public class LabelingResult<D, R, C> {

    private boolean success;
    private ZFrame<D, R, C> records;
    private String message;
    private Exception error;

    private LabelingResult() {}

    public static <D, R, C> LabelingResult<D, R, C> success(ZFrame<D, R, C> records) {
        LabelingResult<D, R, C> r = new LabelingResult<>();
        r.success = true;
        r.records = records;
        return r;
    }

    public static <D, R, C> LabelingResult<D, R, C> empty(String msg) {
        LabelingResult<D, R, C> r = new LabelingResult<>();
        r.success = true;
        r.message = msg;
        return r;
    }

    public static <D, R, C> LabelingResult<D, R, C> failure(String msg, Exception e) {
        LabelingResult<D, R, C> r = new LabelingResult<>();
        r.success = false;
        r.message = msg;
        r.error = e;
        return r;
    }

    public boolean isSuccess() { return success; }
    public boolean hasRecords() { return records != null; }
    public ZFrame<D, R, C> getRecords() { return records; }
    public String getMessage() { return message; }
    public Exception getError() { return error; }
}

