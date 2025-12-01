# Labeller.java - Design Critique

## What does this class do

Looking at the code, Labeller class handles the interactive labeling part where users review record pairs and mark them. It basically:
- loads unmarked records
- shows pairs to user on CLI
- takes input (0,1,2 or 9 to quit)
- saves the labeled data

## Problems I found

### 1. Class is doing too much

The execute() method is handling data loading, preprocessing, user interaction and saving all together. This makes it hard to test or change one part without affecting others.

### 2. Scanner hardcoded to System.in

    int readCliInput() {
        Scanner sc = new Scanner(System.in);
        ...
    }

This is problematic because:
- cant write unit tests for this
- if we want to add GUI later, this wont work
- the scanner is never closed which can cause resource leak

### 3. Exception handling issues

    try {
        markedRecords = getPipeUtil().read(...);
    } catch (Exception e) {
        LOG.warn("No record has been marked yet");
    } catch (ZinggClientException zce) {
        LOG.warn("No record has been marked yet");
    }

Here Exception is caught before ZinggClientException which is wrong order. Also exceptions are swallowed and method returns null which makes debugging harder.

### 4. Magic numbers used

    while (!sc.hasNext("[0129]")) { ... }

Only QUIT_LABELING=9 is defined as constant but 0,1,2 are used directly. Would be better to use enum.

### 5. Duplicate code

getUnmarkedRecords() has similar code in Labeller, ZinggBase and TrainingDataModel. Should be in one place only.

### 6. processRecordsCli is too long

Around 65 lines doing multiple things - getting clusters, showing records, taking input, updating stats. Should be split into smaller methods.

### 7. Dependencies created inside class

    public ITrainingDataModel getTrainingDataModel() {   
        if (trainingDataModel == null) {
            this.trainingDataModel = new TrainingDataModel<>(...);
        }
        return trainingDataModel;
    }

This makes it hard to inject mocks for testing. Better to pass dependencies via constructor.


## My suggestions

### Split the responsibilities

Instead of one big class, we can have:
- LabelingService - main coordinator
- LabelingInputHandler - handles user input/output (interface)
- LabelingDataLoader - loads data (interface)
- LabelingStats - tracks the counts

### Use enum for options

    public enum LabelingOption {
        NOT_A_MATCH(0),
        MATCH(1),
        NOT_SURE(2),
        QUIT(9);
    }

This gives compile time safety and is more readable.

### Abstract the input

Create interface so we can have different implementations:

    public interface LabelingInputHandler {
        LabelingOption getUserChoice(...);
        void displayRecords(...);
        void close();
    }

Then CliInputHandler for command line, can add GuiInputHandler later if needed.

### Use constructor injection

    public LabelingService(LabelingDataLoader loader, 
                          LabelingInputHandler input,
                          LabelingStats stats) {
        this.loader = loader;
        this.input = input;
        this.stats = stats;
    }

Now we can easily pass mock objects for testing.

### Return result object instead of null

    public class LabelingResult {
        boolean success;
        ZFrame labeledRecords;
        String message;
    }

Better than returning null, caller knows exactly what happened.


## How testing becomes easier

Original - hard to test:

    Labeller labeller = new SparkLabeller();
    labeller.execute(); // how to provide input?

With refactored design:

    // create mock
    LabelingInputHandler mock = new MockInputHandler();
    mock.setResponses(MATCH, MATCH, QUIT);
    
    LabelingService service = new LabelingService(loader, mock, stats);
    LabelingResult result = service.execute();
    
    // now we can assert
    assertTrue(result.isSuccess());


## Tradeoffs

More classes means more files to manage but each class is simpler and easier to understand. The interfaces add some overhead but make the code much more testable.


## Summary

Main improvements needed:
1. split responsibilities into separate classes
2. abstract the I/O so its testable
3. use enums instead of magic numbers
4. proper exception handling
5. dependency injection for testing
