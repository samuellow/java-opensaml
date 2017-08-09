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

package org.opensaml.saml.saml2.metadata.impl;

import java.util.List;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.common.AbstractSAMLObject;
import org.opensaml.saml.saml2.metadata.AttributeProfile;

/**
 * A concrete implementation of {@link org.opensaml.saml.saml2.metadata.AttributeProfile}.
 */
public class AttributeProfileImpl extends AbstractSAMLObject implements AttributeProfile {

    /** Profile URI. */
    private String profileURI;

    /**
     * Constructor.
     * 
     * @param namespaceURI name space
     * @param elementLocalName local name
     * @param namespacePrefix prefix
     */
    protected AttributeProfileImpl(final String namespaceURI, final String elementLocalName, final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

    /** {@inheritDoc} */
    public String getProfileURI() {
        return profileURI;
    }

    /** {@inheritDoc} */
    public void setProfileURI(final String theProfileURI) {
        this.profileURI = prepareForAssignment(this.profileURI, theProfileURI);
    }

    /** {@inheritDoc} */
    public List<XMLObject> getOrderedChildren() {
        // No Children
        return null; 
    }
}