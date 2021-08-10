package ma.hero.clients.api.feign;

import ma.hero.clients.dto.SaleDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "sales-service", url = "http://localhost:9001")
public interface SaleClient {

    @GetMapping("/api/sale/client/{id}")
    List<SaleDto> getClientSales(@PathVariable("id") Long clientId);
}
