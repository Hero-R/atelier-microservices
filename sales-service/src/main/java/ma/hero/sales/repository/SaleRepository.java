package ma.hero.sales.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ma.hero.sales.domain.Sale;


/**
 * Spring Data  repository for the Sale entity.
 */
@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {

    List<Sale> findByClientId(Long clientId);

}