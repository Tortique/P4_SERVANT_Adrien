package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class FareCalculatorService {


    public void calculateFare(Ticket ticket){
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }
        double duration = calculateDuration(ticket);
        double calculation;

        switch (ticket.getParkingSpot().getParkingType()) {
            case CAR: {
                calculation = (duration - 0.5) * Fare.CAR_RATE_PER_HOUR;
                break;
            }
            case BIKE: {
                calculation = (duration - 0.5) * Fare.BIKE_RATE_PER_HOUR;
                break;
            }
            default:
                throw new IllegalArgumentException("Unknown Parking Type");
        }

        if (duration <= 0.5) {
            ticket.setPrice(0);
        } else {
            if(ticket.getDiscount() == true) {
                ticket.setPrice(Math.round((calculation - (calculation * 0.05))* 100.0) / 100.0);
            } else {
                ticket.setPrice(Math.round(calculation* 100.0) / 100.0);
            }
        }

    }
    public double calculateDuration (Ticket ticket) {
        Instant inH = ticket.getInTime().toInstant();
        Instant outH = ticket.getOutTime().toInstant();

        LocalDateTime inHour = LocalDateTime.ofInstant(inH, ZoneId.systemDefault());
        LocalDateTime outHour = LocalDateTime.ofInstant(outH, ZoneId.systemDefault());

        Duration duration = Duration.between(inHour, outHour);

        double dr = (double)duration.getSeconds() / 3600;
        return dr;
    }
}