/*
 * Licensed to Apereo under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Apereo licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License.  You may obtain a
 * copy of the License at the following location:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jasig.cas.web.view;

import org.apache.commons.lang3.StringUtils;
import org.jasig.cas.CasProtocolConstants;
import org.springframework.web.servlet.view.AbstractUrlBasedView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Renders and prepares CAS2 views. This view is responsible
 * to simply just prep the base model, and delegates to
 * a the real view to render the final output.
 * @author Misagh Moayyed
 * @since 4.1.0
 */
public class Cas30ResponseView extends Cas20ResponseView {

    /**
     * Represents the collection of attributes in the view.
     */
    public static final String MODEL_ATTRIBUTE_NAME_ATTRIBUTES = "attributes";

    /**
     * Represents the authentication date object in the view.
     */
    public static final String MODEL_ATTRIBUTE_NAME_AUTHENTICATION_DATE = "authenticationDate";

    /**
     * Represents the flag to note whether assertion is backed by new login.
     */
    public static final String MODEL_ATTRIBUTE_NAME_FROM_NEW_LOGIN = "isFromNewLogin";

    /**
     * Represents the flag to note the principal credential used to establish
     * a successful authentication event.
     */
    public static final String MODEL_ATTRIBUTE_NAME_PRINCIPAL_CREDENTIAL = "credential";

    /**
     * Instantiates a new Abstract cas jstl view.
     *
     * @param view the view
     */
    protected Cas30ResponseView(final AbstractUrlBasedView view) {
        super(view);
    }

    @Override
    protected void prepareMergedOutputModel(final Map<String, Object> model, final HttpServletRequest request,
                                            final HttpServletResponse response) throws Exception {

        super.prepareMergedOutputModel(model, request, response);

        final Map<String, Object> attributes = new HashMap<>(getPrincipalAttributesAsMultiValuedAttributes(model));
        attributes.put(MODEL_ATTRIBUTE_NAME_AUTHENTICATION_DATE, Collections.singleton(getAuthenticationDate(model)));
        attributes.put(MODEL_ATTRIBUTE_NAME_FROM_NEW_LOGIN, Collections.singleton(isAssertionBackedByNewLogin(model)));
        attributes.put(CasProtocolConstants.VALIDATION_REMEMBER_ME_ATTRIBUTE_NAME,
                Collections.singleton(isRememberMeAuthentication(model)));

        final String credential = super.getCredentialPasswordFromAuthentication(model);
        if (StringUtils.isNotBlank(credential)) {
            logger.debug("Obtained credential password is passed to the CAS payload under [{}]",
                    MODEL_ATTRIBUTE_NAME_PRINCIPAL_CREDENTIAL);
            attributes.put(MODEL_ATTRIBUTE_NAME_PRINCIPAL_CREDENTIAL, Collections.singleton(credential));
        }
        super.putIntoModel(model, MODEL_ATTRIBUTE_NAME_ATTRIBUTES, attributes);
    }
}
