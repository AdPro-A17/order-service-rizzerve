package id.ac.ui.cs.advprog.orderservice.config;

import id.ac.ui.cs.advprog.orderservice.model.Coupon;
import id.ac.ui.cs.advprog.orderservice.model.MenuItem;
import id.ac.ui.cs.advprog.orderservice.repository.CouponRepository;
import id.ac.ui.cs.advprog.orderservice.repository.MenuItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final MenuItemRepository menuItemRepository;
    private final CouponRepository couponRepository;

    @Override
    public void run(String... args) {
        if (menuItemRepository.count() == 0) {
            initializeMenuItems();
        }

        if (couponRepository.count() == 0) {
            initializeCoupons();
        }
    }

    private void initializeMenuItems() {
        UUID adminId = UUID.fromString("11111111-1111-1111-1111-111111111111");

        MenuItem item1 = MenuItem.create(
                "Nasi Goreng Spesial",
                "Nasi goreng dengan telur dan ayam",
                new BigDecimal("25000"),
                true,
                "Main Course",
                4.5,
                adminId
        );

        MenuItem item2 = MenuItem.create(
                "Ayam Bakar",
                "Ayam bakar dengan bumbu spesial",
                new BigDecimal("35000"),
                true,
                "Main Course",
                4.7,
                adminId
        );

        MenuItem item3 = MenuItem.create(
                "Es Teh Manis",
                "Teh manis dingin",
                new BigDecimal("8000"),
                true,
                "Beverage",
                4.2,
                adminId
        );

        MenuItem item4 = MenuItem.create(
                "Es Jeruk",
                "Jeruk segar dingin",
                new BigDecimal("10000"),
                true,
                "Beverage",
                4.3,
                adminId
        );

        menuItemRepository.save(item1);
        menuItemRepository.save(item2);
        menuItemRepository.save(item3);
        menuItemRepository.save(item4);
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