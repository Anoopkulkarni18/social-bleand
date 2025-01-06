package com.socialBlend.socialBlend.helper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.internet.MimeMessage;

@Component
public class EmailSender {

	@Autowired
	JavaMailSender javaMailSender;

	@Autowired
	TemplateEngine engine;

	public void sendOtp(String to, String name, int otp) {
		MimeMessage message = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		try {
			helper.setFrom("anoopkulkarni1807@gmal.com", "Social Blend");
			helper.setTo(to);
			helper.setSubject("Otp for Verification");

			Context context = new Context();
			context.setVariable("name", name);
			context.setVariable("otp", otp);
			String body = engine.process("otp-template.html", context);
			helper.setText(body, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		javaMailSender.send(message);

	}
}
