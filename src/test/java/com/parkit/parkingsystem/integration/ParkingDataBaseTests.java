package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import static org.junit.jupiter.api.Assertions.*;

import com.parkit.parkingsystem.service.FareCalculatorService;
import org.junit.jupiter.api.*;
import java.sql.*;
import java.util.Date;


public class ParkingDataBaseTests {

    private static final DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;
    private static FareCalculatorService fareCalculatorService;
    private static Date outTime;

    @BeforeAll
    private static void setUp() throws Exception {
        parkingSpotDAO = new ParkingSpotDAO(dataBaseTestConfig);
        ticketDAO = new TicketDAO(dataBaseTestConfig);
        dataBasePrepareService = new DataBasePrepareService();
        dataBasePrepareService.clearDataBaseEntries();
        fareCalculatorService = new FareCalculatorService();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        Ticket ticket = new Ticket();
        Date inTime = new Date();
        outTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (  60 * 60 * 1000));
        ticket.getId();
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber("ABCDEF");
        ticket.setPrice(0);
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setDiscount(ticketDAO.getExistingTicket("ABCDEF"));
        ticketDAO.saveTicket(ticket);
        parkingSpotDAO.updateParking(parkingSpot);
        Ticket fare = ticketDAO.getTicket("ABCDEF");
        fareCalculatorService.calculateFare(fare);
        ticketDAO.updateTicket(fare);
    }

    @BeforeEach
    private void setUpPerTest() throws Exception {
    }

    @AfterAll
    private static void tearDown() {

    }

    @Test
    public void availabilityParkingSpotTest() {
        Connection connection = null;
        try {
            connection = dataBaseTestConfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(DBConstants.SELECT_ALL_PARKING);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) {
                fail("No ticket found");
            } else {
                boolean firstElement = resultSet.getBoolean(2);
                assertFalse(firstElement);
            }
        } catch (Exception ex) {
          fail("Error on availabilityParkingSpotTest");
        } finally {
            dataBaseTestConfig.closeConnection(connection);
        }
    }

    @Test
    public void parkingTicketSavedTest() {
        Connection connection = null;
        try {
            connection = dataBaseTestConfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(DBConstants.SELECT_ALL_TICKET);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) {
               fail("No ticket found");
            } else {
                String firstElement = resultSet.getString(3);
                assertEquals("ABCDEF", firstElement);
            }
        } catch (Exception ex) {
            fail("Error on parkingTicketSavedTest");
        } finally {
            dataBaseTestConfig.closeConnection(connection);
        }
    }
    @Test
    public void outTimeTicketSavedTest() {
        Connection connection = null;
        try {
            connection = dataBaseTestConfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(DBConstants.SELECT_ALL_TICKET);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) {
                fail("No ticket found");
            } else {
                Date firstElement = resultSet.getTimestamp(6);
                Timestamp expectedDate = new Timestamp(1000 *((outTime.getTime()+500)/1000));
                assertEquals(expectedDate, firstElement);
            }
        } catch (Exception ex) {
            fail("Error on outTimeTicketSavedTest");
        } finally {
            dataBaseTestConfig.closeConnection(connection);
        }

    }
    @Test
    public void fareTicketSavedTest() {
        Connection connection = null;
        try {
            connection = dataBaseTestConfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(DBConstants.SELECT_ALL_TICKET);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) {

                fail("No ticket found");
            } else {
                double firstElement = resultSet.getDouble(4);
                assertEquals(0.75, firstElement);
            }
        } catch (Exception ex) {
            fail("Error on fareTicketSavedTest");
        } finally {
            dataBaseTestConfig.closeConnection(connection);
        }

    }
    @Test
    public void getDiscountTest() {
        Connection connection = null;
        try {
            connection = dataBaseTestConfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(DBConstants.SELECT_ALL_TICKET);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) {
                fail("No ticket found");
            } else {
                Boolean firstElement = resultSet.getBoolean(7);
                assertFalse(firstElement);
            }
        } catch (Exception ex) {
            fail("Error on getDiscountTest");
        } finally {
            dataBaseTestConfig.closeConnection(connection);
        }
    }
}
