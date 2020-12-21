package com.parkit.parkingsystem.dao;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.repository.ITicketDAO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

public class TicketDAO implements ITicketDAO {

    private static final Logger logger = LogManager.getLogger("TicketDAO");
    public DataBaseConfig dataBaseConfig;

    public TicketDAO(DataBaseConfig config) {
        this.dataBaseConfig = config;
    }

    public boolean saveTicket(Ticket ticket) {
        Connection con = null;
        try {
            con = dataBaseConfig.getConnection();
            try (PreparedStatement ps = con.prepareStatement(DBConstants.SAVE_TICKET)) {
                //ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME, DISCOUNT)
                //ps.setInt(1,ticket.getId());
                ps.setInt(1, ticket.getParkingSpot().getId());
                ps.setString(2, ticket.getVehicleRegNumber());
                ps.setDouble(3, ticket.getPrice());
                ps.setTimestamp(4, new Timestamp(ticket.getInTime().getTime()));
                ps.setTimestamp(5, (ticket.getOutTime() == null) ? null : (new Timestamp(ticket.getOutTime().getTime())));
                ps.setBoolean(6, (ticket.getDiscount()));
                return ps.execute();
            }
        } catch (Exception ex) {
            logger.error("Error fetching next available slot",ex);
        } finally {
            dataBaseConfig.closeConnection(con);
            return false;
        }
    }

    public Ticket getTicket(String vehicleRegNumber) {
        Connection con = null;
        Ticket ticket = null;
        try {
            con = dataBaseConfig.getConnection();
            try (PreparedStatement ps = con.prepareStatement(DBConstants.GET_TICKET)) {
                //ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME, DISCOUNT)
                ps.setString(1, vehicleRegNumber);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    ticket = new Ticket();
                    ParkingSpot parkingSpot = new ParkingSpot(rs.getInt(1), ParkingType.valueOf(rs.getString(7)), false);
                    ticket.setParkingSpot(parkingSpot);
                    ticket.setId(rs.getInt(2));
                    ticket.setVehicleRegNumber(vehicleRegNumber);
                    ticket.setPrice(rs.getDouble(3));
                    ticket.setInTime(rs.getTimestamp(4));
                    ticket.setOutTime(rs.getTimestamp(5));
                    ticket.setDiscount(rs.getBoolean(6));
                }
                dataBaseConfig.closeResultSet(rs);
                dataBaseConfig.closePreparedStatement(ps);
            }
        } catch (Exception ex) {
            logger.error("Error fetching next available slot",ex);
        } finally {
            dataBaseConfig.closeConnection(con);
            return ticket;
        }
    }

    public boolean updateTicket(Ticket ticket) {
        Connection con = null;
        try {
            con = dataBaseConfig.getConnection();
            try (PreparedStatement ps = con.prepareStatement(DBConstants.UPDATE_TICKET)) {
                ps.setDouble(1, ticket.getPrice());
                ps.setTimestamp(2, new Timestamp(ticket.getOutTime().getTime()));
                ps.setInt(3, ticket.getId());
                ps.execute();
                dataBaseConfig.closePreparedStatement(ps);
            }
            return true;
        } catch (Exception ex) {
            logger.error("Error saving ticket info",ex);
        } finally {
            dataBaseConfig.closeConnection(con);
        }
        return false;
    }

    public boolean getExistingTicket(String vehicleRegNumber) {
        Connection connection = null;
        Ticket ticket = null;
        try {
            connection = dataBaseConfig.getConnection();
            try (PreparedStatement preparedStatement = connection.prepareStatement(DBConstants.GET_TICKET)) {
                preparedStatement.setString(1, vehicleRegNumber);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return true;
                    }
                    dataBaseConfig.closeResultSet(resultSet);
                }
                dataBaseConfig.closePreparedStatement(preparedStatement);
            }
        } catch (Exception ex) {
            logger.error("Error search Ticket", ex);
        } finally {
            dataBaseConfig.closeConnection(connection);
        }
        return false;
    }
}
