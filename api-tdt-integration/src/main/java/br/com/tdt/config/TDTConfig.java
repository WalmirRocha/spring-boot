package br.com.tdt.config;

import org.fosstrak.tdt.TDTEngine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TDTConfig {

	@Bean
	public TDTEngine tdtEngine() throws Exception {
		return new TDTEngine();
	}

}