package com.dangerarmy.loginregisterapp.service;

import com.dangerarmy.loginregisterapp.exception.*;
import com.dangerarmy.loginregisterapp.model.UserModel;
import com.dangerarmy.loginregisterapp.model.VerifyUser;
import com.dangerarmy.loginregisterapp.repo.UserRepo;
import com.dangerarmy.loginregisterapp.repo.VerifyUserRepo;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.Date;
import java.util.HexFormat;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final UserRepo userRepo;
    private final VerifyUserRepo verifyUserRepo;
    private final RedisRateLimiter redisRateLimiter;

    @Value("${spring.mail.username}")
    private String from;

    @Async
    public void sendVerificationEmail(String email , String verificationToken){

        String subject = "Email verification";
        String path = "/html/verifyEmail.html";
        String message = "Click the button below to verify your email address:";
        sendVerEmail(email,verificationToken,subject,path,message);
    }

    private void sendVerEmail(String email, String token, String subject, String path, String message) {

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
            log.info("Verification email has been sent to email :{}",maskEmail(email));
        }catch (MailException e){
            log.error("Verification Email was not sent to :{}",maskEmail(email), e);
//            throw new UnknownHostException("Email Service unavailable, Check your internet or Try again later");
        } catch (MessagingException e) {
            log.error("Verification Email was not created for email :{} cause :{}",maskEmail(email), e.getCause().toString());
//            throw new RuntimeException("Email was not created",e);
        }
    }

    @Async
    public void sendVerificationOtp(String email , String otp){
        String subject = "Forgot Password Verification";
        String message = "Enter this OTP to verify its you";
        sendVerOtp(email, otp , subject, message);
    }

    public void sendVerOtp(String email, String otp , String subject , String message){
        try {
            String content = """
                        <div style="font-family: Arial, sans-serif; max-width: 600px; margin: auto; padding: 20px;\s
                        border-radius: 8px; background-color: #f9f9f9; text-align: center;">
                            <h2 style="color: #333;">%s</h2>
                            <p style="font-size: 16px; color: #555;">%s</p>
                            <p style="font-size: 16px; color: #555;">%s</p>
                            <p style="font-size: 12px; color: #aaa;">This is an automated message. Please do not reply.</p>
                        </div>
                   \s""".formatted(subject,message,otp);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setTo(email);
            helper.setSubject(subject);
            helper.setFrom(from);
            helper.setText(content, true);
            mailSender.send(mimeMessage);
            log.info("Forgot Password Verification OTP email has been sent to email :{}",maskEmail(email));
        } catch (MailException e){
            log.error("Forgot Password Verification OTP email was not sent to :{} cause :{}",maskEmail(email), e.getCause().toString());
            throw new UnknownHostException("Email Service unavailable, Check your internet or Try again later");
        } catch (MessagingException e) {
            log.error("Forgot Password Verification OTP email was not created for email :{} cause :{}",maskEmail(email), e.getCause().toString());
            throw new RuntimeException("Email was not created",e);
        }
    }

    //signup service
    @Async
    public void sendAlreadyVerified(String email){
        String subject = "User Already Verified";
        String message = "<div><p>User Already Verified with this email , no need to verify again, Go login 😊</p><br></div>";
        sendMail(email, subject, message);
    }

    //signup service
    @Async
    public void signupAttempted(String email){
        String subject = "Signup Attempted";
        String message = "<div><p>Signup for your email has been attempted, if its you ignore, if not secure your account by strong password</p></div>";
        sendMail(email, subject, message);
    }

    public void sendMail(String email, String subject, String message) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);

            helper.setTo(email);
            helper.setFrom(from);
            helper.setSubject(subject);
            helper.setText(message , true);
            mailSender.send(mimeMessage);
            log.info("Email for {} has been sent to email :{}",subject, maskEmail(email));
        } catch (MailException e){
            log.error("Email for {} was not sent to email :{} cause :{}",subject, maskEmail(email), e.getCause().toString());
            throw new UnknownHostException("Email Service unavailable, Check your internet or Try again later");
        }catch (MessagingException e) {
            log.error("Email for {} was not created for email :{} cause :{}",subject, maskEmail(email), e.getCause().toString());
            throw new RuntimeException("Email was not created",e);
        }
    }



    //Email controller work
    @Transactional
    public void verifyEmail(String token) {
            VerifyUser dbVerifyUser = verifyUserRepo.findByToken(token);
            if(dbVerifyUser == null){
                log.warn("Verification link to verify email was invalid for token :{} or user not exist for that token",token);
                throw new InvalidTokenException("verification link or token is invalid");
            }
            if(dbVerifyUser.isVerified()){
                log.warn("User already verified with the userId :{}",dbVerifyUser.getUserModel().getId());
                throw new UserAlreadyVerifiedException("Email is already verified");
            }
            if(dbVerifyUser.getExpiresAt().before(new Date(System.currentTimeMillis()))){
                log.error("Verification token is invalid for userId :{} and expired at :{}",
                        dbVerifyUser.getUserModel().getId(), dbVerifyUser.getExpiresAt());
                throw new ExpiredTokenException("Token has been expired");
            }
            dbVerifyUser.setVerified(true);
            dbVerifyUser.setToken(null);
            dbVerifyUser.setExpiresAt(null);
            verifyUserRepo.save(dbVerifyUser);
            log.info("User with userId :{} verified themself using email with token :{}",dbVerifyUser.getUserModel().getId(), token);
    }


    public void resendEmail(String email){
        redisRateLimiter.rateLimiter("Resend_Email_attempts_"+email,3,Duration.ofSeconds(600));

        Optional<UserModel> dbuser = userRepo.findByEmail(email);
        if(dbuser.isEmpty()) {
            log.warn("User not found for resend email :{}",maskEmail(email));
            throw new UsernameNotFoundException("User not found , Go signup first");
        }

        VerifyUser verifyUser = verifyUserRepo.findByUserModel(dbuser.orElseThrow());
        if(verifyUser == null){
            verifyUser = new VerifyUser();
            verifyUser.setUserModel(dbuser.orElseThrow());
        }

        if(verifyUser.isVerified()){
            log.warn("User already verified with the email :{}",maskEmail(email));
            throw new UserAlreadyVerifiedException("User already verified");
        }

        //token present just expired
        if(verifyUser.getExpiresAt().after(new Date(System.currentTimeMillis()))){
            //token preset just expired, so just send token again with new expiration
            sendVerificationEmail(email,verifyUser.getToken());
            verifyUser.setExpiresAt(new Date(System.currentTimeMillis()+(1000*60*10)));
            verifyUserRepo.save(verifyUser);
            log.info("Empty verify User was saved for user email :{}",maskEmail(email));
        }else{
            //if all set then should run
            String token = generateToken();
            sendVerificationEmail(email,token);

            verifyUser.setExpiresAt(new Date(System.currentTimeMillis()+(1000*60*10)));
            verifyUser.setToken(token);
            verifyUser.setVerified(false);
            verifyUserRepo.save(verifyUser);
            log.info("Verification Token was send to email :{}",maskEmail(email));
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

    public String maskEmail(String email){
        if(email == null || !email.contains("@")){
            return "invalid-email";
        }
        String[] parts = email.split("@");
        String name = parts[0];
        String domain = parts[1];

        //a****@gmail.com
        if(name.length() <= 2){
            return name.charAt(0)+ "****@" + domain;
        }
        //ab***c@gmail.com
        return name.substring(0,2)+"****"+name.charAt(name.length() - 1) + "@" + domain;
    }
}