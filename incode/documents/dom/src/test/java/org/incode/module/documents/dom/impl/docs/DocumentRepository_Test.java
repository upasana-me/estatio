/*
 *  Copyright 2016 Dan Haywood
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.incode.module.documents.dom.impl.docs;

import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.services.repository.RepositoryService;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

public class DocumentRepository_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock
    RepositoryService mockRepositoryService;

    @Mock
    DocumentRepository documentRepository;

    @Before
    public void setUp() throws Exception {
        documentRepository.repositoryService = mockRepositoryService;
    }

    public static class Create_Test extends DocumentRepository_Test {

        @Ignore
        @Test
        public void happy_case() throws Exception {

        }
    }

    public static class FindBetween_Test extends DocumentRepository_Test {

        @Ignore
        @Test
        public void when_start_date_and_end_date_both_specified() throws Exception {

        }

        @Ignore
        @Test
        public void when_start_date_only_is_specified() throws Exception {

        }
    }


}