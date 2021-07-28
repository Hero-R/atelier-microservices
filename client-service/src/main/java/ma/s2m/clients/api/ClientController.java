package ma.s2m.clients.api;

import ma.s2m.clients.domain.Client;
import ma.s2m.clients.service.IClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Client controller.
 **/
@RestController
@RequestMapping("/api/client")
public class ClientController {

    private static final Logger log = LoggerFactory.getLogger(ClientController.class);

    @Autowired
    IClientService clientService;

    // ----- Create a Client ----- //
    @PostMapping
    public ResponseEntity<Client> createClient(@RequestBody Client client) {
        log.info("Creating a client in database.");
        client = clientService.createClient(client);
        return new ResponseEntity<>(client, HttpStatus.CREATED);
    }

    // ----- Get a client by id ----- //
    @GetMapping(value = "/{id}")
    public ResponseEntity<Client> getClient(@PathVariable("id") Long id) {
        log.info("Returning a single client from database.");
        Client client = clientService.getClient(id);
        return new ResponseEntity<>(client, HttpStatus.OK);
    }

    // ----- Get all Clients ----- //
    @GetMapping
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<List<Client>> getAllClients() {
        log.info("Returning all clients from database.");
        List<Client> clients = clientService.getAllClients();
        return new ResponseEntity<>(clients, HttpStatus.OK);
    }

    // ----- Update an existing Client ----- //
    @PutMapping
    public ResponseEntity<Client> updateClient(@RequestBody Client client) {
        log.info("Update a client from database.");
        clientService.updateClient(client);
        return new ResponseEntity<>(client, HttpStatus.OK);
    }

    // ----- Delete a Client ----- //
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Client> deleteClient(@PathVariable("id") Long id) {
        log.info("Delete a client from database.");
        clientService.deleteClient(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
