package id.ac.ui.cs.advprog.orderservice.repository;

import id.ac.ui.cs.advprog.orderservice.model.Checkout;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public class CheckoutRepository {
    private final Map<UUID, Checkout> checkouts = new HashMap<>();

    public Checkout save(Checkout checkout) {
        if (checkout.getId() == null) {
            checkout.setId(UUID.randomUUID());
        }
        checkouts.put(checkout.getId(), checkout);
        return checkout;
    }

    public Checkout findById(UUID id) {
        return checkouts.get(id);
    }

    public List<Checkout> findAll() {
        return new ArrayList<>(checkouts.values());
    }

    public void delete(UUID id) {
        checkouts.remove(id);
    }
}