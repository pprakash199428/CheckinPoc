package com.spicejet.enums;

import com.spicejet.util.Constants;

/**
 * Enum to define all the template and property name of template's file.
 */
public enum Template {
    BAG_TAG(Constants.BAG_TAG_TEMPLATE_FILE, ContentType.BAG_TAG),
    BAG_TAG_MULTI_SEG(Constants.BAG_TAG_MULTI_SEG, ContentType.BAG_TAG),
    BOARDING_PASS(Constants.BOARDING_PASS_TEMPLATE_FILE, ContentType.BOARDING_PASS),
    BOARDING_PASS_BARCODE_STREAM(Constants.IATA_BOARDING_PASS_BARCODE_STREAM_TEMPLATE_FILE, ContentType.BOARDING_PASS),
    BOARDING_PASS_HTML(Constants.BOARDING_PASS_HTML_TEMPLATE_FILE,ContentType.BOARDING_PASS),
    BAG_TAG_HTML(Constants.BAG_TAG_HTML_TEMPLATE_FILE,ContentType.BAG_TAG);

    private String propertyName;
    private ContentType contentType;

    Template(String propertyName, ContentType contentType){
        this.propertyName = propertyName;
        this.contentType = contentType;
    }

    /**
     * Method returns property name.
     *
     * @return Property Name from Constants @Constants.
     */
     public String getPropertyName(){
         return this.propertyName;
     }

    /**
     * Method returns property name.
     *
     * @return Types from ContentTypes @ContentTypes.
     */
    public ContentType getContentType(){
        return this.contentType;
    }
}
