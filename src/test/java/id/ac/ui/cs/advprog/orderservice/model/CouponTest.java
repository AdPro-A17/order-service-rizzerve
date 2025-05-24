// package id.ac.ui.cs.advprog.orderservice.model;

// import org.junit.jupiter.api.Test;

// import java.math.BigDecimal;
// import java.time.LocalDateTime;
// import java.util.UUID;

// import static org.junit.jupiter.api.Assertions.*;

// class CouponTest {

//     @Test
//     void create_ShouldCreateCouponWithCorrectValues() {
//         String code = "TEST10";
//         BigDecimal discountAmount = null;
//         Integer discountPercentage = 10;
//         BigDecimal minimumPurchase = new BigDecimal("20000");
//         LocalDateTime validFrom = LocalDateTime.now().minusDays(1);
//         LocalDateTime validUntil = LocalDateTime.now().plusDays(30);
//         boolean active = true;
//         UUID menuItemId = null;
//         UUID createdBy = UUID.randomUUID();

//         Coupon coupon = Coupon.create(
//                 code,
//                 discountAmount,
//                 discountPercentage,
//                 minimumPurchase,
//                 validFrom,
//                 validUntil,
//                 active,
//                 menuItemId,
//                 createdBy
//         );

//         assertEquals(code, coupon.getCode());
//         assertEquals(discountAmount, coupon.getDiscountAmount());
//         assertEquals(discountPercentage, coupon.getDiscountPercentage());
//         assertEquals(minimumPurchase, coupon.getMinimumPurchase());
//         assertEquals(validFrom, coupon.getValidFrom());
//         assertEquals(validUntil, coupon.getValidUntil());
//         assertEquals(active, coupon.isActive());
//         assertEquals(menuItemId, coupon.getMenuItemId());
//         assertEquals(createdBy, coupon.getCreatedBy());
//     }
// }