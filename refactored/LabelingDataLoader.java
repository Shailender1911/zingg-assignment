package zingg.labeling;

import zingg.common.client.ZFrame;
import zingg.common.client.ZinggClientException;

public interface LabelingDataLoader<D, R, C> {

    ZFrame<D, R, C> loadUnmarkedRecords() throws ZinggClientException;

    ZFrame<D, R, C> loadMarkedRecords() throws ZinggClientException;
}

