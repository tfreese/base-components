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
public class MailWrapper
{
    private final Properties additionalHeaderProperties = new Properties();

    private final List<DataSource> attachments = new ArrayList<>();

    private final List<DataSource> inlines = new ArrayList<>();

    private final Properties javaMailProperties = new Properties();

    private final List<InternetAddress> to = new ArrayList<>();

    private InternetAddress from;

    private boolean html;

    private String subject;

    private String text;

    public MailWrapper()
    {
        super();

        setDebug(false);
    }

    /**
     * Hinzufügen eines Anhangs.
     *
     * @param dataSource {@link DataSource}
     */
    public void addAttachment(final DataSource dataSource)
    {
        this.attachments.add(dataSource);
    }

    /**
     * Hinzufügen eines Inlines.
     *
     * @param dataSource {@link DataSource}
     */
    public void addInline(final DataSource dataSource)
    {
        this.inlines.add(dataSource);
    }

    /**
     * Hinzufügen eines weiteren Empfängers.
     *
     * @param to String
     *
     * @throws NullPointerException, wenn die Adresse null ist
     * @throws MessagingException, wenn die Adresse ein ungültiges Format hat
     */
    public void addTO(final String to) throws NullPointerException, MessagingException
    {
        this.to.add(parseAddress(to));
    }

    /**
     * Zusätzliche Mail Header leeren.
     */
    public void clearAdditionalHeader()
    {
        this.additionalHeaderProperties.clear();
    }

    /**
     * Anhänge leeren.
     */
    public void clearAttachments()
    {
        this.attachments.clear();
    }

    /**
     * Inlines leeren.
     */
    public void clearInlines()
    {
        this.inlines.clear();
    }

    /**
     * Leert die Empfänger Liste.
     */
    public void clearTO()
    {
        this.to.clear();
    }

    /**
     * Liefert die Anzahl vorhandener Attachments.
     *
     * @return int
     */
    public int getAttachmentSize()
    {
        return this.attachments.size();
    }

    /**
     * Liefert die Anzahl vorhandener Inlines.
     *
     * @return int
     */
    public int getInlineSize()
    {
        return this.inlines.size();
    }

    /**
     * Liefert die Anzahl vorhandener Empfänger.
     *
     * @return int
     */
    public int getTOSize()
    {
        return this.to.size();
    }

    /**
     * Senden der Mail.
     *
     * @throws Exception Falls was schiefgeht.
     */
    public void send() throws Exception
    {
        if (this.from == null)
        {
            throw new NullPointerException("from");
        }

        if (getTOSize() == 0)
        {
            throw new IllegalStateException("recipients: size = " + getTOSize());
        }

        Session session = Session.getInstance(this.javaMailProperties);
        // session.setDebug(debug);

        MimeMessage mail = new MimeMessage(session);
        mail.setFrom(this.from);
        mail.setRecipients(Message.RecipientType.TO, this.to.toArray(new InternetAddress[0]));
        mail.setSubject((this.subject == null) ? "" : this.subject);
        mail.setSentDate(new Date());

        for (Entry<Object, Object> header : this.additionalHeaderProperties.entrySet())
        {
            mail.addHeader(header.getKey().toString(), header.getValue().toString());
        }

        // mixed, für Attachments
        Multipart rootMultipart = new MimeMultipart("mixed");

        // related, für Text und Inlines
        MimeMultipart relatedMultipart = new MimeMultipart("related");
        MimeBodyPart relatedBodyPart = new MimeBodyPart();
        relatedBodyPart.setContent(relatedMultipart);
        rootMultipart.addBodyPart(relatedBodyPart);

        if (this.text != null)
        {
            MimeBodyPart textBodyPart = new MimeBodyPart();
            // textBodyPart.setText(this.text);

            if (this.html)
            {
                textBodyPart.setContent(this.text, "text/html; charset=UTF-8");
            }
            else
            {
                textBodyPart.setContent(this.text, "text/plain; charset=UTF-8");
            }

            relatedMultipart.addBodyPart(textBodyPart);
        }

        for (int i = 0; i < this.inlines.size(); i++)
        {
            DataSource dataSource = this.inlines.get(i);

            MimeBodyPart inlineBodyPart = new MimeBodyPart();
            inlineBodyPart.setDisposition(Part.INLINE);
            inlineBodyPart.setDataHandler(new DataHandler(dataSource));
            inlineBodyPart.setHeader("Content-ID", "<" + i + ">");
            relatedMultipart.addBodyPart(inlineBodyPart);
        }

        for (DataSource dataSource : this.attachments)
        {
            MimeBodyPart attachmentBodyPart = new MimeBodyPart();
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
    public void setAdditionalHeader(final String key, final String value)
    {
        if (key == null)
        {
            throw new NullPointerException("key");
        }

        if (key.strip().length() == 0)
        {
            throw new IllegalArgumentException("key is empty");
        }

        if ((value == null) || (value.strip().length() == 0))
        {
            this.additionalHeaderProperties.remove(key);
            return;
        }

        this.additionalHeaderProperties.put(key.strip(), value.strip());
    }

    /**
     * Setzt die Debug Option.
     *
     * @param debug boolean
     */
    public void setDebug(final boolean debug)
    {
        this.javaMailProperties.put("mail.debug", debug);
    }

    /**
     * Setzt den Absender.
     *
     * @param from String
     *
     * @throws NullPointerException, wenn die Adresse null ist
     * @throws MessagingException, wenn der Adresse ein ungültiges Format hat
     */
    public void setFrom(final String from) throws NullPointerException, MessagingException
    {
        this.from = parseAddress(from);
    }

    /**
     * Setzt den Host des MailServers.
     *
     * @param host String
     */
    public void setHost(final String host)
    {
        this.javaMailProperties.setProperty("mail.smtp.host", host);
    }

    /**
     * Setzt ein bestimmtes Property.
     *
     * @param key String
     * @param value String
     */
    public void setJavaMailProperty(final String key, final String value)
    {
        this.javaMailProperties.setProperty(key, value);
    }

    /**
     * Setzt den Betreff.
     *
     * @param subject String
     */
    public void setSubject(final String subject)
    {
        this.subject = subject;
    }

    /**
     * Setzt den Text.
     *
     * @param text String
     * @param html boolean HTML Text ?
     */
    public void setText(final String text, final boolean html)
    {
        this.text = text;
        this.html = html;
    }

    /**
     * Überprüft und wandelt eine E-Mail Adresse in ein {@link InternetAddress} Objekt um.
     *
     * @param address String
     *
     * @return {@link InternetAddress}
     *
     * @throws NullPointerException, wenn die Adresse null ist
     * @throws MessagingException, wenn die Adresse ein ungültiges Format hat
     */
    private InternetAddress parseAddress(final String address) throws NullPointerException, MessagingException
    {
        if (address == null)
        {
            throw new NullPointerException("address");
        }

        InternetAddress[] parsed = InternetAddress.parse(address);

        if (parsed.length != 1)
        {
            throw new AddressException("Illegal address", address);
        }

        // parsed[0].validate();

        return parsed[0];
    }
}
