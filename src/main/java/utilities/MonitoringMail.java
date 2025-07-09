package utilities;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

import java.util.Properties;

public class MonitoringMail {
	public void sendMail(String mailServer, String from, String[] to, String subject, String messageBody,
						 String attachmentPath, String attachmentName) throws MessagingException, MessagingException {

		System.setProperty("mail.smtp.ssl.protocols", "TLSv1.2");

		Properties props = new Properties();
		props.put("mail.smtp.host", mailServer);
		props.put("mail.smtp.port", "587");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true"); //  StartTLS
		props.put("mail.smtp.ssl.protocols", "TLSv1.2"); //  Force TLS 1.2
		props.put("mail.smtp.ssl.trust", "smtp.gmail.com"); // rust Gmail

		Authenticator auth = new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(TestConfig.from, TestConfig.password);
			}
		};
		Session session = Session.getInstance(props, auth);
		session.setDebug(true); //  Log everything

		MimeMessage message = new MimeMessage(session);
		message.setFrom(new InternetAddress(from));

		InternetAddress[] addressTo = new InternetAddress[to.length];
		for (int i = 0; i < to.length; i++) {
			addressTo[i] = new InternetAddress(to[i]);
		}
		message.setRecipients(Message.RecipientType.TO, addressTo);
		message.setSubject(subject);

		// Email body part
		MimeBodyPart bodyPart = new MimeBodyPart();
		bodyPart.setContent(messageBody, "text/html");

		// Attachment part
//		MimeBodyPart attachmentPart = new MimeBodyPart();
//		DataSource source = new FileDataSource(new File(attachmentPath));
//		attachmentPart.setDataHandler(new DataHandler(source));
//		attachmentPart.setFileName(attachmentName);

		// Combine parts
		Multipart multipart = new MimeMultipart();
		multipart.addBodyPart(bodyPart);
//		multipart.addBodyPart(attachmentPart);

		message.setContent(multipart);

		//  Send
		Transport.send(message);
		System.out.println(" Email sent successfully to all recipients.");

	}

	private class SMTPAuthenticator extends Authenticator {
		public PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(TestConfig.from, TestConfig.password);
		}
	}

}
