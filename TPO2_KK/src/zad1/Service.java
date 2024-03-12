/**
 * @author Kłodawski Kamil S24777
 */

package zad1;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Currency;
import java.util.Locale;
import java.util.stream.Collectors;


public class Service {

    String weatherUrl = "https://api.openweathermap.org/data/2.5/weather?q=";
    String weatherApiCode = "&appid=de97488005251f98eb2538921098dedc";
    String exchange = "https://api.exchangerate.host/latest?base=";
    String NBP = "http://api.nbp.pl/api/exchangerates/rates/a/";
    String country;
    Currency curr;

    public Service(String country) {
        this.country = country;

        try {
            Locale loc = new Locale("", getCountryCode(country));
            curr = Currency.getInstance(loc);
        } catch(NullPointerException e) {
            System.out.println("Wrong Country!");
        }

    }

    String getWeather(String city) {
        try {
            InputStream inputStream = new URL(weatherUrl + city + weatherApiCode).openConnection().getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            return bufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException e) {
            System.out.println("Wrong City!");
        }
        return null;
    }

    Double getRateFor(String s) {

        InputStream inputStream = null;
        try {
            inputStream = new URL(exchange + curr + "&symbols=" + s).openConnection().getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String jsonString = bufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));

            JSONParser parser = new JSONParser();
            JSONObject mainWeather = (JSONObject) parser.parse(jsonString);

            return (Double) ((JSONObject) mainWeather.get("rates")).get(s);
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    Double getNBPRate() {
        InputStream inputStream = null;
        try {
            URL url = new URL(NBP + curr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/xml");
            conn.connect();

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(conn.getInputStream());
            doc.getDocumentElement().normalize();

            return Double.valueOf(doc.getElementsByTagName("Mid").item(0).getTextContent());
        } catch (IOException e) {
            if(country.equals("Poland")) return 1.0d;
            else return null;
        } catch (ParserConfigurationException | SAXException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getCountryCode(String countryName) {
        for (String iso : Locale.getISOCountries()) {
            Locale locale = new Locale("", iso);
            if (countryName.equals(locale.getDisplayCountry(Locale.ENGLISH))) {
                return iso;
            }
        }
        return null;
    }
}



