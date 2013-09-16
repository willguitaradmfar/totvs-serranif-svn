package br.com.totvs.svn;

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
public class ApuraIssueNaMensagem {		
	
	private final static Logger logger_ = LogFactory.getLogger(ApuraIssueNaMensagem.class);
	
	private final static String ISSUE_NOT_FOUND = "UNDEFINED";
	
	public ApuraIssueNaMensagem() {				
	}
	
	public List<Issue> getIssue(String msg) {
		List<Issue> issues = new ArrayList<Issue>();
				
		String padrao = "([A-Z_a-z0-9]+-[0-9]+)";		
		//msg = ":BAS-401 Correcao no TMenuPopUp (enviava codigo do parent ao inves EEE_XXXE-897 da window no click); Limpeza de codigos eEEEE-897 variaveis nao usadas;";
		
		Pattern pattern = Pattern.compile(padrao);	    
	    Matcher matcher = pattern.matcher(msg);		
	    while (matcher.find()) {	    	
	    	issues.add(new Issue(matcher.group()));
	    	logger_.debug("Issue Encontrada : "+matcher.group());
	    }				
		return issues;
		
	}
	
	public static void main(String[] args){
		
		String msg = ":BAS-401 Correcao no TMenuPopUp (enviava codigo do parent ao inves da window no click); Limpeza de codigos e variaveis nao usadas;";
		
		String padrao = ".*(BAS-401).*";
		
		if(msg.matches(padrao)){
			System.out.println("acertou");
		}
		else{
			System.out.println("errou");
		}
	
	}
	

}
