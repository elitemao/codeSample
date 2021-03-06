
package whatizitws.client;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;


/**
 * This class was generated by the JAXWS SI.
 * JAX-WS RI 2.0_01-b59-fcs
 * Generated source version: 2.0
 * 
 */
@WebServiceClient(name = "whatizit", targetNamespace = "http://www.ebi.ac.uk/webservices/whatizit/ws", wsdlLocation = "c:\\temp\\forWhatizitStubCreation\\whatizit.wsdl")
public class Whatizit_Service
    extends Service
{

    private final static URL WHATIZIT_WSDL_LOCATION;

    static {
        URL url = null;
        try {
            url = new URL("file:/C:/temp/forWhatizitStubCreation/whatizit.wsdl");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        WHATIZIT_WSDL_LOCATION = url;
    }

    public Whatizit_Service(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public Whatizit_Service() {
        super(WHATIZIT_WSDL_LOCATION, new QName("http://www.ebi.ac.uk/webservices/whatizit/ws", "whatizit"));
    }

    /**
     * 
     * @return
     *     returns Whatizit
     */
    @WebEndpoint(name = "pipeline")
    public Whatizit getPipeline() {
        return (Whatizit)super.getPort(new QName("http://www.ebi.ac.uk/webservices/whatizit/ws", "pipeline"), Whatizit.class);
    }

}
