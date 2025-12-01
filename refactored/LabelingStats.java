package zingg.labeling;

public class LabelingStats {

    private long matchCount;
    private long noMatchCount;
    private long unsureCount;
    private long total;

    public void update(LabelingOption opt) {
        if (opt == LabelingOption.MATCH) matchCount++;
        else if (opt == LabelingOption.NOT_A_MATCH) noMatchCount++;
        else if (opt == LabelingOption.NOT_SURE) unsureCount++;
        
        if (opt != LabelingOption.QUIT) total++;
    }

    public void init(long match, long noMatch, long unsure) {
        this.matchCount = match;
        this.noMatchCount = noMatch;
        this.unsureCount = unsure;
        this.total = match + noMatch + unsure;
    }

    public long getMatchCount() { return matchCount; }
    public long getNoMatchCount() { return noMatchCount; }
    public long getUnsureCount() { return unsureCount; }
    public long getTotal() { return total; }
}

