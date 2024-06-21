/*
 * Copyright (C) 2004 - Edwin Peer
 * 
 * Created on Feb 10, 2004
 *
 */
package za.ac.up.cs.cirg.ciclops.services;

import java.security.SecureClassLoader;
import java.util.HashSet;
import java.util.Set;

import javax.ejb.FinderException;

import za.ac.up.cs.cirg.ciclops.domain.Code;
import za.ac.up.cs.cirg.ciclops.domain.CodeHome;

/**
 * @author espeer
 *
 */
public class EJBClassLoader extends SecureClassLoader {
	
	public EJBClassLoader(CodeHome codeHome) {
		this(codeHome, false);
	}

	public EJBClassLoader(CodeHome codeHome, boolean logging) {
		this.codeHome = codeHome;
		this.logging = logging;
		log = new HashSet();
	}
	
    public Class findClass(String name) throws ClassNotFoundException {
        Code code = null;
		try {
			code = codeHome.findByPrimaryKey(name);
		}
		catch (FinderException ex) {
			throw new ClassNotFoundException(name);
		}
		if (logging) {
			log.add(code);
		}
		byte[] b = code.getByteCode();
		return defineClass(name, b, 0, b.length);	
	}
    
    public Set getLog() {
    	return log;
    }
     
	private CodeHome codeHome;
	private boolean logging;
	private Set log;
}
