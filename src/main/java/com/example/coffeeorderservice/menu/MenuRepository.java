package com.example.coffeeorderservice.menu;

import java.util.List;
import java.util.Optional;

public interface MenuRepository {

	List<CoffeeMenu> findAll();

	Optional<CoffeeMenu> findById(long menuId);
}
