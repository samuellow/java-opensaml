/*
 * Copyright [2005] [University Corporation for Advanced Internet Development, Inc.]
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

/**
 * 
 */

package org.opensaml.saml2.core.impl;

import org.opensaml.common.SAMLObject;
import org.opensaml.common.impl.AbstractSAMLObjectMarshaller;
import org.opensaml.common.xml.SAMLConstants;
import org.opensaml.saml2.core.SubjectConfirmation;
import org.opensaml.xml.io.MarshallingException;
import org.w3c.dom.Element;

/**
 * A thread safe {@link org.opensaml.common.io.Marshaller} for {@link org.opensaml.saml2.core.SubjectConfirmation}
 * objects.
 */
public class SubjectConfirmationMarshaller extends AbstractSAMLObjectMarshaller {

    /** Constructor */
    public SubjectConfirmationMarshaller() {
        super(SAMLConstants.SAML20_NS, SubjectConfirmation.LOCAL_NAME);
    }

    /*
     * @see org.opensaml.common.impl.AbstractSAMLObjectMarshaller#marshallAttributes(org.opensaml.common.SAMLObject,
     *      org.w3c.dom.Element)
     */

    protected void marshallAttributes(SAMLObject samlObject, Element domElement) throws MarshallingException {
        SubjectConfirmation subjectConfirmation = (SubjectConfirmation) samlObject;

        if (subjectConfirmation.getMethod() != null) {
            domElement.setAttributeNS(null, SubjectConfirmation.METHOD_ATTRIB_NAME, subjectConfirmation.getMethod());
        }
    }
}