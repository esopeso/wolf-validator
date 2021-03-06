package org.jboss.wolf.validator.reporter;

import static org.jboss.wolf.validator.internal.Utils.sortExceptions;

import java.io.PrintStream;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jboss.wolf.validator.Reporter;
import org.jboss.wolf.validator.ValidatorContext;

public class SimpleUnprocessedExceptionsReporter implements Reporter {
    
    private final PrintStream out;

    public SimpleUnprocessedExceptionsReporter(PrintStream out) {
        this.out = out;
    }

    @Override
    public void report(ValidatorContext ctx) {
        List<Exception> allExceptions = sortExceptions(ctx.getExceptions());
        List<Exception> unprocessedExceptions = sortExceptions(ctx.getUnprocessedExceptions());

        if (!unprocessedExceptions.isEmpty()) {
            out.println("--- UNPROCESSED EXCEPTIONS REPORT ---");
            out.println("Unprocessed exceptions count " + unprocessedExceptions.size() + ", from total " + allExceptions.size() + ".");
            for (Exception unprocessedException : unprocessedExceptions) {
                reportException(unprocessedException, 0);
            }
            out.println();
            out.flush();
        }
    }
    
    private void reportException(Throwable e, int depth) {
        StringBuilder msg = new StringBuilder();
        msg.append(StringUtils.repeat(" ", depth * 4));
        msg.append(e.getClass().getSimpleName());
        msg.append(" ");
        msg.append(e.getMessage());

        out.println(msg.toString());

        if (e.getCause() != null) {
            reportException(e.getCause(), depth + 1);
        }
    }

}