package org.estatio.module.lease.dom.amortisation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.estatio.module.lease.dom.Frequency;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseItem;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = AmortisationSchedule.class
)
public class AmortisationScheduleRepository {

    @Programmatic
    public List<AmortisationSchedule> listAll() {
        return repositoryService.allInstances(AmortisationSchedule.class);
    }

    @Programmatic
    public AmortisationSchedule findUnique(final LeaseItem lease, final LocalDate startDate) {
        return repositoryService.uniqueMatch(
                new QueryDefault<>(
                        AmortisationSchedule.class,
                        "findUnique",
                        "leaseItem", lease,
                        "startDate", startDate));
    }

    @Programmatic
    public List<AmortisationSchedule> findByLeaseItem(final LeaseItem leaseItem) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        AmortisationSchedule.class,
                        "findByLeaseItem",
                        "leaseItem", leaseItem));
    }

    @Programmatic
    public List<AmortisationSchedule> findByLease(final Lease lease) {
        List<AmortisationSchedule> result = new ArrayList<>();
        for (LeaseItem leaseItem : lease.getItems()){
            final List<AmortisationSchedule> schedulesIfAny = findByLeaseItem(leaseItem);
            if (!schedulesIfAny.isEmpty()){
                result.addAll(schedulesIfAny);
            }
        }
        return result;
    }

    @Programmatic
    public AmortisationSchedule findOrCreate(
            final LeaseItem leaseItem,
            final BigDecimal scheduledAmount,
            final Frequency frequency,
            final LocalDate startDate,
            final LocalDate endDate){
        final AmortisationSchedule result = findUnique(leaseItem, startDate);
        if (result == null) return create(leaseItem, scheduledAmount, frequency, startDate, endDate);
        return result;
    }

    private AmortisationSchedule create(
            final LeaseItem leaseItem,
            final BigDecimal scheduledAmount,
            final Frequency frequency,
            final LocalDate startDate,
            final LocalDate endDate){
        final AmortisationSchedule newSchedule = new AmortisationSchedule(leaseItem,scheduledAmount, frequency, startDate, endDate);
        serviceRegistry2.injectServicesInto(newSchedule);
        repositoryService.persistAndFlush(newSchedule);
        return newSchedule;
    }

    @Inject
    RepositoryService repositoryService;

    @Inject
    ServiceRegistry2 serviceRegistry2;

}
