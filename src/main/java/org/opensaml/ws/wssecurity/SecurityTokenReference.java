/*
 * Copyright 2008 Members of the EGEE Collaboration.
 * Copyright 2008 University Corporation for Advanced Internet Development, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensaml.ws.wssecurity;

import java.util.List;

import javax.xml.namespace.QName;

import org.opensaml.xml.AttributeExtensibleXMLObject;
import org.opensaml.xml.ElementExtensibleXMLObject;

/**
 * The &lt;wsse:SecurityTokenReference&gt; element.
 * <p>
 * TODO: add Ch. 7.5 ds:KeyInfo and Ch. 7.6 ds:KeyName support
 * 
 * @see "WS-Security 2004, Chapter 7. Token References."
 * 
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @version $Revision$
 * 
 */
public interface SecurityTokenReference extends AttributedId, AttributedTokenType, AttributeExtensibleXMLObject,
        ElementExtensibleXMLObject, WSSecurityObject {

    /** Element local name. */
    public static final String ELEMENT_LOCAL_NAME = "SecurityTokenReference";

    /** Qualified element name */
    public final static QName ELEMENT_NAME = new QName(WSSecurityConstants.WSSE_NS, ELEMENT_LOCAL_NAME,
            WSSecurityConstants.WSSE_PREFIX);

    /** The wsse:SecurityTokenReference/@Usage attribute local name. */
    public static final String USAGE_ATTR_LOCAL_NAME = "Usage";

    /** The wsse:SecurityTokenReference/@Usage unqualified attribute name. */
    public static final QName USAGE_ATTR_NAME = new QName(USAGE_ATTR_LOCAL_NAME);

    /**
     * Returns the list of &lt;wsse:SecurityTokenReference/@Usage&gt; attributes value.
     * 
     * @return the list of &lt;wsse:SecurityTokenReference/@Usage&gt; attributes value.
     */
    public List<String> getUsages();

    /**
     * Sets the list of &lt;wsse:SecurityTokenReference/@Usage&gt; attributes value.
     * 
     * @param usages the list of &lt;wsse:SecurityTokenReference/@Usage&gt; attributes value.
     */
    public void setUsages(List<String> usages);

    /**
     * Returns the &lt;wsse:Reference&gt; child element.
     * 
     * @return the {@link Reference} child element or <code>null</code>.
     */
    public Reference getReference();

    /**
     * Sets the &lt;wsse:Reference&gt; child element.
     * 
     * @param reference The {@link Reference} object to set.
     */
    public void setReference(Reference reference);

    /**
     * Returns the &lt;wsse:KeyIdentifier&gt; child element.
     * 
     * @return the {@link KeyIdentifier} child element or <code>null</code>.
     */
    public KeyIdentifier getKeyIdentifier();

    /**
     * Sets the &lt;wsse:KeyIdentifier&gt; child element.
     * 
     * @param keyIdentifier the {@link KeyIdentifier} child element to set.
     */
    public void setKeyIdentifier(KeyIdentifier keyIdentifier);

    /**
     * Returns the &lt;wsse:Embedded&gt; child element.
     * 
     * @return the {@link Embedded} child element or <code>null</code>
     */
    public Embedded getEmbedded();

    /**
     * Sets the &lt;wsse:Embedded&gt; child element.
     * 
     * @param embedded the {@link Embedded} child element to set
     */
    public void setEmbedded(Embedded embedded);
}
