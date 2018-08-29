package br.com.wlr.mongodb;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rest/users")
public class DomainResource {

	@Autowired
    private DomainDaoImpl daoImpl;

    @GetMapping("/all")
    public List<HostingCount> getAll() {
        return daoImpl.getHostingCount();
    }
}