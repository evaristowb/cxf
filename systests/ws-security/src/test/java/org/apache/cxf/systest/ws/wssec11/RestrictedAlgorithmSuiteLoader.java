/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.cxf.systest.ws.wssec11;

import org.apache.cxf.Bus;
import org.apache.cxf.ws.security.policy.custom.AlgorithmSuiteLoader;
import org.apache.neethi.Assertion;
import org.apache.neethi.Policy;
import org.apache.wss4j.policy.SPConstants;
import org.apache.wss4j.policy.model.AbstractSecurityAssertion;
import org.apache.wss4j.policy.model.AlgorithmSuite;

/**
 * This class retrieves a custom AlgorithmSuite for use with restricted security policies
 */
public class RestrictedAlgorithmSuiteLoader implements AlgorithmSuiteLoader {
    
    public RestrictedAlgorithmSuiteLoader(Bus bus) {
        bus.setExtension(this, AlgorithmSuiteLoader.class);
    }

    public AlgorithmSuite getAlgorithmSuite(SPConstants.SPVersion version, Policy nestedPolicy) {
        return new CustomAlgorithmSuite(version, nestedPolicy); 
    }

    private static class CustomAlgorithmSuite extends AlgorithmSuite {

        CustomAlgorithmSuite(SPConstants.SPVersion version, Policy nestedPolicy) {
            super(version, nestedPolicy);
        }

        @Override
        protected AbstractSecurityAssertion cloneAssertion(Policy nestedPolicy) {
            return new CustomAlgorithmSuite(getVersion(), nestedPolicy);
        }

        @Override
        protected void parseCustomAssertion(Assertion assertion) {
            String assertionName = assertion.getName().getLocalPart();
            
            AlgorithmSuiteType algorithmSuiteType = algorithmSuiteTypes.get(assertionName);
            
            setAlgorithmSuiteType(new AlgorithmSuiteType(
                    assertionName,
                    algorithmSuiteType.getDigest(),
                    algorithmSuiteType.getEncryption(),
                    algorithmSuiteType.getSymmetricKeyWrap(),
                    algorithmSuiteType.getAsymmetricKeyWrap(),
                    algorithmSuiteType.getEncryptionKeyDerivation(),
                    algorithmSuiteType.getSignatureKeyDerivation(),
                    algorithmSuiteType.getEncryptionDerivedKeyLength(),
                    algorithmSuiteType.getSignatureDerivedKeyLength(),
                    algorithmSuiteType.getMinimumSymmetricKeyLength(),
                    algorithmSuiteType.getMaximumSymmetricKeyLength(),
                    512,
                    algorithmSuiteType.getMaximumAsymmetricKeyLength()));
        }
    }
}
