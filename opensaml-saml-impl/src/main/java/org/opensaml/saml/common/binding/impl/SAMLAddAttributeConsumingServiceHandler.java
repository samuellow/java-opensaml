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

package org.opensaml.saml.common.binding.impl;

import java.util.List;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.logic.Constraint;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.core.xml.util.XMLObjectSupport.CloneOutputOption;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.context.navigate.ChildContextLookup;
import org.opensaml.messaging.handler.AbstractMessageHandler;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.saml.common.messaging.context.AttributeConsumingServiceContext;
import org.opensaml.saml.common.messaging.context.SAMLMetadataContext;
import org.opensaml.saml.common.messaging.context.SAMLPeerEntityContext;
import org.opensaml.saml.ext.reqattr.RequestedAttributes;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.Extensions;
import org.opensaml.saml.saml2.metadata.AttributeConsumingService;
import org.opensaml.saml.saml2.metadata.RequestedAttribute;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SAML {@link org.opensaml.messaging.handler.MessageHandler} that attaches an {@link AttributeConsumingServiceContext}
 * to the {@link SAMLMetadataContext} based on the content of an {@link AuthnRequest} in the message context.
 */
public class SAMLAddAttributeConsumingServiceHandler extends AbstractMessageHandler {
    
    /** Logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(SAMLAddAttributeConsumingServiceHandler.class);

    /** Lookup strategy for {@link SAMLMetadataContext}. */
    @Nonnull private Function<MessageContext,SAMLMetadataContext> metadataContextLookupStrategy;
   
    /** Lookup strategy for an {@link AuthnRequest} index. */
    @Nonnull private Function<MessageContext, AuthnRequest> authnRequestLookupStrategy;

    /** {@link AttributeConsumingService} index - if specified. */
    
    @Nullable private Integer index;

    /** {@link RequestedAttribute} list - if specified. */
    @Nullable private List<RequestedAttribute> requestedAttributes;

    /**
     * Constructor.
     */
    public SAMLAddAttributeConsumingServiceHandler() {
        metadataContextLookupStrategy =
                new ChildContextLookup<>(SAMLMetadataContext.class).compose(
                        new ChildContextLookup<>(SAMLPeerEntityContext.class));
        authnRequestLookupStrategy = new AuthnRequestLookup();
    }

    /**
     * Set the strategy to locate the {@link SAMLMetadataContext} from the {@link MessageContext}.
     * 
     * @param strategy lookup strategy
     */
    public void setMetadataContextLookupStrategy(@Nonnull final Function<MessageContext,SAMLMetadataContext> strategy) {
        metadataContextLookupStrategy = Constraint.isNotNull(strategy,
                "SAMLMetadataContext lookup strategy cannot be null");
    }

    /**
     * Set the strategy to locate the {@link AttributeConsumingService} index from the {@link MessageContext}.
     * 
     * @param strategy lookup strategy
     */
    public void setIndexLookupStrategy(@Nullable final Function<MessageContext,AuthnRequest> strategy) {
        authnRequestLookupStrategy = Constraint.isNotNull(strategy,
                "AuthnRequest lookup strategy cannot be null");
    }
    
    /** {@inheritDoc} */
    @Override
    protected boolean doPreInvoke(@Nonnull final MessageContext messageContext) throws MessageHandlerException {
        
        if (!super.doPreInvoke(messageContext)) {
            return false;
        }

        final AuthnRequest authn = authnRequestLookupStrategy.apply(messageContext);

        if (authn != null) {
            index = authn.getAttributeConsumingServiceIndex();
            requestedAttributes = getRequestedAttributes(authn);

            if (index != null && requestedAttributes != null && !requestedAttributes.isEmpty()) {
                log.info("{} AuthnRequest from {} contained a AttributeConsumingServiceIndex"
                        + " and RequestedAttributes; ignoring the RequestedAttributes.",
                        getLogPrefix(), authn.getProviderName());
                requestedAttributes = null;
            }
        }
        return true;
    }

    /** {@inheritDoc}*/
    @Override protected void doInvoke(@Nonnull final MessageContext messageContext) throws MessageHandlerException {
        final SAMLMetadataContext metadataContext = metadataContextLookupStrategy.apply(messageContext);
        if (metadataContext == null) {
            log.debug("{} No metadata context found, nothing to do", getLogPrefix());
            return;
        } else if (!(metadataContext.getRoleDescriptor() instanceof SPSSODescriptor)) {
            log.debug("{} Metadata context did not contain an SPSSODescriptor, nothing to do", getLogPrefix());
            return;
        }
        
        final SPSSODescriptor ssoDescriptor = (SPSSODescriptor) metadataContext.getRoleDescriptor();
        
        AttributeConsumingService acs = null;
        if (null != index) {
            log.debug("{} Request specified AttributeConsumingService index {}", getLogPrefix(), index);
            for (final AttributeConsumingService acsEntry : ssoDescriptor.getAttributeConsumingServices()) {
                if (index.intValue() == acsEntry.getIndex()) {
                    acs = acsEntry;
                    break;
                }
            }
        }
        if (null == acs) {
            if (requestedAttributes != null && !requestedAttributes.isEmpty()) {
                log.debug("{} Creating AttributeConsumingService with requested Attributes {}", 
                        getLogPrefix(), requestedAttributes);
                acs = attributeConsumingServiceFromRequestedAttributes();
            } else {
                log.debug("{} Selecting default AttributeConsumingService, if any", getLogPrefix());
                acs = ssoDescriptor.getDefaultAttributeConsumingService();
            }
        }
        if (null != acs) {
            log.debug("{} Selected AttributeConsumingService with index {}", getLogPrefix(), acs.getIndex());
            metadataContext.getSubcontext(
                    AttributeConsumingServiceContext.class, true).setAttributeConsumingService(acs);
        } else {
            log.debug("{} No AttributeConsumingService selected", getLogPrefix());
        }
    }

    /** Generate an {@link AttributeConsumingService } from the {@link RequestedAttributes}.
     * @return a suitable AttributeConsumingService
     * @throws MessageHandlerException when the cloning failed
     */
    private AttributeConsumingService attributeConsumingServiceFromRequestedAttributes() 
            throws MessageHandlerException {
        final AttributeConsumingService newAcs = (AttributeConsumingService)
                XMLObjectSupport.buildXMLObject(AttributeConsumingService.DEFAULT_ELEMENT_NAME);
        for (final RequestedAttribute attribute: requestedAttributes) {
            try {
                newAcs.getRequestedAttributes().add(
                        XMLObjectSupport.cloneXMLObject(attribute, CloneOutputOption.DropDOM));
            } catch (final MarshallingException | UnmarshallingException e) {
                log.warn("{} Error cloning requested Attributes: {}", getLogPrefix(), e.getMessage());
                throw new MessageHandlerException(e);
            }
        }
        return newAcs;
    }

    /** Grab the {@link RequestedAttribute} (if any) from the {@link AuthnRequest}.
     * @param authn the request to interrogate
     * @return null or the list.
     */
    private List<RequestedAttribute> getRequestedAttributes(final AuthnRequest authn) {
        final Extensions extensions = authn.getExtensions();
        if (extensions == null) {
            return null;
        }
        final List<XMLObject> bindings = extensions.getUnknownXMLObjects(RequestedAttributes.DEFAULT_ELEMENT_NAME);
        if (bindings == null || bindings.isEmpty()) {
            return null;
        }
        return ((RequestedAttributes)bindings.get(0)).getRequestedAttributes();
    }

    /** Default lookup function that find a SAML 2 {@link AuthnRequest}. */
    private class AuthnRequestLookup implements Function<MessageContext,AuthnRequest> {

        /** {@inheritDoc} */
        @Override
        public AuthnRequest apply(@Nullable final MessageContext input) {
            if (input != null) {
                final Object message = input.getMessage();
                if (message != null && message instanceof AuthnRequest) {
                    return (AuthnRequest) message;
                }
            }
            
            return null;
        }
    }
}