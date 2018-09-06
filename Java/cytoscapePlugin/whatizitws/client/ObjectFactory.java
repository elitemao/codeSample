
package whatizitws.client;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the whatizitws.client package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Contact_QNAME = new QName("http://www.ebi.ac.uk/webservices/whatizit/ws", "contact");
    private final static QName _GetPipelinesStatus_QNAME = new QName("http://www.ebi.ac.uk/webservices/whatizit/ws", "getPipelinesStatus");
    private final static QName _QueryPmid_QNAME = new QName("http://www.ebi.ac.uk/webservices/whatizit/ws", "queryPmid");
    private final static QName _Search_QNAME = new QName("http://www.ebi.ac.uk/webservices/whatizit/ws", "search");
    private final static QName _SearchResponse_QNAME = new QName("http://www.ebi.ac.uk/webservices/whatizit/ws", "searchResponse");
    private final static QName _WhatizitException_QNAME = new QName("http://www.ebi.ac.uk/webservices/whatizit/ws", "WhatizitException");
    private final static QName _QueryPmidResponse_QNAME = new QName("http://www.ebi.ac.uk/webservices/whatizit/ws", "queryPmidResponse");
    private final static QName _ContactResponse_QNAME = new QName("http://www.ebi.ac.uk/webservices/whatizit/ws", "contactResponse");
    private final static QName _GetPipelinesStatusResponse_QNAME = new QName("http://www.ebi.ac.uk/webservices/whatizit/ws", "getPipelinesStatusResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: whatizitws.client
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Search }
     * 
     */
    public Search createSearch() {
        return new Search();
    }

    /**
     * Create an instance of {@link QueryPmid }
     * 
     */
    public QueryPmid createQueryPmid() {
        return new QueryPmid();
    }

    /**
     * Create an instance of {@link SearchResponse }
     * 
     */
    public SearchResponse createSearchResponse() {
        return new SearchResponse();
    }

    /**
     * Create an instance of {@link WhatizitException }
     * 
     */
    public WhatizitException createWhatizitException() {
        return new WhatizitException();
    }

    /**
     * Create an instance of {@link SelectItem }
     * 
     */
    public SelectItem createSelectItem() {
        return new SelectItem();
    }

    /**
     * Create an instance of {@link QueryPmidResponse }
     * 
     */
    public QueryPmidResponse createQueryPmidResponse() {
        return new QueryPmidResponse();
    }

    /**
     * Create an instance of {@link GetPipelinesStatus }
     * 
     */
    public GetPipelinesStatus createGetPipelinesStatus() {
        return new GetPipelinesStatus();
    }

    /**
     * Create an instance of {@link GetPipelinesStatusResponse }
     * 
     */
    public GetPipelinesStatusResponse createGetPipelinesStatusResponse() {
        return new GetPipelinesStatusResponse();
    }

    /**
     * Create an instance of {@link Contact }
     * 
     */
    public Contact createContact() {
        return new Contact();
    }

    /**
     * Create an instance of {@link ContactResponse }
     * 
     */
    public ContactResponse createContactResponse() {
        return new ContactResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Contact }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.ebi.ac.uk/webservices/whatizit/ws", name = "contact")
    public JAXBElement<Contact> createContact(Contact value) {
        return new JAXBElement<Contact>(_Contact_QNAME, Contact.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetPipelinesStatus }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.ebi.ac.uk/webservices/whatizit/ws", name = "getPipelinesStatus")
    public JAXBElement<GetPipelinesStatus> createGetPipelinesStatus(GetPipelinesStatus value) {
        return new JAXBElement<GetPipelinesStatus>(_GetPipelinesStatus_QNAME, GetPipelinesStatus.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link QueryPmid }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.ebi.ac.uk/webservices/whatizit/ws", name = "queryPmid")
    public JAXBElement<QueryPmid> createQueryPmid(QueryPmid value) {
        return new JAXBElement<QueryPmid>(_QueryPmid_QNAME, QueryPmid.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Search }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.ebi.ac.uk/webservices/whatizit/ws", name = "search")
    public JAXBElement<Search> createSearch(Search value) {
        return new JAXBElement<Search>(_Search_QNAME, Search.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SearchResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.ebi.ac.uk/webservices/whatizit/ws", name = "searchResponse")
    public JAXBElement<SearchResponse> createSearchResponse(SearchResponse value) {
        return new JAXBElement<SearchResponse>(_SearchResponse_QNAME, SearchResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WhatizitException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.ebi.ac.uk/webservices/whatizit/ws", name = "WhatizitException")
    public JAXBElement<WhatizitException> createWhatizitException(WhatizitException value) {
        return new JAXBElement<WhatizitException>(_WhatizitException_QNAME, WhatizitException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link QueryPmidResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.ebi.ac.uk/webservices/whatizit/ws", name = "queryPmidResponse")
    public JAXBElement<QueryPmidResponse> createQueryPmidResponse(QueryPmidResponse value) {
        return new JAXBElement<QueryPmidResponse>(_QueryPmidResponse_QNAME, QueryPmidResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ContactResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.ebi.ac.uk/webservices/whatizit/ws", name = "contactResponse")
    public JAXBElement<ContactResponse> createContactResponse(ContactResponse value) {
        return new JAXBElement<ContactResponse>(_ContactResponse_QNAME, ContactResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetPipelinesStatusResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.ebi.ac.uk/webservices/whatizit/ws", name = "getPipelinesStatusResponse")
    public JAXBElement<GetPipelinesStatusResponse> createGetPipelinesStatusResponse(GetPipelinesStatusResponse value) {
        return new JAXBElement<GetPipelinesStatusResponse>(_GetPipelinesStatusResponse_QNAME, GetPipelinesStatusResponse.class, null, value);
    }

}
