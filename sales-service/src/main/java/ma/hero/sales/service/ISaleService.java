package ma.hero.sales.service;

import java.util.List;

import ma.hero.sales.domain.Sale;

public interface ISaleService {

    /**
     * Save a sale.
     *
     * @param sale
     *            the entity to save.
     * @return the persisted entity.
     */
    Sale createSale(Sale sale);

    /**
     * Get all the sales.
     *
     * @return the list of entities.
     */
    List<Sale> getAllSales();

    /**
     * Get one sale by id.
     *
     * @param id
     *            the id of the entity.
     * @return the entity.
     */
    Sale getSale(Long id);

    /**
     * Get one sale by client id.
     *
     * @param clientId
     *            the id of the client.
     * @return the list of entities.
     */
    List<Sale> getSalesByClient(Long clientId);

    /**
     * Update a sale.
     * @param sale the entity to update.
     * @return the updated entity.
     */
    Sale updateSale(Sale sale);

    /**
     * Delete the sale by id.
     *
     * @param id
     *            the id of the entity.
     */
    void deleteSale(Long id);
}