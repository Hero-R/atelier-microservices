package ma.hero.sales.api;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ma.hero.sales.domain.Sale;
import ma.hero.sales.service.ISaleService;

/**
 * Sale controller.
 **/
@RestController
@RequestMapping("/api/sale")
public class SaleController {

    @Autowired
    private ISaleService saleService;

    private static final Logger log = LoggerFactory.getLogger(SaleController.class);

    // -------------------get All Sales-------------------------------------------
    @GetMapping
    public ResponseEntity<List<Sale>> findAll() {
        log.info("Returning sale list from database.");
        List<Sale> sales = saleService.getAllSales();
        return new ResponseEntity<>(sales, HttpStatus.OK);
    }

    // -------------------Retrieve Single Sale------------------------------------
    @GetMapping(value = "/{id}")
    public ResponseEntity<Sale> getSale(@PathVariable("id") long id) {
        Sale sale = saleService.getSale(id);
        return new ResponseEntity<>(sale, HttpStatus.OK);
    }

    // -------------------Retrieve Sales By ClientId------------------------------------
    @GetMapping(value = "/client/{id}")
    public ResponseEntity<List<Sale>> getSalesByClient(@PathVariable("id") long id) {
        List<Sale> sales = saleService.getSalesByClient((id));
        return new ResponseEntity<>(sales, HttpStatus.OK);
    }

    // -------------------Create a Sale-------------------------------------------
    @PostMapping
    public ResponseEntity<Sale> createSale(@RequestBody Sale sale) {
        sale = saleService.createSale(sale);
        return new ResponseEntity<>(sale, HttpStatus.CREATED);
    }

    // ----- Update an existing Sale ----- //
    @PutMapping
    public ResponseEntity<Sale> updateSale(@RequestBody Sale sale) {
        log.info("Update a sale from database.");
        saleService.updateSale(sale);
        return new ResponseEntity<>(sale, HttpStatus.OK);
    }

    // ------------------- Delete a Sale-----------------------------------------
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Sale> deleteSale(@PathVariable("id") long id) {
        saleService.deleteSale(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}