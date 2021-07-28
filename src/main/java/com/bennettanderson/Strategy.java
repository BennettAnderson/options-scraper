package com.bennettanderson;

public class Strategy {
    private double volatilityMax = 48.0;
    private String type = "CALL";


    public boolean isValid(Contract contract) {
        return contract.getVolatility() <= volatilityMax && contract.getType().equals(type);
    }

}
