package zingg.labeling;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

import zingg.common.client.ZFrame;

public class CliInputHandler<D, R, C> implements LabelingInputHandler<D, R, C> {

    private Scanner scanner;
    private PrintStream out;

    public CliInputHandler() {
        this(System.in, System.out);
    }

    public CliInputHandler(InputStream in, PrintStream out) {
        this.scanner = new Scanner(in);
        this.out = out;
    }

    @Override
    public LabelingOption getUserChoice(ZFrame<D, R, C> records, String preMsg, String postMsg) {
        out.println(preMsg);
        // display records here
        out.println(postMsg);
        
        out.println("\tYour choices:");
        out.println("\t0 - No match");
        out.println("\t1 - Match");
        out.println("\t2 - Not sure");
        out.println("\t9 - Quit");
        out.print("\tEnter choice: ");

        while (true) {
            String input = scanner.next();
            if (LabelingOption.isValid(input)) {
                return LabelingOption.fromValue(Integer.parseInt(input));
            }
            out.print("\tInvalid. Enter 0,1,2 or 9: ");
        }
    }

    @Override
    public void showStats(long match, long noMatch, long unsure, long total) {
        out.println();
        out.printf("\tLabelled: %d/%d MATCH, %d/%d NO MATCH, %d/%d UNSURE%n", 
            match, total, noMatch, total, unsure, total);
    }

    @Override
    public void showMessage(String msg) {
        out.println(msg);
    }

    @Override
    public void close() {
        if (scanner != null) {
            scanner.close();
        }
    }
}

