package zad1;

import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

public class GUI extends JFrame {


    private JPanel panel1;
    private JTextField CityField;
    private JTextField CountryField;
    private JButton weatherButton;
    private JLabel weatherTemp;
    private JLabel weatherHumid;
    private JLabel weatherPress;
    private JLabel weatherWind;
    private JTextField CurrencyField;
    private JLabel currencyRatio;
    private JButton currencyButton;
    private JLabel NBPRatio;
    private JButton NBPButton;
    private JPanel webPanel;
    private JButton siteButton;

    private JFXPanel jfxPanel;

    private static final DecimalFormat df = new DecimalFormat("0.00");

    private String site = "";
    public GUI() {

        jfxPanel = new JFXPanel();
        Platform.runLater(this::createJFXContent);
        webPanel.add(jfxPanel);

        this.setVisible(true);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.add(panel1);
        this.pack();
        weatherButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Service s = new Service(CountryField.getText());

                Gson gson = new Gson();
                Weather weather = gson.fromJson(s.getWeather(CityField.getText()), Weather.class);

                weather.main.temp -= 273.15d;

                weatherTemp.setText(String.valueOf(df.format(weather.main.temp)));
                weatherHumid.setText(String.valueOf(weather.main.humidity));
                weatherPress.setText(String.valueOf(weather.main.pressure));
                weatherWind.setText(String.valueOf(weather.wind.speed));
            }
        });
        currencyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Service s = new Service(CountryField.getText());
                Double tmp = s.getRateFor(CurrencyField.getText());
                if(tmp == null) currencyRatio.setText("Unknown");
                else currencyRatio.setText(String.valueOf(tmp));
            }
        });
        NBPButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Service s = new Service(CountryField.getText());
                NBPRatio.setText(String.valueOf(s.getNBPRate()));
            }
        });
        siteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setSite("https://en.wikipedia.org/wiki/" + CityField.getText());
                Platform.runLater(GUI.this::createJFXContent);
            }
        });
    }

    private void createJFXContent() {
        WebView webView = new WebView();
        webView.getEngine().load(site);
        Scene scene = new Scene(webView);
        jfxPanel.setScene(scene);
    }

    private void setSite(String site) {
        this.site = site;
    }


}

class Weather {
    MainWeatherInfo main;
    MainWindInfo wind;

}

class MainWeatherInfo {
    double temp;
    int humidity;
    int pressure;
}
class MainWindInfo {
    double speed;
}



