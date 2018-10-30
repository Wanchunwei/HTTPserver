//
import java.net.*;
import java.nio.*;
import java.nio.file.*;
import java.io.*;
import java.util.*;
import java.text.*;



public class WebServer {
    public static void main(String[] args) {
        // dummy value that is overwritten below
        int port = 8080;
        try {
          port = Integer.parseInt(args[0]);
        } catch (Exception e) {
          System.out.println("Usage: java WebServer <port> ");
          System.exit(0);
        }

        WebServer serverInstance = new WebServer();
        serverInstance.start(port);
    }

    private void start(int port){
      System.out.println("Starting server on port " + port);
      
      // NEEDS IMPLEMENTATION
      // You have to understand how sockets work and how to program
      // them in Java.
      // A good starting point is the socket tutorial from Oracle
      // http://docs.oracle.com/javase/tutorial/networking/sockets/
      // But there are a billion other resources on the Internet.
      //
      // Hints
      // 1. You should set up the socket(s) and then call handleClientSocket.
      try{
        ServerSocket serverSocket = new ServerSocket(port);
        while(true){
          try{
            Socket client = serverSocket.accept();
            handleClientSocket(client);
            //client.close();
         }catch(Exception e){
            e.printStackTrace();
            continue;
         }
        }
      }catch(Exception e){
        e.printStackTrace();
        System.exit(0);
      }
      //HTTP1.0
      
      
    }

    /**
     * Handles requests sent by a client
     * @param  client Socket that handles the client connection
     */
    private void handleClientSocket(Socket client) {
      // NEEDS IMPLEMENTATION
      // This function is supposed to handle the request
      // Things to do:
      // (1) Read the request from the socket 
      // (2) Parse the request and set variables of 
      //     the HttpRequest class (at the end of the file!)
      // (3) Form a response using formHttpResponse.
      // (4) Send a response using sendHttpResponse.
      //
      // A BufferedReader might be useful here, but you can also
      // solve this in many other ways.
      
    	try{
        	InputStream input = client.getInputStream();
        	InputStreamReader reader = new InputStreamReader(input);
        	BufferedReader bf = new BufferedReader(reader);
        	String s = null;

        	
          int timeout = 2000;
          
        	while((s = bf.readLine()) != null){
            //System.out.println("This is parse!");
            String[] spliteds = s.split(" ");
            if(spliteds[0].equals("GET")){
              HttpRequest clientRequest = new HttpRequest();
        		  clientRequest.parse(s);
              sendHttpResponse(client, formHttpResponse(clientRequest));
              if(clientRequest.getProtocal().equals("HTTP/1.0")){
                client.close();
                break;
              }else{
                client.setSoTimeout(timeout);
              }
              
            }
        	} 
          

        	
          
        } catch(Exception e){
        	e.printStackTrace();
        	System.out.println("Cannot get request...");
        }

    }

    /**
     * Sends a response back to the client
     * @param  client Socket that handles the client connection
     * @param  response the response that should be send to the client
     */
    private void sendHttpResponse(Socket client, byte[] response) {
      // NEEDS IMPLEMENTATION
      try{
        OutputStream responseOutput = client.getOutputStream();
        responseOutput.write(response);
      }catch(Exception e){
        e.printStackTrace();
      }

    }

    /**
     * Form a response to an HttpRequest
     * @param  request the HTTP request
     * @return a byte[] that contains the data that should be send to the client
     */
    private byte[] formHttpResponse(HttpRequest request) {
    // NEEDS IMPLEMENTATION
    // Make sure you follow the (modified) HTTP specification
    // in the assignment regarding header fields and newlines
    // You might want to use the concatenate method,
    // but you do not have to.
    // If you want to you can use a StringBuilder here
    // but it is possible to solve this in multiple different ways.
      //System.out.println("I am here!");
    	byte[] headerInByte;
      byte[] blank = new byte[1024];
    	FileInputStream fis = null;
    	
    	try{
    		File file = new File(request.getFilePath());
    		if(file.exists()){
          String responseHeader = request.getProtocal() + " " + "200 OK\r\nContent-Length: " + file.length() 
          + "\r\n" + "Last-Modified: " + getFileTime(request) + "GMT" + "\r\n\r\n";
          headerInByte = responseHeader.getBytes(); 
    			fis = new FileInputStream(file);


    			byte[] fileInByte = new byte[fis.available()];
          fis.read(fileInByte);
          fis.close();

          byte[] responseInByte = concatenate(headerInByte, fileInByte);
          return responseInByte;
    		}else{
    			String error = request.getProtocal() + " 404 Not Found\r\nContent-Length: 0\r\n\r\n";
    			headerInByte = error.getBytes();
          return headerInByte;
    		}
    	} catch (Exception e){
    		System.out.println(e.toString());
    	}
      return blank;
    }
    

    private String getFileTime(HttpRequest request) throws Exception{
      String requestURL = request.getFilePath();
      File requestFile = new File(requestURL);
      long time = requestFile.lastModified();
      Date date = new Date();
      date.setTime(time);
      SimpleDateFormat formatter = new SimpleDateFormat("EEE, dd MM yyyy HH:mm:ss ");
      return formatter.format(date);
    }
    /**
     * Concatenates 2 byte[] into a single byte[]
     * This is a function provided for your convenience.
     * @param  buffer1 a byte array
     * @param  buffer2 another byte array
     * @return concatenation of the 2 buffers
     */
    private byte[] concatenate(byte[] buffer1, byte[] buffer2) {
        byte[] returnBuffer = new byte[buffer1.length + buffer2.length];
        System.arraycopy(buffer1, 0, returnBuffer, 0, buffer1.length);
        System.arraycopy(buffer2, 0, returnBuffer, buffer1.length, buffer2.length);
        return returnBuffer;
    }
}



class HttpRequest {
    // NEEDS IMPLEMENTATION
    // This class should represent a HTTP request.
    // Feel free to add more attributes if needed.
    private String filePath;

    String getFilePath() {
        return "." + requestURL;
    }
    private String method;
    private String protocal;
    private String requestURL;
    private void setMethod(String d){
    	method = d;
    }
    private void setProtocal(String d){
    	protocal = d;
    }
    private void setRequestURL(String d){
    	requestURL = d;
    }
    void parse(String s){
      
    	String[] splitString = s.split(" ");
    	setMethod(splitString[0]);
    	setRequestURL(splitString[1]);
    	setProtocal(splitString[2]);

    }
    String getMethod(){
    	return method;
    }
    String getRequestURL(){
    	return requestURL;
    }
    String getProtocal(){
    	return protocal;
    }
    // NEEDS IMPLEMENTATION
    // If you add more private variables, add your getter methods here
}
