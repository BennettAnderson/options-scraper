package com.bennettanderson;

public class Contract {
    private String name = "-";
    private double strike;
    private double price;
    private double bid;
    private double ask;
    private double change;
    private double percentChange;
    private int volume;
    private int openInterest;
    private double volatility;
    private String type;
    private String expiration;

    public Contract() {
    }

    public String getExpiration() {
        return expiration;
    }

    public void setExpiration(String expiration) {
        this.expiration = expiration;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getStrike() {
        return strike;
    }

    public void setStrike(double strike) {
        this.strike = strike;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getBid() {
        return bid;
    }

    public void setBid(double bid) {
        this.bid = bid;
    }

    public double getAsk() {
        return ask;
    }

    public void setAsk(double ask) {
        this.ask = ask;
    }

    public double getChange() {
        return change;
    }

    public void setChange(double change) {
        this.change = change;
    }

    public double getPercentChange() {
        return percentChange;
    }

    public void setPercentChange(double percentChange) {
        this.percentChange = percentChange;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public int getOpenInterest() {
        return openInterest;
    }

    public void setOpenInterest(int openInterest) {
        this.openInterest = openInterest;
    }

    public double getVolatility() {
        return volatility;
    }

    public void setVolatility(double volatility) {
        this.volatility = volatility;
    }

    @Override
    public String toString() {
        return type + " | " + name + " | " + "strike: " + strike + " | " + "price: " + price + " | " + "bid: " + bid +
                " | " + "ask: " + ask + " | " + "change: " + change + "%" + " | " + "percent change: " + percentChange +
                " | " + "volume: " + volume + " | " + "open interest: " + openInterest + " | " + "volatility: " +
                volatility + "%" + " | " + " exp: " + expiration;
    }
}
