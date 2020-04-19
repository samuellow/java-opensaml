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

package org.opensaml.saml.ext.saml2mdui.impl;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nonnull;

import org.opensaml.core.xml.AbstractXMLObject;
import org.opensaml.core.xml.LangBearing;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.ext.saml2mdui.Keywords;

import com.google.common.base.Strings;

/**
 * Concrete Implementation of  {@link org.opensaml.saml.ext.saml2mdui.Keywords}.
 */
public class KeywordsImpl extends AbstractXMLObject implements Keywords {

    /** The language. */
    private String lang;
    /** The data. */
    @Nonnull private List<String> data = Collections.emptyList();
    /**
     * Constructor.
     *
     * @param namespaceURI the URI
     * @param elementLocalName the local name
     * @param namespacePrefix the prefix
     */
    protected KeywordsImpl(final String namespaceURI, final String elementLocalName, final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

   
    /** {@inheritDoc} */
    public List<XMLObject> getOrderedChildren() {
        return null;
    }

    /** {@inheritDoc} */
    public String getXMLLang() {
        return lang;
    }

    /** {@inheritDoc} */
    public void setXMLLang(final String newLang) {
        final boolean hasValue = newLang != null && !Strings.isNullOrEmpty(newLang);
        lang = prepareForAssignment(lang, newLang);
        manageQualifiedAttributeNamespace(LangBearing.XML_LANG_ATTR_NAME, hasValue);
    }

    /** {@inheritDoc} */
    public List<String> getKeywords() {
        return data;
    }

    /** {@inheritDoc} */
    public void setKeywords(final List<String> val) {
        data = prepareForAssignment(data, val);
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        int hash = lang == null ? 12 :lang.hashCode();
        for (final String s: data) {
            hash = hash * 31 + s.hashCode();
        }
        return hash; 
    }

    /** {@inheritDoc} */
    public boolean equals(final Object obj) {
        if (!(obj instanceof Keywords)) {
            return false;
        }
        final Keywords other = (Keywords) obj;

        if (lang == null) {
            if (other.getXMLLang() != null) {
                return false;
            }
        } else if (!lang.equals(other.getXMLLang())) {
            return false;
        }

        List<String> otherList = other.getKeywords();
        if (otherList == null) {
            otherList = Collections.emptyList();
        }

        if (otherList.size() != data.size()) {
            return false;
        }

        final Iterator<String> me = data.iterator();
        final Iterator<String> him = otherList.iterator();

        while (me.hasNext()) {
            if (!me.next().equals(him.next())) {
                return false;
            }
        }
        return true;
    }

}
