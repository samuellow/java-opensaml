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

package org.opensaml.saml.saml1.core.impl;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.saml.common.AbstractSAMLObjectUnmarshaller;
import org.opensaml.saml.common.SAMLVersion;
import org.opensaml.saml.saml1.core.Advice;
import org.opensaml.saml.saml1.core.Assertion;
import org.opensaml.saml.saml1.core.Conditions;
import org.opensaml.saml.saml1.core.Statement;
import org.opensaml.xmlsec.signature.Signature;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import com.google.common.base.Strings;

import net.shibboleth.utilities.java.support.xml.DOMTypeSupport;

/**
 * A thread-safe Unmarshaller for {@link org.opensaml.saml.saml1.core.Assertion} objects.
 */
public class AssertionUnmarshaller extends AbstractSAMLObjectUnmarshaller {

    /** {@inheritDoc} */
    public XMLObject unmarshall(final Element domElement) throws UnmarshallingException {
        // After regular unmarshalling, check the minor version and set ID-ness if not SAML 1.0
        final Assertion assertion = (Assertion) super.unmarshall(domElement);
        if (assertion.getMinorVersion() != 0 && !Strings.isNullOrEmpty(assertion.getID())) {
            XMLObjectSupport.marshallAttributeIDness(null, Assertion.ID_ATTRIB_NAME, domElement, true);
        }
        return assertion;
    }

    /** {@inheritDoc} */
    protected void processChildElement(final XMLObject parentSAMLObject, final XMLObject childSAMLObject)
            throws UnmarshallingException {

        final Assertion assertion = (Assertion) parentSAMLObject;

        if (childSAMLObject instanceof Signature) {
            assertion.setSignature((Signature) childSAMLObject);
        } else if (childSAMLObject instanceof Conditions) {
            assertion.setConditions((Conditions) childSAMLObject);
        } else if (childSAMLObject instanceof Advice) {
            assertion.setAdvice((Advice) childSAMLObject);
        } else if (childSAMLObject instanceof Statement) {
            assertion.getStatements().add((Statement) childSAMLObject);
        } else {
            super.processChildElement(parentSAMLObject, childSAMLObject);
        }
    }

// Checkstyle: CyclomaticComplexity OFF
    /** {@inheritDoc} */
    protected void processAttribute(final XMLObject samlObject, final Attr attribute) throws UnmarshallingException {

        final Assertion assertion = (Assertion) samlObject;

        if (attribute.getNamespaceURI() == null) {
            if (Assertion.ID_ATTRIB_NAME.equals(attribute.getLocalName())) {
                assertion.setID(attribute.getValue());
            } else if (Assertion.ISSUER_ATTRIB_NAME.equals(attribute.getLocalName())) {
                assertion.setIssuer(attribute.getValue());
            } else if (Assertion.ISSUEINSTANT_ATTRIB_NAME.equals(attribute.getLocalName())
                    && !Strings.isNullOrEmpty(attribute.getValue())) {
                assertion.setIssueInstant(DOMTypeSupport.stringToInstant(attribute.getValue()));
            } else if (Assertion.MAJORVERSION_ATTRIB_NAME.equals(attribute.getLocalName())) {
                final int major;
                try {
                    major = Integer.parseInt(attribute.getValue());
                    if (major != 1) {
                        throw new UnmarshallingException("MajorVersion was invalid, must be 1");
                    }
                } catch (final NumberFormatException n) {
                    throw new UnmarshallingException(n);
                }
            } else if (Assertion.MINORVERSION_ATTRIB_NAME.equals(attribute.getLocalName())) {
                final int minor;
                try {
                    minor = Integer.parseInt(attribute.getValue());
                } catch (final NumberFormatException n) {
                    throw new UnmarshallingException(n);
                }
                if (minor == 0) {
                    assertion.setVersion(SAMLVersion.VERSION_10);
                } else if (minor == 1) {
                    assertion.setVersion(SAMLVersion.VERSION_11);
                }
            } else {
                super.processAttribute(samlObject, attribute);
            }
        } else {
            super.processAttribute(samlObject, attribute);
        }
    }
// Checkstyle: CyclomaticComplexity ON
    
}