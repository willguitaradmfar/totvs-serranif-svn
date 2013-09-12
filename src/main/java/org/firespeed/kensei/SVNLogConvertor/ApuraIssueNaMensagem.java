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
public class ApuraIssueNaMensagem {		
	
	private final static Logger logger_ = LogFactory.getLogger(ApuraIssueNaMensagem.class);
	
	
	public ApuraIssueNaMensagem() {				
	}
	
	public String getIssue(String msg) {
		String padrao = "\\[(.*)\\](.*)";
		if(msg.matches(padrao))
			return msg.replaceAll(padrao, "$1");
		else
			return "Não encontrado";
	}	

}
