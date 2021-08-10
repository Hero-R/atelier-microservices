package ma.hero.clients.service;

import ma.hero.clients.domain.Client;

import java.util.List;

public interface IClientService {

    /**
     * Save a client.
     * @param client the entity to save.
     * @return the persisted entity.
     */
    Client createClient(Client client);

    /**
     * Get one client by id.
     * @param id the id of the entity Client.
     * @return the entity.
     */
    Client getClient(Long id);

    /**
     * Get all the clients.
     * @return the list of entities.
     */
    List<Client> getAllClients();

    /**
     * Update a client.
     * @param client the entity to update.
     * @return the updated entity.
     */
    Client updateClient(Client client);

    /**
     * Delete the client by id.
     * @param id the id of the entity.
     */
    void deleteClient(Long id);
}
