package ma.hero.sales.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ma.hero.sales.domain.Sale;
import ma.hero.sales.repository.SaleRepository;
import ma.hero.sales.service.ISaleService;

/**
 * Service Implementation for managing {@link Sale}.
 */
@Service
@Transactional
public class SaleService implements ISaleService {

    private final Logger log = LoggerFactory.getLogger(SaleService.class);

    private final SaleRepository saleRepository;

    public SaleService(SaleRepository saleRepository) {
        this.saleRepository = saleRepository;
    }

    /**
     * Save a sale.
     * @param sale  the entity to save.
     * @return the persisted entity.
     */
    @Override
    public Sale createSale(Sale sale) {
        log.debug("Request to save Sale : {}", sale);
        return saleRepository.save(sale);
    }

    /**
     * Get all the sales.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Sale> getAllSales() {
        log.debug("Request to get all Sales");
        return saleRepository.findAll();
    }

    /**
     * Get one sale by id.
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Sale getSale(Long id) {
        log.debug("Request to get Sale : {}", id);
        return saleRepository.findById(id).orElse(null);
    }

    /**
     * Get one sale by client id.
     *
     * @param clientId  the id of the client.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Sale> getSalesByClient(Long clientId){
        log.debug("Request to get Sales By Client ID : {}", clientId);
        return saleRepository.findByClientId(clientId);
    }

    /**
     * Update a sale.
     * @param sale the entity to update.
     * @return the updated entity.
     */
    @Override
    public Sale updateSale(Sale sale) {
        log.debug("Request to update Sale : {}", sale);
        if (!saleRepository.existsById(sale.getId())) {
            return null;
        }
        return saleRepository.save(sale);
    }

    /**
     * Delete the sale by id.
     * @param id the id of the entity.
     */
    @Override
    public void deleteSale(Long id) {
        log.debug("Request to delete Sale : {}", id);
        saleRepository.deleteById(id);
    }
}