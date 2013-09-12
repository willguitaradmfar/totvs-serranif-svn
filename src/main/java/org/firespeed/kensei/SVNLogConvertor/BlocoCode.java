package org.firespeed.kensei.SVNLogConvertor;

import java.util.ArrayList;
import java.util.List;

public class BlocoCode {
	
	private String URL;
	private long revision;
	private List<String> linhasAdicionadas;
	private List<String> linhasExcluidas;
	
	public BlocoCode() {
		linhasAdicionadas = new ArrayList<String>();
		linhasExcluidas = new ArrayList<String>();
	}
	
	public String getURL() {
		return URL;
	}
	public void setURL(String uRL) {
		URL = uRL;
	}
	public long getRevision() {
		return revision;
	}
	public void setRevision(long revision) {
		this.revision = revision;
	}

	public List<String> getLinhasAdicionadas() {
		return linhasAdicionadas;
	}

	public void setLinhasAdicionadas(List<String> linhasAdicionadas) {
		this.linhasAdicionadas = linhasAdicionadas;
	}

	public List<String> getLinhasExcluidas() {
		return linhasExcluidas;
	}

	public void setLinhasExcluidas(List<String> linhasExcluidas) {
		this.linhasExcluidas = linhasExcluidas;
	}
	
	
	

}
