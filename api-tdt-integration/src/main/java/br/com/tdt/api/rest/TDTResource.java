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
	 * 	Hexadeciaml : 3074257BF400B7800004CB2F
	 *  Binary: 001100000111010000100101011110111111010000000000101101111000000000000000000001001100101100101111
  
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

		gerarInit();
		
		Map<String, String> params = new HashMap<String, String>();
		
		String origemBinary = engine.hex2bin(tagHexa);

		String length = binaryHeaders.get(origemBinary.substring(0, 8));
		String filter = engine.bin2dec(origemBinary.substring(8, 11));
		String partition = engine.bin2dec(origemBinary.substring(11, 14));
		//gtin=80614141123458;serial=6789
		//3074257bf7194e4000001a85
		PartitionValue partitionValue = partitionValueMap.get(partition);
		
		String gs1companyprefixlength = partitionValue.getCompanyPrefix().getDigits();
		
		params.put("taglength", length);
		params.put("filter",filter);
		params.put("gs1companyprefixlength", gs1companyprefixlength);
		
		String s = engine.convert(origemBinary, params, LevelTypeList.TAG_ENCODING);
		
		return ResponseEntity.ok(s);
	}
	
	@GetMapping("/info/dec/{tagHexa}")
	public ResponseEntity<?> infoDec(@PathVariable("tagHexa") String tagHexa) throws IOException, JAXBException {

		gerarInit();
		
		Map<String, String> result = new HashMap<String, String>();
		
		String origemBinary = engine.hex2bin(tagHexa);

		String length = binaryHeaders.get(origemBinary.substring(0, 8));
		String filter = engine.bin2dec(origemBinary.substring(8, 11));
		String partition = engine.bin2dec(origemBinary.substring(11, 14));

		PartitionValue partitionValue = partitionValueMap.get(partition);
		
		int tamanhoCompanyprefix = 14 + Integer.parseInt(partitionValue.getCompanyPrefix().getBits());
		String companyPrefix = engine.bin2dec(origemBinary.substring(14,  tamanhoCompanyprefix));
		
		int tamanhoItemReference = tamanhoCompanyprefix + Integer.parseInt(partitionValue.getItemReference().getBits());
		String itemReference = engine.bin2dec(origemBinary.substring(tamanhoCompanyprefix,  tamanhoItemReference));
		
		String serial = engine.bin2dec(origemBinary.substring(tamanhoItemReference,  origemBinary.length()));
		
		result.put("filter",filter);
		result.put("Partition",filter);
		result.put("Company prefix", companyPrefix);
		result.put("Item Reference prefix", itemReference);
		result.put("Serial", serial);
		
		return ResponseEntity.ok(result);
	}
	
	@GetMapping("/info/bin/{tagHexa}")
	public ResponseEntity<?> infoBin(@PathVariable("tagHexa") String tagHexa) throws IOException, JAXBException {

		gerarInit();
		
		Map<String, String> result = new HashMap<String, String>();
		
		String origemBinary = engine.hex2bin(tagHexa);

		String length = binaryHeaders.get(origemBinary.substring(0, 8));
		String filter = origemBinary.substring(8, 11);
		String partition = origemBinary.substring(11, 14);

		PartitionValue partitionValue = partitionValueMap.get( engine.bin2dec(partition));
		
		int tamanhoCompanyprefix = 14 + Integer.parseInt(partitionValue.getCompanyPrefix().getBits());
		String companyPrefix = origemBinary.substring(14,  tamanhoCompanyprefix);
		
		int tamanhoItemReference = tamanhoCompanyprefix + Integer.parseInt(partitionValue.getItemReference().getBits());
		String itemReference = origemBinary.substring(tamanhoCompanyprefix,  tamanhoItemReference);
		
		String serial = origemBinary.substring(tamanhoItemReference,  origemBinary.length());
		
		result.put("filter",filter);
		result.put("Partition",filter);
		result.put("Company prefix", companyPrefix);
		result.put("Item Reference prefix", itemReference);
		result.put("Serial", serial);
		
		return ResponseEntity.ok(result);
	}
	
	private static Map<String, PartitionValue> partitionValueMap = new HashMap<String, PartitionValue>();	
	
	private static Map<String, String> binaryHeaders = new HashMap<String, String>();	
	static{
		binaryHeaders.put("00110000", "96");
		binaryHeaders.put("00110110", "198");
	}

	public void gerarInit() {
		
		partitionValueMap.put("0", new PartitionValue(new CompanyPrefix("40", "12"), new ItemReference("4",  "1")));
		partitionValueMap.put("1", new PartitionValue(new CompanyPrefix("37", "11"), new ItemReference("7",  "2")));
		partitionValueMap.put("2", new PartitionValue(new CompanyPrefix("34", "10"), new ItemReference("10", "3")));
		partitionValueMap.put("3", new PartitionValue(new CompanyPrefix("30", "9"),  new ItemReference("14", "4")));
		partitionValueMap.put("4", new PartitionValue(new CompanyPrefix("27", "8"),  new ItemReference("17", "5")));
		partitionValueMap.put("5", new PartitionValue(new CompanyPrefix("24", "7"),  new ItemReference("20", "6")));
		partitionValueMap.put("6", new PartitionValue(new CompanyPrefix("20", "6"),  new ItemReference("24", "7")));
	}

}
	
class PartitionValue {
	
	private CompanyPrefix companyPrefix;
	private ItemReference itemReference;
	
	public PartitionValue(CompanyPrefix companyPrefix, ItemReference itemReference) {
		this.companyPrefix = companyPrefix;
		this.itemReference = itemReference;
	}

	public CompanyPrefix getCompanyPrefix() {
		return companyPrefix;
	}

	public void setCompanyPrefix(CompanyPrefix companyPrefix) {
		this.companyPrefix = companyPrefix;
	}

	public ItemReference getItemReference() {
		return itemReference;
	}

	public void setItemReference(ItemReference itemReference) {
		this.itemReference = itemReference;
	}
}

class CompanyPrefix{
	
	private String bits;
	private String digits;
	
	public CompanyPrefix(String bits, String digits) {
		this.bits = bits;
		this.digits = digits;
	}

	public String getBits() {
		return bits;
	}

	public void setBits(String bits) {
		this.bits = bits;
	}

	public String getDigits() {
		return digits;
	}

	public void setDigits(String digits) {
		this.digits = digits;
	}
	
}

class ItemReference{
	
	private String bits;
	private String digits;
	
	public ItemReference(String bits, String digits) {
		this.bits = bits;
		this.digits = digits;
	}
	
	public String getBits() {
		return bits;
	}

	public void setBits(String bits) {
		this.bits = bits;
	}

	public String getDigits() {
		return digits;
	}

	public void setDigits(String digits) {
		this.digits = digits;
	}
	
}

