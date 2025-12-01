package zingg.labeling;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import zingg.common.client.ZFrame;
import zingg.common.client.ZinggClientException;
import zingg.common.client.util.ColName;

public class LabelingService<D, R, C> {

    private static final Log LOG = LogFactory.getLog(LabelingService.class);

    private LabelingDataLoader<D, R, C> dataLoader;
    private LabelingInputHandler<D, R, C> inputHandler;
    private LabelingStats stats;

    public LabelingService(LabelingDataLoader<D, R, C> loader, 
                          LabelingInputHandler<D, R, C> input,
                          LabelingStats stats) {
        this.dataLoader = loader;
        this.inputHandler = input;
        this.stats = stats;
    }

    public LabelingResult<D, R, C> execute() {
        try {
            ZFrame<D, R, C> records = dataLoader.loadUnmarkedRecords();
            
            if (records == null || records.count() == 0) {
                return LabelingResult.empty("No records to label");
            }

            ZFrame<D, R, C> labeled = processRecords(records);
            return LabelingResult.success(labeled);

        } catch (ZinggClientException e) {
            LOG.error("Labeling failed: " + e.getMessage());
            return LabelingResult.failure("Failed", e);
        }
    }

    private ZFrame<D, R, C> processRecords(ZFrame<D, R, C> records) throws ZinggClientException {
        showStats();
        
        records = records.cache();
        ZFrame<D, R, C> clusterFrame = records.select(ColName.CLUSTER_COLUMN).distinct();
        List<R> clusters = clusterFrame.collectAsList();

        ZFrame<D, R, C> result = null;
        int total = clusters.size();

        for (int i = 0; i < total; i++) {
            ZFrame<D, R, C> pair = getPair(records, clusterFrame, clusters.get(i));
            
            String preMsg = String.format("\tPair %d/%d", i + 1, total);
            String postMsg = getPredictionMsg(pair);
            
            LabelingOption choice = inputHandler.getUserChoice(pair, preMsg, postMsg);
            
            if (choice == LabelingOption.QUIT) {
                LOG.info("User quit");
                break;
            }
            
            stats.update(choice);
            showStats();
            
            result = addToResult(result, pair, choice);
        }

        return result;
    }

    private ZFrame<D, R, C> getPair(ZFrame<D, R, C> records, ZFrame<D, R, C> clusterFrame, R clusterId) {
        String val = clusterFrame.getAsString(clusterId, ColName.CLUSTER_COLUMN);
        return records.filter(records.equalTo(ColName.CLUSTER_COLUMN, val)).cache();
    }

    private ZFrame<D, R, C> addToResult(ZFrame<D, R, C> result, ZFrame<D, R, C> pair, LabelingOption opt) {
        ZFrame<D, R, C> labeled = pair.withColumn(ColName.MATCH_FLAG_COL, opt.getValue());
        return result == null ? labeled : result.union(labeled);
    }

    private void showStats() {
        inputHandler.showStats(stats.getMatchCount(), stats.getNoMatchCount(), 
            stats.getUnsureCount(), stats.getTotal());
    }

    private String getPredictionMsg(ZFrame<D, R, C> pair) {
        double score = pair.getAsDouble(pair.head(), ColName.SCORE_COL);
        double pred = pair.getAsDouble(pair.head(), ColName.PREDICTION_COL);
        
        if (pred < 0) {
            return "\tZingg still collecting data";
        }
        String type = pred == 1.0 ? "MATCH" : "NO MATCH";
        return String.format("\tPrediction: %s (score: %.2f)", type, score);
    }
}

