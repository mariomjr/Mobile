package com.org.marvelapp.services;

import com.org.marvelapp.model.Personagem;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by MarioJr on 01/06/2015.
 */
public class PersonagemService {

    private final String apiKeyPublic = "6b9be8c8ad38d3893cd06747e2c9fcac";
    private final String apiKeyPrivate = "13cc4622f44b21a5758732fa678b64391b236a27";
    private final String listPersonagensUrl= "http://gateway.marvel.com:80/v1/public/characters?";
    private final String ts = "mario";

    public List<Personagem> listPersonagens(Integer offset) throws Exception {

        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        pairs.add(new BasicNameValuePair("ts", ts));
        pairs.add(new BasicNameValuePair("orderBy", "name"));
        pairs.add(new BasicNameValuePair("limit", String.valueOf(30)));
        pairs.add(new BasicNameValuePair("offset", String.valueOf(offset)));
        pairs.add(new BasicNameValuePair("apikey", apiKeyPublic));

        StringBuilder hashStr = new StringBuilder();
        hashStr.append(ts);
        hashStr.append(apiKeyPrivate);
        hashStr.append(apiKeyPublic);

        String apiPublicPrivate = hashStr.toString();

        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(apiPublicPrivate.getBytes());

        byte byteData[] = md.digest();

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
            sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }
        System.out.println(sb.toString());
        pairs.add(new BasicNameValuePair("hash", sb.toString()));


        String paramString = URLEncodedUtils.format(pairs, "utf-8");

        HttpGet requisicao = new HttpGet(listPersonagensUrl+""+paramString);

        requisicao.setHeader("Accept","application/json");
        requisicao.setHeader("Content-Type","application/json");
        HttpClient clienthttp = new DefaultHttpClient();

        HttpResponse resposta = clienthttp.execute(requisicao);

        if(resposta.getStatusLine().getStatusCode() == 200) {
            System.out.println(resposta.getStatusLine().getStatusCode());
            InputStream input = resposta.getEntity().getContent();

            String retorno1 = convertStreamToString(input);
            System.out.println(retorno1);

            JSONObject jsObject = new JSONObject(retorno1);

            List<Object> listObjects = getListFromJsonObject(jsObject);
            List<Personagem> listPersonagens = listPersonagensByListObjects(listObjects);

            return listPersonagens;
        }
        return null;
    }

    private List<Personagem> listPersonagensByListObjects(List<Object> listObjects) {

        List<Personagem> listPersonagens = new ArrayList<Personagem>();
        for(Object a :listObjects){
            List<Object> results = (List<Object>) a;
            if(results.get(0) instanceof String && ((String)results.get(0)).equals("data")){
                List<Object> data = (List<Object>) results.get(1);
                for(Object datas : data){
                    List<Object> resultData = (List<Object>) datas;
                    if(resultData.get(0) instanceof String && ((String)resultData.get(0)).equals("results")){
                        List<Object> personagens = (List<Object>) resultData.get(1);
                        for(Object personagem : personagens){
                            Personagem personagemAdd = new Personagem();

                            for(Object infoPerso : (List<Object>)personagem) {

                                List<Object> info = (List<Object>)infoPerso;

                                if (info.get(0) instanceof String && ((String) info.get(0)).equals("id")) {
                                    personagemAdd.setId(Long.parseLong(String.valueOf((Integer) info.get(1))));
                                }
                                if (info.get(0) instanceof String && ((String) info.get(0)).equals("name")) {
                                    personagemAdd.setName(String.valueOf(info.get(1)));
                                }
                                if (info.get(0) instanceof String && ((String) info.get(0)).equals("description")) {
                                    personagemAdd.setDescription(String.valueOf(info.get(1)));
                                }
                            }
                            listPersonagens.add(personagemAdd);
                        }
                    }
                }
            }
        }

        return listPersonagens;
    }

    public static List<Object> getListFromJsonObject(JSONObject jObject) throws JSONException {
        List<Object> returnList = new ArrayList<Object>();

        Iterator<String> keys = jObject.keys();

        List<String> keysList = new ArrayList<String>();
        while (keys.hasNext()) {
            keysList.add(keys.next());
        }
        Collections.sort(keysList);

        for (String key : keysList) {
            List<Object> nestedList = new ArrayList<Object>();
            nestedList.add(key);
            nestedList.add(convertJsonItem(jObject.get(key)));
            returnList.add(nestedList);
        }

        return returnList;
    }

    public static Object convertJsonItem(Object o) throws JSONException {
        if (o == null) {
            return "null";
        }

        if (o instanceof JSONObject) {
            return getListFromJsonObject((JSONObject) o);
        }

        if (o instanceof JSONArray) {
            return getListFromJsonArray((JSONArray) o);
        }

        if (o.equals(Boolean.FALSE) || (o instanceof String &&
                ((String) o).equalsIgnoreCase("false"))) {
            return false;
        }

        if (o.equals(Boolean.TRUE) || (o instanceof String && ((String) o).equalsIgnoreCase("true"))) {
            return true;
        }

        if (o instanceof Number) {
            return o;
        }

        return o.toString();
    }

    public static List<Object> getListFromJsonArray(JSONArray jArray) throws JSONException {
        List<Object> returnList = new ArrayList<Object>();
        for (int i = 0; i < jArray.length(); i++) {
            returnList.add(convertJsonItem(jArray.get(i)));
        }
        return returnList;
    }

    public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }

    private static String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
