package br.com.tdt.api.rest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.epcglobalinc.tdt.LevelTypeList;
import org.fosstrak.tdt.TDTEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rest/tdt")
public class TDTResource {

	@Autowired
	private TDTEngine engine;

	/**
	 * 	Hexadeciaml : 30700048440663802E185523
	 *  Binary: 00110000 011 100 000000000001001000010001-00000001100110010000000000000101110000110000101010100100011
   
	 *  Cabeçalho, que é de 8 bits e é comum para todas as tags SGTIN-96
	 *  Filtro, que é de três bits e especifica se o objeto marcado é um item, caso ou palete
	 *  Partição, que é de três bits e indica como os campos subseqüentes são divididos para obter os dados corretos para cada
	 *  Prefixo da empresa, que é de 20 a 40 bits (dependendo da partição) e contém o prefixo da empresa EAN.UCC.
	 *  Referência do item, que é de 24 a 4 bits (dependendo da partição) e contém o número de referência do item GTIN do item
	 *  Número de série, que tem 38 bits e contém o número de série exclusivo do item
	 * 
	 * @param tagHexa
	 * @param levelTypeListOut
	 * @return
	 * @throws IOException
	 * @throws JAXBException
	 */
	@GetMapping("/{tagHexa}")
	public ResponseEntity<?> converterTag(@PathVariable("tagHexa") String tagHexa) throws IOException, JAXBException {

		Map<String, String> params = new HashMap<String, String>();
		
		String origemBinary = engine.hex2bin(tagHexa);

		String DECIMA =engine.bin2dec(engine.hex2bin(tagHexa));
		
		String length = engine.bin2dec(origemBinary.substring(0, 8));
		String filter = engine.bin2dec(origemBinary.substring(8, 11));
		String partition = engine.bin2dec(origemBinary.substring(11, 14));
		
		int tamanho=  14 + 24;
		String prefixCompany = engine.bin2dec(origemBinary.substring(14, tamanho));
		
		int tamanhoItem = tamanho+20;
		String itemReferencia = engine.bin2dec(origemBinary.substring(tamanho, tamanhoItem));

		int serialLength = Integer.parseInt(length);
		String serial = engine.bin2dec(origemBinary.substring(tamanhoItem, origemBinary.length()-1));
		
		/*
		String item = engine.bin2dec(orig.substring(20, 32));*/
		
		params.put("taglength", length);
		params.put("filter",filter);
		params.put("gs1companyprefixlength", "7");
		
		
		/*
		 * String header = engine.bin2dec(orig.substring(0, 8));
		
		8 bits 3 bits 3 bits 20-40 bits 24-4 bits 38 bits*/
		
/*		params.put("taglength", "96");
		params.put("filter", "3");
		params.put("gs1companyprefixlength", "7");*/
		
/*		
		
		String value = 
		
		String s = engine.convert(orig, params, levelTypeList);*/
		

		
		String s = engine.convert(origemBinary, params, LevelTypeList.TAG_ENCODING);
		
		return ResponseEntity.ok(s);
	}

	public int hex2decimal(String s) {
		String digits = "0123456789ABCDEF";
		s = s.toUpperCase();
		int val = 0;
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			int d = digits.indexOf(c);
			val = 16 * val + d;
		}
		return val;
	}
}