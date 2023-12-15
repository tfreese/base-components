package de.freese.base.net.mail;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.Part;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

/**
 * Wrapper für die JavaMail API.<br>
 * Dieser Wrapper arbeitet nach dem Prinzip "Fire and forget".
 *
 * @author Thomas Freese
 */
public class MailWrapper {
    private final Properties additionalHeaderProperties = new Properties();
    private final List<DataSource> attachments = new ArrayList<>();
    private final List<DataSource> inlines = new ArrayList<>();
    private final Properties javaMailProperties = new Properties();
    private final List<InternetAddress> to = new ArrayList<>();

    private InternetAddress from;
    private boolean html;
    private String subject;
    private String text;

    public MailWrapper() {
        super();

        setDebug(false);
    }

    public void addAttachment(final DataSource dataSource) {
        this.attachments.add(dataSource);
    }

    public void addInline(final DataSource dataSource) {
        this.inlines.add(dataSource);
    }

    public void addTO(final String to) throws NullPointerException, MessagingException {
        this.to.add(parseAddress(to));
    }

    public void clearAdditionalHeader() {
        this.additionalHeaderProperties.clear();
    }

    public void clearAttachments() {
        this.attachments.clear();
    }

    public void clearInlines() {
        this.inlines.clear();
    }

    public void clearTo() {
        this.to.clear();
    }

    public int getAttachmentSize() {
        return this.attachments.size();
    }

    public int getInlineSize() {
        return this.inlines.size();
    }

    public int getToSize() {
        return this.to.size();
    }

    public void send() throws Exception {
        if (this.from == null) {
            throw new NullPointerException("from");
        }

        if (getToSize() == 0) {
            throw new IllegalStateException("recipients: size = " + getToSize());
        }

        final Session session = Session.getInstance(this.javaMailProperties);
        // session.setDebug(debug);

        final MimeMessage mail = new MimeMessage(session);
        mail.setFrom(this.from);
        mail.setRecipients(Message.RecipientType.TO, this.to.toArray(new InternetAddress[0]));
        mail.setSubject((this.subject == null) ? "" : this.subject);
        mail.setSentDate(new Date());

        for (Entry<Object, Object> header : this.additionalHeaderProperties.entrySet()) {
            mail.addHeader(header.getKey().toString(), header.getValue().toString());
        }

        // mixed, für Attachments
        final Multipart rootMultipart = new MimeMultipart("mixed");

        // related, für Text und Inlines
        final MimeMultipart relatedMultipart = new MimeMultipart("related");
        final MimeBodyPart relatedBodyPart = new MimeBodyPart();
        relatedBodyPart.setContent(relatedMultipart);
        rootMultipart.addBodyPart(relatedBodyPart);

        if (this.text != null) {
            final MimeBodyPart textBodyPart = new MimeBodyPart();
            // textBodyPart.setText(this.text);

            if (this.html) {
                textBodyPart.setContent(this.text, "text/html; charset=UTF-8");
            }
            else {
                textBodyPart.setContent(this.text, "text/plain; charset=UTF-8");
            }

            relatedMultipart.addBodyPart(textBodyPart);
        }

        for (int i = 0; i < this.inlines.size(); i++) {
            final DataSource dataSource = this.inlines.get(i);

            final MimeBodyPart inlineBodyPart = new MimeBodyPart();
            inlineBodyPart.setDisposition(Part.INLINE);
            inlineBodyPart.setDataHandler(new DataHandler(dataSource));
            inlineBodyPart.setHeader("Content-ID", "<" + i + ">");
            relatedMultipart.addBodyPart(inlineBodyPart);
        }

        for (DataSource dataSource : this.attachments) {
            final MimeBodyPart attachmentBodyPart = new MimeBodyPart();
            attachmentBodyPart.setDisposition(Part.ATTACHMENT);
            attachmentBodyPart.setFileName(dataSource.getName());
            attachmentBodyPart.setDataHandler(new DataHandler(dataSource));
            rootMultipart.addBodyPart(attachmentBodyPart);
        }

        mail.setContent(rootMultipart);

        Transport.send(mail);
    }

    /**
     * Hinzufügen zusätzlicher Mail Header.<br>
     * Values die null oder leer sind, werden aus dem Header entfernt.
     *
     * @param key String, darf nicht null oder leer sein
     * @param value String
     */
    public void setAdditionalHeader(final String key, final String value) {
        if (key == null) {
            throw new NullPointerException("key");
        }

        if (key.strip().length() == 0) {
            throw new IllegalArgumentException("key is empty");
        }

        if ((value == null) || (value.strip().length() == 0)) {
            this.additionalHeaderProperties.remove(key);
            return;
        }

        this.additionalHeaderProperties.put(key.strip(), value.strip());
    }

    public void setDebug(final boolean debug) {
        this.javaMailProperties.put("mail.debug", debug);
    }

    public void setFrom(final String from) throws NullPointerException, MessagingException {
        this.from = parseAddress(from);
    }

    public void setHost(final String host) {
        this.javaMailProperties.setProperty("mail.smtp.host", host);
    }

    public void setJavaMailProperty(final String key, final String value) {
        this.javaMailProperties.setProperty(key, value);
    }

    public void setSubject(final String subject) {
        this.subject = subject;
    }

    public void setText(final String text, final boolean html) {
        this.text = text;
        this.html = html;
    }

    private InternetAddress parseAddress(final String address) throws NullPointerException, MessagingException {
        if (address == null) {
            throw new NullPointerException("address");
        }

        final InternetAddress[] parsed = InternetAddress.parse(address);

        if (parsed.length != 1) {
            throw new AddressException("Illegal address", address);
        }

        // parsed[0].validate();

        return parsed[0];
    }
}
