package org.estatio.module.coda.dom.codadocument;

import java.util.SortedSet;
import java.util.TreeSet;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Unique;
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;

import com.google.common.collect.ComparisonChain;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.eventbus.ActionDomainEvent;

import org.isisaddons.module.security.dom.tenancy.HasAtPath;

import org.estatio.module.coda.dom.CodaCurrency;
import org.estatio.module.coda.dom.CodaDocumentType;

import lombok.Getter;
import lombok.Setter;

@PersistenceCapable(
        identityType = IdentityType.DATASTORE,
        schema = "dbo",
        table = "CodaDocument"
)
@DatastoreIdentity(
        strategy = IdGeneratorStrategy.IDENTITY,
        column = "id")
@Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@Queries({
        @Query(
                name = "findByCmpCodeAndDocCodeAndDocNum", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.coda.dom.codadocument.CodaDocument "
                        + "WHERE cmpCode == :cmpCode "
                        + "   && docCode == :docCode "
                        + "   && docNum  == :docNum "),

})
@Unique(name = "CodaDocument_uuid_UNQ", members = { "uuid" })
@DomainObject(
        objectType = "codadocument.CodaDocument",
        editing = Editing.DISABLED
)
@DomainObjectLayout(
        bookmarking = BookmarkPolicy.AS_ROOT
)
public class CodaDocument implements Comparable<CodaDocument>, HasAtPath {

    public CodaDocument() {
        setUuid(java.util.UUID.randomUUID().toString());
    }

    public String title() {
        if (getDocNum() == null) {
            return String.format("%s | %s ", getCmpCode(), getDocCode());
        } else {
            return String.format("%s | %s | %s", getCmpCode(), getDocCode(), getDocNum());
        }
    }

    @Column(allowsNull = "false")
    @Getter @Setter
    private CodaDocumentType documentType;

    @Property(hidden = Where.EVERYWHERE)
    @Column(allowsNull = "false")
    @Getter @Setter
    private String uuid;

    @Column(allowsNull = "false", length = 24)
    @Getter @Setter
    private String cmpCode;

    @Column(allowsNull = "false", length = 12)
    @Getter @Setter
    private String docCode;

    @Column(allowsNull = "true", length = 12)
    @Getter @Setter
    private String docNum;

    @Column(allowsNull = "true")
    @Getter @Setter
    private LocalDate inputDate;

    @Column(allowsNull = "true")
    @Getter @Setter
    private LocalDate docDate;

    @Column(allowsNull = "true", length = 9)
    @Getter @Setter
    private String codaPeriod;

    @Column(allowsNull = "false", length = 9)
    @Getter @Setter
    private CodaCurrency currency;

    @Column(allowsNull = "false")
    @Getter @Setter
    private String atPath;

    @Getter @Setter
    @Column(allowsNull = "false")
    private LocalDateTime createdAt;

    public static class CodaDocumentChangePostedAtEvent
            extends ActionDomainEvent<CodaDocument> {}

    @Getter @Setter
    @Column(allowsNull = "true")
    private LocalDateTime postedAt;

    @Persistent(
            mappedBy = "document", dependentElement = "true"
    )
    @Getter @Setter
    private SortedSet<CodaDocumentLine> lines = new TreeSet<>();


    @Programmatic
    public void updatePostedAtAndAttachedScheduleEntryIfAny(final LocalDateTime dateTime) {
        setPostedAt(dateTime);
        if (dateTime==null) return; // guard, but should not be possble
        codaDocumentLinkRepository.findEntryLinkByDocument(this).forEach(l->{
            l.getAmortisationEntry().setDateReported(dateTime.toLocalDate());
            l.getAmortisationEntry().getSchedule().verifyOutstandingValue();
        });

    }

    //region > compareTo, toString

    @Override
    public int compareTo(final CodaDocument other) {
        return ComparisonChain.start()
                .compare(getCmpCode(), other.getCmpCode())
                .compare(getDocCode(), other.getDocCode())
                .compare(getUuid(), other.getUuid())
                .result();
    }

    @Override
    public String toString() {
        return "CodaDocHead{" +
                "companyCode='" + getCmpCode() + '\'' +
                ", docCode='" + getDocCode() + '\'' +
                ", uuid='" + getUuid() + '\'' +
                '}';
    }

    @Inject CodaDocumentLinkRepository codaDocumentLinkRepository;

}
