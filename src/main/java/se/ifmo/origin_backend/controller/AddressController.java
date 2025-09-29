package se.ifmo.origin_backend.controller;

import org.springframework.web.bind.annotation.*;
import se.ifmo.origin_backend.dto.AddressDTO;
import se.ifmo.origin_backend.model.Address;
import se.ifmo.origin_backend.service.AddressService;

import java.util.List;

@RestController
@RequestMapping("/addrs")
public class AddressController {
    private final AddressService service;

    public AddressController(AddressService service) {
        this.service = service;
    }

    @GetMapping
    public List<Address> getAllAddresses() {
        return service.getAddresses();
    }

    @GetMapping("/{id}")
    public Address getAddressById(@PathVariable Long id) {
        return service.getAddressById(id);
    }

    @PostMapping
    public void addAddress(@RequestBody AddressDTO dto) {
        service.addAddress(dto);
    }

    @PutMapping("/{id}")
    public void updateAddress(@PathVariable Long id, @RequestBody AddressDTO dto) {
        service.updateAddress(id, dto);
    }

    @DeleteMapping("/{id}")
    public void deleteAddress(@PathVariable Long id) {
        service.deleteAddress(id);
    }
}
