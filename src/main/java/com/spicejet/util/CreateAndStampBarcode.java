package com.spicejet.util;

import java.io.FileOutputStream;
import java.io.IOException;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BarcodePDF417;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfTemplate;

public class CreateAndStampBarcode {
	public void stampBarCode(String text, String src, String dest) throws IOException, DocumentException {
		PdfReader reader = new PdfReader(src);
		PdfStamper pdfStamper = new PdfStamper(reader, new FileOutputStream(dest));
		PdfContentByte cb = pdfStamper.getOverContent(1);
		try {

			Image img = createBarcode(cb, text, 2, 4);
			cb.addImage(img);
			pdfStamper.close();
			reader.close();
			System.out.println("printed");
		} catch (BadElementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.print("file replaced");
	}

	public  Image createBarcode(PdfContentByte cb, String text, float mh, float mw) throws BadElementException {
		BarcodePDF417 pf = new BarcodePDF417();
		// pf.setOptions(1);
		pf.setText(text);
		pf.setAspectRatio(0.35f);
		pf.setYHeight(1);
		// pf.setErrorLevel(5);
		pf.setCodeColumns(2);
		// pf.setCodeRows(Integer.parseInt(PropertyConfigurator.getInstance().getValueOf("barCode.dataRow")));
		Rectangle size = pf.getBarcodeSize();

		PdfTemplate template = cb.createTemplate(mw * size.getWidth(), mh * size.getHeight());
		pf.placeBarcode(template, BaseColor.BLACK, 1, 1);
		Image img = Image.getInstance(template);
		// Image img = pf.getImage();
		img.scaleAbsolute(600, 90);
		img.setAbsolutePosition(400f, 470f);
		// img.setRight(Integer.parseInt(PropertyConfigurator.getInstance().getValueOf("barCode.width")));

		return img;
	}
}
