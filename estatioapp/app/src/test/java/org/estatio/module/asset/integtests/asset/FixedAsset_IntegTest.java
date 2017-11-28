package org.estatio.module.asset.integtests.asset;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.wrapper.InvalidException;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.dom.PropertyRepository;
import org.estatio.module.asset.dom.role.FixedAssetRole;
import org.estatio.module.asset.dom.role.FixedAssetRoleRepository;
import org.estatio.module.asset.dom.role.FixedAssetRoleTypeEnum;
import org.estatio.module.asset.fixtures.property.personas.PropertyAndOwnerAndManagerForKalNl;
import org.estatio.module.asset.integtests.AssetModuleIntegTestAbstract;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.PartyRepository;
import org.estatio.module.party.fixtures.organisation.personas.OrganisationForAcmeNl;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class FixedAsset_IntegTest extends AssetModuleIntegTestAbstract {

    @Inject
    PartyRepository partyRepository;

    @Inject
    PropertyRepository properties;

    @Inject
    FixedAssetRoleRepository fixedAssetRoles;

    Party party;

    Property property;

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                executionContext.executeChild(this, new PropertyAndOwnerAndManagerForKalNl());
            }
        });
    }

    @Before
    public void setUp() {
        party = partyRepository.findPartyByReferenceOrNull(OrganisationForAcmeNl.REF);
        assertNotNull(party);

        property = properties.findPropertyByReference(PropertyAndOwnerAndManagerForKalNl.REF);
        assertThat(property.getReference(), is(PropertyAndOwnerAndManagerForKalNl.REF));

        List<FixedAssetRole> allFixedAssetRoles = fixedAssetRoles.findAllForProperty(property);
        assertThat(allFixedAssetRoles.size(), is(2));
        assertThat(allFixedAssetRoles.get(0).getStartDate(), is(new LocalDate(1999, 1, 1)));
        assertNull(allFixedAssetRoles.get(1).getStartDate());
    }

    public static class NewRole extends FixedAsset_IntegTest {

        @Test
        public void happyCase() throws Exception {
            // when
            wrap(property).newRole(
                    FixedAssetRoleTypeEnum.PROPERTY_OWNER, party, new LocalDate(2014, 1, 1), new LocalDate(2014, 12, 31));
            wrap(property).newRole(FixedAssetRoleTypeEnum.PROPERTY_OWNER, party, new LocalDate(2015, 1, 1), null);

            // then
            assertThat(fixedAssetRoles.findAllForProperty(property).size(), is(4));
        }

        @Test(expected = InvalidException.class)
        public void sadCase() throws Exception {
            // when
            wrap(property).newRole(
                    FixedAssetRoleTypeEnum.PROPERTY_OWNER, party, new LocalDate(2014, 1, 1), new LocalDate(2014, 12, 31));
            wrap(property).newRole(FixedAssetRoleTypeEnum.PROPERTY_OWNER, party, new LocalDate(2014, 12, 31), null);

            // then
            assertThat(fixedAssetRoles.findAllForProperty(property).size(), is(2));
        }
    }
}