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

package org.opensaml.saml2.core.validator;

import org.opensaml.saml2.core.AuthnStatement;
import org.opensaml.xml.validation.ValidationException;
import org.opensaml.xml.validation.Validator;

/**
 * Checks {@link org.opensaml.saml2.core.AuthnStatement} for Schema compliance.
 */
public class AuthnStatementSchemaValidator implements Validator<AuthnStatement> {

    /** Constructor */
    public AuthnStatementSchemaValidator() {

    }

    /*
     * @see org.opensaml.xml.validation.Validator#validate(org.opensaml.xml.XMLObject)
     */
    public void validate(AuthnStatement authnStatement) throws ValidationException {
        validateAuthnInstant(authnStatement);
        validateAuthnContext(authnStatement);
    }

    /**
     * Checks that the AuthnInstant attribute is present.
     * 
     * @param authnStatement
     * @throws ValidationException
     */
    protected void validateAuthnInstant(AuthnStatement authnStatement) throws ValidationException {
        if (authnStatement.getAuthnInstant() == null) {
            throw new ValidationException("AuthnInstant required");
        }
    }

    /**
     * Checks that the AuthnContext element is present.
     * 
     * @param authnStatement
     * @throws ValidationException
     */
    protected void validateAuthnContext(AuthnStatement authnStatement) throws ValidationException {
        if (authnStatement.getAuthnContext() == null) {
            throw new ValidationException("AuthnContext required");
        }
    }
}