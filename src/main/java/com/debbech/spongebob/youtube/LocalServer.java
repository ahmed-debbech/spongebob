package com.debbech.spongebob.youtube;

import com.debbech.spongebob.Config;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class LocalServer {

    private static HttpServer httpServer = null;
    private static boolean isRunning = false;
    private final static Object lock = new Object();

    private LocalServer(){}

    public synchronized static void start() throws Exception{
        if(!isRunning){
            if(httpServer == null) {
                try {
                    httpServer = HttpServer.create(new InetSocketAddress("127.0.0.1", Config.getInstance().PORT), 0);
                    System.out.println("http server created for callback.");
                } catch (IOException e) {
                    throw new Exception("can not start http server to listen to google's callback: " + e.getMessage());
                }
            }
            httpServer.createContext("/", new RootHandler());
            httpServer.setExecutor(Executors.newCachedThreadPool()); // creates a default executor
            new Thread(()-> {
                synchronized (lock) {
                    isRunning = true;
                    httpServer.start();
                    System.out.println("http server started to listen to google's callback on port " + Config.getInstance().PORT);
                    try {
                        Thread.sleep(Config.getInstance().timeToStopCallbackServer * 1000);
                    } catch (InterruptedException e) {
                    }
                    httpServer.stop(1);
                    isRunning = false;
                    System.out.println("http server has stoped automatically after " + Config.getInstance().timeToStopCallbackServer + " seconds");
                }
            }).start();
        }else{
            System.out.println("server already started");
        }
    }
    static class RootHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            System.out.println("received from code after google callback");
            try {
                String code = getParams(t,"code");
                System.out.println("got 'code' from google's response");
                TokenResp tr = getTokens(code);
                System.out.println("done calling get tokens");
                YoutubeCore.getInstance().setUserTokens(tr);
                respond200(t);
            }catch(UnsupportedEncodingException uee){
                System.err.println("could not parse google's callback correctly");
            }catch (Exception e){
                System.err.println("FAILED processing google's callback: " + e.getMessage());
            }
        }
        private void respond200(HttpExchange t){
            String response = "<html>\n" +
                    "\n" +
                    "<head>\n" +
                    "    <title>Spongebob</title>\n" +
                    "</head>\n" +
                    "\n" +
                    "<body>\n" +
                    "    <h2>You may now close this window!</h2>\n"  +
                    "</body>\n" +
                    "\n" +
                    "</html>";

            try {
                t.sendResponseHeaders(200, response.length());
                OutputStream os = t.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }catch(Exception e){
                System.err.println("could not send 'you may close this window message back to clients browser.'");
            }
        }
        private String getParams(HttpExchange exchange, String key) throws UnsupportedEncodingException {
            URI requestUri = exchange.getRequestURI();
            String query = requestUri.getQuery();
            Map<String, String> queryParams = new HashMap<>();
            if (query == null) throw new UnsupportedEncodingException();

            String[] pairs = query.split("&");
            for (String pair : pairs) {
                int idx = pair.indexOf("=");
                if (idx > 0) {
                    queryParams.put(
                            java.net.URLDecoder.decode(pair.substring(0, idx), "UTF-8"),
                            java.net.URLDecoder.decode(pair.substring(idx + 1), "UTF-8")
                    );
                }
            }

            return queryParams.get(key);
        }
        private TokenResp getTokens(String code) throws Exception{
            String x = "https://oauth2.googleapis.com/token";
            URL url = new URL(x);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            Map<String, String> parameters = new HashMap<>();
            parameters.put("code", code);
            parameters.put("client_secret", Config.getInstance().getGoogleSecret().installed.client_secret);
            parameters.put("client_id", Config.getInstance().getGoogleSecret().installed.client_id );
            parameters.put("redirect_uri", "http://127.0.0.1:9004");
            parameters.put("grant_type", "authorization_code");

            con.setDoOutput(true);
            DataOutputStream out = new DataOutputStream(con.getOutputStream());
            out.writeBytes(paramString(parameters));
            out.flush();
            out.close();
            int status = con.getResponseCode();

            Reader streamReader;
            if (status > 299) {
                streamReader = new InputStreamReader(con.getErrorStream());
                System.err.println("Failed with " + status + " error code in http request to get tokens from google");
            } else {
                streamReader = new InputStreamReader(con.getInputStream());
            }
            BufferedReader in = new BufferedReader(streamReader);
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            con.disconnect();

            Gson gson = new Gson();
            TokenResp tr = gson.fromJson(content.toString(), TokenResp.class);
            return tr;
        }

        private String paramString(Map<String, String> params){

            StringBuilder concatenatedString = new StringBuilder();

            for (Map.Entry<String, String> entry : params.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();

                concatenatedString.append(key).append("=").append(value).append("&");
            }
            String result = concatenatedString.toString().substring(0, concatenatedString.toString().length()-1);
            return result;
        }
    }}
