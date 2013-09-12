package org.firespeed.kensei.SVNLogConvertor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

/**
 * 
 * Replacer
 * 
 * @author BCC
 *
 */
public class ApuraLinhaDeCodigo {
		
	
	private final static Logger logger_ = LogFactory.getLogger(ApuraLinhaDeCodigo.class);
	private List<BlocoCode> listBlocoCode = new ArrayList<BlocoCode>();
	
	public void countAddLine(String content) {
		logger_.info(content);
		
		String[] blocos = content.split("===================================================================");		
		logger_.debug("QUANTIDADE DE BLOCOS : "+blocos.length);
		for(String bloco : blocos){
			BlocoCode blocoCode = new BlocoCode();
			String[] lines = bloco.split("\n");
			for(String line : lines){
				//logger_.info(line);
				String padraoExcluiLine = "^--- (.*)\\(revision (\\d*)\\)$";
				String padraoAddLine = "^\\+\\+\\+ (.*)\\(revision (\\d*)\\)$";
				if(line.matches(padraoAddLine)){
					blocoCode.setURL(line.replaceAll(padraoAddLine, "$1"));
					blocoCode.setRevision(Long.parseLong(line.replaceAll(padraoAddLine, "$2")));
				}
				if(line.matches("^-.*$") && !line.matches(padraoExcluiLine)){
					blocoCode.getLinhasExcluidas().add(line);
				}
				if(line.matches("^\\+.*$") && !line.matches(padraoAddLine)){
					blocoCode.getLinhasAdicionadas().add(line);
				}
				
			}
			listBlocoCode.add(blocoCode);			
		}
	}

	public List<BlocoCode> getListBlocoCode() {
		return listBlocoCode;
	}

	public void setListBlocoCode(List<BlocoCode> listBlocoCode) {
		this.listBlocoCode = listBlocoCode;
	}
	
	
}
