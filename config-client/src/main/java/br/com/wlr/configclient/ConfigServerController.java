package br.com.wlr.configclient;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Component
@RefreshScope
@RestController
public class ConfigServerController {

	 	@Value("${host.ip:defaut}")
	    String hostIp;

	    @Value("${server.port:-1}")	    
	    int port;

	    @Value("${configuration.projectName:defaut}")
	    String projectName;

	    @RequestMapping(value = "/", produces = "application/json")
	    public List<String> index(){
	        List<String> env = Arrays.asList(
	                "host.ip is: " + hostIp,
	                "server.port is: " + port,
	                "configuration.projectName is: " + projectName
	        );
	        return env;
	    }
	    
	    
}