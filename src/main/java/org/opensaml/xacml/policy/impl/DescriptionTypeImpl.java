/*
 * Copyright 2008 Members of the EGEE Collaboration.
 * Copyright 2008 University Corporation for Advanced Internet Development, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensaml.xacml.policy.impl;

import java.util.List;

import org.opensaml.xacml.policy.DescriptionType;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.validation.AbstractValidatingXMLObject;

/**
 *
 * Concrete implementation of {@link org.opensaml.xacml.policy.DescriptionType}.
 *
 */
public class DescriptionTypeImpl extends AbstractValidatingXMLObject implements DescriptionType {

    /**Value of the description.*/
    private String description;
    
    /**
     * Constructor.
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected DescriptionTypeImpl(String namespaceURI, String elementLocalName, String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }
    
    /** {@inheritDoc} */
    public String getValue() {       
        return description;
    }

    /** {@inheritDoc} */
    public void setValue(String arg0) {
        description = prepareForAssignment(this.description, arg0);
    }

    /** {@inheritDoc} */
    public List<XMLObject> getOrderedChildren() {       
        return null;
    }
}