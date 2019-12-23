package org.estatio.module.turnover.dom.aggregate;

import java.math.BigDecimal;

import javax.jdo.annotations.Column;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;

import lombok.Getter;
import lombok.Setter;

@DomainObject(
        editing = Editing.DISABLED,
        objectType = "org.estatio.module.turnover.dom.aggregate.TurnoverAggregateToDate"
)
public class TurnoverAggregateToDate {

    @Getter @Setter
    @Column(name = "turnoverAggregationId", allowsNull = "false")
    private TurnoverAggregation aggregation;

    @Getter @Setter
    @Column(allowsNull = "true")
    private BigDecimal grossAmount;

    @Getter @Setter
    @Column(allowsNull = "true")
    private BigDecimal netAmount;

    @Getter @Setter
    private boolean nonComparableThisYear;

    @Getter @Setter
    private int turnoverCount;

    @Getter @Setter
    @Column(allowsNull = "true")
    private BigDecimal grossAmountPreviousYear;

    @Getter @Setter
    @Column(allowsNull = "true")
    private BigDecimal netAmountPreviousYear;

    @Getter @Setter
    private boolean nonComparablePreviousYear;

    @Getter @Setter
    private int turnoverCountPreviousYear;

    @Getter @Setter
    private boolean comparable;

}