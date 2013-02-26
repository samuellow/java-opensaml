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

package org.opensaml.util.storage;

import javax.annotation.Nonnull;

import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;

import com.google.common.base.Optional;

/**
 * Represents a versioned record in a {@link StorageService}.
 */
public class StorageRecord {

    /** Version field. */
    private int version;
    
    /** Value field. */
    private String value;
    
    /** Expiration field. */
    private Long expiration;
    
    /**
     * Constructor.
     *
     * @param val   value
     * @param exp   optional expiration
     */
    public StorageRecord(@Nonnull @NotEmpty final String val, @Nonnull final Optional<Long> exp) {
        version = 1;
        value = val;
        expiration = exp.orNull();
    }
    
    /**
     * Get the record version.
     * 
     * @return  the record version
     */
    public int getVersion() {
        return version;
    }

    /**
     * Get the record value.
     * 
     * @return  the record value
     */
    @Nonnull public String getValue() {
        return value;
    }

    /**
     * Get the optional record expiration.
     * 
     * @return  the optional record expiration
     */
    @Nonnull public Optional<Long> getExpiration() {
        return Optional.fromNullable(expiration);
    }

    /**
     * Set the record value.
     * 
     * @param val   the new record value
     */
    protected void setValue(@Nonnull @NotEmpty final String val) {
        value = val;
    }
    
    /**
     * Set the optional record expiration.
     * 
     * @param exp   the new record expiration
     */
    protected void setExpiration(@Nonnull Optional<Long> exp) {
        expiration = exp.orNull();
    }
    
    /**
     * Increment the record version and returns the new value.
     * 
     * @return  the updated version
     */
    protected int incrementVersion() {
        return ++version;
    }
    
}