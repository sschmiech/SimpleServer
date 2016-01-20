/**
 * Created by Sebastian Schmiech on 19.01.2016.
 */
import java.net.*;
import java.io.*;
import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.core.*;


public class SimpleServerThread extends Thread {
    private Socket socket = null;

    public SimpleServerThread(Socket socket) {
        super("SimpleServerThread");
        this.socket = socket;
    }
    public String buildHttpResponse(String callback, String response)
    {
        response=callback+"("+response+")\n";
        StringBuilder responseString = new StringBuilder();
        responseString.append("HTTP/1.1 200 OK\n");
        responseString.append("Date: "+LocalDateTime.now().toString()+"\n");
        responseString.append("Connection: close\n");
        responseString.append("Content-Type: application/javascript;charset=utf-8\n");
        responseString.append("Content-Length: "+response.getBytes().length+"\n");
        responseString.append("\n");
        responseString.append(response);
        return responseString.toString();
    }

    public void run() {

        try (
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(
                                socket.getInputStream()));
        ) {
            String inputLine, outputLine;
            OutputStream output = new OutputStream()
            {
                private StringBuilder string = new StringBuilder();
                @Override
                public void write(int b) throws IOException {
                    this.string.append((char) b );
                }

                //Netbeans IDE automatically overrides this toString()
                public String toString(){
                    return this.string.toString();
                }

            };
            JsonFactory factory = new JsonFactory();

            JsonGenerator generator = factory.createGenerator(
                    output, JsonEncoding.UTF8);

            generator.writeStartObject();
            generator.writeStringField("DateTime", LocalDateTime.now().toString());
            generator.writeEndObject();
            generator.close();

            outputLine = output.toString();

            String received, what, callback, content, response;

            while ((inputLine = in.readLine()) != null) {
                if(inputLine.startsWith("GET /")){
                    received = inputLine;
                    what = inputLine.substring(5, inputLine.indexOf("?"));
                    callback = inputLine.substring(inputLine.indexOf("callback=")+9, inputLine.indexOf("?",inputLine.indexOf("callback=")+10));
                    content = inputLine.substring(inputLine.indexOf("content=")+8, inputLine.indexOf(" ",inputLine.indexOf("content=")+10));
                    response=buildHttpResponse(callback,outputLine);
                    System.out.println("Received: " + received);
                    System.out.println("What:: " + what);
                    System.out.println("callback: " + callback);
                    System.out.println("content: " + content);
                    System.out.println("response: "+ response);
                    out.println(response);
                }
            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}