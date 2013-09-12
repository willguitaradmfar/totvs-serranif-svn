package org.firespeed.kensei.SVNLogConvertor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.tmatesoft.svn.core.wc.DefaultSVNDiffGenerator;

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
	private boolean ignorarLinhasBrancas = false; // VALOR PADRÃO
	
	public ApuraLinhaDeCodigo() {
		if(System.getProperty("ignorarLinhasBrancas") != null){
			ignorarLinhasBrancas = Boolean.parseBoolean(System.getProperty("ignorarLinhasBrancas"));			
		}
		logger_.debug("ignorarLinhasBrancas : "+ignorarLinhasBrancas);	
				
	}
	
	public void countAddLine(String content) {
		//logger_.debug(content);
		
		String[] blocos = content.split("===================================================================");		
		logger_.debug("QUANTIDADE DE BLOCOS : "+(blocos.length-1));
		for(String bloco : blocos){
			BlocoCode blocoCode = new BlocoCode();
			String[] lines = bloco.split("\n");
			for(String line : lines){
				//logger_.debug(line);
				String padraoDelLine = "^--- (.*)	\\(revision (\\d*)\\)$";
				String padraoAddLine = "^\\+\\+\\+ (.*)	\\(revision (\\d*)\\)$";
				if(line.matches(padraoAddLine)){
					blocoCode.setURL(line.replaceAll(padraoAddLine, "$1"));
					blocoCode.setRevisionAdd(Long.parseLong(line.replaceAll(padraoAddLine, "$2")));
				}							
				if(line.matches(padraoDelLine)){					
					blocoCode.setRevisionDel(Long.parseLong(line.replaceAll(padraoDelLine, "$2")));
				}
				if(line.matches("^-.*$") && !line.matches(padraoDelLine)){					
						blocoCode.getLinhasExcluidas().add(line);					
				}
				if(line.matches("^\\+.*$") && !line.matches(padraoAddLine)){
						blocoCode.getLinhasAdicionadas().add(line);						
				}
				
			}
			if(blocoCode.getURL() != null){
				listBlocoCode.add(blocoCode);
			}
		}
	}

	public List<BlocoCode> getListBlocoCode() {
		return listBlocoCode;
	}

	public void setListBlocoCode(List<BlocoCode> listBlocoCode) {
		this.listBlocoCode = listBlocoCode;
	}

	public boolean isIgnorarLinhasBrancas() {
		return ignorarLinhasBrancas;
	}

	public void setIgnorarLinhasBrancas(boolean ignorarLinhasBrancas) {
		this.ignorarLinhasBrancas = ignorarLinhasBrancas;
	}
	
	
	
}
