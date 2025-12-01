package zingg.labeling;

import zingg.common.client.ZFrame;

public interface LabelingInputHandler<D, R, C> {

    LabelingOption getUserChoice(ZFrame<D, R, C> records, String preMsg, String postMsg);

    void showStats(long match, long noMatch, long unsure, long total);

    void showMessage(String msg);

    void close();
}

