/**
 * Copyright (c) 2007, 2008 The Planets Project Partners.
 * 
 * All rights reserved. This program and the accompanying 
 * materials are made available under the terms of the 
 * GNU Lesser General Public License v3 which 
 * accompanies this distribution, and is available at 
 * http://www.gnu.org/licenses/lgpl.html
 * 
 */
package eu.planets_project.ifr.core.storage.api.query;

/**
 * @author AnJackson
 *
 */
public class QueryValidationException extends Exception {

    /**
     * 
     */
    public QueryValidationException() {
        super();
    }

    /**
     * @param message
     * @param cause
     */
    public QueryValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message
     */
    public QueryValidationException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public QueryValidationException(Throwable cause) {
        super(cause);
    }

}