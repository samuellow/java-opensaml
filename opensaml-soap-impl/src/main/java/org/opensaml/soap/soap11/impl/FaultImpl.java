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

package org.opensaml.soap.soap11.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.opensaml.core.xml.AbstractXMLObject;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.soap.soap11.Detail;
import org.opensaml.soap.soap11.Fault;
import org.opensaml.soap.soap11.FaultActor;
import org.opensaml.soap.soap11.FaultCode;
import org.opensaml.soap.soap11.FaultString;

/**
 * Concrete implemenation of {@link org.opensaml.soap.soap11.Fault}.
 */
public class FaultImpl extends AbstractXMLObject implements Fault {

    /** Fault code. */
    private FaultCode faultCode;

    /** Fault message. */
    private FaultString message;

    /** Actor that faulted. */
    private FaultActor actor;

    /** Details of the fault. */
    private Detail detail;

    /**
     * Constructor.
     * 
     * @param namespaceURI namespace of the element
     * @param elementLocalName name of the element
     * @param namespacePrefix namespace prefix of the element
     */
    protected FaultImpl(final String namespaceURI, final String elementLocalName, final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

    /** {@inheritDoc} */
    public FaultCode getCode() {
        return faultCode;
    }

    /** {@inheritDoc} */
    public void setCode(final FaultCode newFaultCode) {
        faultCode = prepareForAssignment(faultCode, newFaultCode);
    }

    /** {@inheritDoc} */
    public FaultString getMessage() {
        return message;
    }

    /** {@inheritDoc} */
    public void setMessage(final FaultString newMessage) {
        message = prepareForAssignment(message, newMessage);
    }

    /** {@inheritDoc} */
    public FaultActor getActor() {
        return actor;
    }

    /** {@inheritDoc} */
    public void setActor(final FaultActor newActor) {
        actor = prepareForAssignment(actor, newActor);
    }

    /** {@inheritDoc} */
    public Detail getDetail() {
        return detail;
    }

    /** {@inheritDoc} */
    public void setDetail(final Detail newDetail) {
        detail = prepareForAssignment(detail, newDetail);
    }

    /** {@inheritDoc} */
    public List<XMLObject> getOrderedChildren() {
        final ArrayList<XMLObject> children = new ArrayList<>();

        children.add(faultCode);
        children.add(message);
        children.add(actor);
        children.add(detail);

        return Collections.unmodifiableList(children);
    }
}
