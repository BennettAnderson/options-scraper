package com.bennettanderson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    private List<Contract> contractList = new ArrayList<>();
    private Document doc;
    private String ticker;

    public static void main(String[] args) {
        Application application = new Application();
        application.run();
    }

    private void run() {
        getTicker();
        populateContracts();
        printContracts();
    }

    private void getTicker() {
        ticker = getUserInput("Stock ticker");
    }

    private void populateContracts() {
        try {
            doc = Jsoup.connect("https://finance.yahoo.com/quote/" + ticker + "/options").get();

            Elements contracts = doc.getElementsByClass("BdT");

            for (Element contract : contracts) {

                Contract newContract = new Contract();

                String name = contract.select(".data-col0 > a").text();
                String strike = contract.select(".data-col2 > a").text();
                String price = contract.select(".data-col3").text();
                String bid = contract.select(".data-col4").text();
                String ask = contract.select(".data-col5").text();
                String change = contract.select(".data-col6 span").text();
                String percentChange = contract.select(".data-col7 > span").text().replaceAll("%", "");
                String volume = contract.select(".data-col8").text().replaceAll(",", "");
                String openInterest = contract.select(".data-col9").text().replaceAll(",", "");
                String volatility = contract.select(".data-col10").text().replaceAll("%", "").replaceAll(",", "");

                if (name.charAt(name.length() - 9) == 'C') {
                    newContract.setType("CALL");
                } else {
                    newContract.setType("PUT");
                }
                newContract.setName(name);
                newContract.setStrike(Double.parseDouble(strike));
                newContract.setPrice(Double.parseDouble(price));
                if (!bid.equals("-")) {
                    newContract.setBid(Double.parseDouble(bid));
                }
                newContract.setAsk(Double.parseDouble(ask));
                newContract.setChange(Double.parseDouble(change));
                if (!percentChange.equals("-")) {
                    newContract.setPercentChange(Double.parseDouble(percentChange));
                }
                if (!volume.equals("-")) {
                    newContract.setVolume(Integer.parseInt(volume));
                }
                if (!openInterest.equals("-")) {
                    newContract.setOpenInterest(Integer.parseInt(openInterest));
                }
                newContract.setVolatility(Double.parseDouble(volatility));

                //set date using Standard Equity Option Convention
                String expiration = name.substring(name.length() - 13, name.length() - 11) + "/" +
                        name.substring(name.length() - 11, name.length() - 9) + "/" + "20" +
                        name.substring(name.length() - 15, name.length() - 13);
                newContract.setExpiration(expiration);

                contractList.add(newContract);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printContracts() {
        System.out.printf("Title: %s\n", doc.title());
        System.out.print("\n________________________________________________________________________________________________________________________________________________");
        System.out.format("\n%6s" + " | " + "%19s" + " | " + "%7s" + " | " + "%7s" + " | " + "%7s" + " | " + "%7s" +
                        " | " + "%7s" + " | " + "%7s" + " | " + "%7s" + " | " + "%7s" + " | " + "%7s" + " | " +
                        "%11s", "Type", "Contract Name", "Strike", "Price", "Bid", "Ask", "Change", "% Change", "Volume",
                "Open Interest", "Volatility", "Expiration");
        System.out.print("\n________________________________________________________________________________________________________________________________________________");
        for (Contract contract : contractList) {
            System.out.format("\n%6s" + " | " + "%19s" + " | " + "%7s" + " | " + "%7s" + " | " + "%7s" + " | " + "%7s" +
                            " | " + "%7s" + " | " + "%8s" + " | " + "%7s" + " | " + "%13s" + " | " + "%10s" + " | " +
                            "%11s", contract.getType(), contract.getName(), contract.getStrike(), contract.getPrice(),
                    contract.getBid(), contract.getAsk(), contract.getChange(), contract.getPercentChange(),
                    contract.getVolume(), contract.getOpenInterest(), contract.getVolatility(), contract.getExpiration());
        }
        System.out.println("hello");
    }

    private String getUserInput(String prompt) {
        System.out.print(prompt + " >>> ");
        return new Scanner(System.in).nextLine();
    }
}

