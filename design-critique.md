# Labeller.java - Design Critique

## what this class does

so basically this Labeller class is for interactive labeling where user reviews record pairs and marks them as match or not match. it does following things:
- loads unmarked records from storage
- shows pairs to user on command line
- takes input from user (0,1,2 or 9 to quit)
- saves the labeled data back

## problems i found in the code

### 1. class is doing too many things

the execute() method is doing everything - loading data, preprocessing, taking user input, saving results. all in one place. this makes it difficult to test individual parts or make changes without breaking something else.

### 2. scanner is hardcoded

    int readCliInput() {
        Scanner sc = new Scanner(System.in);
        ...
    }

this is a problem bcoz:
- we cant write unit tests for this method
- if tomorrow we want to add gui, this wont work
- also the scanner is never closed so there is resource leak

### 3. exception handling has issues

    try {
        markedRecords = getPipeUtil().read(...);
    } catch (Exception e) {
        LOG.warn("No record has been marked yet");
    } catch (ZinggClientException zce) {
        LOG.warn("No record has been marked yet");
    }

here Exception is caught before ZinggClientException which is wrong bcoz ZinggClientException extends Exception so it will never reach second catch block. also the exception is just logged and method returns null which makes it hard to debug when something goes wrong.

### 4. magic numbers

    while (!sc.hasNext("[0129]")) { ... }

only QUIT_LABELING=9 has a constant defined but 0,1,2 are used directly in code. better to use enum for all options.

### 5. duplicate code exists

getUnmarkedRecords() method has almost same code in Labeller, ZinggBase and TrainingDataModel classes. this is violation of DRY principle. should be in one place.

### 6. processRecordsCli method is very long

this method is around 65 lines and doing multiple things - getting clusters, displaying records, taking input, updating stats, building result. should be broken into smaller methods.

### 7. dependencies created inside class

    public ITrainingDataModel getTrainingDataModel() {   
        if (trainingDataModel == null) {
            this.trainingDataModel = new TrainingDataModel<>(...);
        }
        return trainingDataModel;
    }

dependencies are created inside the class with lazy initialization. this makes it hard to inject mock objects for testing. better approach is constructor injection.


## my suggestions for improvement

### split into smaller classes

instead of one big class we can have separate classes for each responsibility:
- LabelingService - coordinates everything
- LabelingInputHandler - interface for user input/output
- LabelingDataLoader - interface for loading data
- LabelingStats - for tracking counts

### use enum for options

    public enum LabelingOption {
        NOT_A_MATCH(0),
        MATCH(1),
        NOT_SURE(2),
        QUIT(9);
    }

this way we get compile time checking and code is more readable.

### create interface for input handling

    public interface LabelingInputHandler {
        LabelingOption getUserChoice(...);
        void displayRecords(...);
        void close();
    }

then we can have CliInputHandler for command line. later if needed can add GuiInputHandler or even ApiInputHandler for rest api.

### use constructor injection

    public LabelingService(LabelingDataLoader loader, 
                          LabelingInputHandler input,
                          LabelingStats stats) {
        this.loader = loader;
        this.input = input;
        this.stats = stats;
    }

now for testing we can easily pass mock objects.

### return result object

    public class LabelingResult {
        boolean success;
        ZFrame labeledRecords;
        String message;
    }

instead of returning null when something fails, return proper result object. caller will know exactly what happened.


## how testing becomes easier with this design

with original code testing is hard:

    Labeller labeller = new SparkLabeller();
    labeller.execute(); // how to give input? how to verify?

with refactored code:

    // create mock handler
    LabelingInputHandler mock = new MockInputHandler();
    mock.setResponses(MATCH, MATCH, QUIT);
    
    LabelingService service = new LabelingService(loader, mock, stats);
    LabelingResult result = service.execute();
    
    // now we can check
    assertTrue(result.isSuccess());


## tradeoffs to consider

yes there will be more classes now which means more files. but each class is smaller and does one thing only. the interfaces add some extra code but they make testing much easier which is worth it in long run.


## summary

main things to improve:
1. split the class into smaller classes with single responsibility
2. abstract the input/output behind interface for testability
3. use enum instead of magic numbers
4. fix the exception handling - dont swallow exceptions
5. use dependency injection so we can mock during tests
