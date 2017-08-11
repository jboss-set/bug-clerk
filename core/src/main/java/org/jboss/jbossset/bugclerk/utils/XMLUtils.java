package org.jboss.jbossset.bugclerk.utils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.util.JAXBSource;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public final class XMLUtils {

    private XMLUtils() {
    }

    public static <T> void xmlToXhtml(T catalog, StreamSource xslt, StreamResult result) {
        try {
            TransformerFactory.newInstance().newTransformer(xslt)
                    .transform(new JAXBSource(JAXBContext.newInstance(catalog.getClass()), catalog), result);
        } catch (JAXBException | TransformerException e) {
            throw new IllegalStateException(e);
        }
    }

}
