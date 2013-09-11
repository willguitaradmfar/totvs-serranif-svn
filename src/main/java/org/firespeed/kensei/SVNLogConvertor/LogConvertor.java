package org.firespeed.kensei.SVNLogConvertor;

import java.io.ByteArrayOutputStream;
import java.util.Collection;
import java.util.Iterator;
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
import org.tmatesoft.svn.core.wc.ISVNOptions;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNDiffClient;
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
				logger_.info("Initializing local repository factory");
				FSRepositoryFactory.setup();
			} else if (StringUtils.equalsIgnoreCase("http", protocol)) {
				logger_.info("Initializing http repository factory");
				DAVRepositoryFactory.setup();
			} else if (StringUtils.equalsIgnoreCase("svn", protocol)) {
				logger_.info("Initializing svn repository factory");
				SVNRepositoryFactoryImpl.setup();
			} else {
				throw new LogConvertorException("Unknown protocol " + protocol);
			}
			repository = getRepos(svnUrl, userName, password);
		} catch (SVNException e) {
			StringBuffer message = new StringBuffer(
					"Erreur connecting to svn server : ")
					.append(e.getMessage());
			logger_.info(message, e);
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

		DAVRepositoryFactory.setup();
		logger_.debug("------------------------------------START------------------------------------");

		final long startnum = 2400;
		// final long lastnum = 50;
		final long lastnum = repository.getLatestRevision();
		logger_.debug("Convert revision " + String.valueOf(startnum) + " to "
				+ String.valueOf(lastnum));

		try {
			@SuppressWarnings("unchecked")
			Collection<SVNLogEntry> revisions = (Collection<SVNLogEntry>) repository
					.log(new String[] { "" }, null, startnum, lastnum, true,
							true);

			logger_.info("qtde revision : " + revisions.size());

			ISVNOptions options = SVNWCUtil.createDefaultOptions(true);
			SVNClientManager clientManager = SVNClientManager.newInstance(
					options, repository.getAuthenticationManager());
			SVNDiffClient diffClient = clientManager.getDiffClient();

			String msg = "";
			for (SVNLogEntry revision : revisions) {

				logger_.debug("revision:"
						+ String.valueOf(revision.getRevision()));
				logger_.info("    before -> " + revision.getMessage());
				logger_.info("    author -> " + revision.getAuthor());
				logger_.info("    date   -> " + revision.getDate());

				Map map = revision.getChangedPaths();
				Iterator it = map.keySet().iterator();
				while (it.hasNext()) {
					Object key = it.next();
					logger_.info("    Paths  -> " + key.toString() + " -- "
							+ map.get(key));
				}
				SVNURL svnurl1 = SVNURL.parseURIEncoded(svnUrl);

				SVNRevision svnRevision1 = SVNRevision.create(revision
						.getRevision());
				SVNRevision svnRevision2 = SVNRevision.create(revision
						.getRevision() - 1);

				try {
					ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();

					diffClient.doDiff(svnurl1, svnRevision1, svnurl1,
							svnRevision2, SVNDepth.INFINITY, true,
							arrayOutputStream);

					System.out.println(arrayOutputStream.toString());

				} catch (org.tmatesoft.svn.core.SVNException ex) {
					logger_.error(ex);
				}
			}

		} catch (SVNException e) {
			throw new LogConvertorException(e);
		} finally {
			repository.closeSession();
		}
		logger_.debug("------------------------------------END------------------------------------");
	}
}
