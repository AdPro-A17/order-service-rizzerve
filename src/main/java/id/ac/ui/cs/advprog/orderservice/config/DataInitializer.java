package id.ac.ui.cs.advprog.orderservice.config;

import id.ac.ui.cs.advprog.orderservice.model.Coupon;
import id.ac.ui.cs.advprog.orderservice.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    
    private final CouponRepository couponRepository;

    @Override
    public void run(String... args) {
        

        if (couponRepository.count() == 0) {
            initializeCoupons();
        }
    }

    
    private void initializeCoupons() {
        UUID adminId = UUID.fromString("11111111-1111-1111-1111-111111111111");

        Coupon coupon1 = Coupon.create(
                "WELCOME10",
                null,
                10,
                new BigDecimal("20000"),
                LocalDateTime.now().minusDays(10),
                LocalDateTime.now().plusDays(30),
                true,
                null,
                adminId
        );

        Coupon coupon2 = Coupon.create(
                "DISC5000",
                new BigDecimal("5000"),
                null,
                new BigDecimal("30000"),
                LocalDateTime.now().minusDays(10),
                LocalDateTime.now().plusDays(30),
                true,
                null,
                adminId
        );

        couponRepository.save(coupon1);
        couponRepository.save(coupon2);
    }
}