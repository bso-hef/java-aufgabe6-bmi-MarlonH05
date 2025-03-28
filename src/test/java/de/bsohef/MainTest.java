package de.bsohef;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.invocation.Invocation;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MainTest {
    static InputStream sysInBackup;
    static PrintStream sysOutBackup;

    private class InputProvide implements Runnable {
//        InputStream inputStream;
        OutputStream outStream;
        int port;

//        public OutputProvider(InputStream in, PrintStream out) {
//            this.inputStream = in;
//            this.outStream = out;
//        }

        public InputProvide(int port, PrintStream out) {
            this.outStream = out;
            this.port = port;
        }


        @Override
        public void run() {
            try (ServerSocket socket = new ServerSocket(53000)){
                Socket client = socket.accept();
                OutputStream out = client.getOutputStream();
                int expectedNumberOfInputs = 2;
                int numberOfGivenInputs = 0;
                while (numberOfGivenInputs <= expectedNumberOfInputs) {
                    if(outputStream.size() == 0) {
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    else {
                        if(outputStream.toString().contains("größe")
                                || outputStream.toString().contains("grösse")
                                || outputStream.toString().contains("groeße")
                                || outputStream.toString().contains("groesse")
                        ) {
                            MainTest.sysOutBackup.println("write größe 2 to stream");
                            out.write("2\n".getBytes());
                            outputStream.reset();
                            numberOfGivenInputs++;
                        }
                        else if(outputStream.toString().contains("gewicht")
                                ||outputStream.toString().contains("Gewicht")) {
                            MainTest.sysOutBackup.println("Write gewicht 80 to stream");
                            out.write("80\n".getBytes());
                            numberOfGivenInputs++;
                        }
                        Thread.sleep(100);
                    }
                }
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    //Read output
    @Spy
    PrintStream out = new PrintStream(outputStream);

    @BeforeAll
    static void beforeAll() {
        sysInBackup = System.in;
        sysOutBackup = System.out;
    }

    @AfterAll
    static void afterAll() {
        System.setIn(sysInBackup);
    }

    @Test
    void testMain() throws IOException, InterruptedException {

        System.setOut(out);


        int port = 53000;
        Thread socketInputProvider = new Thread(new InputProvide(port, out));
        socketInputProvider.start();
        Thread.sleep(100);

        Socket socket = new Socket("localhost", port);
        BufferedInputStream in = new BufferedInputStream(socket.getInputStream());
        System.setIn(in);

        //Call Method under test
        Main.main(null);

        // details of all invocations including methods and arguments
        Collection<Invocation> invocations = Mockito.mockingDetails(out).getInvocations();

        // just a number of calls of any mock's methods
        int numberOfCalls = invocations.size();

        invocations = invocations.stream().filter( invocation -> {
            if(invocation.getMethod().getName().contains("print")){
                return true;
            }
            return false;
        }).collect(Collectors.toCollection(ArrayList::new));

        assertTrue(invocations.size() > 2, "Es müssen mindestens zwei Ausgaben verwendet werden. \n" +
                "1. Benutzer auffordern die Größe in einer bestimmten Einheit einzugeben\n" +
                "2. Benutzer auffordern das Gewicht in einer bestimmten Einheit einzugeben\n");

        //Validate output
        String output = outputStream.toString();
        assertTrue(output.contains("20"), "Der BMI aus größe 2m und gewicht 80kg sollte 20 sein" +
                "; \noutput was: " + output);
    }
}