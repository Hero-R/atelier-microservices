package ma.s2m.clients.repository;

import ma.s2m.clients.domain.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


/**
 * Spring Data repository for the Client entity.
 */
@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
}
