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

/**
 * 
 */

package org.opensaml.saml.saml2.core.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.util.XMLObjectChildrenList;
import org.opensaml.saml.common.AbstractSAMLObject;
import org.opensaml.saml.saml2.core.Action;
import org.opensaml.saml.saml2.core.AuthzDecisionStatement;
import org.opensaml.saml.saml2.core.DecisionTypeEnumeration;
import org.opensaml.saml.saml2.core.Evidence;

/**
 * A concrete implementation of {@link org.opensaml.saml.saml2.core.AuthzDecisionStatement}.
 */
public class AuthzDecisionStatementImpl extends AbstractSAMLObject implements AuthzDecisionStatement {

    /** URI of the resource to which authorization is sought. */
    private String resource;

    /** Decision of the authorization request. */
    private DecisionTypeEnumeration decision;

    /** Actions authorized to be performed. */
    private final XMLObjectChildrenList<Action> actions;

    /** SAML assertion the authority relied on when making the authorization decision. */
    private Evidence evidence;

    /**
     * Constructor.
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected AuthzDecisionStatementImpl(final String namespaceURI, final String elementLocalName, final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        actions = new XMLObjectChildrenList<>(this);
    }

    /** {@inheritDoc} */
    public String getResource() {
        return resource;
    }

    /** {@inheritDoc} */
    public void setResource(final String newResourceURI) {
        this.resource = prepareForAssignment(this.resource, newResourceURI, false);
    }

    /** {@inheritDoc} */
    public DecisionTypeEnumeration getDecision() {
        return decision;
    }

    /** {@inheritDoc} */
    public void setDecision(final DecisionTypeEnumeration newDecision) {
        this.decision = prepareForAssignment(this.decision, newDecision);
    }

    /** {@inheritDoc} */
    public List<Action> getActions() {
        return actions;
    }

    /** {@inheritDoc} */
    public Evidence getEvidence() {
        return evidence;
    }

    /** {@inheritDoc} */
    public void setEvidence(final Evidence newEvidence) {
        this.evidence = prepareForAssignment(this.evidence, newEvidence);
    }

    /** {@inheritDoc} */
    public List<XMLObject> getOrderedChildren() {
        ArrayList<XMLObject> children = new ArrayList<>();

        children.addAll(actions);
        children.add(evidence);
        return Collections.unmodifiableList(children);
    }
}