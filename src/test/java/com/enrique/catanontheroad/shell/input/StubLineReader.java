package com.enrique.catanontheroad.shell.input;

import org.jline.reader.LineReader;
import org.mockito.Mockito;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Creates a mock LineReader that returns pre-defined inputs.
 */
class StubLineReader {

    static LineReader create(String... inputs) {
        Queue<String> inputQueue = new LinkedList<>(List.of(inputs));
        LineReader mock = Mockito.mock(LineReader.class);
        when(mock.readLine(anyString())).thenAnswer(inv -> inputQueue.isEmpty() ? "" : inputQueue.poll());
        return mock;
    }
}
