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

package org.opensaml.xmlsec.encryption.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.logic.Constraint;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.xmlsec.encryption.EncryptedData;
import org.opensaml.xmlsec.encryption.EncryptedKey;
import org.opensaml.xmlsec.signature.KeyInfo;
import org.opensaml.xmlsec.signature.KeyInfoReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of {@link EncryptedKeyResolver} which finds {@link EncryptedKey} elements by dereferencing
 * {@link KeyInfoReference} children of the {@link org.opensaml.xmlsec.signature.KeyInfo} of the {@link EncryptedData}
 * context.
 * 
 * The <code>URI</code> attribute value must be a same-document fragment identifier (via ID attribute).
 * Processing of external resources is not supported. Furthermore, the target of the reference must itself
 * contain either an {@link EncryptedKey} or a subsequent {@link KeyInfoReference}, up to a depth limit.
 * Other forms of resolution cannot be mixed together with this one.
 */
public class SimpleKeyInfoReferenceEncryptedKeyResolver extends AbstractEncryptedKeyResolver {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(SimpleKeyInfoReferenceEncryptedKeyResolver.class);
    
    /** Number of times to follow a reference before failing. */
    private int depthLimit;

    /** Constructor. */
    public SimpleKeyInfoReferenceEncryptedKeyResolver() {
        this((Set<String>)null);
    }

    /** 
     * Constructor. 
     * 
     * @param recipients the set of recipients
     */
    public SimpleKeyInfoReferenceEncryptedKeyResolver(@Nullable final Set<String> recipients) {
        super(recipients);
        depthLimit = 5;
    }
    
    /** 
     * Constructor. 
     * 
     * @param recipient the recipient
     */
    public SimpleKeyInfoReferenceEncryptedKeyResolver(@Nullable final String recipient) {
        this(Collections.singleton(recipient));
    }
    
    /**
     * Gets the reference depth limit.
     * 
     * @return the depth limit
     */
    public int getDepthLimit() {
        return depthLimit;
    }

    /**
     * Sets the reference depth limit, to a minimum of 1.
     * 
     * @param limit limit to set
     */
    public void setDepthLimit(final int limit) {
        depthLimit = Math.max(1, limit);
    }
    
    /** {@inheritDoc} */
    @Override
    @Nonnull public Iterable<EncryptedKey> resolve(@Nonnull final EncryptedData encryptedData) {
        Constraint.isNotNull(encryptedData, "EncryptedData cannot be null");
        
        return resolveKeyInfo(encryptedData.getKeyInfo(), depthLimit);
    }

    /**
     * Turn a KeyInfo into an EncryptedKey collection.
     * 
     * @param keyInfo KeyInfo to process
     * @param limit depth of references to follow
     * @return  encrypted keys
     */
    @Nonnull protected Iterable<EncryptedKey> resolveKeyInfo(@Nullable final KeyInfo keyInfo, final int limit) {
        final List<EncryptedKey> resolvedEncKeys = new ArrayList<>();
        
        if (keyInfo == null) {
            return resolvedEncKeys;
        }
        
        // The first time in, we don't directly resolve any keys, only references.
        // After that, we always start by looking inline.
        if (limit < depthLimit) {
            for (final EncryptedKey encKey : keyInfo.getEncryptedKeys()) {
                if (matchRecipient(encKey.getRecipient())) {
                    resolvedEncKeys.add(encKey);
                }
            }
        }
        
        if (limit == 0) {
            log.info("Reached depth limit for KeyInfoReferences");
        } else {
            for (final KeyInfoReference ref : keyInfo.getKeyInfoReferences()) {
                for (final EncryptedKey encKey : resolveKeyInfo(dereferenceURI(ref), limit-1)) {
                    resolvedEncKeys.add(encKey);
                }
            }
        }
        
        return resolvedEncKeys;
    }
    
    /**
     * Dereference the URI attribute of the specified retrieval method into a KeyInfo.
     * 
     * @param ref the KeyInfoReference to process
     * @return the dereferenced KeyInfo
     */
    @Nullable protected KeyInfo dereferenceURI(@Nonnull final KeyInfoReference ref) {
        final String uri = ref.getURI();
        if (uri == null || uri.isEmpty() || !uri.startsWith("#")) {
            log.warn("EncryptedKey KeyInfoReference did not contain a same-document URI reference, cannot process");
            return null;
        }
        final XMLObject target = ref.resolveIDFromRoot(uri.substring(1));
        if (target == null) {
            log.warn("EncryptedKey KeyInfoReference URI could not be dereferenced");
            return null;
        } else if (!(target instanceof KeyInfo)) {
            log.warn("The product of dereferencing the EncryptedKey KeyInfoReference was not a KeyInfo");
            return null;
        }
        return (KeyInfo) target;
    }

}