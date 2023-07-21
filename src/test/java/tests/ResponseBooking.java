package tests;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseBooking {
    private Object booking;
    private BookingId bookingid;
}
