package com.passaparola.thiagodesales.passaparolaview.connection;

import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.util.Xml;

import com.passaparola.thiagodesales.passaparolaview.model.Parola;
import com.passaparola.thiagodesales.passaparolaview.model.RSSMeditationItem;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Connections extends AsyncTask<String, Integer, String> {

    public enum REQUEST_TYPES {PAROLA, MEDITATION};

    private REQUEST_TYPES requestType;
    private URL parolaURL;
    private ConnectionResponseHandler responseHandler;
    private HttpURLConnection connection;
    private String todaysParola;
    private HashMap<Object, Object> params;
    boolean isItem = false;
    private ArrayList<RSSMeditationItem> meditationList;
    private Pattern pattern;
    private Parola parolaFromWeb;

    public Connections(ConnectionResponseHandler responseHandler) {
        this.responseHandler = responseHandler;
        pattern = Pattern.compile("(\\d{1,2}/\\d{1,2}/\\d{4}|\\d{1,2}/\\d{1,2})", Pattern.CASE_INSENSITIVE);
    }

    public void setParameters(HashMap<Object, Object> params) {
        this.params = params;
    }

    public void setRequestType(REQUEST_TYPES requestType) {
        this.requestType = requestType;
    }

    private void requestMeditations() {
        String publishedDate = null;
        String parola = null;
        String meditation = null;
        meditationList = new ArrayList<>();

        try {
            URL url = new URL("http://parolafocolare.blogspot.com/feeds/posts/default");

            InputStream input = url.openConnection().getInputStream();
            XmlPullParser xmlPullParser = Xml.newPullParser();
            xmlPullParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            xmlPullParser.setInput(new InputStreamReader(input));


            xmlPullParser.nextTag();
            while (xmlPullParser.next() != XmlPullParser.END_DOCUMENT) {
                int eventType = xmlPullParser.getEventType();

                String name = xmlPullParser.getName();
                if(name == null)
                    continue;
                else
                    Log.d("requestMeditationsName", name);

                if(eventType == XmlPullParser.END_TAG) {
                    if(name.equalsIgnoreCase("entry")) {
                        isItem = false;
                    }
                    continue;
                }

                if (eventType == XmlPullParser.START_TAG) {
                    if(name.equalsIgnoreCase("entry")) {
                        isItem = true;
                        continue;
                    }
                }

                Log.d("MyXmlParser", "Parsing name ==> " + name);
                String result = "";
                if (xmlPullParser.next() == XmlPullParser.TEXT) {
                    result = xmlPullParser.getText();
                    xmlPullParser.nextTag();
                }

                if (name.equalsIgnoreCase("title")) {
                    Log.d("MEDITACAO TITLE", result);
                    parola = result;
                } else if (name.equalsIgnoreCase("content")) {
                    Log.d("MEDITACAO CONTENT", result);
                    meditation = result;
                } else if (name.equalsIgnoreCase("published")) {
                    Log.d("MEDITACAO DATE", result);
                    publishedDate = result;
                }

                Log.d("Connections", "Verificando campos");
                if (publishedDate != null && parola != null && meditation != null) {
                    Log.d("CAMPOS", "TODOS OS CAMPOS NÃO NULOS");
                    if(isItem) {
                        String meditationPtIt[] = meditation.split("#");
                        String parolaPtIt[] = parola.split("#");

                        HashMap<String, String> parolas = new HashMap<>();
                        parolas.put("pt", parolaPtIt[0]);
                        parolas.put("it", parolaPtIt[1]);

                        HashMap<String, String> meditations = new HashMap<>();
                        meditations.put("pt", Html.fromHtml(meditationPtIt[0]).toString());
                        meditations.put("it", meditationPtIt[1]);

                        RSSMeditationItem item = new RSSMeditationItem(publishedDate, parolas, meditations);
                        Log.d("MEDITAÇÃO COMPLETA", item.toString());
                        meditationList.add(item);
                    }

                    publishedDate = null;
                    parola = null;
                    meditation = null;
                    isItem = false;
                } else Log.d("CAMPOS", "AINDA NULOS");
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }

    }

    private void requestParola(String language) {
        try {
            this.parolaURL = new URL("http://passaparola.focolare.org/passaparola.asp");
            connection = (HttpURLConnection) parolaURL.openConnection();
            connection.setRequestMethod("POST");

            String urlParameters =
                    "password=" + URLEncoder.encode("passaparola", "UTF-8") +
                            "&Lang=" + URLEncoder.encode(language, "UTF-8");
            connection.setRequestProperty("Content-Length", "" +
                    Integer.toString(urlParameters.getBytes().length));
            connection.setRequestProperty("Content-Language", "en-US");

            connection.setUseCaches (false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            //Send request
            DataOutputStream wr = new DataOutputStream (
                    connection.getOutputStream ());
            wr.writeBytes (urlParameters);
            wr.flush ();
            wr.close ();

            //Get Response
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer();
            while((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }

            String responseStr = response.toString();
            String parolaDate = "";
            Matcher m = pattern.matcher(responseStr);
            while (m.find()) {
                parolaDate = m.group(1);
            }

            Log.d("html downloadado", responseStr);
            todaysParola = responseStr.split("</STRONG>")[1].split("<STRONG>")[1];
//            todaysParola = todaysParola + "#" + parolaDate;
            Log.d("response web", "response: " + todaysParola);

            parolaFromWeb = new Parola(parolaDate, todaysParola, language);
            rd.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    protected String doInBackground(String... params) {
        Log.d("doInBackground Tag", "começou a executar doInBack ");

        switch (requestType) {
            case PAROLA:
                requestParola((String) this.params.get("language"));
                break;
            case MEDITATION:
                requestMeditations();
                break;
        }

        return "";
    }

    protected void onProgressUpdate(Integer... progress) {
        Log.d("onProgressUp", "Executando onProgressUp");
    }

    protected void onPostExecute(String result) {
        Log.d("onPostExecute", "Executando onPostExecute");

        if (parolaFromWeb != null)
            responseHandler.fireResponse(parolaFromWeb);
        else if (meditationList != null)
            responseHandler.fireResponse(meditationList);
    }



}
