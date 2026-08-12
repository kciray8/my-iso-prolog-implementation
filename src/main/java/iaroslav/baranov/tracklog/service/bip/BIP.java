package iaroslav.baranov.tracklog.service.bip;

import iaroslav.baranov.tracklog.lexer.TokenType;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface BIP {
    String indicator();
}
