package com.example.foodsellingapp;

import android.os.AsyncTask;
import android.util.Log;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailSender {

    private static final String TAG = "EmailSender";
    private static final String SMTP_HOST = "smtp.sendgrid.net";
    private static final String SMTP_PORT = "587";
    private static final String FROM_EMAIL = "nghkphung95@gmail.com"; // Địa chỉ email đã xác minh
    private static final String SMTP_USERNAME = "apikey";
    private static final String SMTP_PASSWORD = "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"; // API Key của bạn

    public interface EmailCallback {
        void onSuccess();
        void onFailure(Exception e);
    }

    public static void sendEmail(String toEmail, String subject, String htmlBody, EmailCallback callback) {
        new SendEmailTask(toEmail, subject, htmlBody, callback).execute();
    }

    private static class SendEmailTask extends AsyncTask<Void, Void, Exception> {
        private final String toEmail;
        private final String subject;
        private final String htmlBody;
        private final EmailCallback callback;

        public SendEmailTask(String toEmail, String subject, String htmlBody, EmailCallback callback) {
            this.toEmail = toEmail;
            this.subject = subject;
            this.htmlBody = htmlBody;
            this.callback = callback;
        }

        @Override
        protected Exception doInBackground(Void... voids) {
            try {
                // Thiết lập thuộc tính máy chủ email
                Properties props = new Properties();
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.starttls.enable", "true");
                props.put("mail.smtp.host", SMTP_HOST);
                props.put("mail.smtp.port", SMTP_PORT);

                // Tạo phiên với xác thực
                Session session = Session.getInstance(props, new javax.mail.Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(SMTP_USERNAME, SMTP_PASSWORD);
                    }
                });

                // Tạo email mới
                MimeMessage message = new MimeMessage(session);
                message.setFrom(new InternetAddress(FROM_EMAIL));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
                message.setSubject(subject);

                // Gửi nội dung email dạng HTML
                message.setContent(htmlBody, "text/html; charset=utf-8");

                // Gửi email
                Transport.send(message);
                return null;
            } catch (MessagingException e) {
                return e;
            }
        }

        @Override
        protected void onPostExecute(Exception e) {
            if (e == null) {
                Log.d(TAG, "Email đã gửi thành công đến " + toEmail);
                callback.onSuccess();
            } else {
                Log.e(TAG, "Không thể gửi email đến " + toEmail, e);
                callback.onFailure(e);
            }
        }
    }
}