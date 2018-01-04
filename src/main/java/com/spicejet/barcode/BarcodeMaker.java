package com.spicejet.barcode;


import java.io.File;

import net.sourceforge.barbecue.Barcode;
import net.sourceforge.barbecue.BarcodeFactory;
import net.sourceforge.barbecue.BarcodeImageHandler;

public class BarcodeMaker {

  public static void main (String [] args) throws Exception {

    //Get 128B Barcode instance from the Factory
    Barcode barcode = BarcodeFactory.createPDF417("nikhil");
    barcode.setBarHeight(60);
    barcode.setBarWidth(2);

    File imgFile = new File("C:\\Users\\nikhil\\workspace\\CheckinPoc\\src\\main\\resources\\testsize.png");

    //Write the bar code to PNG file
    BarcodeImageHandler.savePNG(barcode, imgFile);
    System.out.println("printed");
  }
}	     
		
		 

		
						
	
	


