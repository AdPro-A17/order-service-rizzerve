package id.ac.ui.cs.advprog.orderservice.config;

import id.ac.ui.cs.advprog.orderservice.model.Coupon;
import id.ac.ui.cs.advprog.orderservice.model.MenuItem;
import id.ac.ui.cs.advprog.orderservice.repository.CouponRepository;
import id.ac.ui.cs.advprog.orderservice.repository.MenuItemRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DataInitializerTest {

    @Mock
    private MenuItemRepository menuItemRepository;

    @Mock
    private CouponRepository couponRepository;

    @InjectMocks
    private DataInitializer dataInitializer;

    @Test
    void run_ShouldInitializeMenuItemsAndCoupons_WhenRepositoriesAreEmpty() {
        MockitoAnnotations.openMocks(this);
        when(menuItemRepository.count()).thenReturn(0L);
        when(couponRepository.count()).thenReturn(0L);

        dataInitializer.run();

        verify(menuItemRepository, times(4)).save(any(MenuItem.class));
        verify(couponRepository, times(2)).save(any(Coupon.class));
    }

    @Test
    void run_ShouldNotInitializeMenuItems_WhenMenuItemRepositoryIsNotEmpty() {
        MockitoAnnotations.openMocks(this);
        when(menuItemRepository.count()).thenReturn(1L);
        when(couponRepository.count()).thenReturn(0L);

        dataInitializer.run();

        verify(menuItemRepository, never()).save(any(MenuItem.class));
        verify(couponRepository, times(2)).save(any(Coupon.class));
    }

    @Test
    void run_ShouldNotInitializeCoupons_WhenCouponRepositoryIsNotEmpty() {
        MockitoAnnotations.openMocks(this);
        when(menuItemRepository.count()).thenReturn(0L);
        when(couponRepository.count()).thenReturn(1L);

        dataInitializer.run();

        verify(menuItemRepository, times(4)).save(any(MenuItem.class));
        verify(couponRepository, never()).save(any(Coupon.class));
    }

    @Test
    void run_ShouldNotInitializeAnything_WhenBothRepositoriesAreNotEmpty() {
        MockitoAnnotations.openMocks(this);
        when(menuItemRepository.count()).thenReturn(1L);
        when(couponRepository.count()).thenReturn(1L);

        dataInitializer.run();

        verify(menuItemRepository, never()).save(any(MenuItem.class));
        verify(couponRepository, never()).save(any(Coupon.class));
    }
}
