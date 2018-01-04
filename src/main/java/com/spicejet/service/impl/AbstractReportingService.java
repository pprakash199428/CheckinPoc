package com.spicejet.service.impl;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.data.JsonDataSource;
import net.sf.jasperreports.engine.util.JRLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.Map;

public abstract class AbstractReportingService {
    protected JsonDataSource getJsonDataSource(final String json) throws FileNotFoundException, JRException {
        File jsonfile = new File(getClass().getClassLoader().getResource(json).getFile());
        return new JsonDataSource(jsonfile);
    }

    protected JRBeanCollectionDataSource getJRBeanCollectionDataSource(final Collection<?> collection) {
        return new JRBeanCollectionDataSource(collection);
    }

    protected JasperPrint getJasperPrint(final String jasperFile, final Map<String, Object> map,
                                         JRDataSource dataSource) throws JRException, FileNotFoundException {
        JasperReport jasperReport = getJasperReport(jasperFile);
         return JasperFillManager.fillReport(jasperReport, map, dataSource);
        
    }

    protected JasperReport getJasperReport(final String jasperFile) throws JRException {
       File file = new File(getClass().getClassLoader().getResource(jasperFile).getFile());
        //File file = new File(jasperFile);

        return (JasperReport) JRLoader.loadObject(file);
    }

    protected void getCompiledJasperReport(final String jrxmFile, final String jasperFile) throws JRException {
        JasperCompileManager.compileReportToFile(jrxmFile, jasperFile);
    }
}
