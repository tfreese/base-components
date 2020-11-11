package de.freese.base.swing.exception;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Locale;
import java.util.logging.Level;
import javax.activation.DataSource;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.html.HTMLEditorKit;
import org.jdesktop.swingx.error.ErrorInfo;
import org.slf4j.LoggerFactory;
import de.freese.base.net.mail.MailWrapper;
import de.freese.base.swing.layout.GbcBuilder;
import de.freese.base.utils.GuiUtils;

/**
 * Panel zur Darstellung von Exceptions.
 *
 * @author Thomas Freese
 */
public class ErrorPane extends JPanel
{
    /**
     *
     */
    private static final long serialVersionUID = 8841473190098899651L;

    /**
     *
     */
    private static final Dimension SIZE_DETAIL = new Dimension(6400, 350);

    /**
     *
     */
    private static final Dimension SIZE_MESSAGE = new Dimension(640, 130);

    /**
     * @param args String[]
     */
    public static void main(final String[] args)
    {
        try
        {
            Exception cause = new Exception("I'm the cause");
            Exception ex = new Exception("I'm a secondary exception", cause);

            throw ex;
        }
        catch (Exception ex)
        {
            ErrorInfo errorInfo = new ErrorInfo("ErrorTitle", "basic error message", null, "category", ex, Level.ALL, null);

            showDialog(null, errorInfo, false);
        }
    }

    /**
     * Creates a new showDialog object.
     *
     * @param owner {@link Component}
     * @param errorInfo {@link ErrorInfo}
     * @param enableSendMail boolean
     */
    public static final void showDialog(final Component owner, final ErrorInfo errorInfo, final boolean enableSendMail)
    {
        JOptionPane pane =
                new JOptionPane(new ErrorPane(errorInfo, null, enableSendMail), JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new String[] {});

        JDialog dialog = pane.createDialog(owner, errorInfo.getTitle());
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setResizable(true);
        dialog.setVisible(true);
    }

    /**
     *
     */
    private JButton buttonClipboard;

    /**
     *
     */
    private JButton buttonClose;

    /**
     *
     */
    private JButton buttonDetails;

    /**
     *
     */
    private JButton buttonSend;

    /**
     *
     */
    private JPanel detailPanel;

    /**
     *
     */
    private JEditorPane editorPaneDetails;

    /**
     *
     */
    private JEditorPane editorPaneMessage;

    /**
     *
     */
    private final ErrorInfo errorInfo;

    /**
     *
     */
    private JLabel labelIcon;

    /**
     *
     */
    private final Component owner;

    /**
     *
     */
    private JScrollPane scrollPaneMessage;

    /**
     * Creates a new {@link ErrorPane} object.
     *
     * @param errorInfo {@link ErrorInfo}
     * @param owner {@link Component}
     * @param enableSendMail boolean
     */
    private ErrorPane(final ErrorInfo errorInfo, final Component owner, final boolean enableSendMail)
    {
        super();

        this.errorInfo = errorInfo;
        this.owner = owner;

        initialize(enableSendMail);
    }

    /**
     * Formatiert Sonderzeichen in HTML Zeichen.
     *
     * @param input String
     * @return String
     */
    protected String escapeXml(final String input)
    {
        String s = (input == null) ? "" : input.replace("&", "&amp;");
        s = s.replace("<", "&lt;");
        s = s.replace(">", "&gt;");

        return s;
    }

    /**
     * Button zum kopieren der Exception in die Zwischenablage.
     *
     * @return {@link JButton}
     */
    private JButton getButtonClipboard()
    {
        if (this.buttonClipboard == null)
        {
            this.buttonClipboard = new JButton();

            if (Locale.getDefault().getLanguage().equals(Locale.GERMAN.getLanguage()))
            {
                this.buttonClipboard.setText("In Zwischenablage kopieren");
            }
            else
            {
                this.buttonClipboard.setText("Copy to Clipboard");
            }

            this.buttonClipboard.addActionListener(event -> {
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

                getEditorPaneDetails().selectAll();

                String text = getEditorPaneDetails().getSelectedText();

                // getEditorPaneDetails().select(-1, -1);
                StringSelection selection = new StringSelection(text);
                clipboard.setContents(selection, selection);

                // TransferHandler transferHandler =
                // new TransferHandler()
                // {
                // private static final long serialVersionUID = 0L;
                //
                // /**
                // * @see javax.swing.TransferHandler#createTransferable(javax.swing.JComponent)
                // */
                // protected Transferable createTransferable(JComponent c)
                // {
                // String text = getEditorPaneDetails().getSelectedText();
                //
                // if ((text == null) || text.equals(""))
                // {
                // getEditorPaneDetails().selectAll();
                // text = getEditorPaneDetails().getSelectedText();
                // getEditorPaneDetails().select(-1, -1);
                // }
                //
                // return new StringSelection(text);
                // }
                //
                // /**
                // * @see javax.swing.TransferHandler#getSourceActions(javax.swing.JComponent)
                // */
                // public int getSourceActions(JComponent c)
                // {
                // return TransferHandler.COPY;
                // }
                // };
                //
                // getEditorPaneDetails().setTransferHandler(transferHandler);
                // getEditorPaneDetails().copy();
                // getEditorPaneDetails().setTransferHandler(null);
            });
        }

        return this.buttonClipboard;
    }

    /**
     * Button zum Schliessen.
     *
     * @return {@link JButton}
     */
    private JButton getButtonClose()
    {
        if (this.buttonClose == null)
        {
            this.buttonClose = new JButton();

            if (Locale.getDefault().getLanguage().equals(Locale.GERMAN.getLanguage()))
            {
                this.buttonClose.setText("Schliessen");
            }
            else
            {
                this.buttonClose.setText("Close");
            }

            this.buttonClose.addKeyListener(new KeyAdapter()
            {
                /**
                 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
                 */
                @Override
                public void keyPressed(final KeyEvent e)
                {
                    if ((e.getKeyCode() == KeyEvent.VK_ENTER))
                    {
                        ErrorPane.this.buttonClose.doClick();
                    }
                }
            });

            this.buttonClose.addActionListener(event -> {
                Component c = getOwner();

                if (c instanceof Window)
                {
                    ((Window) c).dispose();
                }
            });
        }

        return this.buttonClose;
    }

    /**
     * Button fuer die Details.
     *
     * @return {@link JButton}
     */
    private JButton getButtonDetails()
    {
        if (this.buttonDetails == null)
        {
            final String text = "Details";

            this.buttonDetails = new JButton();
            this.buttonDetails.setText(text + " >>");

            this.buttonDetails.addKeyListener(new KeyAdapter()
            {
                /**
                 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
                 */
                @Override
                public void keyPressed(final KeyEvent e)
                {
                    if ((e.getKeyCode() == KeyEvent.VK_ENTER))
                    {
                        ErrorPane.this.buttonDetails.doClick();
                    }
                }
            });

            this.buttonDetails.addActionListener(event -> {
                Dimension newSize = null;

                Component owner = getOwner();

                if (!getDetailPanel().isVisible())
                {
                    newSize = new Dimension(owner.getWidth(), owner.getHeight() + SIZE_DETAIL.height);
                    ErrorPane.this.buttonDetails.setText(text + " <<");
                }
                else
                {
                    newSize = new Dimension(owner.getWidth(), owner.getHeight() - SIZE_DETAIL.height);
                    ErrorPane.this.buttonDetails.setText(text + " >>");
                }

                owner.setSize(newSize);
                owner.validate();
                owner.repaint();

                // owner.doLayout();
                getDetailPanel().setVisible(!getDetailPanel().isVisible());
            });
        }

        return this.buttonDetails;
    }

    /**
     * Button zum versenden der Fehlermeldung.
     *
     * @return {@link JButton}
     */
    private JButton getButtonSend()
    {
        if (this.buttonSend == null)
        {
            this.buttonSend = new JButton();

            if (Locale.getDefault().getLanguage().equals(Locale.GERMAN.getLanguage()))
            {
                this.buttonSend.setText("Senden");
            }
            else
            {
                this.buttonSend.setText("Send");
            }

            this.buttonSend.addActionListener(event -> {
                // TODO Noch nich ganz fertich
                // String userID = Context.getUser().getUserId();

                try
                {
                    Component x = getOwner();
                    Component ownerParent = SwingUtilities.getAncestorOfClass(Dialog.class, x);

                    if (ownerParent == null)
                    {
                        ownerParent = SwingUtilities.getAncestorOfClass(Frame.class, x);
                    }

                    DataSource dataSource = GuiUtils.createScreenShot(ownerParent);

                    // String[] recipients =
                    // Context.getClientProperties().getMailExceptionTOs();

                    MailWrapper mailWrapper = new MailWrapper();
                    // mailWrapper.setHost(Context.getClientProperties().getMailHost());
                    mailWrapper.setFrom("system@system.de");
                    // mailWrapper.setSubject("Exception (" + userID + ")");
                    mailWrapper.setAdditionalHeader("System-Category", "EXCEPTION");

                    // for (String recipient : recipients)
                    // {
                    // mailWrapper.addTO(recipient);
                    // }

                    mailWrapper.setText(getDetailsAsHTML(getErrorInfo()), true);
                    mailWrapper.addAttachment(dataSource);
                    // mailWrapper.addInline(dataSource);
                    mailWrapper.send();

                    // NOTE Spring's JavaMailSender funktioniert nur mit gueltiger
                    // Absender Email und Username + Passwort.
                    //
                    // JavaMailSenderImpl mailSender = SpringContext.getBean("mailSender");
                    // MimeMessage mail = mailSender.createMimeMessage();
                    // mail.addHeader("System-Category", "EXCEPTION");
                    //
                    // MimeMessageHelper helper = new MimeMessageHelper(mail, true);
                    // helper.setFrom("system@mail.de");
                    // helper.setSubject("Exception");
                    //
                    // for (String recipient : recipients)
                    // {
                    // helper.addTo(recipient);
                    // }
                    //
                    // helper.setText(getDetailsAsHTML(getErrorInfo()), true);
                    //
                    // Resource resource = new InputStreamResource(dataSource.getInputStream())
                    // {
                    // /**
                    // * @see org.springframework.core.io.InputStreamResource#isOpen()
                    // */
                    // @Override
                    // public boolean isOpen()
                    // {
                    // return false;
                    // }
                    // };
                    // helper.addAttachment(dataSource.getName(), resource,
                    // ByteArrayDataSource.MIMETYPE_IMAGE_PNG);
                    // // helper.addInline("screenShot1", resource,
                    // // ByteArrayDataSource.MIMETYPE_IMAGE_PNG);
                    //
                    // mailSender.send(mail);

                    LoggerFactory.getLogger(getClass()).info("Mail send...");
                }
                catch (Exception ex)
                {
                    LoggerFactory.getLogger(getClass()).error(null, ex);
                }

                ErrorPane.this.buttonSend.setEnabled(false);
            });
        }

        return this.buttonSend;
    }

    /**
     * Panel der Detailuebersicht.
     *
     * @return {@link JPanel}
     */
    private JPanel getDetailPanel()
    {
        if (this.detailPanel == null)
        {
            this.detailPanel = new JPanel();
            this.detailPanel.setLayout(new GridBagLayout());
            this.detailPanel.setMinimumSize(SIZE_DETAIL);
            this.detailPanel.setPreferredSize(SIZE_DETAIL);
            this.detailPanel.setMaximumSize(SIZE_DETAIL);

            JScrollPane scrollPane = new JScrollPane(getEditorPaneDetails());

            this.detailPanel.add(scrollPane, new GbcBuilder(0, 0).fillBoth());
            this.detailPanel.add(getButtonClipboard(), new GbcBuilder(0, 1).anchorCenter());

            this.detailPanel.setVisible(false);
        }

        return this.detailPanel;
    }

    /**
     * Liefrt einen HTML-String des StackTraces der Exception.
     *
     * @param errorInfo {@link ErrorInfo}
     * @return String
     */
    protected String getDetailsAsHTML(final ErrorInfo errorInfo)
    {
        if (errorInfo.getErrorException() != null)
        {
            StringBuilder html = new StringBuilder("<html>");
            html.append("<h2>" + escapeXml(errorInfo.getTitle()) + "</h2>");
            html.append("<HR size='1' noshade>");
            html.append("<div></div>");
            html.append("<b>Message:</b>");
            html.append("<pre>");
            html.append("    " + escapeXml(errorInfo.getBasicErrorMessage() + "\n\n"));
            html.append("    " + escapeXml(errorInfo.getErrorException().toString()));
            html.append("</pre>");
            html.append("<br>");
            html.append("<b>Level:</b>");
            html.append("<pre>");
            html.append("    " + errorInfo.getErrorLevel());
            html.append("</pre>");
            html.append("<br>");
            html.append("<b>Stack Trace:</b>");

            Throwable ex = errorInfo.getErrorException();

            while (ex != null)
            {
                html.append("<h4>" + ex.getMessage() + "</h4>");
                html.append("<pre>");

                for (int i = 0; i < ex.getStackTrace().length; i++)
                {
                    StackTraceElement el = ex.getStackTrace()[i];
                    html.append("    " + el.toString().replace("<init>", "&lt;init&gt;") + "\n");
                }

                html.append("</pre>");
                ex = ex.getCause();
            }

            html.append("</html>");

            return html.toString();
        }

        return null;
    }

    /**
     * JEditorPane fuer die Fehlermeldung.
     *
     * @return {@link JEditorPane}
     */
    private JEditorPane getEditorPaneDetails()
    {
        if (this.editorPaneDetails == null)
        {
            this.editorPaneDetails = new JEditorPane();

            this.editorPaneDetails.setEditable(false);
            this.editorPaneDetails.setContentType("text/html");
            this.editorPaneDetails.setEditorKitForContentType("text/plain", new StyledEditorKit());
            this.editorPaneDetails.setEditorKitForContentType("text/html", new HTMLEditorKit());

            // editorPaneDetails.setOpaque(false);
            setMessage(this.editorPaneDetails, getDetailsAsHTML(getErrorInfo()));
        }

        return this.editorPaneDetails;
    }

    /**
     * JEditorPane fuer die Fehlermeldung.
     *
     * @return {@link JEditorPane}
     */
    private JEditorPane getEditorPaneMessage()
    {
        if (this.editorPaneMessage == null)
        {
            this.editorPaneMessage = new JEditorPane();

            this.editorPaneMessage.setEditable(false);
            this.editorPaneMessage.setContentType("text/html");
            this.editorPaneMessage.setEditorKitForContentType("text/plain", new StyledEditorKit());
            this.editorPaneMessage.setEditorKitForContentType("text/html", new HTMLEditorKit());
            this.editorPaneMessage.setOpaque(false);

            setMessage(this.editorPaneMessage, getErrorInfo().getBasicErrorMessage());

            // this.editorPaneMessage.setMinimumSize(SIZE_MESSAGE);
            // this.editorPaneMessage.setPreferredSize(SIZE_MESSAGE);
            // this.editorPaneMessage.setMaximumSize(SIZE_MESSAGE);
        }

        return this.editorPaneMessage;
    }

    /**
     * Liefert den Inhalt der Fehlermeldung.
     *
     * @return {@link ErrorInfo}
     */
    protected ErrorInfo getErrorInfo()
    {
        return this.errorInfo;
    }

    /**
     * Label für das Icon.
     *
     * @return {@link JLabel}
     */
    private JLabel getLabelIcon()
    {
        if (this.labelIcon == null)
        {
            this.labelIcon = new JLabel();

            this.labelIcon.setIcon(UIManager.getIcon("OptionPane.errorIcon"));
        }

        return this.labelIcon;
    }

    /**
     * Eigentümer des Panels.
     *
     * @return {@link Component}
     */
    protected Component getOwner()
    {
        if (this.owner == null)
        {
            for (Component p = this; p != null; p = p.getParent())
            {
                if (p instanceof Dialog)
                {
                    return p;
                }

                if (p instanceof Window)
                {
                    return p;
                }
            }

            return null;
        }

        return this.owner;
    }

    /**
     * @return {@link JScrollPane}
     */
    private JScrollPane getScrollPaneMessage()
    {
        if (this.scrollPaneMessage == null)
        {
            this.scrollPaneMessage = new JScrollPane(getEditorPaneMessage());
            this.scrollPaneMessage.setBorder(null);

            this.scrollPaneMessage.setMinimumSize(SIZE_MESSAGE);
            this.scrollPaneMessage.setPreferredSize(SIZE_MESSAGE);
            this.scrollPaneMessage.setMaximumSize(SIZE_MESSAGE);
        }

        return this.scrollPaneMessage;
    }

    /**
     * @param enableSendMail boolean
     */
    private void initialize(final boolean enableSendMail)
    {
        setLayout(new GridBagLayout());

        add(getLabelIcon(), new GbcBuilder(0, 0).anchorNorthWest().insets(10, 10, 10, 20));

        add(getScrollPaneMessage(), new GbcBuilder(1, 0).gridwidth(3).fillHorizontal());

        add(getButtonClose(), new GbcBuilder(1, 1).weightx(1));
        // getButtonClose().setBorder(BorderFactory.createLineBorder(Color.RED));

        add(getButtonSend(), new GbcBuilder(2, 1).weightx(1));
        getButtonSend().setEnabled(enableSendMail);
        // getButtonSend().setBorder(BorderFactory.createLineBorder(Color.GREEN));

        add(getButtonDetails(), new GbcBuilder(3, 1).weightx(1));
        // getButtonDetails().setBorder(BorderFactory.createLineBorder(Color.BLUE));

        add(getDetailPanel(), new GbcBuilder(1, 2).gridwidth(3).fillBoth());

        getDetailPanel().setVisible(false);

        SwingUtilities.invokeLater(() -> {
            getButtonClose().setSelected(true);
            getButtonClose().requestFocus();
        });
    }

    /**
     * Setzt die Message in eine {@link JEditorPane} und passt deren ContentType an.
     *
     * @param editorPane {@link JEditorPane}
     * @param message String
     */
    protected void setMessage(final JEditorPane editorPane, final String message)
    {
        if (BasicHTML.isHTMLString(message))
        {
            editorPane.setContentType("text/html");
        }
        else
        {
            editorPane.setContentType("text/plain");
        }

        editorPane.setText(message);
        editorPane.setCaretPosition(0);
    }
}
