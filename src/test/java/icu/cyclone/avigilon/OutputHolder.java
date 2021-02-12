package icu.cyclone.avigilon;

import com.google.common.base.Strings;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Aleksey Babanin
 * @since 2021/02/12
 */
class OutputHolder {
    private final OutputStream outputStream;
    private final PrintStream printStream;

    public OutputHolder() {
        this.outputStream = new ByteArrayOutputStream();
        this.printStream = new PrintStream(outputStream);
    }

    public PrintStream getPrintStream() {
        return printStream;
    }

    public String getOutput() {
        return outputStream.toString();
    }

    public List<String> getNotEmptyOutputLines() {
        String output = getOutput();
        if (Strings.isNullOrEmpty(output)) {
            return Collections.emptyList();
        }

        return Arrays
                .stream(output.split(System.lineSeparator()))
                .filter(s -> !Strings.isNullOrEmpty(s))
                .collect(Collectors.toList());
    }
}
