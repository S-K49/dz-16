package tests;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseBooking {
    private BookingId booking;
    private Integer bookingid;
}
