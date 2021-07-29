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
    private List<Contract> filteredList = new ArrayList<>();
    private Document doc;
    private String ticker;
    private String currentPrice;
    private List<String> dates = new ArrayList<>();

    public static void main(String[] args) {
        Application application = new Application();
        application.run();
    }

    private void run() {
        getTicker();
        getBasicInfo();
        populateContracts();
        setFilteredList();
        printContracts();
    }

    private void getTicker() {
        ticker = getUserInput("Stock ticker");
    }

    private void getBasicInfo() {
        try {
            doc = Jsoup.connect("https://finance.yahoo.com/quote/" + ticker + "/options").get();
            currentPrice = doc.select("#quote-header-info > div:eq(2) > div:eq(0) > div:eq(0) > span:eq(0)").text();

            Elements dateValues = doc.select("#Col1-1-OptionContracts-Proxy > section:eq(0) > div:eq(0) > div:eq(0) > select:eq(0) > option");
            for (Element element : dateValues) {
                dates.add(element.attr("value"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void populateContracts() {
        try {

            for (String date : dates) {
                Document thisDoc = Jsoup.connect("https://finance.yahoo.com/quote/" + ticker + "/options?date=" + date).get();

                Elements contracts = thisDoc.getElementsByClass("BdT");

                for (Element contract : contracts) {

                    Contract newContract = new Contract();

                    String name = contract.select(".data-col0 > a").text();
                    String strike = contract.select(".data-col2 > a").text().replaceAll(",", "");
                    String price = contract.select(".data-col3").text().replaceAll(",", "");
                    String bid = contract.select(".data-col4").text().replaceAll(",", "");
                    String ask = contract.select(".data-col5").text().replaceAll(",", "");
                    String change = contract.select(".data-col6 span").text().replaceAll(",", "");
                    String percentChange = contract.select(".data-col7 > span").text().replaceAll("%", "").replaceAll(",", "");
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
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean strategy(Contract contract) {
        return contract.getVolatility() <= 100 && contract.getVolatility() >= 60 && contract.getStrike() > contract.getPrice();
    }

    private void setFilteredList() {
        for (Contract contract : contractList) {
            if (strategy(contract)) {
                filteredList.add(contract);
            }
        }
    }

    private void printContracts() {
        System.out.printf("Title: %s\n", doc.title());
        System.out.println("Stock price: " + currentPrice);
        System.out.print("\n________________________________________________________________________________________________________________________________________________");
        System.out.format("\n%6s" + " | " + "%19s" + " | " + "%7s" + " | " + "%7s" + " | " + "%7s" + " | " + "%7s" +
                        " | " + "%7s" + " | " + "%7s" + " | " + "%7s" + " | " + "%7s" + " | " + "%7s" + " | " +
                        "%11s", "Type", "Contract Name", "Strike", "Price", "Bid", "Ask", "Change", "% Change", "Volume",
                "Open Interest", "Volatility", "Expiration");
        System.out.print("\n________________________________________________________________________________________________________________________________________________");
        for (Contract contract : filteredList) {
            System.out.format("\n%6s" + " | " + "%19s" + " | " + "%7s" + " | " + "%7s" + " | " + "%7s" + " | " + "%7s" +
                            " | " + "%7s" + " | " + "%8s" + " | " + "%7s" + " | " + "%13s" + " | " + "%10s" + " | " +
                            "%11s", contract.getType(), contract.getName(), contract.getStrike(), contract.getPrice(),
                    contract.getBid(), contract.getAsk(), contract.getChange(), contract.getPercentChange(),
                    contract.getVolume(), contract.getOpenInterest(), contract.getVolatility(), contract.getExpiration());
        }
    }

    private String getUserInput(String prompt) {
        System.out.print(prompt + " >>> ");
        return new Scanner(System.in).nextLine();
    }
}
//html > body > div:eq(1) > div > div > div:eq(1) > div > div:eq(2) > div > div > div:eq(5) > div > div > div > div:eq(3) > div:eq(1) > div:eq(1) > span:eq(1)
//#quote-header-info > div.My\(6px\).Pos\(r\).smartphone_Mt\(6px\) > div.D\(ib\).Va\(m\).Maw\(65\%\).Ov\(h\) > div.D\(ib\).Mend\(20px\) > span.Trsdu\(0\.3s\).Fw\(b\).Fz\(36px\).Mb\(-4px\).D\(ib\)