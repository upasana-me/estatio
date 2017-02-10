package org.estatio.integtests.budget;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.asset.dom.Property;
import org.estatio.asset.dom.PropertyRepository;
import org.estatio.budget.dom.budget.Budget;
import org.estatio.budget.dom.budget.BudgetRepository;
import org.estatio.budget.dom.budgetcalculation.BudgetCalculation;
import org.estatio.budget.dom.budgetcalculation.BudgetCalculationRepository;
import org.estatio.budget.dom.budgetcalculation.BudgetCalculationService;
import org.estatio.budget.dom.budgetcalculation.BudgetCalculationType;
import org.estatio.budget.dom.budgetcalculation.Status;
import org.estatio.budget.dom.keyitem.KeyItem;
import org.estatio.budget.dom.partioning.PartitionItem;
import org.estatio.budget.dom.partioning.PartitionItemRepository;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.charge.ChargeRepository;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.asset.PropertyForOxfGb;
import org.estatio.fixture.budget.PartitionItemsForOxf;
import org.estatio.fixture.budget.BudgetsForOxf;
import org.estatio.fixture.charge.ChargeRefData;
import org.estatio.integtests.EstatioIntegrationTest;

import static org.assertj.core.api.Assertions.assertThat;

public class BudgetCalculationRepository_IntegTest extends EstatioIntegrationTest {

    @Inject
    BudgetCalculationRepository budgetCalculationRepository;

    @Inject
    PropertyRepository propertyRepository;

    @Inject
    BudgetRepository budgetRepository;

    @Inject
    PartitionItemRepository partitionItemRepository;

    @Inject
    ChargeRepository chargeRepository;

    @Inject
    BudgetCalculationService budgetCalculationService;

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(final ExecutionContext executionContext) {
                executionContext.executeChild(this, new EstatioBaseLineFixture());
                executionContext.executeChild(this, new PartitionItemsForOxf());
            }
        });
    }

    public static class FindUnique extends BudgetCalculationRepository_IntegTest {

        @Test
        public void happyCase() throws Exception {
            // given
            PartitionItem partitionItem = partitionItemRepository.allPartitionItems().get(0);
            KeyItem keyItem = partitionItem.getKeyTable().getItems().first();
            BudgetCalculation newBudgetCalculation = budgetCalculationRepository.createBudgetCalculation(partitionItem, keyItem, BigDecimal.ZERO, BudgetCalculationType.BUDGETED);

            // when
            BudgetCalculation budgetCalculation = budgetCalculationRepository.findUnique(partitionItem, keyItem, BudgetCalculationType.BUDGETED);

            // then
            assertThat(budgetCalculation).isEqualTo(newBudgetCalculation);

        }

    }

    public static class FindByPartitionItem extends BudgetCalculationRepository_IntegTest {

        @Test
        public void happyCase() throws Exception {
            // given
            PartitionItem partitionItem = partitionItemRepository.allPartitionItems().get(0);
            KeyItem keyItem = partitionItem.getKeyTable().getItems().first();
            BudgetCalculation newBudgetCalculation = budgetCalculationRepository.createBudgetCalculation(partitionItem, keyItem, BigDecimal.ZERO, BudgetCalculationType.BUDGETED);

            // when
            List<BudgetCalculation> budgetCalculations = budgetCalculationRepository.findByPartitionItem(partitionItem);

            // then
            assertThat(budgetCalculations.size()).isEqualTo(1);
            assertThat(budgetCalculations.get(0)).isEqualTo(newBudgetCalculation);

        }

    }

    public static class FindByBudgetAndCharge extends BudgetCalculationRepository_IntegTest {

        @Test
        public void happyCase() throws Exception {

            // given
            Property property = propertyRepository.findPropertyByReference(PropertyForOxfGb.REF);
            Budget budget = budgetRepository.findByPropertyAndStartDate(property, BudgetsForOxf.BUDGET_2015_START_DATE);
            Charge charge = chargeRepository.findByReference(ChargeRefData.GB_SERVICE_CHARGE);
            Charge chargeNotToBeFound = chargeRepository.findByReference(ChargeRefData.GB_INCOMING_CHARGE_1);
            budgetCalculationService.calculatePersistedCalculations(budget);

            // when
            List<BudgetCalculation> budgetCalculationsForCharge = budgetCalculationRepository.findByBudgetAndCharge(budget, charge);

            // then
            assertThat(budgetCalculationsForCharge.size()).isEqualTo(75);

            // and when
            budgetCalculationsForCharge = budgetCalculationRepository.findByBudgetAndCharge(budget, chargeNotToBeFound);

            // then
            assertThat(budgetCalculationsForCharge.size()).isEqualTo(0);

        }
    }

    public static class FindByBudgetAndStatus extends BudgetCalculationRepository_IntegTest {

        @Test
        public void happyCase() throws Exception {

            // given
            Property property = propertyRepository.findPropertyByReference(PropertyForOxfGb.REF);
            Budget budget = budgetRepository.findByPropertyAndStartDate(property, BudgetsForOxf.BUDGET_2015_START_DATE);
            budgetCalculationService.calculatePersistedCalculations(budget);

            // when
            List<BudgetCalculation> budgetCalculationsForAllocationOfType = budgetCalculationRepository.findByBudgetAndStatus(budget, Status.NEW);

            // then
            assertThat(budgetCalculationsForAllocationOfType.size()).isEqualTo(75);

            // and when
            budgetCalculationsForAllocationOfType = budgetCalculationRepository.findByBudgetAndStatus(budget, Status.ASSIGNED);

            // then
            assertThat(budgetCalculationsForAllocationOfType.size()).isEqualTo(0);

        }
    }

    public static class FindByBudgetAndUnitAndInvoiceChargeAndType extends BudgetCalculationRepository_IntegTest {

        @Test
        public void happyCase() throws Exception {

            // given
            Property property = propertyRepository.findPropertyByReference(PropertyForOxfGb.REF);
            Budget budget = budgetRepository.findByPropertyAndStartDate(property, BudgetsForOxf.BUDGET_2015_START_DATE);
            PartitionItem partitionItem = budget.getItems().first().getPartitionItems().get(0);
            budgetCalculationService.calculatePersistedCalculations(budget);

            // when
            List<BudgetCalculation> budgetCalculations = budgetCalculationRepository.findByBudgetAndUnitAndInvoiceChargeAndType(budget, property.getUnits().first(), partitionItem.getCharge(), BudgetCalculationType.BUDGETED);

            // then
            assertThat(budgetCalculations.size()).isEqualTo(3);

        }
    }

    public static class FindByBudgetAndUnitAndInvoiceChargeAndInomingChargeAndType extends
            BudgetCalculationRepository_IntegTest {

        @Test
        public void happyCase() throws Exception {

            // given
            Property property = propertyRepository.findPropertyByReference(PropertyForOxfGb.REF);
            Budget budget = budgetRepository.findByPropertyAndStartDate(property, BudgetsForOxf.BUDGET_2015_START_DATE);
            PartitionItem partitionItem = budget.getItems().first().getPartitionItems().get(0);
            budgetCalculationService.calculatePersistedCalculations(budget);

            // when
            List<BudgetCalculation> budgetCalculations = budgetCalculationRepository.findByBudgetAndUnitAndInvoiceChargeAndIncomingChargeAndType(budget, property.getUnits().first(), partitionItem.getCharge(), partitionItem.getBudgetItem().getCharge(), BudgetCalculationType.BUDGETED);

            // then
            assertThat(budgetCalculations.size()).isEqualTo(1);

        }
    }

}
