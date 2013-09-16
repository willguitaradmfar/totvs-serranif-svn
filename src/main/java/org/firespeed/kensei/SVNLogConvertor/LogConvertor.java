package org.firespeed.kensei.SVNLogConvertor;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.firespeed.kensei.SVNLogConvertor.Exception.LogConvertorConnectException;
import org.firespeed.kensei.SVNLogConvertor.Exception.LogConvertorException;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.DefaultSVNDiffGenerator;
import org.tmatesoft.svn.core.wc.ISVNOptions;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNDiffClient;
import org.tmatesoft.svn.core.wc.SVNDiffOptions;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

public class LogConvertor {

	private final static Logger logger_ = LogFactory
			.getLogger(LogConvertor.class);
	private static SVNRepository repository;
	private Boolean logOnlyMode = false;

	private String svnUrl;
	private String userName;
	private String password;
	private String protocol;
	private List<CSVLine> csvLines;
	
	private long startnum = 1;
	private long lastnum = 1;	

	public void init() throws Exception {
		logger_.debug("init");

		svnUrl = System.getenv("svnURL");
		userName = System.getenv("svnUserName");
		password = System.getenv("svnPassword");
		protocol = System.getenv("svnProtocol");

		logger_.debug("svnUrl : " + svnUrl);
		logger_.debug("userName : " + userName);
		logger_.debug("password : " + password);
		logger_.debug("protocol : " + protocol);

		validateURL(svnUrl);

		try {
			if (StringUtils.equalsIgnoreCase("file", protocol)) {
				logger_.debug("Initializing local repository factory");
				FSRepositoryFactory.setup();
			} else if (StringUtils.equalsIgnoreCase("http", protocol)) {
				logger_.debug("Initializing http repository factory");
				DAVRepositoryFactory.setup();
			} else if (StringUtils.equalsIgnoreCase("svn", protocol)) {
				logger_.debug("Initializing svn repository factory");
				SVNRepositoryFactoryImpl.setup();
			} else {
				throw new LogConvertorException("Unknown protocol " + protocol);
			}
			repository = getRepos(svnUrl, userName, password);
		} catch (SVNException e) {
			StringBuffer message = new StringBuffer(
					"Erreur connecting to svn server : ")
					.append(e.getMessage());
			logger_.debug(message, e);
			throw new LogConvertorConnectException(message.toString());
		}
	}

	private void validateURL(String url) throws LogConvertorConnectException {
		try {
			@SuppressWarnings("unused")
			SVNURL svnUrl = SVNURL.parseURIDecoded(url);
		} catch (SVNException e) {
			StringBuffer message = new StringBuffer("Error creation SVNURL : ")
					.append(e.getMessage());
			logger_.error(message, e);
			throw new LogConvertorConnectException(message.toString());
		}
	}

	private SVNRepository getRepos(String url, String userName, String passWord)
			throws SVNException {
		SVNURL svnUrl = SVNURL.parseURIDecoded(url);
		SVNRepository repos = SVNRepositoryFactory.create(svnUrl);
		if (userName != null) {
			ISVNAuthenticationManager authManager = SVNWCUtil
					.createDefaultAuthenticationManager(userName,
							passWord == null ? "" : passWord);
			repos.setAuthenticationManager(authManager);
		}
		return repos;
	}

	/**
	 * 
	 * @throws Exception
	 */
	public void entry() throws Exception {
		
		csvLines = new ArrayList<CSVLine>();
		
		DAVRepositoryFactory.setup();
		
		logger_.debug("------------------------------------START------------------------------------");		
		
		if(System.getProperty("startRevision") != null){
			startnum = Long.parseLong(System.getProperty("startRevision"));			
		}
		
		if(System.getProperty("lastRevision") != null){
			lastnum = Long.parseLong(System.getProperty("lastRevision"));
		}else{
			lastnum = repository.getLatestRevision();
		}
		
		logger_.info("startRevision : "+startnum);
		logger_.info("lastRevision  : "+lastnum);		
		
		logger_.debug("Convert revision " + String.valueOf(startnum) + " to "+ String.valueOf(lastnum));

		try {
			@SuppressWarnings("unchecked")
			Collection<SVNLogEntry> revisions = (Collection<SVNLogEntry>) repository.log(new String[] { "" }, null, startnum, lastnum, true, true);

			logger_.debug("qtde revision : " + revisions.size());

			ISVNOptions options = SVNWCUtil.createDefaultOptions(true);
			
			SVNClientManager clientManager = SVNClientManager.newInstance(null, repository.getAuthenticationManager());
						
			SVNDiffClient diffClient = clientManager.getDiffClient();
			
			SVNDiffOptions svnDiffOptions = new SVNDiffOptions();
			svnDiffOptions.setIgnoreAllWhitespace(true);
			svnDiffOptions.setIgnoreAmountOfWhitespace(true);
			svnDiffOptions.setIgnoreEOLStyle(true);
			
			DefaultSVNDiffGenerator defaultSVNDiffGenerator = new DefaultSVNDiffGenerator();			
			defaultSVNDiffGenerator.setDiffOptions(svnDiffOptions);			
			
			diffClient.setDiffGenerator(defaultSVNDiffGenerator);			
			
			for (SVNLogEntry revision : revisions) {
					String msg = revision.getMessage();					
				
					List<Issue> issues = new ApuraIssueNaMensagem().getIssue(msg);
					logger_.debug("QUANTIDADE DE ISSUE ENCONTRADA : "+issues.size());
									
					logger_.debug("--------------------------------------------------------------------------------");
					logger_.debug("--------------------------------------------------------------------------------");
					logger_.debug("revision.........:" + String.valueOf(revision.getRevision()));
					logger_.debug("msg..............:" + msg);
					logger_.debug("author...........:" + revision.getAuthor());
					logger_.debug("date.............:" + revision.getDate());
					logger_.debug("qtde issue.......:" + issues.size());
					logger_.debug("--------------------------------------------------------------------------------");
					logger_.debug("--------------------------------------------------------------------------------");
	
					Map map = revision.getChangedPaths();
					Iterator it = map.keySet().iterator();
					while (it.hasNext()) {
						Object key = it.next();
						logger_.debug("    Paths  -> " + key.toString() + " -- " + map.get(key));
					}
					
					SVNURL svnurl1 = SVNURL.parseURIEncoded(svnUrl);
					SVNRevision svnRevision1 = SVNRevision.create(revision.getRevision() - 1);
					SVNRevision svnRevision2 = SVNRevision.create(revision.getRevision());
	
					try {
						ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
						logger_.debug("Comparando revisão : "+revision.getRevision()+ " com a revisão : "+(revision.getRevision() - 1));
						logger_.debug("Comparando utl : "+svnurl1);
						diffClient.doDiff(svnurl1, svnRevision1, svnurl1, svnRevision2, SVNDepth.INFINITY, true, arrayOutputStream);
												
						ApuraLinhaDeCodigo apuraLinhaDeCodigo = new ApuraLinhaDeCodigo();
						apuraLinhaDeCodigo.countAddLine(arrayOutputStream.toString());
						
						for(BlocoCode blocoCode : apuraLinhaDeCodigo.getListBlocoCode()){
							logger_.debug("Revisão Add.........................: "+blocoCode.getRevisionAdd());
							logger_.debug("Revisão Del.........................: "+blocoCode.getRevisionDel());
							logger_.debug("URL  ...............................: "+blocoCode.getURL());
							logger_.debug("Inclusões  .........................: "+blocoCode.getLinhasAdicionadas().size());						
							for(String line : blocoCode.getLinhasAdicionadas()){
								logger_.debug("\t\t"+line);
							}						
							logger_.debug("Deleções   .........................: "+blocoCode.getLinhasExcluidas().size());
							for(String line : blocoCode.getLinhasExcluidas()){
								logger_.debug("\t\t"+line);
							}
							logger_.debug("--------------------------------------------------------------------------------");
								
							CSVLine csvLine = new CSVLine();
							csvLine.setUser(revision.getAuthor());
							csvLine.setIssues(issues);
							csvLine.setReview(String.valueOf(revision.getRevision()));
							csvLine.setDate_review(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(revision.getDate()));
							csvLine.setFile(blocoCode.getURL());
							csvLine.setLOG_I(String.valueOf(blocoCode.getLinhasAdicionadas().size()));
							csvLine.setLOG_D(String.valueOf(blocoCode.getLinhasExcluidas().size()));
							csvLine.setMsg(msg);
							csvLine.setBlocoCode(blocoCode);
							
							logger_.debug(String.format(CSVLine.formatLine
											, revision.getAuthor()
											, issues.size()
											, String.valueOf(revision.getRevision())
											, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(revision.getDate())
											, blocoCode.getURL()
											, blocoCode.getLinhasAdicionadas().size()
											, blocoCode.getLinhasExcluidas().size()));
								if(issues.size() > 1)
								csvLines.add(csvLine);
							}
						
	
					} catch (org.tmatesoft.svn.core.SVNException ex) {
						logger_.error(ex);
					}
				
				
			}

		} catch (SVNException e) {
			throw new LogConvertorException(e);
		} finally {
			repository.closeSession();
			ImprimeCSV();
		}
		logger_.debug("------------------------------------END------------------------------------");
	}
	
	public void ImprimeCSV(){
		System.out.println(CSVLine.formatLineHead);
		for(CSVLine line : csvLines){
			for(Issue issue : line.getIssues()){
				System.out.println(String.format(CSVLine.formatLine
						, line.getUser()
						, issue.getCode()
						, line.getReview()
						, line.getDate_review()
						, line.getFile()
						, line.getLOG_I()
						, line.getLOG_D()));				
			}
		}
		
	}
}
