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
        Date outTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (  60 * 60 * 1000));
        ticket.getId();
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber("ABCDEF");
        ticket.setPrice(0);
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setDiscount(true);
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
    public void testAvailabilityParkingSpot() {
        Connection connection = null;
        try {
            connection = dataBaseTestConfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(DBConstants.SELECT_ALL_PARKING);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) {
                // No parking found
                fail("Fail");
            } else {
                boolean firstElement = resultSet.getBoolean(2);
                assertFalse(firstElement);
            }
        } catch (Exception ex) {
          fail("Fail");
        } finally {
            dataBaseTestConfig.closeConnection(connection);
        }
    }

    @Test
    public void testParkingTicketSaved() {
        Connection connection = null;
        try {
            connection = dataBaseTestConfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(DBConstants.SELECT_ALL_TICKET);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) {
                // No parking found
               fail("Fail");
            } else {
                String firstElement = resultSet.getString(3);
                assertEquals("ABCDEF", firstElement);
            }
        } catch (Exception ex) {
            fail("Fail");
        } finally {
            dataBaseTestConfig.closeConnection(connection);
        }
    }
    @Test
    public void testOutTimeTicketSaved() {
        Connection connection = null;
        try {
            connection = dataBaseTestConfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(DBConstants.SELECT_ALL_TICKET);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) {
                // No parking found
                fail("Fail");
            } else {
                Date firstElement = resultSet.getTimestamp(6);
                Timestamp expectedDate = new Timestamp(System.currentTimeMillis());
                assertEquals(expectedDate, firstElement);
            }
        } catch (Exception ex) {
            fail("Fail");
        } finally {
            dataBaseTestConfig.closeConnection(connection);
        }

    }
    @Test
    public void testFareTicketSaved() {
        Connection connection = null;
        try {
            connection = dataBaseTestConfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(DBConstants.SELECT_ALL_TICKET);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) {
                // No parking found
                fail("Fail");
            } else {
                double firstElement = resultSet.getDouble(4);
                assertEquals(0.71, firstElement);
            }
        } catch (Exception ex) {
            fail("Fail");
        } finally {
            dataBaseTestConfig.closeConnection(connection);
        }

    }
}
