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

package org.opensaml.xmlsec.signature.support;

import java.security.Key;

import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.signature.XMLSignatureException;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.CredentialSupport;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.impl.SignatureImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A validator that validates an XML Signature on its content.
 */
public class SignatureValidator {

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(SignatureValidator.class);

    /** Credential used to validate signature. */
    private Credential validationCredential;

    /**
     * Constructor.
     * 
     * @param validatingCredential credential used to validate the signature
     */
    public SignatureValidator(Credential validatingCredential) {
        validationCredential = validatingCredential;
    }

    /** {@inheritDoc} */
    public void validate(Signature signature) throws SignatureException {
        log.debug("Attempting to validate signature using key from supplied credential");

        XMLSignature xmlSig = buildSignature(signature);

        Key validationKey = CredentialSupport.extractVerificationKey(validationCredential);
        if (validationKey == null) {
            log.debug("Supplied credential contained no key suitable for signature validation");
            throw new SignatureException("No key available to validate signature");
        }
        
        log.debug("Validating signature with signature algorithm URI: {}", signature.getSignatureAlgorithm());
        log.debug("Validation credential key algorithm '{}', key instance class '{}'", 
                validationKey.getAlgorithm(), validationKey.getClass().getName());

        try {
            if (xmlSig.checkSignatureValue(validationKey)) {
                log.debug("Signature validated with key from supplied credential");
                return;
            }
        } catch (XMLSignatureException e) {
            throw new SignatureException("Unable to evaluate key against signature", e);
        }

        log.debug("Signature did not validate against the credential's key");

        throw new SignatureException("Signature did not validate against the credential's key");
    }

    /**
     * Constructs an {@link XMLSignature} from the given signature object.
     * 
     * @param signature the signature
     * 
     * @return the constructed XMLSignature
     */
    protected XMLSignature buildSignature(Signature signature) {
        log.debug("Creating XMLSignature object");

        return ((SignatureImpl) signature).getXMLSignature();
    }

}