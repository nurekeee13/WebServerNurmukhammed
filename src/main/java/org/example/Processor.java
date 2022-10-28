package org.example;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Processor of HTTP request.
 */
public class Processor{
    private final Socket socket;
    private final HttpRequest request;
    public String response = "";

    public Processor(Socket socket, HttpRequest request) {
        this.socket = socket;
        this.request = request;
    }

    int toInt(String s){
        int res = 0;
        for(int i = 0; i < s.length(); ++i){
            res *= 10;
            res += (int)(s.charAt(i) - '0');
        }
        return res;
    }

    String sieve(int n){
        String res = "";
        int n1 = n + 100;
        res += "Prime numbers between 1 and " + n + ": \n";
        ArrayList<Boolean> prime=new ArrayList<>();
        for(int i = 0; i <= n; ++i){
            prime.add(true);
        }
        for(int i = 2; i * i <= n; ++i){
            if(prime.get(i)){
                for(int j = i * i; j <= n; j += i){
                    prime.set(j, false);
                }
            }
        }
        for(int i = 2; i <= n; ++i){
            if(prime.get(i)){
                res += i + " ";
            }
        }
        return res;
    }

    public void process() throws IOException {
        System.out.println("Got request:");
        System.out.println(request.toString());
        System.out.flush();
        String s = request.toString();
        if(s.contains("create")){
            String name = "";
            int pos = 12;
            while(s.charAt(pos) != ' '){
                char c = s.charAt(pos);
                name += c;
                pos++;
            }

            File myObj = new File(name);
            if (myObj.createNewFile()) {
                this.response += ("File created: " + myObj.getName());
            }
            else {
                this.response += "File already exists.";
            }
        }
        else if(s.contains("write")){
            String filename = "";
            int pos = 11;
            while(s.charAt(pos) != '/'){
                char c = s.charAt(pos);
                filename += c;
                pos++;
            }
            pos++;
            String text = "";
            while(s.charAt(pos) != ' '){
                char c = s.charAt(pos);
                text += c;
                pos++;
            }
            FileWriter myWriter = new FileWriter(filename);
            myWriter.write(text);
            myWriter.close();
            this.response += "Successfully wrote to the file.";
        }
        else if(s.contains("delete")){
            int pos = 12;
            String filename = "";
            while(s.charAt(pos) != ' '){
                char c = s.charAt(pos);
                filename += c;
                pos++;
            }
            File f= new File(filename);
            if(f.delete()){
                this.response += f.getName() + " deleted";   //getting and printing the file name
            }
            else
            {
                this.response += "failed";
            }
        }
        else if(s.charAt(5) == 'c' && s.charAt(6) == 'a' && s.charAt(7) == 't'){
            String filename = "";
            int pos = 9;
            while(s.charAt(pos) != ' '){
                char c = s.charAt(pos);
                filename += c;
                pos++;
            }
            BufferedReader myReader = new BufferedReader(new FileReader(filename));
            String strCurrentLine = "";
            while ((strCurrentLine = myReader.readLine()) != null) {
                this.response += strCurrentLine;
            }
            myReader.close();
        }
        else if(s.contains("eratothenes_sieve")){
            int pos = 7;
            while(s.charAt(pos) != '/'){
                pos++;
            }
            pos++;
            String digit = "";
            while(s.charAt(pos) != ' '){
                char c = s.charAt(pos);
                digit += c;
                pos++;
            }
            int x = toInt(digit);
            this.response += sieve(x);
        }
        PrintWriter output = new PrintWriter(socket.getOutputStream());
        // We are returning a simple web page now.
        output.println("HTTP/1.1 200 OK");
        output.println("Content-Type: text/html; charset=utf-8");
        output.println();
        output.println("<html>");
        output.println("<head><title>Hello</title></head>");
        output.println("<body><p>" + this.response + "</p></body>");
        output.println("</html>");
        output.flush();

        socket.close();
        this.response = "";
    }
}