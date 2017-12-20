package com.spicejet.util;

import java.util.Arrays;

public class EmailApiRequestBody {
    private String from;
    private String subject;
    private EmailContent attachmentContent[];
    private String plainTextContent;
    private String htmlContent;
    private String to[];
    private String Cc[];
    private String Bcc[];
    private String applicationKey;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public EmailContent[] getAttachmentContent() {
        return attachmentContent;
    }

    public void setAttachmentContent(EmailContent[] attachmentContent) {
        this.attachmentContent = attachmentContent;
    }

    public String getPlainTextContent() {
        return plainTextContent;
    }

    public void setPlainTextContent(String plainTextContent) {
        this.plainTextContent = plainTextContent;
    }

    public String getHtmlContent() {
        return htmlContent;
    }

    public void setHtmlContent(String htmlContent) {
        this.htmlContent = htmlContent;
    }

    public String[] getTo() {
        return to;
    }

    public void setTo(String[] to) {
        this.to = to;
    }

    public String[] getCc() {
        return Cc;
    }

    public void setCc(String[] cc) {
        Cc = cc;
    }

    public String[] getBcc() {
        return Bcc;
    }

    public void setBcc(String[] bcc) {
        Bcc = bcc;
    }

    public String getApplicationKey() {
        return applicationKey;
    }

    public void setApplicationKey(String applicationKey) {
        this.applicationKey = applicationKey;
    }

    @Override
    public String toString() {
        return "EmailApiRequestBody [from=" + from + ", subject=" + subject + ", attachmentContent="
                + Arrays.toString(attachmentContent) + ", plainTextContent=" + plainTextContent + ", htmlContent="
                + htmlContent + ", to=" + Arrays.toString(to) + ", Cc=" + Arrays.toString(Cc) + ", Bcc="
                + Arrays.toString(Bcc) + ", applicationKey=" + applicationKey + "]";
    }
}
