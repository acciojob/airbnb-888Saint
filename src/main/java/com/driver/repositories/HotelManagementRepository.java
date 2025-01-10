package com.driver.repositories;

import com.driver.model.Booking;
import com.driver.model.Facility;
import com.driver.model.Hotel;
import com.driver.model.User;

import java.util.*;

public class HotelManagementRepository {

    private Map<String, Hotel> hotelDb = new HashMap<>();
    private Map<Integer, User> userDb = new HashMap<>();
    private Map<String, List<Booking>> bookingDb = new HashMap<>();


    public String addHotel(Hotel hotel) {

        if (hotel == null || hotel.getHotelName() == null || hotelDb.containsKey(hotel.getHotelName())) {
            return "FAILURE";
        }

        hotelDb.put(hotel.getHotelName(), hotel);
        return "SUCCESS";
    }

    public Integer addUser(User user) {

        userDb.put(user.getaadharCardNo(), user);
        return user.getaadharCardNo();


    }

    public String getHotelWithMostFacilities() {
        String result = "";
        int maxFacilities = 0;

        for (Hotel hotel : hotelDb.values()) {
            int facilityCount = hotel.getFacilities() != null ? hotel.getFacilities().size() : 0;

            if (facilityCount > maxFacilities ||
                    (facilityCount == maxFacilities && hotel.getHotelName().compareTo(result) < 0)) {
                maxFacilities = facilityCount;
                result = hotel.getHotelName();
            }
        }

        return result.isEmpty() ? "" : result;
    }

    public int bookARoom(Booking booking) {

        String hotelName = booking.getHotelName();

        if (!hotelDb.containsKey(hotelName)) {
            return -1;
        }

        Hotel hotel = hotelDb.get(hotelName);

        if (hotel.getAvailableRooms() < booking.getNoOfRooms()) {
            return -1;
        }

        hotel.setAvailableRooms(hotel.getAvailableRooms() - booking.getNoOfRooms());
        hotelDb.put(hotelName, hotel);

        String bookingId = UUID.randomUUID().toString();
        int amountToBePaid = booking.getNoOfRooms() * hotel.getPricePerNight();

        booking.setBookingId(bookingId);
        booking.setAmountToBePaid(amountToBePaid);

        bookingDb.put(bookingId, List.of(booking));

        return amountToBePaid;
    }

    public int getBookingsByPerson(Integer aadharCard) {
        int count = 0;

        for (List<Booking> bookings : bookingDb.values()) {
            for (Booking booking : bookings) {
                if (Objects.equals(booking.getBookingAadharCard(), aadharCard)) {
                    count++;
                }
            }
        }
        return count;
    }



    public Hotel updateFacilities(List<Facility> newFacilities, String hotelName) {

        if (!hotelDb.containsKey(hotelName)) {
            return null;
        }

        Hotel hotel = hotelDb.get(hotelName);

        Set<Facility> facilitySet = new HashSet<>(hotel.getFacilities());

        for (Facility facility : newFacilities) {
            facilitySet.add(facility);
        }

        hotel.setFacilities(new ArrayList<>(facilitySet));

        hotelDb.put(hotelName, hotel);

        return hotel;
    }
}
