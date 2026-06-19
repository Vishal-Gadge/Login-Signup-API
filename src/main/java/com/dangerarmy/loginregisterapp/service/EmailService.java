package com.dangerarmy.loginregisterapp.service;

import com.dangerarmy.loginregisterapp.exception.*;
import com.dangerarmy.loginregisterapp.model.UserModel;
import com.dangerarmy.loginregisterapp.model.VerifyUser;
import com.dangerarmy.loginregisterapp.repo.UserRepo;
import com.dangerarmy.loginregisterapp.repo.VerifyUserRepo;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.security.SecureRandom;
import java.util.Date;
import java.util.HexFormat;
import java.util.Optional;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private VerifyUserRepo verifyUserRepo;

    @Value("${spring.mail.username}")
    private String from;

    public void sendVerificationEmail(String email , String verificationToken){
        String subject = "Email verification";
        String path = "/html/verifyEmail.html";
        String message = "Click the button below to verify your email address:";
        sendEmail(email,verificationToken,subject,path,message);
    }

    private void sendEmail(String email, String token, String subject, String path, String message) {
        try{
            String actionUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path(path)
                    .queryParam("token",token)
                    .toUriString();

            String content = """
                    <div style="font-family: Arial, sans-serif; max-width: 600px; margin: auto; padding: 20px;\s
                    border-radius: 8px; background-color: #f9f9f9; text-align: center;">
                        <h2 style="color: #333;">%s</h2>
                        <p style="font-size: 16px; color: #555;">%s</p>
                        <a href="%s" style="display: inline-block; margin: 20px 0; padding: 10px 20px; font-size: 16px;\s
                        color: #fff; background-color: #007bff; text-decoration: none;">verify</a>
                        <p style="font-size: 14px; color: #777;">Or click the link:</p>
                        <p style="font-size: 14px; color: #007bff;">%s</p>
                        <p style="font-size: 12px; color: #aaa;">This is an automated message. Please do not reply.</p>
                    </div>
               \s""".formatted(subject,message,actionUrl,actionUrl);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setTo(email);
            helper.setSubject(subject);
            helper.setFrom(from);
            helper.setText(content, true);
            mailSender.send(mimeMessage);
        }catch (MailException e){
            throw new UnknownHostException("Email Service unavailable, Try again later");
        } catch (MessagingException e) {
            throw new RuntimeException("Email was not created",e);
        }
    }


    //http://localhost:8080/html/verifyEmail.html?token=fim
    //Email controller work
    @Transactional
    public void verifyEmail(String token) {
            VerifyUser dbVerifyUser = verifyUserRepo.findByToken(token);
            if(dbVerifyUser == null){
                throw new InvalidTokenException("verification link is invalid");
            }
            if(dbVerifyUser.isVerified()){
                throw new UserAlreadyVerifiedException("Email is already verified");
            }
            if(dbVerifyUser.getExpiresAt().before(new Date(System.currentTimeMillis()))){
                throw new ExpiredTokenException("Token has been expired");
            }
            dbVerifyUser.setVerified(true);
            dbVerifyUser.setToken(null);
            dbVerifyUser.setExpiresAt(null);
            verifyUserRepo.save(dbVerifyUser);
            verifyUserRepo.flush();
    }


    public void resendEmail(String email) throws MailSendException  {
        Optional<UserModel> dbuser = userRepo.findByEmail(email);
        if(dbuser.isEmpty()) {
            throw new UsernameNotFoundException("User not found , Go signup first");
        }

        VerifyUser verifyUser = verifyUserRepo.findByUserModel(dbuser.orElseThrow());
        if(verifyUser == null){
            verifyUser = new VerifyUser();
            verifyUser.setUserModel(dbuser.orElseThrow());
        }

        if(verifyUser.isVerified()){
            throw new UserAlreadyVerifiedException("User already verified");
        }

        //token present just expired
        if(verifyUser.getExpiresAt().after(new Date(System.currentTimeMillis()))){
            System.out.println("token preset just expired, so just send token again with new expiration");
            sendVerificationEmail(email,verifyUser.getToken());
            verifyUser.setExpiresAt(new Date(System.currentTimeMillis()+(1000*60*10)));
            verifyUserRepo.save(verifyUser);
        }else{
            //if all set then should run
            String token = generateToken();
            sendVerificationEmail(email,token);

            verifyUser.setExpiresAt(new Date(System.currentTimeMillis()+(1000*60*10)));
            verifyUser.setToken(token);
            verifyUser.setVerified(false);
            verifyUserRepo.save(verifyUser);
        }
    }

    //utility method
    public void isValidEmail(String email) {

        if(email == null){
            throw  new InvalidEmailException("Email is null");
        }

        email = email.trim().toLowerCase();

        if(!email.endsWith("@gmail.com")){
            throw new InvalidEmailException("Email is not valid");
        }
    }

    public String generateToken(){
        byte[] bytes = new byte[8];
        new SecureRandom().nextBytes(bytes);
        return HexFormat.of().formatHex(bytes);
    }
}
