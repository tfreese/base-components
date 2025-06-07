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
import java.io.Serial;
import java.util.Locale;

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

import jakarta.activation.DataSource;

import org.jdesktop.swingx.error.ErrorInfo;
import org.slf4j.LoggerFactory;

import de.freese.base.net.mail.MailWrapper;
import de.freese.base.swing.layout.GbcBuilder;
import de.freese.base.utils.GuiUtils;

/**
 * @author Thomas Freese
 */
public final class ErrorPane extends JPanel {
    private static final Dimension SIZE_DETAIL = new Dimension(6400, 350);
    private static final Dimension SIZE_MESSAGE = new Dimension(640, 130);

    @Serial
    private static final long serialVersionUID = 8841473190098899651L;

    public static void showDialog(final Component owner, final ErrorInfo errorInfo, final boolean enableSendMail) {
        final JOptionPane pane = new JOptionPane(new ErrorPane(errorInfo, null, enableSendMail), JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new String[]{});

        final JDialog dialog = pane.createDialog(owner, errorInfo.getTitle());
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setResizable(true);
        dialog.setVisible(true);
    }

    private final transient ErrorInfo errorInfo;
    private final Component owner;
    private JButton buttonClipboard;
    private JButton buttonClose;
    private JButton buttonDetails;
    private JButton buttonSend;
    private JPanel detailPanel;
    private JEditorPane editorPaneDetails;
    private JEditorPane editorPaneMessage;
    private JLabel labelIcon;
    private JScrollPane scrollPaneMessage;

    private ErrorPane(final ErrorInfo errorInfo, final Component owner, final boolean enableSendMail) {
        super();

        this.errorInfo = errorInfo;
        this.owner = owner;

        initialize(enableSendMail);
    }

    private String escapeXml(final String input) {
        String s = (input == null) ? "" : input.replace("&", "&amp;");
        s = s.replace("<", "&lt;");
        s = s.replace(">", "&gt;");

        return s;
    }

    private JButton getButtonClipboard() {
        if (buttonClipboard == null) {
            buttonClipboard = new JButton();

            if (Locale.getDefault().getLanguage().equals(Locale.GERMAN.getLanguage())) {
                buttonClipboard.setText("In Zwischenablage kopieren");
            }
            else {
                buttonClipboard.setText("Copy to Clipboard");
            }

            buttonClipboard.addActionListener(event -> {
                final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

                getEditorPaneDetails().selectAll();

                final String text = getEditorPaneDetails().getSelectedText();

                // getEditorPaneDetails().select(-1, -1);
                final StringSelection selection = new StringSelection(text);
                clipboard.setContents(selection, selection);

                // final TransferHandler transferHandler =
                // new TransferHandler()
                // {
                // private static final long serialVersionUID = 0L;
                //
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

        return buttonClipboard;
    }

    private JButton getButtonClose() {
        if (buttonClose == null) {
            buttonClose = new JButton();

            if (Locale.getDefault().getLanguage().equals(Locale.GERMAN.getLanguage())) {
                buttonClose.setText("Schliessen");
            }
            else {
                buttonClose.setText("Close");
            }

            buttonClose.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(final KeyEvent event) {
                    if (event.getKeyCode() == KeyEvent.VK_ENTER) {
                        ErrorPane.this.buttonClose.doClick();
                    }
                }
            });

            buttonClose.addActionListener(event -> {
                final Component c = getOwner();

                if (c instanceof Window w) {
                    w.dispose();
                }
            });
        }

        return buttonClose;
    }

    private JButton getButtonDetails() {
        if (buttonDetails == null) {
            final String text = "Details";

            buttonDetails = new JButton();
            buttonDetails.setText(text + " >>");

            buttonDetails.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(final KeyEvent event) {
                    if (event.getKeyCode() == KeyEvent.VK_ENTER) {
                        ErrorPane.this.buttonDetails.doClick();
                    }
                }
            });

            buttonDetails.addActionListener(event -> {
                Dimension newSize = null;

                final Component component = getOwner();

                if (!getDetailPanel().isVisible()) {
                    newSize = new Dimension(component.getWidth(), component.getHeight() + SIZE_DETAIL.height);
                    ErrorPane.this.buttonDetails.setText(text + " <<");
                }
                else {
                    newSize = new Dimension(component.getWidth(), component.getHeight() - SIZE_DETAIL.height);
                    ErrorPane.this.buttonDetails.setText(text + " >>");
                }

                component.setSize(newSize);
                component.validate();
                component.repaint();

                // owner.doLayout();
                getDetailPanel().setVisible(!getDetailPanel().isVisible());
            });
        }

        return buttonDetails;
    }

    private JButton getButtonSend() {
        if (buttonSend == null) {
            buttonSend = new JButton();

            if (Locale.getDefault().getLanguage().equals(Locale.GERMAN.getLanguage())) {
                buttonSend.setText("Senden");
            }
            else {
                buttonSend.setText("Send");
            }

            buttonSend.addActionListener(event -> {
                try {
                    final Component x = getOwner();
                    Component ownerParent = SwingUtilities.getAncestorOfClass(Dialog.class, x);

                    if (ownerParent == null) {
                        ownerParent = SwingUtilities.getAncestorOfClass(Frame.class, x);
                    }

                    final DataSource dataSource = GuiUtils.createScreenShot(ownerParent);

                    // String[] recipients =
                    // Context.getClientProperties().getMailExceptionTOs();

                    final MailWrapper mailWrapper = new MailWrapper();
                    // mailWrapper.setHost(Context.getClientProperties().getMailHost());
                    mailWrapper.setFrom("system@system.de");
                    // mailWrapper.setSubject("Exception (" + userID + ")");
                    mailWrapper.setAdditionalHeader("System-Category", "EXCEPTION");

                    // for (String recipient : recipients) {
                    // mailWrapper.addTO(recipient);
                    // }

                    mailWrapper.setText(getDetailsAsHTML(getErrorInfo()), true);
                    mailWrapper.addAttachment(dataSource);
                    // mailWrapper.addInline(dataSource);
                    mailWrapper.send();

                    // NOTE Spring's JavaMailSender works onl with valid Sender Email and Username + Password.
                    //
                    // JavaMailSenderImpl mailSender = SpringContext.getBean("mailSender");
                    // MimeMessage mail = mailSender.createMimeMessage();
                    // mail.addHeader("System-Category", "EXCEPTION");
                    //
                    // MimeMessageHelper helper = new MimeMessageHelper(mail, true);
                    // helper.setFrom("system@mail.de");
                    // helper.setSubject("Exception");
                    //
                    // for (String recipient : recipients) {
                    // helper.addTo(recipient);
                    // }
                    //
                    // helper.setText(getDetailsAsHTML(getErrorInfo()), true);
                    //
                    // Resource resource = new InputStreamResource(dataSource.getInputStream()) {
                    // @Override
                    // public boolean isOpen() {
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
                catch (Exception ex) {
                    LoggerFactory.getLogger(getClass()).error(ex.getMessage(), ex);
                }

                ErrorPane.this.buttonSend.setEnabled(false);
            });
        }

        return buttonSend;
    }

    private JPanel getDetailPanel() {
        if (detailPanel == null) {
            detailPanel = new JPanel();
            detailPanel.setLayout(new GridBagLayout());
            detailPanel.setMinimumSize(SIZE_DETAIL);
            detailPanel.setPreferredSize(SIZE_DETAIL);
            detailPanel.setMaximumSize(SIZE_DETAIL);

            final JScrollPane scrollPane = new JScrollPane(getEditorPaneDetails());

            detailPanel.add(scrollPane, GbcBuilder.of(0, 0).fillBoth());
            detailPanel.add(getButtonClipboard(), GbcBuilder.of(0, 1).anchorCenter());

            detailPanel.setVisible(false);
        }

        return detailPanel;
    }

    private String getDetailsAsHTML(final ErrorInfo errorInfo) {
        if (errorInfo.getErrorException() != null) {
            final StringBuilder html = new StringBuilder("<html>");
            html.append("<h2>").append(escapeXml(errorInfo.getTitle())).append("</h2>");
            html.append("<HR size='1' noshade>");
            html.append("<div></div>");
            html.append("<b>Message:</b>");
            html.append("<pre>");
            html.append("    ").append(escapeXml(errorInfo.getBasicErrorMessage())).append(System.lineSeparator()).append(System.lineSeparator());
            html.append("    ").append(escapeXml(errorInfo.getErrorException().toString()));
            html.append("</pre>");
            html.append("<br>");
            html.append("<b>Level:</b>");
            html.append("<pre>");
            html.append("    ").append(errorInfo.getErrorLevel());
            html.append("</pre>");
            html.append("<br>");
            html.append("<b>Stack Trace:</b>");

            Throwable ex = errorInfo.getErrorException();

            while (ex != null) {
                html.append("<h4>").append(ex.getMessage()).append("</h4>");
                html.append("<pre>");

                for (int i = 0; i < ex.getStackTrace().length; i++) {
                    final StackTraceElement el = ex.getStackTrace()[i];
                    html.append("    ").append(el.toString().replace("<init>", "&lt;init&gt;")).append(System.lineSeparator());
                }

                html.append("</pre>");
                ex = ex.getCause();
            }

            html.append("</html>");

            return html.toString();
        }

        return null;
    }

    private JEditorPane getEditorPaneDetails() {
        if (editorPaneDetails == null) {
            editorPaneDetails = new JEditorPane();

            editorPaneDetails.setEditable(false);
            editorPaneDetails.setContentType("text/html");
            editorPaneDetails.setEditorKitForContentType("text/plain", new StyledEditorKit());
            editorPaneDetails.setEditorKitForContentType("text/html", new HTMLEditorKit());

            // editorPaneDetails.setOpaque(false);
            setMessage(editorPaneDetails, getDetailsAsHTML(getErrorInfo()));
        }

        return editorPaneDetails;
    }

    private JEditorPane getEditorPaneMessage() {
        if (editorPaneMessage == null) {
            editorPaneMessage = new JEditorPane();

            editorPaneMessage.setEditable(false);
            editorPaneMessage.setContentType("text/html");
            editorPaneMessage.setEditorKitForContentType("text/plain", new StyledEditorKit());
            editorPaneMessage.setEditorKitForContentType("text/html", new HTMLEditorKit());
            editorPaneMessage.setOpaque(false);

            setMessage(editorPaneMessage, getErrorInfo().getBasicErrorMessage());

            // editorPaneMessage.setMinimumSize(SIZE_MESSAGE);
            // editorPaneMessage.setPreferredSize(SIZE_MESSAGE);
            // editorPaneMessage.setMaximumSize(SIZE_MESSAGE);
        }

        return editorPaneMessage;
    }

    private ErrorInfo getErrorInfo() {
        return errorInfo;
    }

    private JLabel getLabelIcon() {
        if (labelIcon == null) {
            labelIcon = new JLabel();

            labelIcon.setIcon(UIManager.getIcon("OptionPane.errorIcon"));
        }

        return labelIcon;
    }

    private Component getOwner() {
        if (owner == null) {
            for (Component p = this; p != null; p = p.getParent()) {
                if (p instanceof Window) {
                    return p;
                }
            }

            return null;
        }

        return owner;
    }

    private JScrollPane getScrollPaneMessage() {
        if (scrollPaneMessage == null) {
            scrollPaneMessage = new JScrollPane(getEditorPaneMessage());
            scrollPaneMessage.setBorder(null);

            scrollPaneMessage.setMinimumSize(SIZE_MESSAGE);
            scrollPaneMessage.setPreferredSize(SIZE_MESSAGE);
            scrollPaneMessage.setMaximumSize(SIZE_MESSAGE);
        }

        return scrollPaneMessage;
    }

    private void initialize(final boolean enableSendMail) {
        setLayout(new GridBagLayout());

        add(getLabelIcon(), GbcBuilder.of(0, 0).anchorNorthWest().insets(10, 10, 10, 20));

        add(getScrollPaneMessage(), GbcBuilder.of(1, 0).gridWidth(3).fillHorizontal());

        add(getButtonClose(), GbcBuilder.of(1, 1).weightX(1));
        // getButtonClose().setBorder(BorderFactory.createLineBorder(Color.RED));

        add(getButtonSend(), GbcBuilder.of(2, 1).weightX(1));
        getButtonSend().setEnabled(enableSendMail);
        // getButtonSend().setBorder(BorderFactory.createLineBorder(Color.GREEN));

        add(getButtonDetails(), GbcBuilder.of(3, 1).weightX(1));
        // getButtonDetails().setBorder(BorderFactory.createLineBorder(Color.BLUE));

        add(getDetailPanel(), GbcBuilder.of(1, 2).gridWidth(3).fillBoth());

        getDetailPanel().setVisible(false);

        SwingUtilities.invokeLater(() -> {
            getButtonClose().setSelected(true);
            getButtonClose().requestFocus();
        });
    }

    private void setMessage(final JEditorPane editorPane, final String message) {
        if (BasicHTML.isHTMLString(message)) {
            editorPane.setContentType("text/html");
        }
        else {
            editorPane.setContentType("text/plain");
        }

        editorPane.setText(message);
        editorPane.setCaretPosition(0);
    }
}
