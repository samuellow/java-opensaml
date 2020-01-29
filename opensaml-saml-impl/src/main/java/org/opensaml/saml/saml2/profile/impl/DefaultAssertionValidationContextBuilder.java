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

package org.opensaml.saml.saml2.profile.impl;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import net.shibboleth.utilities.java.support.collection.LazySet;
import net.shibboleth.utilities.java.support.collection.Pair;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.primitive.StringSupport;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;

import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.messaging.MessageException;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.context.navigate.ChildContextLookup;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.profile.context.navigate.InboundMessageContextLookup;
import org.opensaml.saml.common.assertion.ValidationContext;
import org.opensaml.saml.common.binding.SAMLBindingSupport;
import org.opensaml.saml.common.messaging.context.SAMLMetadataContext;
import org.opensaml.saml.common.messaging.context.SAMLPeerEntityContext;
import org.opensaml.saml.common.messaging.context.SAMLProtocolContext;
import org.opensaml.saml.common.messaging.context.SAMLSelfEntityContext;
import org.opensaml.saml.criterion.EntityRoleCriterion;
import org.opensaml.saml.criterion.ProtocolCriterion;
import org.opensaml.saml.criterion.RoleDescriptorCriterion;
import org.opensaml.saml.saml2.assertion.SAML2AssertionValidationParameters;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.profile.impl.ValidateAssertions.AssertionValidationInput;
import org.opensaml.security.SecurityException;
import org.opensaml.security.credential.UsageType;
import org.opensaml.security.criteria.UsageCriterion;
import org.opensaml.security.messaging.ServletRequestX509CredentialAdapter;
import org.opensaml.security.x509.X509Credential;
import org.opensaml.xmlsec.context.SecurityParametersContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicates;

/**
 *  Function which implements default behavior for building an instance of {@link ValidationContext}
 *  from an instance of {@link AssertionValidationInput}.
 */
public class DefaultAssertionValidationContextBuilder 
        implements Function<AssertionValidationInput, ValidationContext> {
    
    /** Logger. */
    @Nullable private Logger log = LoggerFactory.getLogger(DefaultAssertionValidationContextBuilder.class);
    
    /** A function for resolving the signature validation CriteriaSet for a particular function. */
    private Function<Pair<ProfileRequestContext, Assertion>, CriteriaSet> signatureCriteriaSetFunction;
    
    /** Predicate for determining whether an Assertion signature is required. */
    private Predicate<ProfileRequestContext> signatureRequired;
    
    /** Predicate for determining whether an Assertion's network address(es) should be checked. */
    private Predicate<ProfileRequestContext> checkAddress;
    
    /** Function for determining the max allowed time since authentication. */
    private Function<ProfileRequestContext, Duration> maximumTimeSinceAuthn;

    /** Predicate for determining whether to include the self entityID as a valid Recipient. */
    private Predicate<ProfileRequestContext> includeSelfEntityIDAsRecipient;
    
    /** Function for determining additional valid audience values. */
    private Function<ProfileRequestContext, Set<String>> additionalAudiences;

    /** Resolver for security parameters context. */
    private Function<ProfileRequestContext, SecurityParametersContext> securityParametersLookupStrategy;

    /**
     * Constructor.
     */
    public DefaultAssertionValidationContextBuilder() {
        signatureRequired = Predicates.alwaysTrue();
        includeSelfEntityIDAsRecipient = Predicates.alwaysFalse();
        checkAddress = Predicates.alwaysTrue();

        securityParametersLookupStrategy = new ChildContextLookup<>(SecurityParametersContext.class)
                .compose(new InboundMessageContextLookup());
    }

    /**
     * Get the strategy by which to resolve a {@link SecurityParametersContext}.
     *
     * @return the lookup strategy
     */
    @Nonnull public Function<ProfileRequestContext, SecurityParametersContext> getSecurityParametersLookupStrategy() {
        return securityParametersLookupStrategy;
    }

    /**
     * Set the strategy by which to resolve a {@link SecurityParametersContext}.
     *
     * @param strategy the strategy function
     */
    public void setSecurityParametersLookupStrategy(
            @Nonnull final Function<ProfileRequestContext, SecurityParametersContext> strategy) {
        securityParametersLookupStrategy =
                Constraint.isNotNull(strategy, "SecurityParametersContext lookup strategy was null") ;
    }

    /**
     * Get the predicate which determines whether to include the self entityID as a valid Recipient.
     * 
     * <p>
     * Defaults to an always false predicate;
     * </p>
     * 
     * @return the predicate
     */
    public Predicate<ProfileRequestContext> getIncludeSelfEntityIDAsRecipient() {
        return includeSelfEntityIDAsRecipient;
    }

    /**
     * Set the predicate which determines whether to include the self entityID as a valid Recipient.
     * 
     * <p>
     * Defaults to an always false predicate.
     * </p>
     * 
     * @param predicate the predicate, must be non-null
     */
    public void setIncludeSelfEntityIDAsRecipient(final @Nonnull Predicate<ProfileRequestContext> predicate) {
        includeSelfEntityIDAsRecipient = Constraint.isNotNull(predicate, "Signature required predicate was null");
    }
    
    /**
     * Get the predicate which determines whether an Assertion signature is required.
     * 
     * <p>
     * Defaults to an always true predicate;
     * </p>
     * 
     * @return the predicate
     */
    public Predicate<ProfileRequestContext> getSignatureRequired() {
        return signatureRequired;
    }

    /**
     * Set the predicate which determines whether an Assertion signature is required.
     * 
     * <p>
     * Defaults to an always true predicate.
     * </p>
     * 
     * @param predicate the predicate, must be non-null
     */
    public void setSignatureRequired(final @Nonnull Predicate<ProfileRequestContext> predicate) {
        signatureRequired = Constraint.isNotNull(predicate, "Signature required predicate was null");
    }

    /**
     * Get the predicate which determines whether an Assertion's network address(es) should be checked.
     *
     * <p>
     * Defaults to an always true predicate;
     * </p>
     *
     * @return the predicate
     */
    public Predicate<ProfileRequestContext> getCheckAddress() {
        return checkAddress;
    }

    /**
     * Set the predicate which determines whether an Assertion's network address(es) should be checked.
     *
     * <p>
     * Defaults to an always true predicate.
     * </p>
     *
     * @param predicate the predicate, must be non-null
     */
    public void setCheckAddress(final @Nonnull Predicate<ProfileRequestContext> predicate) {
        checkAddress = Constraint.isNotNull(predicate, "Check address predicate was null");
    }

    /**
     * Get the function for determining additional audience values.
     *
     * <p>
     * Defaults to null.
     * </p>
     *
     * @return the function
     */
    public Function<ProfileRequestContext,Set<String>> getAdditionalAudiences() {
        return additionalAudiences;
    }

    /**
     * Set the function for determining additional audience values.
     *
     * <p>
     * Defaults to null.
     * </p>
     *
     * @param function the function, may be null
     */
    public void setAdditionalAudiences(final @Nonnull Function<ProfileRequestContext,Set<String>> function) {
        additionalAudiences = function;
    }

    /**
     * Get the function for determining the max allowed time since authentication.
     *
     * <p>
     * Defaults to null.
     * </p>
     *
     * @return the function
     */
    public Function<ProfileRequestContext,Duration> getMaximumTimeSinceAuthn() {
        return maximumTimeSinceAuthn;
    }

    /**
     * Set the function for determining the max allowed time since authentication.
     *
     * <p>
     * Defaults to null.
     * </p>
     *
     * @param function the function, may be null
     */
    public void setMaximumTimeSinceAuthn(final @Nonnull Function<ProfileRequestContext,Duration> function) {
        maximumTimeSinceAuthn = function;
    }

    /**
     * Get the function for resolving the signature validation CriteriaSet for a particular function.
     * 
     * <p>
     * Defaults to: {@code null}.
     * </p>
     * 
     * @return a criteria set instance, or null
     */
    @Nullable public Function<Pair<ProfileRequestContext, Assertion>, CriteriaSet> getSignatureCriteriaSetFunction() {
        return signatureCriteriaSetFunction;
    }

    /**
     * Set the function for resolving the signature validation CriteriaSet for a particular function.
     * 
     * <p>
     * Defaults to: {@code null}.
     * </p>
     * 
     * @param function the resolving function, may be null
     */
    public void setSignatureCriteriaSetFunction(
            @Nullable final Function<Pair<ProfileRequestContext, Assertion>, CriteriaSet> function) {
        signatureCriteriaSetFunction = function;
    }

    /** {@inheritDoc} */
    @Nullable public ValidationContext apply(@Nullable final AssertionValidationInput input) {
        if (input == null) {
            return null;
        }
        
        return new ValidationContext(buildStaticParameters(input));
    }
    
    /**
     * Build the static parameters map for input to the {@link ValidationContext}.
     * 
     * @param input the assertion validation input
     * 
     * @return the static parameters map
     */
    @Nonnull protected Map<String,Object> buildStaticParameters(
            @Nonnull final AssertionValidationInput input) {
        
        final HashMap<String, Object> staticParams = new HashMap<>();
        
        //For signature validation
        staticParams.put(SAML2AssertionValidationParameters.SIGNATURE_REQUIRED, 
                Boolean.valueOf(getSignatureRequired().test(input.getProfileRequestContext())));
        staticParams.put(SAML2AssertionValidationParameters.SIGNATURE_VALIDATION_CRITERIA_SET, 
                getSignatureCriteriaSet(input));
        final SecurityParametersContext securityParameters = getSecurityParametersLookupStrategy()
                .apply(input.getProfileRequestContext());
        if (securityParameters != null && securityParameters.getSignatureValidationParameters() != null) {
            staticParams.put(SAML2AssertionValidationParameters.SIGNATURE_VALIDATION_TRUST_ENGINE,
                    securityParameters.getSignatureValidationParameters().getSignatureTrustEngine());
        }
        
        // For HoK subject confirmation
        final X509Certificate attesterCertificate = getAttesterCertificate(input);
        if (attesterCertificate != null) {
            staticParams.put(SAML2AssertionValidationParameters.SC_HOK_PRESENTER_CERT, attesterCertificate);
        }
        final PublicKey attesterPublicKey = getAttesterPublicKey(input);
        if (attesterPublicKey != null) {
            staticParams.put(SAML2AssertionValidationParameters.SC_HOK_PRESENTER_KEY, attesterPublicKey);
        }
        
        final Set<InetAddress> validAddresses = getValidAddresses(input);
        final Boolean checkAddressEnabled = Boolean.valueOf(getCheckAddress().test(input.getProfileRequestContext()));
        
        // For SubjectConfirmationData
        staticParams.put(SAML2AssertionValidationParameters.SC_VALID_RECIPIENTS, getValidRecipients(input));
        staticParams.put(SAML2AssertionValidationParameters.SC_VALID_ADDRESSES, validAddresses);
        staticParams.put(SAML2AssertionValidationParameters.SC_CHECK_ADDRESS, checkAddressEnabled);
        
        // For Audience Condition
        staticParams.put(SAML2AssertionValidationParameters.COND_VALID_AUDIENCES, getValidAudiences(input));
        
        // For AuthnStatement
        staticParams.put(SAML2AssertionValidationParameters.STMT_AUTHN_VALID_ADDRESSES, validAddresses);
        staticParams.put(SAML2AssertionValidationParameters.STMT_AUTHN_CHECK_ADDRESS, checkAddressEnabled);
        if (getMaximumTimeSinceAuthn() != null) {
            staticParams.put(SAML2AssertionValidationParameters.STMT_AUTHN_MAX_TIME, 
                    getMaximumTimeSinceAuthn().apply(input.getProfileRequestContext()));
        }
        
        log.trace("Built static parameters map: {}", staticParams);
        
        return staticParams;
    }
    
    /**
     * Get the signature validation criteria set.
     * 
     * <p>
     * This implementation first evaluates the result of applying the function 
     * {@link #getSignatureCriteriaSetFunction()}, if configured. If that evaluation did not
     * produce an {@link EntityIdCriterion}, one is added based on the issuer of the {@link Assertion}.
     * If that evaluation did not produce an instance of {@link UsageCriterion}, one is added with
     * the value of {@link UsageType#SIGNING}.
     * </p>
     * 
     * <p>
     * Finally the following criteria are added if not already present and if the corresponding data
     * is available in the inbound {@link MessageContext}:
     * </p>
     * <ul>
     *  <li>{@link RoleDescriptorCriterion}</li>
     *  <li>{@link EntityRoleCriterion}</li>
     *  <li>{@link ProtocolCriterion}</li>
     * </ul>
     * 
     * @param input the assertion validation input
     *
     * @return the criteria set based on the message context data
     */
    @Nonnull protected CriteriaSet getSignatureCriteriaSet(@Nonnull final AssertionValidationInput input) {
        final CriteriaSet criteriaSet = new CriteriaSet();
        
        if (getSignatureCriteriaSetFunction() != null) {
            final CriteriaSet dynamicCriteria = getSignatureCriteriaSetFunction().apply(
                    new Pair<>(input.getProfileRequestContext(), input.getAssertion()));
            if (dynamicCriteria != null) {
                criteriaSet.addAll(dynamicCriteria);
            }
        }
        
        if (!criteriaSet.contains(EntityIdCriterion.class)) {
            String issuer = null;
            if (input.getAssertion().getIssuer() != null) {
                issuer = StringSupport.trimOrNull(input.getAssertion().getIssuer().getValue());
            }
            if (issuer != null) {
                log.debug("Adding internally-generated EntityIdCriterion with value of: {}", issuer);
                criteriaSet.add(new EntityIdCriterion(issuer));
            }
        }
        
        if (!criteriaSet.contains(UsageCriterion.class)) {
            log.debug("Adding internally-generated UsageCriterion with value of: {}", UsageType.SIGNING);
            criteriaSet.add(new UsageCriterion(UsageType.SIGNING));
        }
        
        final MessageContext inboundContext = input.getProfileRequestContext().getInboundMessageContext();
        if (inboundContext != null) {
            populateSignatureCriteriaFromInboundContext(criteriaSet, inboundContext);
        }

        log.debug("Resolved Signature validation CriteriaSet: {}", criteriaSet);
        
        return criteriaSet;
    }

    /**
     * Populate signature criteria from the specified {@link MessageContext}.
     *
     * <ul>
     *  <li>{@link RoleDescriptorCriterion}</li>
     *  <li>{@link EntityRoleCriterion}</li>
     *  <li>{@link ProtocolCriterion}</li>
     * </ul>
     *
     * @param criteriaSet the criteria set to populate
     * @param inboundContext the inbound message context
     */
    protected void populateSignatureCriteriaFromInboundContext(@Nonnull final CriteriaSet criteriaSet,
            @Nonnull final MessageContext inboundContext) {

        final SAMLPeerEntityContext peerContext = inboundContext.getSubcontext(SAMLPeerEntityContext.class);
        if (peerContext != null) {
            if (!criteriaSet.contains(RoleDescriptorCriterion.class)) {
                final SAMLMetadataContext metadataContext = peerContext.getSubcontext(SAMLMetadataContext.class);
                if (metadataContext != null && metadataContext.getRoleDescriptor() != null) {
                    criteriaSet.add(new RoleDescriptorCriterion(metadataContext.getRoleDescriptor()));
                }
            }
            if (!criteriaSet.contains(EntityRoleCriterion.class)) {
                final QName role = peerContext.getRole();
                if (role != null) {
                    criteriaSet.add(new EntityRoleCriterion(role));
                }
            }
        }

        final SAMLProtocolContext protocolContext = inboundContext.getSubcontext(SAMLProtocolContext.class);
        if (!criteriaSet.contains(ProtocolCriterion.class)
                && protocolContext != null && protocolContext.getProtocol() != null) {
            criteriaSet.add(new ProtocolCriterion(protocolContext.getProtocol()));
        }
    }

    /**
     * Get the attesting entity's {@link X509Certificate}.
     * 
     * <p>
     * This implementation returns the client TLS certificate present in the 
     * {@link javax.servlet.http.HttpServletRequest}, or null if one is not present.
     * </p>
     * 
     * @param input the assertion validation input
     * 
     * @return the entity certificate, or null
     */
    @Nullable protected X509Certificate getAttesterCertificate(
            @Nonnull final AssertionValidationInput input) {
        try {
            final X509Credential credential = new ServletRequestX509CredentialAdapter(input.getHttpServletRequest());
            return credential.getEntityCertificate();
        } catch (final SecurityException e) {
            log.debug("Peer TLS X.509 certificate was not present. " 
                    + "Holder-of-key proof-of-possession via client TLS cert will not be possible");
            return null;
        }
    }

    /**
     * Get the attesting entity's {@link PublicKey}.
     * 
     * <p>
     * This implementation returns null. Subclasses should override to implement specific logic.
     * </p>
     * 
     * @param input the assertion validation input
     * 
     * @return the entity public key, or null
     */
    @Nullable protected PublicKey getAttesterPublicKey(@Nonnull final AssertionValidationInput input) {
        return null;
    }
    
    /**
     * Get the valid recipient endpoints for attestation.
     * 
     * <p>
     * This implementation returns a set containing the 2 values;
     * </p>
     * <ol>
     * <li>
     * the result of evaluating
     * {@link SAMLBindingSupport#getActualReceiverEndpointURI(MessageContext, javax.servlet.http.HttpServletRequest)}
     * </li>
     * <li>
     * if enabled via the eval of {@link #getIncludeSelfEntityIDAsRecipient()}, the value from evaluating
     * {@link #getSelfEntityID(AssertionValidationInput)} if non-null
     * 
     * </li>
     * </ol>
     * 
     * @param input the assertion validation input
     * 
     * @return set of recipient endpoint URI's
     */
    @Nonnull protected Set<String> getValidRecipients(@Nonnull final AssertionValidationInput input) {
        final LazySet<String> validRecipients = new LazySet<>();
        
        try {
            final String endpoint = SAMLBindingSupport.getActualReceiverEndpointURI(
                    input.getProfileRequestContext().getInboundMessageContext(), input.getHttpServletRequest());
            if (endpoint != null) {
                validRecipients.add(endpoint);
            }
        } catch (final MessageException e) {
            log.warn("Attempt to resolve recipient endpoint failed", e);
        }
        
        if (getIncludeSelfEntityIDAsRecipient().test(input.getProfileRequestContext())) {
            final String selfEntityID = getSelfEntityID(input);
            if (selfEntityID != null) {
                validRecipients.add(selfEntityID);
            }
        }
        
        log.debug("Resolved valid subject confirmation recipients set: {}", validRecipients);
        return validRecipients;
    }

    /**
     * Get the set of addresses which are valid for subject confirmation.
     * 
     * <p>
     * This implementation simply returns the set based on 
     * {@link #getAttesterIPAddress(AssertionValidationInput)}, if that produces a value.
     * Otherwise an empty set is returned.
     * </p>
     * 
     * @param input the assertion validation input
     * 
     * @return the set of valid addresses
     */
    @Nonnull protected Set<InetAddress> getValidAddresses(@Nonnull final AssertionValidationInput input) {
        try {
            final LazySet<InetAddress> validAddresses = new LazySet<>();
            InetAddress[] addresses = null;
            final String attesterIPAddress = getAttesterIPAddress(input);
            log.debug("Saw attester IP address: {}", attesterIPAddress);
            if (attesterIPAddress != null) {
                addresses = InetAddress.getAllByName(attesterIPAddress);
                validAddresses.addAll(Arrays.asList(addresses));
                log.debug("Resolved valid subject confirmation InetAddress set: {}", validAddresses);
                return validAddresses;
            }
            log.warn("Could not determine attester IP address. Validation of Assertion may or may not succeed");
            return Collections.emptySet();
        } catch (final UnknownHostException e) {
            log.warn("Processing of attester IP address failed. Validation of Assertion may or may not succeed", e);
            return Collections.emptySet();
        }
    }
    
    /**
     * Get the attester's IP address.
     * 
     * <p>
     * This implementation returns the value of {@link javax.servlet.http.HttpServletRequest#getRemoteAddr()}.
     * </p>
     * 
     * @param input the assertion validation input
     * 
     * @return the IP address of the attester
     */
    @Nonnull protected String getAttesterIPAddress(@Nonnull final AssertionValidationInput input) {
        //TODO support indirection via SAMLBindingSupport and use of SAMLMessageReceivedEndpointContext?
        return input.getHttpServletRequest().getRemoteAddr();
    }
    
    /**
     * Get the valid audiences for attestation.
     * 
     * <p>
     * This implementation returns a set containing the union of:
     * </p>
     * <ol>
     * <li>the result of {@link #getSelfEntityID(AssertionValidationInput)}, if non-null</li>
     * <li>the result of evaluating {@link #getAdditionalAudiences()}, if non-null</li>
     * </ol>
     * 
     * @param input the assertion validation input
     * 
     * @return set of audience URI's
     */
    @Nonnull protected Set<String> getValidAudiences(@Nonnull final AssertionValidationInput input) {
        final LazySet<String> validAudiences = new LazySet<>();
        
        final String selfEntityID = getSelfEntityID(input);
        if (selfEntityID != null) {
            validAudiences.add(selfEntityID);
        }
        
        if (getAdditionalAudiences() != null) {
            final Set<String> additional = getAdditionalAudiences().apply(input.getProfileRequestContext());
            if (additional != null) {
                validAudiences.addAll(additional);
            }
        }
        
        log.debug("Resolved valid audiences set: {}", validAudiences);
        return validAudiences;
    }
    
    /**
     * Get the self entityID.
     * 
     * @param input the assertion validation input
     * 
     * @return the self entityID, or null if could not be resolved
     */
    @Nullable protected String getSelfEntityID(@Nonnull final AssertionValidationInput input) {
        final SAMLSelfEntityContext selfContext = input.getProfileRequestContext()
                .getInboundMessageContext()
                .getSubcontext(SAMLSelfEntityContext.class);
        
        if (selfContext != null) {
            return selfContext.getEntityId();
        }
        
        return null;
    }

}