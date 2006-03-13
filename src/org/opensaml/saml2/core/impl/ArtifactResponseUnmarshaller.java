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

import org.opensaml.common.xml.SAMLConstants;
import org.opensaml.saml2.core.ArtifactResponse;

/**
 * A thread-safe Unmarshaller for {@link org.opensaml.saml2.core.ArtifactResponse}.
 */
public class ArtifactResponseUnmarshaller extends StatusResponseUnmarshaller {

    /**
     * Constructor
     * 
     */
    public ArtifactResponseUnmarshaller() {
        super(SAMLConstants.SAML20P_NS, ArtifactResponse.LOCAL_NAME);
    }

    /**
     * Constructor
     * 
     * @param namespaceURI
     * @param elementLocalName
     */
    protected ArtifactResponseUnmarshaller(String namespaceURI, String elementLocalName) {
        super(namespaceURI, elementLocalName);
    }

    // TODO need to process any <any ...> elements, or leave to a subclass of this class

}
