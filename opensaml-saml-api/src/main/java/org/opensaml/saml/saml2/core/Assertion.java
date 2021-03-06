/*
 * Licensed to the University Corporation for Advanced Internet Development,
 * Inc. (UCAID) under one or more contributor license agreements.  See the
 * NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The UCAID licenses this file to You under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensaml.saml.saml2.core;

import java.time.Instant;
import java.util.List;

import javax.annotation.Nonnull;
import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.opensaml.saml.common.SAMLVersion;
import org.opensaml.saml.common.SignableSAMLObject;
import org.opensaml.saml.common.xml.SAMLConstants;

/**
 * SAML 2.0 Core Assertion.
 */
public interface Assertion extends SignableSAMLObject, Evidentiary {

    /** Element local name. */
    static final String DEFAULT_ELEMENT_LOCAL_NAME = "Assertion";

    /** Default element name. */
    static final QName DEFAULT_ELEMENT_NAME = new QName(SAMLConstants.SAML20_NS, DEFAULT_ELEMENT_LOCAL_NAME,
            SAMLConstants.SAML20_PREFIX);

    /** Local name of the XSI type. */
    static final String TYPE_LOCAL_NAME = "AssertionType";

    /** QName of the XSI type. */
    static final QName TYPE_NAME = new QName(SAMLConstants.SAML20_NS, TYPE_LOCAL_NAME,
            SAMLConstants.SAML20_PREFIX);

    /** Version attribute name. */
    static final String VERSION_ATTRIB_NAME = "Version";

    /** IssueInstant attribute name. */
    static final String ISSUE_INSTANT_ATTRIB_NAME = "IssueInstant";

    /** IssueInstant attribute QName. */
    @Nonnull static final QName ISSUEINSTANT_ATTRIB_QNAME =
            new QName(null, "IssueInstant", XMLConstants.DEFAULT_NS_PREFIX);
    
    /** ID attribute name. */
    static final String ID_ATTRIB_NAME = "ID";

    /**
     * Gets the SAML Version of this assertion.
     * 
     * @return the SAML Version of this assertion.
     */
    SAMLVersion getVersion();

    /**
     * Sets the SAML Version of this assertion.
     * 
     * @param newVersion the SAML Version of this assertion
     */
    void setVersion(SAMLVersion newVersion);

    /**
     * Gets the issue instance of this assertion.
     * 
     * @return the issue instance of this assertion
     */
    Instant getIssueInstant();

    /**
     * Sets the issue instance of this assertion.
     * 
     * @param newIssueInstance the issue instance of this assertion
     */
    void setIssueInstant(Instant newIssueInstance);

    /**
     * Sets the ID of this assertion.
     * 
     * @return the ID of this assertion
     */
    String getID();

    /**
     * Sets the ID of this assertion.
     * 
     * @param newID the ID of this assertion
     */
    void setID(String newID);

    /**
     * Gets the Issuer of this assertion.
     * 
     * @return the Issuer of this assertion
     */
    Issuer getIssuer();

    /**
     * Sets the Issuer of this assertion.
     * 
     * @param newIssuer the Issuer of this assertion
     */
    void setIssuer(Issuer newIssuer);

    /**
     * Gets the Subject of this assertion.
     * 
     * @return the Subject of this assertion
     */
    Subject getSubject();

    /**
     * Sets the Subject of this assertion.
     * 
     * @param newSubject the Subject of this assertion
     */
    void setSubject(Subject newSubject);

    /**
     * Gets the Conditions placed on this assertion.
     * 
     * @return the Conditions placed on this assertion
     */
    Conditions getConditions();

    /**
     * Sets the Conditions placed on this assertion.
     * 
     * @param newConditions the Conditions placed on this assertion
     */
    void setConditions(Conditions newConditions);

    /**
     * Gets the Advice for this assertion.
     * 
     * @return the Advice for this assertion
     */
    Advice getAdvice();

    /**
     * Sets the Advice for this assertion.
     * 
     * @param newAdvice the Advice for this assertion
     */
    void setAdvice(Advice newAdvice);

    /**
     * Gets the list of statements attached to this assertion.
     * 
     * @return the list of statements attached to this assertion
     */
    List<Statement> getStatements();

    /**
     * Gets the list of statements attached to this assertion that match a particular QName.
     * 
     * @param typeOrName the QName of the statements to return
     * @return the list of statements attached to this assertion
     */
    List<Statement> getStatements(QName typeOrName);

    /**
     * Gets the list of AuthnStatements attached to this assertion.
     * 
     * @return the list of AuthnStatements attached to this assertion
     */
    List<AuthnStatement> getAuthnStatements();

    /**
     * Gets the list of AuthzDecisionStatements attached to this assertion.
     * 
     * @return the list of AuthzDecisionStatements attached to this assertion
     */
    List<AuthzDecisionStatement> getAuthzDecisionStatements();

    /**
     * Gets the list of AttributeStatement attached to this assertion.
     * 
     * @return the list of AttributeStatement attached to this assertion
     */
    List<AttributeStatement> getAttributeStatements();
}