package com.vertuoso.notificacao.business;

import com.vertuoso.notificacao.business.dto.TarefaDTO;
import com.vertuoso.notificacao.infrastructure.exceptions.EmailException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

@Service
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    public EmailService(JavaMailSender javaMailSender, TemplateEngine templateEngine) {
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
    }

    @Value("${envio.email.remetente}")
    public String remetente;

    @Value("${envio.email.nomeRemetente}")
    private String nomeRemetente;

    public void enviaEmail(TarefaDTO tarefaDTO){

        try{
            MimeMessage mensagem = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mensagem, true, StandardCharsets.UTF_8.name());

            mimeMessageHelper.setFrom(new InternetAddress(remetente, nomeRemetente));
            mimeMessageHelper.setTo(InternetAddress.parse(tarefaDTO.getEmailUsuario()));
            mimeMessageHelper.setSubject("Notificação de Tarefa");

            Context context = new Context();
            context.setVariable("nomeTarefa", tarefaDTO.getNomeTarefa());
            context.setVariable("dataEvento", tarefaDTO.getDataEvento());
            context.setVariable("descricao", tarefaDTO.getDescricao());
            String template = templateEngine.process("notificacao", context);
            mimeMessageHelper.setText(template, true);
            javaMailSender.send(mensagem);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new EmailException("Erro ao enviar o e-mail ", e.getCause());
        }
    }
}
